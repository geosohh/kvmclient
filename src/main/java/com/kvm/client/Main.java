package com.kvm.client;

public class Main {

    private Main(){
    }
    
    public static void main(String[] args) {
        WebSocket wsClient = new WebSocket("ws://"+args[0]+":8080/socialweb/ws/kvm-server");
        Thread window = new Thread(new VideoWindow(wsClient));
        window.start();
    }
    
}
