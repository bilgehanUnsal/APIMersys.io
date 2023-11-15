package Campus;
import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
public class _06_PositionsBusra {


    Faker randomUreteci = new Faker();
    RequestSpecification reqSpec;
    String positionID = "";

    String rndPositionName = "";
    String rndPositionShortName = "";

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
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();
        ;

        reqSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();

        System.out.println("Login Test: Successfully passed !");

    }

    @Test
    public void createPositions() {

        rndPositionName = randomUreteci.address().firstName();
        rndPositionShortName = randomUreteci.name().title();

        Map<String, Object> newPosition = new HashMap<>();
        newPosition.put("name", rndPositionName);
        newPosition.put("shortName", rndPositionShortName);
        newPosition.put("tenantId", "65524cc4fcdb8d23bf5f478d");
        newPosition.put("active", true);

        positionID =
                given()
                        .spec(reqSpec)
                        .body(newPosition)
                        //.log().all()
                        .when()
                        .post("school-service/api/employee-position")

                        .then()
                        .contentType(ContentType.JSON)
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

    }

    @Test(dependsOnMethods = "createPositions")
    public void createPositionsNegative() {

        Map<String, Object> newPosition = new HashMap<>();
        newPosition.put("id", positionID);
        newPosition.put("name", rndPositionName);
        newPosition.put("shortName", rndPositionShortName);
        newPosition.put("tenantId", "65524cc4fcdb8d23bf5f478d");
        newPosition.put("active", true);


        given()
                .spec(reqSpec)
                .body(newPosition)
                //.log().all()
                .when()
                .post("school-service/api/employee-position")

                .then()
                .contentType(ContentType.JSON)
                .log().body()
                .statusCode(400)
                .extract().path("id");

    }

    @Test(dependsOnMethods = "createPositionsNegative")
    public void updatePosition() {

        String newPositionName = "Updated Position" + randomUreteci.number().digits(5);

        Map<String, Object> updatePosition = new HashMap<>();
        updatePosition.put("id", positionID);
        updatePosition.put("name", newPositionName);
        updatePosition.put("shortName", rndPositionShortName);
        updatePosition.put("tenantId", "65524cc4fcdb8d23bf5f478d");
        updatePosition.put("active", true);


        given()
                .spec(reqSpec)
                .body(updatePosition)
                .when()
                .put("school-service/api/employee-position")

                .then()
                .log().body()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("name", equalTo(newPositionName));
    }

    @Test(dependsOnMethods = "updatePosition")
    public void deletePosition() {

        given()
                .spec(reqSpec)
                .when()
                .delete("school-service/api/employee-position/"+positionID)

                .then()
                .log().body()
                .statusCode(204);

        System.out.println("Delete Position Test: Successfully passed !");


    }

    @Test(dependsOnMethods = "deletePosition")
    public void deletePositionNegative() {


        given()
                .spec(reqSpec)
                .when()
                .delete("school-service/api/employee-position/"+positionID)

                .then()
                .log().body()
                .statusCode(204);

        System.out.println("Delete Position Negative Test: Successfully passed !");

    }


}
