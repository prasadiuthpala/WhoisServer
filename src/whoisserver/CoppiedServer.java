package WhoisServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Prasadi_uthpala
 */
public class CoppiedServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        boolean listeningSocket = true;
       

        try {

            System.out.println("Server Listening......");
            server = new ServerSocket(43);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 43");
        }
        while (listeningSocket) {
            try {
                Socket client = server.accept();
                System.out.println("connection Established");
                ServerThread st = new ServerThread(client);
                st.start();
            } catch (Exception e) {
            }

        }
    }
}
