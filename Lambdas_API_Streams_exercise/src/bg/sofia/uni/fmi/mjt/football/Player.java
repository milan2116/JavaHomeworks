package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Player(
        String name,
        String fullName,
        LocalDate birthDate,
        int age,
        double heightCm,
        double weightKg,
        List<Position> positions,
        String nationality,
        int overallRating,
        int potential,
        long valueEuro,
        long wageEuro,
        Foot preferredFoot
) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public static Player of(String line) {
        String[] data = line.split(";");
        String name = data[0];
        String fullName = data[1];
        LocalDate birthDate = LocalDate.parse(data[2], DATE_FORMATTER);
        int age = Integer.parseInt(data[3]);
        double heightCm = Double.parseDouble(data[4]);
        double weightKg = Double.parseDouble(data[5]);
        List<Position> positions = Arrays.stream(data[6].split(","))
                .map(Position::valueOf)
                .collect(Collectors.toList());
        String nationality = data[7];
        int overallRating = Integer.parseInt(data[8]);
        int potential = Integer.parseInt(data[9]);
        long valueEuro = Long.parseLong(data[10]);
        long wageEuro = Long.parseLong(data[11]);
        Foot preferredFoot = Foot.valueOf(data[12].toUpperCase());

        return new Player(name, fullName, birthDate, age, heightCm, weightKg, positions,
                nationality, overallRating, potential, valueEuro, wageEuro, preferredFoot);
    }
}
