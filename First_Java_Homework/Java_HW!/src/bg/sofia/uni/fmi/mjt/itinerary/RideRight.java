package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;
import java.util.*;
public class RideRight implements ItineraryPlanner {

    private final List<Journey> schedule;

    public RideRight(List<Journey> schedule) {
        this.schedule = new ArrayList<>(schedule);
    }

    private Journey citiesToJourney(City from, City to)  {
        for (Journey journey: schedule) {
            if(journey.from().equals(from) && journey.to().equals(to)) {
                return journey;
            }
        }
        return null;
    }
    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException {
        if (!scheduleContainsCity(start) || !scheduleContainsCity(destination)) {
            throw new CityNotKnownException("Start or destination city is not present in the schedule");
        }
        if(!allowTransfer) {
            Journey resJourney = null;
            BigDecimal cost = null;
            for (Journey journey : schedule) {

                if(journey.from().equals(start) && journey.to().equals(destination) && (cost == null ||
                        cost.compareTo(journey.getCost()) > 0)) {
                    ;resJourney = journey;
                    cost = journey.getCost();

                }
            }
            if(resJourney == null) {
                throw new NoPathToDestinationException("No path satisfying the conditions");
            }
            return List.of(resJourney);
        }
        Map<City, Journey> cameFrom = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparing(State::estimatedCost));
        queue.add(new State(start, BigDecimal.ZERO, getCost(start, destination), null));

        while (!queue.isEmpty()) {

            State current = queue.poll();
            cameFrom.put(current.city(), citiesToJourney(current.previous(), current.city()));
            if (current.city().equals(destination)) {
                return reconstructPath(cameFrom, start, destination);
            }

            for (Journey journey : getJourneysFromCity(current.city(), allowTransfer)) {
                City next = journey.to();
                queue.add(new State(next, current.currCost().add(journey.getCost()),
                        current.currCost().add(journey.getCost()).add(getCost(next,destination)) , current.city()));


            }

        }

        throw new NoPathToDestinationException("No path satisfying the conditions");
    }

    private boolean scheduleContainsCity(City city) {
        for (Journey journey : schedule) {
            if (journey.from().equals(city) || journey.to().equals(city)) {
                return true;
            }
        }
        return false;
    }

    private List<Journey> getJourneysFromCity(City city, boolean allowTransfer) {
        List<Journey> journeys = new ArrayList<>();
        for (Journey journey : schedule) {
            if (journey.from().equals(city)) {
                journeys.add(journey);
            }
        }
        return journeys;
    }

    private BigDecimal getCost(City from, City to) {
        return new BigDecimal( from.location().manhattanDistanceTo(to.location())).multiply(new BigDecimal("20"))
                .divide(new BigDecimal("1000"));
    }

    private SequencedCollection<Journey> reconstructPath(Map<City, Journey> cameFrom, City start, City destination) {
        List<Journey> path = new ArrayList<>();
        City current = destination;

        while (!current.equals(start)) {
            Journey journey = cameFrom.get(current);
            path.add(journey);
            current = journey.from();
        }

        Collections.reverse(path);
        return path;
    }
}
