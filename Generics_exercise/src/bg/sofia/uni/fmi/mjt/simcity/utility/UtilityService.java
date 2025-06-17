package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.HashMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {

    private final Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = taxRates;
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null || billable == null) {
            throw new IllegalArgumentException("Utility type or billable cannot be null");
        }
        double consumption = getUtilityConsumption(utilityType, billable);
        return taxRates.get(utilityType) * consumption;
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException("Billable cannot be null");
        }
        double totalCost = 0.0;
        for (UtilityType utilityType : UtilityType.values()) {
            totalCost += getUtilityCosts(utilityType, billable);
        }
        return totalCost;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException("Billable buildings cannot be null");
        }

        Map<UtilityType, Double> costDifference = new HashMap<>();
        for (UtilityType utilityType : UtilityType.values()) {
            double firstCost = getUtilityCosts(utilityType, firstBillable);
            double secondCost = getUtilityCosts(utilityType, secondBillable);
            costDifference.put(utilityType, Math.abs(firstCost - secondCost));
        }
        return Map.copyOf(costDifference); // Return an unmodifiable map of cost differences
    }

    private <T extends Billable> double getUtilityConsumption(UtilityType utilityType, T billable) {
        switch (utilityType) {
            case WATER:
                return billable.getWaterConsumption();
            case ELECTRICITY:
                return billable.getElectricityConsumption();
            case NATURAL_GAS:
                return billable.getNaturalGasConsumption();
            default:
                throw new IllegalArgumentException("Invalid utility type");
        }
    }
}
