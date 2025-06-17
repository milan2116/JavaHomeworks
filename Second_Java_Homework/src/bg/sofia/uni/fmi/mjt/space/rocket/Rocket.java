package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {

    public Rocket {
        if (id == null || name == null || wiki.isEmpty() || height.isEmpty()) {
            throw new IllegalArgumentException("Rocket fields cannot be null");
        }
    }

    public static Rocket createRocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
        return new Rocket(id, name, wiki, height);
    }
}
