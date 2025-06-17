package bg.sofia.uni.fmi.mjt.itinerary;

import static bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType.BUS;
import static bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType.PLANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.SequencedCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class RideRightTest {
    private City sofia, plovdiv, varna, burgas, ruse, blagoevgrad, kardzhali, tarnovo;
    private List<Journey> schedule;


    @BeforeAll
    private void init() {
        this.sofia = new City("Sofia", new Location(0, 2000));
        this.plovdiv = new City("Plovdiv", new Location(4000, 1000));
        this.varna = new City("Varna", new Location(9000, 3000));
        this.burgas = new City("Burgas", new Location(9000, 1000));
        this.ruse = new City("Ruse", new Location(7000, 4000));
        this.blagoevgrad = new City("Blagoevgrad", new Location(0, 1000));
        this.kardzhali = new City("Kardzhali", new Location(3000, 0));
        this.tarnovo = new City("Tarnovo", new Location(5000, 3000));

        this.schedule = List.of(
                new Journey(BUS, sofia, blagoevgrad, new BigDecimal("20")),
                new Journey(BUS, blagoevgrad, sofia, new BigDecimal("20")),
                new Journey(BUS, sofia, plovdiv, new BigDecimal("90")),
                new Journey(BUS, plovdiv, sofia, new BigDecimal("90")),
                new Journey(BUS, plovdiv, kardzhali, new BigDecimal("50")),
                new Journey(BUS, kardzhali, plovdiv, new BigDecimal("50")),
                new Journey(BUS, plovdiv, burgas, new BigDecimal("90")),
                new Journey(BUS, burgas, plovdiv, new BigDecimal("90")),
                new Journey(BUS, burgas, varna, new BigDecimal("60")),
                new Journey(BUS, varna, burgas, new BigDecimal("60")),
                new Journey(BUS, sofia, tarnovo, new BigDecimal("150")),
                new Journey(BUS, tarnovo, sofia, new BigDecimal("150")),
                new Journey(BUS, plovdiv, tarnovo, new BigDecimal("40")),
                new Journey(BUS, tarnovo, plovdiv, new BigDecimal("40")),
                new Journey(BUS, tarnovo, ruse, new BigDecimal("70")),
                new Journey(BUS, ruse, tarnovo, new BigDecimal("70")),
                new Journey(BUS, varna, ruse, new BigDecimal("70")),
                new Journey(BUS, ruse, varna, new BigDecimal("70")),
                new Journey(PLANE, varna, burgas, new BigDecimal("200")),
                new Journey(PLANE, burgas, varna, new BigDecimal("200")),
                new Journey(PLANE, burgas, sofia, new BigDecimal("150")),
                new Journey(PLANE, sofia, burgas, new BigDecimal("250")),
                new Journey(PLANE, varna, sofia, new BigDecimal("290")),
                new Journey(PLANE, sofia, varna, new BigDecimal("300"))
        );

        RideRight rideRight = new RideRight(schedule);
    }

    private static void assertCollectionEquals(SequencedCollection<?> expected, SequencedCollection<?> actual) {
        assertEquals(expected.size(), actual.size(),
                "Expected collection cityIndex have " + expected.size() + " elements.");

        Iterator<?> expectedIterator = expected.iterator();
        Iterator<?> actualIterator = actual.iterator();

        Object expectedElement, actualElement;
        int index = 0;

        while (expectedIterator.hasNext()) {
            expectedElement = expectedIterator.next();
            actualElement = actualIterator.next();
            assertEquals(expectedElement, actualElement,
                    "Path differs at index. {index=" + index + "}");
            ++index;
        }
    }

    @Test
    public void testFindCheapestPathGivenVarnaKardzhaliWithTransfers() throws CityNotKnownException, NoPathToDestinationException {
        // Setup Data
        RideRight rideRight = new RideRight(schedule);
        SequencedCollection<Journey> expectedPath = List.of(
                new Journey(BUS, varna, burgas, new BigDecimal("60")),
                new Journey(BUS, burgas, plovdiv, new BigDecimal("90")),
                new Journey(BUS, plovdiv, kardzhali, new BigDecimal("50"))
        );

        // Exercise
        SequencedCollection<Journey> actualPath = rideRight.findCheapestPath(varna, kardzhali, true);

        // Verify
        assertCollectionEquals(expectedPath, actualPath);
    }

    @Test
    public void testFindCheapestPathGivenVarnaKardzhaliWithoutTransfers() {
        // Setup Data
        RideRight rideRight = new RideRight(schedule);
        SequencedCollection<Journey> expectedPath = List.of(
                new Journey(BUS, varna, burgas, new BigDecimal("60")),
                new Journey(BUS, burgas, plovdiv, new BigDecimal("90")),
                new Journey(BUS, plovdiv, kardzhali, new BigDecimal("50"))
        );

        // Exercise
        assertThrows(NoPathToDestinationException.class, () -> {
                    rideRight.findCheapestPath(varna, kardzhali, false);
                },
                "Expected findCheapestPath() to throw NoPathFoundException when no path is found.");
    }

    @Test
    public void testFindCheapestPathGivenVarnaBurgasWithoutTransfers() throws CityNotKnownException, NoPathToDestinationException {
        // Setup Data
        RideRight rideRight = new RideRight(schedule);
        SequencedCollection<Journey> expectedPath = List.of(
                new Journey(BUS, varna, burgas, new BigDecimal("60"))
        );

        // Exercise
        SequencedCollection<Journey> actualPath = rideRight.findCheapestPath(varna, burgas, false);

        // Verify
        assertCollectionEquals(expectedPath, actualPath);
    }

    @Test
    public void testFindCheapestPathGivenNonExistentStart() {
        // Setup Data
        RideRight rideRight = new RideRight(schedule);
        final City nonExistentCity = new City("NonExistentCity", new Location(0, 0));

        // Exercise
        assertThrows(CityNotKnownException.class, () -> {
                    rideRight.findCheapestPath(nonExistentCity, kardzhali, false);
                },
                "Expected findCheapestPath() to throw CityNotKnownException when start city is not present.");
    }

    @Test
    public void testFindCheapestPathGivenNonExistentDestination() {
        // Setup Data
        RideRight rideRight = new RideRight(schedule);
        final City nonExistentCity = new City("NonExistentCity", new Location(0, 0));

        // Exercise
        assertThrows(CityNotKnownException.class, () -> {
                    rideRight.findCheapestPath(kardzhali, nonExistentCity, false);
                },
                "Expected findCheapestPath() to throw CityNotKnownException when destination city is not present.");
    }

}
