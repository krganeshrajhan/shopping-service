package com.shopping.server;

import com.shopping.db.User;
import com.shopping.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UserServer {
    private static final Logger logger = Logger.getLogger(UserServer.class.getName());
    private Server server;

    public void startServer() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new UserServiceImpl()).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                UserServer.this.stopServer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public void stopServer() throws InterruptedException {
        if(server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        UserServer userServer = new UserServer();
        userServer.startServer();
        userServer.blockUntilShutdown();
    }
}
