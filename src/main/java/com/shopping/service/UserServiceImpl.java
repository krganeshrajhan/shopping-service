package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private UserDao userDao = new UserDao();
    @Override
    public void getUserService(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = userDao.getDetails(request.getUserName());
        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()));

        List<Order> orderList = getOrders(builder);

        builder.setNoOfOrders(orderList.size());
        UserResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(UserResponse.Builder builder) {
        logger.info("Created a channel and calling the Order client");
        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:50052").usePlaintext().build();
        OrderClient orderClient = new OrderClient(managedChannel);
        List<Order> orderList = orderClient.getOrders(builder.getId());

        try {
            managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Channel did not shutdown", e);
        }
        return orderList;
    }
}
