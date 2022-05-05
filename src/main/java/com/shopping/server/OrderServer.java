package com.shopping.server;

import com.shopping.service.OrderServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class OrderServer {
    private Logger logger = Logger.getLogger(OrderServer.class.getName());
    private Server server;

    public void startServer() throws IOException {
        int port = 50052;
        server = ServerBuilder.forPort(port)
                .addService(new OrderServiceImpl()).build().start();
        logger.info("Order server started on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                OrderServer.this.stopServer();
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
        OrderServer orderServer = new OrderServer();
        orderServer.startServer();
        orderServer.blockUntilShutdown();
    }
}
