package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;

import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private OrderDao orderDao = new OrderDao();

    @Override
    public void getOrdersForUser(com.shopping.stubs.order.OrderRequest request,
                                 io.grpc.stub.StreamObserver<com.shopping.stubs.order.OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());
        List<com.shopping.stubs.order.Order> ordersList = orders.stream()
                .map(order -> com.shopping.stubs.order.Order.newBuilder()
                        .setOrderId(order.getOrderId())
                        .setUserId(order.getUserId())
                        .setNoOfItems(order.getNoOfItems())
                        .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
                        .build()).collect(Collectors.toUnmodifiableList());
        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(ordersList).build();
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }
}
