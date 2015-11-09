package com.kvm.client;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.log4j.Logger;

@ClientEndpoint
public class WebSocket {
    private Session userSession;
    private VideoDataListener videoDataListener;
    
    private static final Logger LOG = Logger.getLogger(WebSocket.class);

    public WebSocket(String endpoint){
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI endpointURI = new URI(endpoint);
            container.connectToServer(this, endpointURI);
        } catch(URISyntaxException e) {
            LOG.error("Invalid URI ("+endpoint+") :"+e);
        } catch (DeploymentException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }
    }
    
    @OnOpen
    public void onOpen(Session session){
        userSession = session;
        LOG.trace("Client session open");
    }
    
    @OnClose
    public void onClose(){
        userSession = null;
        LOG.trace("Client session closed");
    }
    
    @OnError
    public void onError(Session session, Throwable throwable){
        LOG.error("Error on session "+session.getId()+"): "+throwable);
    }
    
    /*
    @OnMessage
    public void onMessage(String message){
        JsonReader jsonReader = Json.createReader(new StringReader(message));
        JsonObject jsonData = jsonReader.readObject();
        if (jsonData.containsKey(WebSocketEvent.VIDEO_DATA) && videoDataListener!=null){
            String imgString = jsonData.getString(WebSocketEvent.VIDEO_DATA);
            byte[] byteArray = Base64.getDecoder().decode(imgString);
            videoDataListener.receiveVideoData(byteArray);
        }
    }*/
    
    @OnMessage
    public void onMessage(byte[] byteArray){
    	videoDataListener.receiveVideoData(byteArray);
    }
    
    public void sendKeyEvent(KeyEvent e, String eventType){
        JsonObject jsonKeyEvent = Json.createObjectBuilder()
                                      .add(eventType, e.getKeyCode()).build();
        String messageKeyEvent = jsonKeyEvent.toString();
        sendMessage(messageKeyEvent);
    }
    
    public void sendMouseEvent(int x, int y, int button, String eventType){
        JsonObject jsonMouseEvent = Json.createObjectBuilder()
                                        .add(eventType,"")
                                        .add(WebSocketEvent.MOUSE_X, x)
                                        .add(WebSocketEvent.MOUSE_Y, y)
                                        .add(WebSocketEvent.MOUSE_BUTTON, button).build();
        String messageMouseEvent = jsonMouseEvent.toString();
        sendMessage(messageMouseEvent);
    }
    
    public void sendMessage(String message) {
        try {
            userSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOG.error("Error sending message: "+e);
        }
    }
    
    public void setVideoDataListener(VideoDataListener videoDataListener){
        this.videoDataListener = videoDataListener;
    }
}
