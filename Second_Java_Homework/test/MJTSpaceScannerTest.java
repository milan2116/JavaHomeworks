import bg.sofia.uni.fmi.mjt.space.MJTSpaceScanner;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MJTSpaceScannerTest {

    private static final String missionsData = """
            Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
            0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
            1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
            2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
            3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
            4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
            5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
            6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Jul 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
            7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
            8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
            9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Success
            """;

    private static final String rocketsData = """
            "",Name,Wiki,Rocket Height
            0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
            1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
            2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
            3,Unha-3,https://en.wikipedia.org/wiki/Unha,32.0 m
            4,Vanguard,https://en.wikipedia.org/wiki/Vanguard_(rocket),23.0 m
            5,Vector-H,https://en.wikipedia.org/wiki/Vector-H,18.3 m
            6,Vector-R,https://en.wikipedia.org/wiki/Vector-R,13.0 m
            7,Vega,https://en.wikipedia.org/wiki/Vega_(rocket),29.9 m
            8,Vega C,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
            9,Vega E,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m""";

    private MJTSpaceScanner spaceScanner;

    SecretKey yourSecretKey = new SecretKeySpec(
            new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 121, 122, 123, 124},
            "AES"
    );

    @BeforeEach
    void setUp() {
        Reader missionsReader = new StringReader(missionsData);
        Reader rocketsReader = new StringReader(rocketsData);

        spaceScanner = new MJTSpaceScanner(missionsReader, rocketsReader, yourSecretKey);
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() throws TimeFrameMismatchException {
        String companyWithMostSuccessfulMissions = spaceScanner.getCompanyWithMostSuccessfulMissions(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2021, 1, 1)
        );
        assertEquals("CASC", companyWithMostSuccessfulMissions);
    }

    @Test
    void testGetAllRockets() {
        Collection<Rocket> allRockets = spaceScanner.getAllRockets();
        assertNotNull(allRockets);
        assertEquals(10, allRockets.size());
    }

    @Test
    void testGetTopNTallestRockets() {
        List<Rocket> topTallestRockets = spaceScanner.getTopNTallestRockets(3);
        assertNotNull(topTallestRockets);
        assertEquals(3, topTallestRockets.size());
    }

    @Test
    void testGetAllMissions() {
        Collection<Mission> allMissions = spaceScanner.getAllMissions();
        assertNotNull(allMissions);
        assertEquals(10, allMissions.size());
    }

    @Test
    void testGetAllMissionsWithStatus() {
        Collection<Mission> missionsWithStatus = spaceScanner.getAllMissions(MissionStatus.SUCCESS);
        assertNotNull(missionsWithStatus);
        assertEquals(10, missionsWithStatus.size());
    }

    @Test
    void testGetMissionsPerCountry() {
        Map<String, Collection<Mission>> missionsPerCountry = spaceScanner.getMissionsPerCountry();
        assertNotNull(missionsPerCountry);
        assertEquals(4, missionsPerCountry.size());
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        List<Mission> topLeastExpensiveMissions = spaceScanner.getTopNLeastExpensiveMissions(3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
        assertNotNull(topLeastExpensiveMissions);
        assertEquals(3, topLeastExpensiveMissions.size());
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> mostDesiredLocationPerCompany = spaceScanner.getMostDesiredLocationForMissionsPerCompany();
        assertNotNull(mostDesiredLocationPerCompany);
        assertEquals(5, mostDesiredLocationPerCompany.size());
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompany() throws TimeFrameMismatchException {
        Map<String, String> locationWithMostSuccessfulMissions = spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2021, 1, 1)
        );
        assertNotNull(locationWithMostSuccessfulMissions);
        assertEquals(5, locationWithMostSuccessfulMissions.size());
    }


    @Test
    void testGetWikiPageForRocket() {
        Map<String, Optional<String>> rocketWikiPages = spaceScanner.getWikiPageForRocket();
        assertNotNull(rocketWikiPages);
        assertEquals(10, rocketWikiPages.size());
    }

}