package com.kvm.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class VideoWindow implements Runnable,VideoDataListener,KeyListener,MouseListener {

    private JFrame frame;
    private JLabel label;
    private WebSocket wsClient;
    
    private int serverScreenWidth = -1;
    private int serverScreenHeight = -1;
    private int resizedScreenWidth = -1;
    private int resizedScreenHeight = -1;
    
    private int[] previousImageArray = null;
    
    private static final Logger LOG = Logger.getLogger(VideoWindow.class);
    
    public VideoWindow(WebSocket wsClient){
        this.wsClient = wsClient;
        
        frame = new JFrame("V in KVM");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(640,480));
        frame.setBackground(Color.black);
        JPanel panel = new JPanel();
        label = new JLabel();
        panel.add(label, "wrap");
        frame.add(panel);
        frame.pack();
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        
        frame.addKeyListener(this);
        label.addMouseListener(this);
    }
    
    @Override
    public void run() {
        frame.setVisible(true);
        wsClient.setVideoDataListener(this);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        wsClient.sendKeyEvent(e,WebSocketEvent.KEY_PRESSED);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        wsClient.sendKeyEvent(e,WebSocketEvent.KEY_RELEASED);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {  
    }
    @Override
    public void mousePressed(MouseEvent e) {  
    }
    @Override
    public void mouseExited(MouseEvent e) {  
    }
    @Override
    public void mouseEntered(MouseEvent e) { 
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (serverScreenWidth!=-1 && resizedScreenWidth!=-1){
            float fX = (float)e.getX()/(float)resizedScreenWidth;
            int serverX = (int)((float)serverScreenWidth*fX);

            float fY = (float)e.getY()/(float)resizedScreenHeight;
            int serverY = (int)((float)serverScreenHeight*fY);

            int button = InputEvent.getMaskForButton(e.getButton());
            wsClient.sendMouseEvent(serverX,serverY,button,WebSocketEvent.MOUSE_CLICKED);
        }
    }

    @Override
    public void receiveVideoData(byte[] byteArray) {
        try {
        	ByteArrayInputStream byteInput = new ByteArrayInputStream(byteArray);
        	GZIPInputStream gzip = new GZIPInputStream(byteInput);
            BufferedImage tempImg = ImageIO.read(gzip);
            int[] imageArray = new int[tempImg.getWidth()*tempImg.getHeight()]; 
            tempImg.getRGB(0, 0, tempImg.getWidth(), tempImg.getHeight(), imageArray, 0, tempImg.getWidth());
            
        	if (previousImageArray!=null){
        		for (int i=0;i<imageArray.length;i++){
        			if (imageArray[i]==0){
        				imageArray[i] = previousImageArray[i];
        			}
        		}
        	}
        	previousImageArray = imageArray;
        	
        	BufferedImage screen = new BufferedImage(tempImg.getWidth(),tempImg.getHeight(),BufferedImage.TYPE_INT_ARGB);
        	screen.setRGB(0, 0, tempImg.getWidth(), tempImg.getHeight(), imageArray, 0, tempImg.getWidth());
            if (serverScreenWidth==-1){
                serverScreenWidth = screen.getWidth();
                serverScreenHeight = screen.getHeight();
            }
            Rectangle r = frame.getContentPane().getBounds();
            Image resizedImage = ImageHelper.resizeImage(screen, r.width, r.height, true);
            ImageIcon img = new ImageIcon(resizedImage);
            label.setIcon(img);
            label.invalidate();
            
            resizedScreenWidth = resizedImage.getWidth(null);
            resizedScreenHeight = resizedImage.getHeight(null);
        } catch (IOException e) {
            LOG.error("Error receiving image: "+e);
        }
    }

}
