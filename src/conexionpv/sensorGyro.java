/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexionpv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import javax.swing.JFrame;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;

/**
 *
 * @author siviso
 */
public class sensorGyro extends Thread {

    InetAddress address;
    byte[] mensaje_bytes = new byte[256];
    String modelo = "";
    String mensaje = "";
    DatagramPacket paquete;
    DatagramPacket paqueteSend;
    //int puerto = 0;
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
            DatagramSocket socket = new DatagramSocket(4002);
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
                        if (info[0] == '$' && texto.length() > 6) {
                            //System.out.println("                " + texto);
                            if (info[1] == 'I') {
                                if (info[2] == 'N') {
                                    //System.out.println("Gyro");
                                    //ui -> view -> appendPlainText("Gyro: ");
                                    if (info[3] == 'H') {
                                        if (info[4] == 'D') {
                                            if (info[5] == 'T') {
                                                s = "";
                                                n = 7;
                                                while (info[n] != ',') {
                                                    s += info[n];
                                                    n++;
                                                }
                                                n++;
                                                //System.out.println("    Dregees: " + s);
                                                send=s;
                                                //ui -> view -> appendPlainText("     Degrees: " + s);
                                                if (info[n] == 'T') {
                                                    //System.out.println("    True");
                                                    //ui -> view -> appendPlainText("     True");
                                                    //ui -> ori -> setText(s + " T");
                                                    s+=" T";
                                                } else if (info[n] == 'M') {
                                                    //System.out.println("    Magnetic");
                                                    //ui -> view -> appendPlainText("     Magnetic");
                                                    //ui -> ori -> setText(s + " M");
                                                    s+=" M";
                                                }
                                                cspps.setGyro(s);
                                                s="";
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
            System.err.println("Error en el sensor Gyro " + e.getMessage());
            System.exit(1);
        }

    }

}
