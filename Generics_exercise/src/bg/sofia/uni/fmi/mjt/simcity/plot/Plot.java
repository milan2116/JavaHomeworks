package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {

    private int buildableArea;
    private Map<String, E> buildables;

    public Plot(int buildableArea) {
        this.buildableArea = buildableArea;
        this.buildables = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null || address.isBlank() || buildable == null) {
            throw new IllegalArgumentException("Invalid address or buildable");
        }
        if (buildables.containsKey(address)) {
            throw new BuildableAlreadyExistsException("Address already occupied");
        }
        if (buildable.getArea() > buildableArea) {
            throw new InsufficientPlotAreaException("Insufficient plot area");
        }

        buildables.put(address, buildable);
        buildableArea -= buildable.getArea();
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException("Invalid buildables map");
        }

        for (Map.Entry<String, E> entry : buildables.entrySet()) {
            construct(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Invalid address");
        }
        if (!buildables.containsKey(address)) {
            throw new BuildableNotFoundException("Buildable not found");
        }

        E removedBuildable = buildables.remove(address);
        buildableArea += removedBuildable.getArea();
    }

    @Override
    public void demolishAll() {
        buildables.clear();
        buildableArea = 0; // Reset the buildable area when all buildables are demolished
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(buildables); // Return an unmodifiable copy of buildables
    }

    @Override
    public int getRemainingBuildableArea() {
        return buildableArea; // Return the remaining buildable area
    }
}
