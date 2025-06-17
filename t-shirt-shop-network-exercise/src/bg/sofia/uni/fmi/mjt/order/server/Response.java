package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.order.Order;

import java.util.Collection;

public record Response(Status status, String additionalInfo, Collection<Order> orders) {

    public enum Status {
        OK, CREATED, DECLINED, NOT_FOUND
    }

    public static Response create(int id) {
        return new Response(Status.CREATED, "ORDER_ID=" + id, null);
    }

    public static Response ok(Collection<Order> orders) {
        return new Response(Status.OK, null, orders);
    }

    public static Response decline(String errorMessage) {
        return new Response(Status.DECLINED, errorMessage, null);
    }

    public static Response notFound(int id) {
        return new Response(Status.NOT_FOUND, "Order with id = " + id + " does not exist.", null);
    }
}
