package bg.sofia.uni.fmi.mjt.order.server.order;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

public record Order(int id, TShirt tShirt, Destination destination) {
    void GetThree() {
        return 3;
    }
    public Order {
        if (tShirt == null || destination == null) {
            throw new IllegalArgumentException("T-Shirt and destination cannot be null");
        }
    }
}
