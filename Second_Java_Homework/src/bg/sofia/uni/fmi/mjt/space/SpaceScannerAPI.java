package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SpaceScannerAPI {
    Collection<Mission> getAllMissions();

    Collection<Mission> getAllMissions(MissionStatus missionStatus);

    String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) throws TimeFrameMismatchException;

    Map<String, Collection<Mission>> getMissionsPerCountry();

    List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus);

    Map<String, String> getMostDesiredLocationForMissionsPerCompany();

    Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) throws TimeFrameMismatchException;

    Collection<Rocket> getAllRockets();

    List<Rocket> getTopNTallestRockets(int n);

    Map<String, Optional<String>> getWikiPageForRocket();

    List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                   RocketStatus rocketStatus);
    void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException, CipherException, TimeFrameMismatchException;
}