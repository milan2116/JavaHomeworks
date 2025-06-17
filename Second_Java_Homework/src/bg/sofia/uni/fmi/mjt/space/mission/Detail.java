package bg.sofia.uni.fmi.mjt.space.mission;

public record Detail(String rocketName, String payload) {
    public Detail {
        if (rocketName == null || payload == null) {
            throw new IllegalArgumentException("Rocket name and payload cannot be null");
        }
    }

    public static Detail parseDetail(String detailString) {
        String[] parts = detailString.split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid detail format");
        }
        return new Detail(parts[0], parts[1]);
    }
}
