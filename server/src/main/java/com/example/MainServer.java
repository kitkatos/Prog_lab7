package com.example;

public class MainServer {
    static Server server = new Server(1112);

    public static void main(String[] args) {
        server.start();
    }
}
