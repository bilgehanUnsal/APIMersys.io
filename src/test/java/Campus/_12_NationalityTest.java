package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.*;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;
import java.util.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


public class _12_NationalityTest {

    Faker randomUreteci = new Faker();
    RequestSpecification reqSpec;
    String nationalitiesID = "";
    String rndNationalitiesName = "";
    Nationalities newNationalities = new Nationalities();
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

    }

    @Test
    public void createNationalities() {

        rndNationalitiesName = randomUreteci.address().country() + randomUreteci.address().countryCode();
        newNationalities.name = rndNationalitiesName;
        newNationalities.translateName = new Object[1];

        nationalitiesID =
                given()
                        .spec(reqSpec)
                        .body(newNationalities)
                        .when()
                        .post("school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
        ;
    }

    @Test(dependsOnMethods = "createNationalities")
    public void createNationalitiesNegative() {

        newNationalities.name = rndNationalitiesName;
        newNationalities.translateName = new Object[1];


        given()
                .spec(reqSpec)
                .body(newNationalities)
                .when()
                .post("school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createNationalitiesNegative")
    public void updateNationalities(){

        String newCountryName=""+rndNationalitiesName+randomUreteci.number().digits(5);

        rndNationalitiesName = randomUreteci.address().country() + randomUreteci.address().countryCode()+newCountryName;
        newNationalities.name = rndNationalitiesName;
        newNationalities.id=nationalitiesID;
        newNationalities.translateName = new Object[1];

        nationalitiesID =
                given()
                        .spec(reqSpec)
                        .body(newNationalities)

                        .when()
                        .put("school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .body("name", containsString(newCountryName))
                        .extract().path("id")
        ;

    }
    @Test(dependsOnMethods = "updateNationalities")
    public void searchNationalities() {


        given()
                .spec(reqSpec)
                .body(newNationalities)
                .when()
                .post("school-service/api/nationality/search")

                .then()
                .log().body()
                .statusCode(200)

        ;
    }
    @Test(dependsOnMethods = "searchNationalities")
    public void deleteNationalities() {
        given()
                .spec(reqSpec)
                .when()
                .delete("school-service/api/nationality/"+nationalitiesID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteNationalities")
    public void deleteNationalitiesNegative()
    {
        given()
                .spec(reqSpec)
                .when()
                .delete("school-service/api/nationality/"+nationalitiesID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Nationality not  found"))
        ;
    }
}

