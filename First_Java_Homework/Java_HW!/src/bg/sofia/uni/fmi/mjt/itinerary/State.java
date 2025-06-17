package bg.sofia.uni.fmi.mjt.itinerary;

import java.math.BigDecimal;

public record  State(City city, BigDecimal currCost, BigDecimal estimatedCost, City previous) {

}
