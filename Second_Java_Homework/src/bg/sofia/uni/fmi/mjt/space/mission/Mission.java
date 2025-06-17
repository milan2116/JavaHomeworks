package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {

    public Mission {
        if (id == null || company == null || location == null || date == null || detail == null ||
                rocketStatus == null || missionStatus == null) {
            throw new IllegalArgumentException("Mission fields cannot be null");
        }
    }

    public static Mission createMission(String id, String company, String location, LocalDate date,
                                        String detailString, RocketStatus rocketStatus, Optional<Double> cost,
                                        MissionStatus missionStatus) {
        Detail detail = Detail.parseDetail(detailString);
        return new Mission(id, company, location, date, detail, rocketStatus, cost, missionStatus);
    }
}
