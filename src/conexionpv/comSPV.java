/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexionpv;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFrame;

/**
 *
 * @author siviso
 */
public class comSPV extends Thread {

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    Socket socket;
    byte[] mensaje_bytes = new byte[256];
    String mensaje = "";
    String msn;
    String texto;
    String word;
    String save;
    int nDatos;
    boolean error;
    boolean habilitado = false;
    int t = 500;
    DatagramSocket socketComInterfaz;
    int portComInterfaz;
    InetAddress address;
    DatagramPacket comUP;
    DatagramPacket comDW;
    DatagramPacket paqGan;
    InetAddress ping;
    int gan;
    boolean bSensorPV = true;
    Properties prop = new Properties();
    InputStream input = null;
    String DIR = "";
    int PORT = 0;
    int n;
    comSPPsend cspps;

    public boolean getHabilitado() {
        return this.habilitado;
    }

    public boolean getHabilitarSensorPV() {
        return this.bSensorPV;
    }

    public void setHabilitado(boolean h) {
        this.habilitado = h;
    }

    public void setPortComInterfaz(int portComInterfaz) {
        this.portComInterfaz = portComInterfaz;
    }

    public void setHabilitarSensorPV(boolean bSensorPV) {
        this.bSensorPV = bSensorPV;
    }

    public void setSend(comSPPsend cspps) {
        this.cspps = cspps;
    }

    public void run() {
        try {
            //socket = new Socket(DIR, PORT);
            socketComInterfaz = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            mensaje = "C_UP";
            mensaje_bytes = mensaje.getBytes();
            comUP = new DatagramPacket(mensaje_bytes, mensaje.length(), address, portComInterfaz);
            mensaje = "C_DW";
            mensaje_bytes = mensaje.getBytes();
            comDW = new DatagramPacket(mensaje_bytes, mensaje.length(), address, portComInterfaz);
            //DataOutputStream out = new DataOutputStream(socketComInterfaz.getOutputStream());
            //BufferedReader inp = new BufferedReader(new InputStreamReader(socketComInterfaz.getInputStream()));
            sleep(1000);
            int n = 0;
            String ip = "192.168.1.10"; // Ip de la máquina remota
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                DIR = prop.getProperty("dirSSPV");
                PORT = Integer.parseInt(prop.getProperty("portBTR"));
                System.out.println("BTR comSPV " + DIR + " " + PORT);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            socket = new Socket(DIR, PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                if (getHabilitado()) {

                    try {
                        ping = InetAddress.getByName(ip);
                        if (ping.isReachable(5000)) {
                            System.out.println("Enlazado");
                            socketComInterfaz.send(comUP);
                        } else {
                            System.out.println("Desconectado");
                            socketComInterfaz.send(comDW);
                        }
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
                n = 0;
                if (getHabilitarSensorPV()) {
                    do {
                        try {
                            sleep(2000);                                //espera un segundo
                        } catch (Exception e) {
                            System.err.println("Error espera en sleep: " + e.getMessage());
                        }
                        mensaje = "TempHum";
                        out.writeUTF(mensaje);
                        System.out.println("Envie: " + mensaje);
                        msn = inp.readLine();
                        System.out.println("Recibí: " + msn);
                        mensaje = "ConfPulsRx";
                        out.writeUTF(mensaje);

                        System.out.println("Envie: " + mensaje);
                        msn = inp.readLine();
                        System.out.println("Recibí: " + msn);
                        n++;
                    } while (!"NO OK".equals(msn) && n < 2);
                    char[] charArray = msn.toCharArray();
                    word = "";
                    for (char temp : charArray) {
                        if (temp == '1' || temp == '2' || temp == '3' || temp == '4' || temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9' || temp == '0'|| temp == '.') {
                            word += temp;
                        }
                        /*if (temp == ',') {
                            System.out.println("Temp interior: " + word);
                            cspps.setTempIn(word);
                        } else if (temp == ';' && !"".equals(temp)) {
                            System.out.println("Hum interior: " + word);
                            cspps.setHum(word);
                        }*/
                        if (temp == ',' || temp == ';') {
                            System.out.println("TempHum: " + word);
                            switch (n) {
                                case 1:
                                    cspps.setTempIn(word);
                                    break;
                                case 2:
                                    cspps.setHum(word);
                                    break;
                            }
                            n++;
                        }
                        word = "";
                    }
                    mensaje = "Paq1";
                    out.writeUTF(mensaje);
                    System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    System.out.println("Recibí: " + msn);
                    mensaje = "MLM_Rx";
                    out.writeUTF(mensaje);
                    System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    System.out.println("Recibí: " + msn);
                    n = 0;
                    word = "";
                    for (char temp : charArray) {
                        if (temp == '1' || temp == '2' || temp == '3' || temp == '4' || temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9' || temp == '0'|| temp == '.') {
                            word += temp;
                        }
                        if (temp == ',' || temp == ';') {
                            System.out.println("YPR: " + word);
                            switch (n) {
                                case 1:
                                    cspps.setYaw(word);
                                    break;
                                case 2:
                                    cspps.setPitch(word);
                                    break;
                                case 3:
                                    cspps.setRoll(word);
                                    break;
                            }
                            n++;
                        }
                        word = "";
                    }
                    mensaje = "Paq2";
                    out.writeUTF(mensaje);
                    System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    System.out.println("Recibí: " + msn);
                    mensaje = "MLM_Rx";
                    out.writeUTF(mensaje);
                    System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    System.out.println("Recibí: " + msn);
                    n = 0;
                    word = "";
                    for (char temp : charArray) {
                        if (temp == '1' || temp == '2' || temp == '3' || temp == '4' || temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9' || temp == '0'|| temp == '.') {
                            word += temp;
                        }
                        if (temp == ',' || temp == ';') {
                            System.out.println("PTS: " + word);
                            switch (n) {
                                case 1:
                                    cspps.setProf(word);
                                    break;
                                case 2:
                                    cspps.setTemp(word);
                                    break;
                                case 3:
                                    cspps.setSal(word);
                                    break;
                            }
                            n++;
                        }
                        word = "";
                    }
                    mensaje = "Paq3";
                    out.writeUTF(mensaje);
                    System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    System.out.println("Recibí: " + msn);
                    mensaje = "MLM_Rx";
                    out.writeUTF(mensaje);
                    System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    System.out.println("Recibí: " + msn);
                    cspps.setVelSound(msn);
                }
                try {
                    sleep(t);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.err.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
