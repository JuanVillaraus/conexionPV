/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexionpv;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;

/**
 *
 * @author siviso
 */
public class sensorAnem extends Thread {

    InetAddress address;
    byte[] mensaje_bytes = new byte[256];
    String modelo = "";
    String mensaje = "";
    DatagramPacket paquete;
    DatagramPacket paqueteSend;
    int puerto = 0;
    String cadenaMensaje = "";
    char[] info, sr, paqInfo;
    String s = "", segundos = "";
    String sLat, sLog;
    DatagramPacket servPaquete;
    byte[] RecogerServidor_bytes = new byte[256];
    String texto = "";
    Properties prop = new Properties();
    InputStream input = null;
    int n, c;
    double lat, log;
    comSPPsend cspps;
    String send="";

    public void setSend(comSPPsend cspps) {
        this.cspps = cspps;
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(4006);
            do {
                RecogerServidor_bytes = new byte[256];
                servPaquete = new DatagramPacket(RecogerServidor_bytes, 256);
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                //System.out.println("Recibí: " + cadenaMensaje);
                paqInfo = cadenaMensaje.toCharArray();
                texto = "";
                for (int i = 0; i < cadenaMensaje.length(); i++) {
                    if (paqInfo[i] == '$') {
                        info = texto.toCharArray();
                        if (texto.length() > 6) {
                            if (info[0] == '$') {
                                //System.out.println("                " + texto);
                                if (info[1] == 'W') {
                                    //System.out.println("Anemometro");
                                    if (info[2] == 'I') {
                                        //ui -> view -> appendPlainText("Gyro: ");
                                        if (info[3] == 'M') {
                                            if (info[4] == 'W') {
                                                if (info[5] == 'V') {
                                                    s = "";
                                                    n = 1;
                                                    for (int x = 7; x < info.length; x++) {
                                                        if (info[x] != ',' && info[x] != '*') {
                                                            s += info[x];
                                                        } else {
                                                            sr = s.toCharArray();
                                                            switch (n) {
                                                                case 1:
                                                                    //System.out.println("     Wind Ange: " + s + " degrees");
                                                                    send=s;
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                                case 2:
                                                                    if (sr[0] == 'R') {
                                                                        //System.out.println("    Relative");
                                                                        send+=" R ";
                                                                        cspps.setAnemA(send);
                                                                    } else if (sr[0] == 'T') {
                                                                        //System.out.println("    True");
                                                                        send+=" T ";
                                                                        cspps.setAnemA(send);
                                                                    }
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                                case 3:
                                                                    //System.out.println("    Wind Speed: " + s);
                                                                    send=s;
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                                case 4:
                                                                    if (sr[0] == 'K') {
                                                                        //System.out.println("    Km/h");
                                                                        send += " KM/h ";
                                                                        cspps.setAnemV(send);
                                                                    } else if (sr[0] == 'M') {
                                                                        //System.out.println("    m/s");
                                                                        send += " m/s ";
                                                                        cspps.setAnemV(send);
                                                                    } else if (sr[0] == 'N') {
                                                                        //System.out.println("    Knots");
                                                                        send += " Knots ";
                                                                        cspps.setAnemV(send);
                                                                    } else {
                                                                        //System.out.println("    ?");
                                                                    }
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        texto = "";
                    }
                    texto += paqInfo[i];
                }
            } while (true);

        } catch (Exception e) {
            System.err.println("Error en el sensor Anemometro " + e.getMessage() + "    info: " + info.length + "  texto: " + texto);
            System.exit(1);
        }

    }
}
