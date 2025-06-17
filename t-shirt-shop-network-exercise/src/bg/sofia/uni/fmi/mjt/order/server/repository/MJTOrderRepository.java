package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;

import java.util.*;

public class MJTOrderRepository implements OrderRepository {
    private final Map<Integer, Order> orders;
    private int orderIdCounter;

    public MJTOrderRepository() {
        this.orders = new HashMap<>();
        this.orderIdCounter = 1;
    }

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null) {
            throw new IllegalArgumentException("size, color, or destination cannot be null");
        }

        Size tShirtSize = validateSize(size);
        Color tShirtColor = validateColor(color);
        Destination tShirtDestination = validateDestination(destination);

        if (tShirtSize == Size.UNKNOWN || tShirtColor == Color.UNKNOWN || tShirtDestination == Destination.UNKNOWN) {
            return Response.decline ("invalid: size,color,destination");
        }

        Order newOrder = new Order(generateOrderId(), new TShirt(tShirtSize, tShirtColor), tShirtDestination);
        orders.put(newOrder.id(), newOrder);

        return Response.create(newOrder.id());
    }


    @Override
    public Response getOrderById(int id) {
        if (id <= 0) {
            return Response.notFound(id);
        }

        Order order = orders.get(id);
        if (order == null) {
            return Response.notFound(id);
        }

        return Response.ok(Collections.singletonList(order));
    }

    @Override
    public Response getAllOrders() {
        if (orders.isEmpty()) {
            return Response.ok(new ArrayList<>());
        }
        return Response.ok(new ArrayList<>(orders.values()));
    }

    @Override
    public Response getAllSuccessfulOrders() {
        Collection<Order> successfulOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (isValidOrder(order)) {
                successfulOrders.add(order);
            }
        }
        return Response.ok(successfulOrders);
    }

    private boolean isValidOrder(Order order) {
        int invalidAttributeCount = 0;

        if (order.tShirt().size() == Size.UNKNOWN) {
            invalidAttributeCount++;
        }

        if (order.tShirt().color() == Color.UNKNOWN) {
            invalidAttributeCount++;
        }

        if (order.destination() == Destination.UNKNOWN) {
            invalidAttributeCount++;
        }

        return invalidAttributeCount <= 1;
    }


    private Size validateSize(String size) {
        try {
            return Size.valueOf(size.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Size.UNKNOWN;
        }
    }

    private Color validateColor(String color) {
        try {
            return Color.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Color.UNKNOWN;
        }
    }

    private Destination validateDestination(String destination) {
        try {
            return Destination.valueOf(destination.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Destination.UNKNOWN;
        }
    }

    private int generateOrderId() {
        return orderIdCounter++;
    }
}
