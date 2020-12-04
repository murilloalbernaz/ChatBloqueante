
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author murillo
 */
public class Chatserver {

    public static final int PORT = 4010;
    private ServerSocket serveSocket;
    private BufferedReader in;
    private List<ClientSocket> clientes = new LinkedList<>();

    public void Start() throws IOException {
        serveSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta" + PORT);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(serveSocket.accept());
            clientes.add(clientSocket);
            new Thread(() -> clientMessageLoop(clientSocket)).start();
        }
    }

    private void clientMessageLoop(ClientSocket clientSocket) {
        String msg;
        try {
            while ((msg = clientSocket.getMessage()) != null) {
                if ("sair".equalsIgnoreCase(msg)) {
                    return;
                }

                System.out.printf("Msg recbida do cliente %s: %s \n", clientSocket.getRemoteSocketAddress(), msg);
                sendMsgTotal(clientSocket, msg);
            }
        } finally {
            clientSocket.close();
        }
    }

    private void sendMsgTotal(ClientSocket sender, String msg) {
        Iterator<ClientSocket> iterator = clientes.iterator();
        while (iterator.hasNext()) {
            ClientSocket clientSocket = iterator.next();
            if (!sender.equals(clientSocket)) {
                if (!clientSocket.sendMsg("cliente" +sender.getRemoteSocketAddress()+ ": "+msg)) {
                    iterator.remove();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Chatserver server = new Chatserver();
            server.Start();
        } catch (IOException ex) {
            System.out.println("Erro ao iniciar servidor " + ex.getMessage());
        }
        System.out.println("Finalizo");
    }
}
