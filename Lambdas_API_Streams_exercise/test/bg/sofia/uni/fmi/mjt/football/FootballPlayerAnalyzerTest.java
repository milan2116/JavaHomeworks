package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FootballPlayerAnalyzerTest {

    private FootballPlayerAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        String testDataset = """
                name1;Full Name 1;4/3/1990;30;180.0;75.0;ST;Bulgarian;85;90;1000000;50000;Left
                name2;Full Name 2;5/6/1992;28;175.0;70.0;LM;Spanish;82;88;900000;45000;Right
                """;
        analyzer = new FootballPlayerAnalyzer(new StringReader("name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot\n" + testDataset));
    }

    @Test
    public void testGetAllPlayers() {
        List<Player> players = analyzer.getAllPlayers();
        assertEquals(2, players.size());
    }

    @Test
    public void testGetAllNationalities() {
        Set<String> nationalities = analyzer.getAllNationalities();
        assertEquals(2, nationalities.size());
        assertTrue(nationalities.contains("Bulgarian"));
        assertTrue(nationalities.contains("Spanish"));
    }

    @Test
    public void testGetHighestPaidPlayerByNationality() {
        Player highestPaidBulgarian = analyzer.getHighestPaidPlayerByNationality("Bulgarian");
        assertEquals("name1", highestPaidBulgarian.name());
        assertEquals("Full Name 1", highestPaidBulgarian.fullName());
    }

    @Test
    public void testGroupByPosition() {
        Map<Position, Set<Player>> groupedByPosition = analyzer.groupByPosition();
        assertEquals(2, groupedByPosition.size());
        assertTrue(groupedByPosition.containsKey(Position.ST));
        assertTrue(groupedByPosition.containsKey(Position.LM));
    }

    @Test
    public void testGetTopProspectPlayerForPositionInBudget() {
        Optional<Player> topProspect = analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 1000000);
        assertTrue(topProspect.isPresent());
        assertEquals("name1", topProspect.get().name());
    }

    @Test
    public void testGetSimilarPlayers() {

        //String testDataset = """
        //                name1;Full Name 1;4/3/1990;30;180.0;75.0;ST;Bulgarian;85;90;1000000;50000;Left
        //                name2;Full Name 2;5/6/1992;28;175.0;70.0;LM;Spanish;82;88;900000;45000;Right
        //                """;
        Player player = new Player(
                "testName",
                "Test Player",
                LocalDate.of(1995, 5, 15),
                28,
                170.0,
                65.0,
                List.of(Position.LM),
                "Test Nationality",
                80,
                85,
                800000,
                40000,
                Foot.RIGHT);

        Set<Player> similarPlayers = analyzer.getSimilarPlayers(player);
        assertEquals(1, similarPlayers.size());
        assertTrue(similarPlayers.stream().anyMatch(p -> p.name().equals("name2")));
    }

    @Test
    public void testGetPlayersByFullNameKeyword() {
        Set<Player> playersWithKeyword = analyzer.getPlayersByFullNameKeyword("Name 1");
        assertEquals(1, playersWithKeyword.size());
        assertTrue(playersWithKeyword.stream().anyMatch(p -> p.name().equals("name1")));
    }
}
