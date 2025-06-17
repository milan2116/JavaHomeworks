package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.util.SequencedCollection;

public interface ItineraryPlanner {
    SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException;
}