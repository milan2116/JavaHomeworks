package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;

public record Journey(VehicleType vehicleType, City from, City to, BigDecimal price) {
    private BigDecimal getTax() {
        return price.add(price.multiply(vehicleType.getGreenTax()));
    }
    public BigDecimal getCost() {
        return getTax().add( new BigDecimal( from.location().manhattanDistanceTo(to.location())).multiply(new BigDecimal("20"))
                .divide(new BigDecimal("1000")));
    }
}
