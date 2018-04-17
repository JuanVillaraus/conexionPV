/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexionpv;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

/**
 *
 * @author juan
 */
class comInterfaz extends Thread {

    DatagramSocket socket;
    Socket socketGan;
    InetAddress address;
    byte[] mensaje_bytes = new byte[2000000];
    String mensaje = "";
    DatagramPacket paquete;
    DatagramPacket paqueteAntena;
    DatagramPacket paquetePlay;
    String cadenaMensaje = "";
    DatagramPacket servPaquete;
    byte[] RecogerServidor_bytes = new byte[2000000];
    String texto = "";
    despliegue desp = new despliegue();
    archivo a = new archivo();
    char[] charArray;
    String word;

    PlaySound s = new PlaySound();
    int[] sound = new int[600000];
    int limSound = 0;

    //@Override
    public void run() {
        try {
           address = InetAddress.getByName("localhost");
            mensaje = "runConxPV";
            mensaje_bytes = mensaje.getBytes();
            paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5003);
            socket = new DatagramSocket();
            socket.send(paquete);

            System.out.println("enviamos " + mensaje + " para inicializar la comunicación con el software");
            /*mensaje = "ANT";
            mensaje_bytes = mensaje.getBytes();
            paqueteAntena = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5003);
            socket.send(paqueteAntena);*/
            mensaje = "PLAY OK";
            mensaje_bytes = mensaje.getBytes();
            paquetePlay = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5003);
            comSPPsend cspps = new comSPPsend();
            
            sensorGyro s1 = new sensorGyro();
            sensorGPS s2 = new sensorGPS();
            sensorAnem s3 = new sensorAnem();
            sensorEcos s4 = new sensorEcos();
            s1.setSend(cspps);
            s2.setSend(cspps);
            s3.setSend(cspps);
            s4.setSend(cspps);
            //cspps.setPuerto(5003);
            //cspps.setHabilitarSensor(true);
            
            

            comSPV cspv = new comSPV();
            cspv.setSend(cspps);
            //cspv.setPortComInterfaz(socket.getLocalPort());
            cspv.start();
            //cspv.setHabilitado(true);

            //cspps.setEstado(0);
            
            try {
                    sleep(2000);                                //espera un segundo
                } catch (Exception e) {
                    System.err.println("Error en espera"+e.getMessage());
                }
            cspps.start();
            s1.start();
            s2.start();
            s3.start();
            s4.start();
            
            desp.fCom(0);

            /*Properties prop = new Properties();
            InputStream input = null;
            String DIR = "";
            int PORT = 0;
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                DIR = prop.getProperty("dirSSPV");
                PORT = Integer.parseInt(prop.getProperty("portLF"));
                System.out.println("conexionPV comInterfaz " + DIR + " " + PORT);
            } catch (IOException e) {
                System.err.println("Error al leer el archivo config: " + e.getMessage());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        System.err.println("Error al cerrar el archivo config: " + e.getMessage());
                    }
                }
            }
            
            /*socketGan = new Socket(DIR, PORT);
            DataOutputStream out = new DataOutputStream(socketGan.getOutputStream());
            BufferedReader inp = new BufferedReader(new InputStreamReader(socketGan.getInputStream()));*/

            do {
                RecogerServidor_bytes = new byte[256];
                servPaquete = new DatagramPacket(RecogerServidor_bytes, 256);
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                System.out.println(cadenaMensaje);

                if (null != cadenaMensaje) {
                    switch (cadenaMensaje) {
                        case "OFF":
                        case "C_DW":
                            desp.fCom(0);
                            break;
                        case "ON":
                        case "C_UP":
                            desp.fCom(2);
                            break;
                        case "RUN":
                            //cspv.setHabilitado(true);
                            break;
                        case "RP":
                            desp.rp();
                            break;
                        case "EXIT":
                            System.exit(0);
                            break;
                        case "PLAY":
                            txtToSound("resource/audio.txt");
                            break;
                        case "REC":
                            desp.audioOpen();
                            rec();
                            socket.send(paquetePlay);
                            desp.close();
                            break;
                        default:
                            charArray = cadenaMensaje.toCharArray();
                            if (charArray[0] == 'G') {
                                for (int i = 1; i < charArray.length; i++) {
                                    word += charArray[i];
                                }
                                word = "Ganancia " + word;
                                /*out.writeUTF(word);
                                word = inp.readLine();
                                System.out.println(word);
                                word = "";*/
                            }
                            break;
                    }
                }
            } while (true);
        } catch (Exception e) {
            System.err.println("Error ConxSPV run comInterfaz " + e.getMessage());
            System.exit(1);
        }
    }

    public void txtToSound(String dir) {                                     //lee lo que haya en un archivo txt, recibe como parametros la direccion tipo String y devuelve el String del contenido en una sola linea
        String info = "";
        int lim = 0;
        limSound = 0;
        for (int i = 0; i < sound.length; i++) {
            sound[i] = 0;
        }
        try {
            BufferedReader bf = new BufferedReader(new FileReader(dir));
            String bfRead;
            while ((bfRead = bf.readLine()) != null && lim < 20) {
                info += bfRead;
                lim++;
            }
            bf.close();
        } catch (Exception e) {
            System.err.println("SOY READ LINE: - Error: " + e);
            try {
                socket.send(paquetePlay);
            } catch (IOException ex) {
                System.err.println("SOY READ LINE: intenté enviar PLAY OK para habilitar boton de reproducir - Error: " + ex);
            }
        }

        char[] charArray = info.toCharArray();
        info = "";
        for (char temp : charArray) {
            if (!(temp == ',') && !(temp == ';')) {
                info += temp;
            } else {
                try {
                    sound[limSound] = Integer.parseInt(info);
                    //System.out.print(info + " ");

                } catch (Exception e) {
                    System.err.println("ParseInt: " + e.getMessage());
                    limSound--;
                }
                info = "";
                limSound++;
            }
        }
        System.out.println("limSound " + limSound);

        byte[] arreglobyte = new byte[limSound];
        //System.out.println("lim byte " + arreglobyte.length);
        for (int j = 0; j < limSound - 1; j++) {
            arreglobyte[j] = (byte) (sound[j] & 0xFF);

        }
        //System.out.println(limSound + " " + (limSound/760));
        try {
            s.setAudio(arreglobyte);
            //s.run();
            s.audio();
            socket.send(paquetePlay);
        } catch (Exception e) {
            System.err.println("Play: No se encontro el archivo " + e);
        }
    }

    public void rec() {
        long time = System.currentTimeMillis();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Socket socketTCP;
        byte[] mensaje_bytes = new byte[256];
        String mensaje = "";
        String msn;
        String audioTxt;
        int p;
        int[] sound = new int[262144];
        try {

            socketTCP = new Socket("192.168.1.10", 30000);
            DataOutputStream out = new DataOutputStream(socketTCP.getOutputStream());
            BufferedReader inp = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));

            int n = socketTCP.getLocalPort();
            System.out.println(n);
            String info;
            int limSound = 0;
            audioTxt = "";
            p = 0;
            mensaje = "activarAudio";
            out.writeUTF(mensaje);
            msn = inp.readLine();
            System.out.println(msn);
            for (int i = 1; i <= 6553; i++) {
                mensaje = "Audio " + i;
                //mensaje = "Audio 1";
                out.writeUTF(mensaje);
                msn = inp.readLine();
                System.out.println(msn);
                //System.out.println(toHexadecimal(msn));
                char[] charArray = msn.toCharArray();
                info = "";
                for (char temp : charArray) {
                    if (temp == '1' || temp == '2' || temp == '3' || temp == '4' || temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9' || temp == '0' || temp == ',' || temp == ';') {
                        audioTxt += temp;
                        if (!(temp == ',') && !(temp == ';')) {
                            info += temp;

                        } else {
                            try {
                                sound[limSound] = Integer.parseInt(info);
                                //System.out.print(info + " ");

                            } catch (Exception e) {
                                System.err.println("Error: ParseInt: " + e.getMessage());
                            }
                            info = "";
                            limSound++;
                        }
                    }
                }
                p++;
                System.out.println(p + " peticiones\t" + limSound + " datos");
            }

            byte[] arreglobyte = new byte[limSound];
            //System.out.println("lim byte " + arreglobyte.length);
            for (int j = 0; j < limSound - 1; j++) {
                arreglobyte[j] = (byte) (sound[j] & 0xFF);
            }
            try {
                s.setAudio(arreglobyte);
                s.audio();
                socket.send(paquetePlay);
            } catch (Exception e) {
                System.err.println("Play: No se encontro el archivo " + e);
            }

            a.escribirTxt("resource/audio.txt", audioTxt);

            time = System.currentTimeMillis() - time;
            System.out.println("Averaged " + time + "ms per iteration");

        } catch (Exception e) {
            System.err.println("ERROR " + e.getMessage());
            System.exit(1);
        }
    }
}
