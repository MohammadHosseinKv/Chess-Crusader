package main.java.MohammadHosseinKv.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketManager {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public SocketManager(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void sendRequest(Object... args) throws IOException {
        for (Object arg : args) {
            outputStream.writeObject(arg);
        }
        outputStream.flush();
    }

    public Object readRequest() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    public void close() throws IOException {
        socket.close();
    }
}
