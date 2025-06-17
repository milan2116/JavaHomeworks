package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        this.missions = initializeMissionsFromReader(missionsReader);
        this.rockets = initializeRocketsFromReader(rocketsReader);
        this.secretKey = secretKey;
    }

    public RocketStatus getRocketStatusFromString(String str) {
        for (RocketStatus status : RocketStatus.class.getEnumConstants()) {
            if (status.toString().equals(str)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No RocketStatus found for input: " + str);
    }

    public MissionStatus getMissionStatusFromString(String str) {
        for (MissionStatus status : MissionStatus.class.getEnumConstants()) {
            if (status.toString().equals(str)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No MissionStatus found for input: " + str);
    }

    private List<Mission> initializeMissionsFromReader(Reader missionsReader) {
        List<Mission> missions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(missionsReader)) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length >= 7) {
                    String id = parts[0].trim();
                    String company = parts[1].trim();
                    String location = parts[2].trim();
                    String dateStr = parts[3].replaceAll("\"", "");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH);
                    LocalDate date = LocalDate.parse(dateStr, formatter);
                    String[] detailParts = parts[4].split("\\|");
                    String rocketName = detailParts[0].trim();
                    String payload = (detailParts.length > 1) ? detailParts[1].trim() : "";
                    RocketStatus rocketStatus =getRocketStatusFromString(parts[5].trim());
                    String cleanCost = parts[6].replaceAll("[\"\\s]", "");
                    Optional<Double> cost = (!cleanCost.isEmpty()) ? Optional.of(Double.parseDouble(cleanCost.trim())) : Optional.empty();
                    MissionStatus missionStatus = getMissionStatusFromString(parts[7].trim());
                    Detail detail = new Detail(rocketName, payload);
                    Mission mission = new Mission(id, company, location, date, detail, rocketStatus, cost, missionStatus);
                    missions.add(mission);
                }
            }
        } catch (IOException | DateTimeParseException | IllegalArgumentException e) {
            // Handle exceptions
            e.printStackTrace();
        }

        return missions;
    }


    // Helper method to parse cost from string to Double (null-safe)
    private Optional<Double> parseCost(String costString) {
        try {
            return Optional.of(Double.parseDouble(costString));
        } catch (NumberFormatException | NullPointerException e) {
            return Optional.empty();
        }
    }


    private Detail parseDetail(String detail) {
        String[] parts = detail.split("\\|");
        if (parts.length != 2) {
            return new Detail("", "");
        }

        return new Detail(parts[0], parts[1]);
    }

    private List<Rocket> initializeRocketsFromReader(Reader rocketsReader) {
        List<Rocket> rockets = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(rocketsReader)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rocketData = line.split(",", -1);

                try {
                    String id = rocketData[0];
                    String name = rocketData[1];
                    Optional<String> wiki = Optional.ofNullable(rocketData[2].isEmpty() ? null : rocketData[2]);
                    Optional<Double> height = rocketData[3].isEmpty()
                            ? Optional.empty()
                            : Optional.of(Double.parseDouble(rocketData[3].replaceAll("[^0-9.]", "")));

                    Rocket rocket = new Rocket(id, name, wiki, height);
                    rockets.add(rocket);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing rocket data: " + Arrays.toString(rocketData));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rockets;
    }


    @Override
    public Collection<Mission> getAllMissions() {
        return new ArrayList<>(missions);
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        return missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus)
                .collect(Collectors.toList());
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) throws TimeFrameMismatchException {
        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("Invalid time frame");
        }
        Map<String, Integer> successfulMissionsPerCompany = new HashMap<>();
        for (Mission mission : missions) {
            LocalDate missionDate = mission.date();
            if (!missionDate.isBefore(from) && !missionDate.isAfter(to)) {
                if (mission.missionStatus() == MissionStatus.SUCCESS) {
                    String company = mission.company();
                    successfulMissionsPerCompany.put(company, successfulMissionsPerCompany.getOrDefault(company, 0) + 1);
                }
            }
        }

        // Find the company with the most successful missions
        String companyWithMostSuccessfulMissions = "";
        int maxSuccessfulMissions = 0;
        for (Map.Entry<String, Integer> entry : successfulMissionsPerCompany.entrySet()) {
            if (entry.getValue() > maxSuccessfulMissions) {
                maxSuccessfulMissions = entry.getValue();
                companyWithMostSuccessfulMissions = entry.getKey();
            }
        }

        return companyWithMostSuccessfulMissions;
    }


    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        Map<String, Collection<Mission>> missionsPerCountry = new HashMap<>();

           for (Mission mission : missions) {
           String[]  lst = mission.location().split(" ");
           String country = lst[lst.length - 1];
            missionsPerCountry.computeIfAbsent(country, k -> new ArrayList<>()).add(mission);
        }

        return missionsPerCountry;
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("Invalid value for N");
        }

        return missions.stream()
                .filter(mission ->
                        mission.missionStatus() == missionStatus &&
                                mission.cost().isPresent()) // Considering cost is present
                .sorted(Comparator.comparing(mission -> mission.cost().orElse(Double.MAX_VALUE)))
                .limit(n)
                .collect(Collectors.toList());
    }
    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> mostDesiredLocationPerCompany = new HashMap<>();

        Map<String, Map<String, Integer>> companyLocationCount = new HashMap<>();

        for (Mission mission : missions) {
            String company = mission.company();
            String location = mission.location();

            companyLocationCount.putIfAbsent(company, new HashMap<>());
            Map<String, Integer> locationCount = companyLocationCount.get(company);
            locationCount.put(location, locationCount.getOrDefault(location, 0) + 1);
        }

        for (Map.Entry<String, Map<String, Integer>> entry : companyLocationCount.entrySet()) {
            String company = entry.getKey();
            Map<String, Integer> locationCount = entry.getValue();

            String mostDesiredLocation = Collections.max(locationCount.entrySet(), Map.Entry.comparingByValue()).getKey();
            mostDesiredLocationPerCompany.put(company, mostDesiredLocation);
        }

        return mostDesiredLocationPerCompany;
    }


    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to)
            throws TimeFrameMismatchException {
        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("Invalid time frame");
        }

        Map<String, Map<String, Integer>> successfulMissionsPerCompanyAndLocation = new HashMap<>();
        for (Mission mission : missions) {
            LocalDate missionDate = mission.date();
            if (!missionDate.isBefore(from) && !missionDate.isAfter(to)) {
                if (mission.missionStatus() == MissionStatus.SUCCESS) {
                    String company = mission.company();
                    String location = mission.location();

                    successfulMissionsPerCompanyAndLocation.putIfAbsent(company, new HashMap<>());
                    Map<String, Integer> locationCount = successfulMissionsPerCompanyAndLocation.get(company);
                    locationCount.put(location, locationCount.getOrDefault(location, 0) + 1);
                }
            }
        }

        // Find the location with the most successful missions for each company
        Map<String, String> locationWithMostSuccessfulMissionsPerCompany = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : successfulMissionsPerCompanyAndLocation.entrySet()) {
            String company = entry.getKey();
            Map<String, Integer> locationCount = entry.getValue();

            String locationWithMostSuccessfulMissions = Collections.max(locationCount.entrySet(), Map.Entry.comparingByValue()).getKey();
            locationWithMostSuccessfulMissionsPerCompany.put(company, locationWithMostSuccessfulMissions);
        }

        return locationWithMostSuccessfulMissionsPerCompany;
    }


    @Override
    public Collection<Rocket> getAllRockets() {
        return new ArrayList<>(rockets);
    }


    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Invalid value for N");
        }

        List<Rocket> sortedByHeight = rockets.stream()
                .filter(rocket -> rocket.height().isPresent()) // Considering height is present
                .sorted(Comparator.comparing(rocket -> rocket.height().orElse(Double.MIN_VALUE)))
                .limit(n)
                .collect(Collectors.toList());

        return new ArrayList<>(sortedByHeight);
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        Map<String, Optional<String>> rocketWikiPages = new HashMap<>();

        for (Rocket rocket : rockets) {
            rocketWikiPages.put(rocket.name(), rocket.wiki());
        }

        return rocketWikiPages;
    }


    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("Invalid value for N");
        }

        List<Mission> filteredMissions = missions.stream()
                .filter(mission ->
                        mission.missionStatus() == missionStatus &&
                                mission.cost().isPresent()) // Considering cost is present
                .sorted(Comparator.comparingDouble(mission ->
                        mission.cost().orElse(Double.MAX_VALUE))) // Sort based on cost
                .limit(n)
                .collect(Collectors.toList());

        Set<String> rocketNames = filteredMissions.stream()
                .map(mission -> mission.detail().rocketName())
                .collect(Collectors.toSet());

        List<String> wikiPages = rockets.stream()
                .filter(rocket -> rocketNames.contains(rocket.name()) && rocket.wiki().isPresent())
                .map(rocket -> rocket.wiki().get())
                .collect(Collectors.toList());

        return wikiPages;
    }




    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to)
            throws CipherException, TimeFrameMismatchException {
        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("Invalid time frame");
        }

        // Calculate reliability for each rocket
        Map<String, Integer> successfulMissionsCount = new HashMap<>();
        Map<String, Integer> unsuccessfulMissionsCount = new HashMap<>();
        Map<String, Integer> totalMissionsCount = new HashMap<>();

        for (Mission mission : missions) {
            LocalDate missionDate = mission.date();
            if (!missionDate.isBefore(from) && !missionDate.isAfter(to)) {
                String rocketName = mission.detail().rocketName();

                totalMissionsCount.put(rocketName, totalMissionsCount.getOrDefault(rocketName, 0) + 1);

                if (mission.missionStatus() == MissionStatus.SUCCESS) {
                    successfulMissionsCount.put(rocketName, successfulMissionsCount.getOrDefault(rocketName, 0) + 1);
                } else {
                    unsuccessfulMissionsCount.put(rocketName, unsuccessfulMissionsCount.getOrDefault(rocketName, 0) + 1);
                }
            }
        }

        Map<String, Double> rocketReliability = new HashMap<>();
        for (String rocket : totalMissionsCount.keySet()) {
            int successful = successfulMissionsCount.getOrDefault(rocket, 0);
            int unsuccessful = unsuccessfulMissionsCount.getOrDefault(rocket, 0);
            int total = totalMissionsCount.getOrDefault(rocket, 0);

            double reliability = total == 0 ? 0 : (2.0 * successful + unsuccessful) / (2.0 * total);
            rocketReliability.put(rocket, reliability);
        }

        // Find the most reliable rocket
        String mostReliableRocket = rocketReliability.entrySet().stream()

                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new CipherException("No rockets found"))
                .getKey();

        // Encrypt the most reliable rocket name
        Rijndael rijndael = new Rijndael(secretKey);
        rijndael.encrypt(new ByteArrayInputStream(mostReliableRocket.getBytes()), outputStream);
    }
}
