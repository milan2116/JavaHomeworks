package bg.sofia.uni.fmi.mjt.football;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FootballPlayerAnalyzer {

    private final List<Player> players;

    public FootballPlayerAnalyzer(Reader reader) {
        this.players = loadPlayersFromReader(reader);
    }

    private List<Player> loadPlayersFromReader(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                    .skip(1) // Skip the header line
                    .map(Player::of)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading data", e);
        }
    }

    public List<Player> getAllPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Set<String> getAllNationalities() {
        return players.stream()
                .map(Player::nationality)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Player getHighestPaidPlayerByNationality(String nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("Nationality cannot be null");
        }

        return players.stream()
                .filter(player -> player.nationality().equalsIgnoreCase(nationality))
                .max(Comparator.comparing(Player::wageEuro))
                .orElseThrow(() -> new NoSuchElementException("No player found with the provided nationality"));
    }

    public Map<Position, Set<Player>> groupByPosition() {
        Map<Position, Set<Player>> groupedByPosition = new EnumMap<>(Position.class);
        players.forEach(player -> player.positions().forEach(position ->
                groupedByPosition.computeIfAbsent(position, k -> new HashSet<>()).add(player)));
        return Collections.unmodifiableMap(groupedByPosition);
    }

    public Optional<Player> getTopProspectPlayerForPositionInBudget(Position position, long budget) {
        if (position == null || budget < 0) {
            throw new IllegalArgumentException("Position cannot be null and budget cannot be negative");
        }

        return players.stream()
                .filter(player -> player.positions().contains(position))
                .filter(player -> player.valueEuro() <= budget)
                .max(Comparator.comparing(player ->
                        ((double) (player.overallRating() + player.potential()) / player.age())));
    }

    public Set<Player> getSimilarPlayers(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        return players.stream()
                .filter(p -> p != null && p != player &&
                        p.positions().stream().anyMatch(player.positions()::contains) &&
                        p.preferredFoot() == player.preferredFoot() &&
                        Math.abs(p.overallRating() - player.overallRating()) <= 3)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Player> getPlayersByFullNameKeyword(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Keyword cannot be null");
        }

        return players.stream()
                .filter(player -> player.fullName().contains(keyword))
                .collect(Collectors.toUnmodifiableSet());
    }
}
