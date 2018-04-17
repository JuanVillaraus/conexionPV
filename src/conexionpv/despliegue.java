/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexionpv;

import java.awt.*;
import java.io.*;
import static java.lang.Thread.sleep;
import java.util.Properties;
import javax.swing.*;

/**
 *
 * @author siviso
 */
class despliegue extends JComponent {

    int sizeCanalX = 0;
    int sizeCanalY;
    String info;
    private int[] foco = {0, 0, 0};
    int limF = 0;
    JFrame w;

    public despliegue() {
        JFrame w = new JFrame();
        //w.pack();
        w.setUndecorated(true);
        w.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //w.setLocationRelativeTo(null);
        w.setVisible(true);
        w.setAlwaysOnTop(true);
        //w.setFocusable(true);
        //w.setLocation(1610, 1215);
         Properties prop = new Properties();
        InputStream input = null;
        int posicionX = 0;
        int posicionY = 0;
        int dimensionX = 0;
        int dimensionY = 0;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            posicionX = Integer.parseInt(prop.getProperty("posicionXConx"));
            posicionY = Integer.parseInt(prop.getProperty("posicionYConx"));
            dimensionX = Integer.parseInt(prop.getProperty("dimensionXConx"));
            dimensionY = Integer.parseInt(prop.getProperty("dimensionYConx"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("Error despliegue: "+e.getMessage());
                }
            }
        }
        w.setSize(dimensionX, dimensionY);
        w.setLocation(posicionX, posicionY);
        w.add(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        String[] etiqueta = {"COM", "Conf", "INFO"};
        super.paintComponent(g);
        g.setColor(new Color(40, 40, 40));
        g.fillRect(0, 0, getSize().width, getSize().height);
        limF = getSize().width / 4;
        for (int x = 0; x < getFoco().length; x++) {
            g.setColor(new Color(180, 180, 180));
            g.drawString(etiqueta[x], (limF * (x + 1)) - 15, (getSize().height / 2) - 10);
            g.setColor(getEdo(getFoco()[x]));
            g.fillOval((limF * (x + 1)) - 5, (getSize().height / 2), 10, 10);
        }
    }

    private Color getEdo(int n) {
        switch (n) {
            case 0:
                return Color.RED;
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.CYAN;
        }
        return Color.BLACK;
    }

    public int[] getFoco() {
        return this.foco;
    }

    public void setFoco(int[] foco) {
        this.foco = foco;
    }

    public void fCom(int n) {
        int[] foco = getFoco();
        foco[0] = n;
        setFoco(foco);
        repaint();
    }

    public void fConf() {
        int[] foco = getFoco();
        foco[2] = 2;
        setFoco(foco);
        repaint();
        try {
            sleep(500);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        foco[2] = 0;
        setFoco(foco);
        repaint();
    }

    public void fInfo() {
        int[] foco = getFoco();
        foco[3] = 2;
        setFoco(foco);
        repaint();
        try {
            sleep(300);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        foco[3] = 0;
        setFoco(foco);
        repaint();
    }
    
    public void audioOpen(){
        int[] foco = getFoco();
        foco[2] = 3;
        setFoco(foco);
        repaint();
    }
    
    public void close(){
        int[] foco = getFoco();
        foco[2] = 0;
        setFoco(foco);
        repaint();
    }
    
    public void rp(){
        repaint();
    }
    
}
