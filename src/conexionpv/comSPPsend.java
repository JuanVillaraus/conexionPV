/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexionpv;

/**
 *
 * @author juan
 */
//public class comSPPsend extends Thread {
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author juan
 */
public class comSPPsend extends Thread {

    DatagramSocket socket;
    InetAddress address;
    byte[] mensaje_bytes = new byte[1024];
    String mensaje = "";
    int puerto = 5003;
    //Paquete
    DatagramPacket paquete;
    DatagramPacket paqueteSensores;
    boolean habilitado = false;
    int t = 1000;
    boolean bSensor = true, bSensorPV = true;
    String Gyro = "", GPSt = "",GPSg="", AnemA = "", AnemV ="", Ecos = "";
    String TempIn = "", Hum = "", yaw = "", pitch = "", roll = "";
    String prof = "", temp = "", sal = "", velSound = "";

    public comSPPsend() {

    }

    public boolean getHabilitado() {
        return this.habilitado;
    }

    public boolean getHabilitarSensor() {
        return this.bSensor;
    }
    
    public boolean getHabilitarSensorPV() {
        return this.bSensorPV;
    }

    public String getGyro() {
        return this.Gyro;
    }

    public String getGPSt(){
        return this.GPSt;
    }
    
    public String getGPSg() {
        return this.GPSg;
    }

    public String getAnemA() {
        return this.AnemA;
    }
    
    public String getAnemV() {
        return this.AnemV;
    }

    public String getEcos() {
        return this.Ecos;
    }
    
    public String getTempIn(){
        return this.TempIn;
    }
    
    public String getHum(){
        return this.Hum;
    }
    
    public String getYaw(){
        return this.yaw;
    }
    
    public String getPitch(){
        return this.pitch;
    }
    
    public String getRoll(){
        return this.roll;
    }
    
    public String getProf(){
        return this.prof;
    }
    
    public String getTemp(){
        return this.temp;
    }
    
    public String getSal(){
        return this.sal;
    }
    
    public String getVelSound(){
        return this.velSound;
    }

    public void setHabilitado(boolean h) {
        this.habilitado = h;
    }

    public void setHabilitarSensor(boolean bSensor) {
        this.bSensor = bSensor;
    }
    
    public void setHabilitarSensorPV(boolean bSensorPV) {
        this.bSensorPV = bSensorPV;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public void setGyro(String Gyro) {
        this.Gyro = Gyro;
    }

    public void setGPSt(String GPSt) {
        this.GPSt = GPSt;
    }
    
    public void setGPSg(String GPSg) {
        this.GPSg = GPSg;
    }

    public void setAnemA(String AnemA) {
        this.AnemA = AnemA;
    }
    
    public void setAnemV(String AnemV) {
        this.AnemV = AnemV;
    }

    public void setEcos(String Ecos) {
        this.Ecos = Ecos;
    }
    
    public void setTempIn(String TempIn){
        this.TempIn = TempIn;
    }
    
    public void setHum(String Hum){
        this.Hum = Hum;
    }
    
    public void setYaw(String yaw){
        this.yaw = yaw;
    }
    
    public void setPitch(String pitch){
        this.pitch = pitch;
    }
    
    public void setRoll(String roll){
        this.roll = roll;
    }
    
    public void setProf(String prof){
        this.prof = prof;
    }
    
    public void setTemp(String temp){
        this.temp = temp;
    }
    
    public void setSal(String sal){
        this.sal = sal;
    }
    
    public void setVelSound(String velSound){
        this.velSound = velSound;
    }

    @Override
    public void run() {
        try {
            mensaje_bytes = mensaje.getBytes();
            address = InetAddress.getByName("localhost");
            mensaje = "indicadores";
            mensaje_bytes = mensaje.getBytes();
            paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
            socket = new DatagramSocket();
            int n = 0;
            Properties prop = new Properties();
            InputStream input = null;
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                t = Integer.parseInt(prop.getProperty("timeSend"));
            } catch (IOException e) {
                System.err.println("Error al leer config "+e.getMessage());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        System.err.println("Error al cerrar config "+e.getMessage());
                    }
                }
            }
            while (true) {
                if (getHabilitado()) {
                    n++;
                    System.out.println(n);
                    socket.send(paquete);
                }
                if (getHabilitarSensor()) {
                    mensaje = "G" + getGyro();
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "Pt" + getGPSt();
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "Pg" + getGPSg();
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "Aa" + getAnemA();
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "Av" + getAnemV();
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "E" + getEcos();
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                }
                if (getHabilitarSensorPV()) {
                    mensaje = "Ti" + getTempIn();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "H" + getHum();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "Y" + getYaw();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "Pi" + getPitch();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "R" + getRoll();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "Pr" + getProf();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "T" + getTemp();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "S" + getSal();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                    mensaje = "V" + getVelSound();                    
                    mensaje_bytes = mensaje.getBytes();
                    paqueteSensores = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                    socket.send(paqueteSensores);
                }
                try {
                    sleep(t);                                //espera un segundo
                } catch (Exception e) {
                    System.err.println("Error espera en comSend: "+e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error consSend "+e.getMessage());
            System.exit(1);
        }
    }

}
