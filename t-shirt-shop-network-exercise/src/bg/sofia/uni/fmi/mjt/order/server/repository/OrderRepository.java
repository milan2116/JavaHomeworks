package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;

public interface OrderRepository {
    Response request(String size, String color, String destination);

    Response getOrderById(int id);

    Response getAllOrders();

    Response getAllSuccessfulOrders();
}
