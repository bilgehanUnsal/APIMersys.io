package Campus;

import com.github.javafaker.Faker;
import io.restassured.specification.RequestSpecification;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class _07_SchoolLocations {
    Faker randomMaker = new Faker();
    RequestSpecification reqSpec;
    String locationsID = "";

    String rndLocationsName = "";
    String rndLocationsCode = "";
    String rndCapacity = "";

    @BeforeClass
    public void Setup() {
        baseURI = "https://test.mersys.io/";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .body(userCredential)
                        .contentType(ContentType.JSON)
                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();
        ;

        reqSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void createLocations() {
        rndLocationsName = randomMaker.address().country();
        rndLocationsCode = randomMaker.address().countryCode();
        rndCapacity = randomMaker.numerify("123");

        Map<String, String> newLocation = new HashMap<>();
        newLocation.put("name", rndLocationsName);
        newLocation.put("shortName", rndLocationsCode);
        newLocation.put("active", "true");
        newLocation.put("capacity", "156");
        newLocation.put("type", "CLASS");
        newLocation.put("school", "646cbb07acf2ee0d37c6d984");

        locationsID =
                given()
                        .spec(reqSpec)
                        .body(newLocation)
                        .when()
                        .post("school-service/api/location")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
        ;
    }

    @Test(dependsOnMethods = "createLocations")
    public void createLocationNegative() {
        Map<String, String> newLocation = new HashMap<>();
        newLocation.put("name", rndLocationsName);
        newLocation.put("shortname", rndLocationsCode);
        newLocation.put("active", "true");
        newLocation.put("capacity", "222");
        newLocation.put("type", "CLASS");
        newLocation.put("school", "646cbb07acf2ee0d37c6d984");

        given()
                .spec(reqSpec)
                .body(newLocation)

                .when()
                .post("school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createLocationNegative")
    public void updateLocation() {
        String newLocation = randomMaker.address().country();
        String newCode = randomMaker.address().countryCode();
        Map<String, String> updLocation = new HashMap<>();
        updLocation.put("id", locationsID);
        updLocation.put("name", newLocation);
        updLocation.put("shortname", newCode);
        updLocation.put("active", "true");
        updLocation.put("capacity", "210");
        updLocation.put("type", "CLASS");
        updLocation.put("school", "646cbb07acf2ee0d37c6d984");

        given()
                .spec(reqSpec)
                .body(updLocation)

                .when()
                .put("school-service/api/location")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(newLocation))
        ;
    }

    @Test(dependsOnMethods = "updateLocation")
    public void deleteLocation(){
        given()
                .spec(reqSpec)
                .when()
                .delete("school-service/api/location/" + locationsID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }
}
