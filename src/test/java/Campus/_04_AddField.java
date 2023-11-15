package Campus;
import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
public class _04_AddField {
    RequestSpecification reqSpec;
    Faker random = new Faker();
    String rnName = "";
    String rnCode = "";
    String fieldID = "";

    @BeforeClass
    public void Setup() {

        baseURI = "https://test.mersys.io/";
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "turkeyts");
        loginData.put("password", "TechnoStudy123");
        loginData.put("rememberMe", "ture");
        Cookies cookies =
                given()
                        .body(loginData)
                        .contentType(ContentType.JSON)
                        .when()
                        .post("auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().detailedCookies();
        reqSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();
    }
    @Test
    public void createField() {
        rnName = random.name().firstName() + "131";
        rnCode = random.code().asin() + "132";
        List<Object> translateNameList = new ArrayList<>();
        List<Object> childrenList = new ArrayList<>();
        Map<String, Object> newField = new HashMap<>();
        newField.put("name", rnName);
        newField.put("code", rnCode);
        newField.put("id", null);
        newField.put("translateName", translateNameList);
        newField.put("type", "STRING");
        newField.put("children", childrenList);
        newField.put("systemField", String.valueOf(false));
        newField.put("systemFieldName", null);
        newField.put("schoolId","646cbb07acf2ee0d37c6d984");
        fieldID =
                given()
                        .spec(reqSpec)
                        .body(newField)
                        .when()
                        .post("school-service/api/entity-field")
                        .then()
                        .statusCode(201)
                        .extract().path("id");
    }
    @Test(dependsOnMethods = "createField")
    public void cereatFieldNigative(){
        List<Object> translateNameList = new ArrayList<>();
        List<Object> childrenList = new ArrayList<>();
        Map<String,Object> nigativeField = new HashMap<>();
        nigativeField.put("name", rnName);
        nigativeField.put("code", rnCode);
        nigativeField.put("id", null);
        nigativeField.put("translateName", translateNameList);
        nigativeField.put("type", "STRING");
        nigativeField.put("children", childrenList);
        nigativeField.put("systemField", String.valueOf(false));
        nigativeField.put("systemFieldName", null);
        nigativeField.put("schoolId","646cbb07acf2ee0d37c6d984");

        given()

                .spec(reqSpec)
                .body(nigativeField)
                .when()
                .post("school-service/api/entity-field")
                .then()
                .statusCode(400)
        ;
    }
    @Test(dependsOnMethods = "cereatFieldNigative")
    public void editField(){

        List<Object> translateNameList = new ArrayList<>();
        List<Object> childrenList = new ArrayList<>();
        Map<String, Object> newField = new HashMap<>();
        newField.put("name", "technov3");
        newField.put("code", "2023_df134");
        newField.put("id", fieldID);
        newField.put("translateName", translateNameList);
        newField.put("type", "STRING");
        newField.put("children", childrenList);
        newField.put("systemField", String.valueOf(false));
        newField.put("systemFieldName", null);
        newField.put("schoolId","646cbb07acf2ee0d37c6d984");
        given()
                .spec(reqSpec)
                .body(newField)
                .when()
                .put("school-service/api/entity-field")
                .then()
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "editField")
    public void deletField(){
        given()
                .spec(reqSpec)
                .when()
                .delete("school-service/api/entity-field/"+fieldID)
                .then()
                .statusCode(204)
        ;
    }

}
