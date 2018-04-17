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
public class sensorGPS extends Thread {

    InetAddress address;
    byte[] mensaje_bytes = new byte[512];
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
    byte[] RecogerServidor_bytes = new byte[512];
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
            DatagramSocket socket = new DatagramSocket(4004);
            do {
                RecogerServidor_bytes = new byte[512];
                servPaquete = new DatagramPacket(RecogerServidor_bytes, 512);
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                //System.out.println("Recibí: " + cadenaMensaje);
                paqInfo = cadenaMensaje.toCharArray();
                texto = "";
                //System.out.println("GPS -> " + cadenaMensaje.length());
                for (int i = 0; i < cadenaMensaje.length(); i++) {
                    if (paqInfo[i] == '$') {
                        info = texto.toCharArray();
                        if (texto.length() > 6) {
                            if (info[0] == '$') {
                                //System.out.println("                " + texto);
                                if (info[1] == 'G') {
                                    //System.out.println("GPS: ");
                                    if (info[2] == 'P') {
                                        if (info[3] == 'G') {
                                            if (info[4] == 'G') {
                                                if (info[5] == 'A') {
                                                    s = "";
                                                    n = 1;
                                                    for (int x = 7; x < info.length; x++) {
                                                        if (info[x] != ',' && info[x] != '*') {
                                                            s += info[x];
                                                        } else {
                                                            sr = s.toCharArray();
                                                            switch (n) {
                                                                case 1:
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                                case 2:
                                                                    sLat = "";
                                                                    c = 0;
                                                                    sLat += sr[c];
                                                                    c++;
                                                                    sLat += sr[c];
                                                                    sLat += "°";
                                                                    c++;
                                                                    sLat += sr[c];
                                                                    c++;
                                                                    sLat += sr[c];
                                                                    sLat += "°";
                                                                    segundos = "";
                                                                    c++;
                                                                    for (; c < s.length(); c++) {
                                                                        segundos += sr[c];
                                                                    }
                                                                    lat = Double.parseDouble(segundos);
                                                                    lat *= 60;
                                                                    sLat += Integer.toString((int) lat);
                                                                    sLat += "°";
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                                case 3:
                                                                    if (sr[0] == 'N') {
                                                                        sLat += " N ";
                                                                        //System.out.println(sLat);
                                                                        cspps.setGPSt(sLat);
                                                                    } else if (sr[0] == 'S') {
                                                                        sLat += " S ";
                                                                        //System.out.println(sLat);
                                                                        cspps.setGPSt(sLat);
                                                                    }
                                                                    
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                                case 4:
                                                                    sLog = "";
                                                                    c = 1;
                                                                    sLog += sr[c];
                                                                    c++;
                                                                    sLog += sr[c];
                                                                    sLog += "°";
                                                                    c++;
                                                                    sLog += sr[c];
                                                                    c++;
                                                                    sLog += sr[c];
                                                                    sLog += "°";
                                                                    segundos = "";
                                                                    c++;
                                                                    for (; c < s.length(); c++) {
                                                                        segundos += sr[c];
                                                                    }
                                                                    log = Double.parseDouble(segundos);
                                                                    log *= 60;
                                                                    sLog += Integer.toString((int) log);
                                                                    sLog += "°";
                                                                    s = "";
                                                                    n++;
                                                                    break;
                                                                case 5:
                                                                    if (sr[0] == 'E') {
                                                                        sLog += " E ";
                                                                        //System.out.println(sLog);
                                                                        //ui->log->setText(sLog);
                                                                        cspps.setGPSg(sLog);
                                                                    } else if (sr[0] == 'W') {
                                                                        sLog += " W ";
                                                                        //System.out.println(sLog);
                                                                        cspps.setGPSg(sLog);
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
                        texto = "$";
                    } else {
                        texto += paqInfo[i];
                    }
                }
            } while (true);
        } catch (Exception e) {
            System.err.println("Error en el sensor GPS " + e.getMessage() + "    info: " + info.length + "  texto: " + texto);
            System.exit(1);
        }

    }

}
