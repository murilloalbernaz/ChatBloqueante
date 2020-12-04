
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author murillo
 */
public class Chatclient implements Runnable {

    private static final String Adress = "127.0.0.1";
    private ClientSocket clientSocket;
    private Scanner scanner;

    /**
     * @param args the command line arguments
     */
    public Chatclient() {
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException {
        try {
            clientSocket = new ClientSocket(new Socket(Adress, Chatserver.PORT));

            System.out.println("Cliente conectado ao servidor em " + Adress + " " + Chatserver.PORT);
            new Thread(this).start();
            messageLoop();
        }finally{
            clientSocket.close();
        }

    }

    private void messageLoop() throws IOException {
        String msg;
        do {
            System.out.println("Digite uma mensagem, ou Sair para terminar");
            msg = scanner.nextLine();
            clientSocket.sendMsg(msg);
        } while (!msg.equalsIgnoreCase("Sair"));
    }

    @Override
    public void run() {
        String msg;
        while ((msg = clientSocket.getMessage()) != null) {
            System.out.printf("Msg recebida : %s\n", msg);
        }

    }

    ;
    
    public static void main(String[] args) {
        try {
            Chatclient client = new Chatclient();
            client.start();
        } catch (IOException ex) {
            System.out.println("Erro ao iniciar client" + ex.getMessage());
        }
        System.out.println("Cliente Finalizado");
    }

}
