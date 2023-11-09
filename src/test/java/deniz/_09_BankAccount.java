package deniz;

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


public class _09_BankAccount {
    String name="";
    String iban="";
    String integrationCode="";
    String currency="";
    Faker randomUreteci=new Faker();
    String bankID="";
    RequestSpecification reqSpec;

    @BeforeClass
    public void Setup(){
        baseURI ="https://test.mersys.io/";

        Map<String, String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
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
    public void createBankAccount() {

        name = randomUreteci.name().firstName();
        iban = randomUreteci.finance().iban();
        integrationCode = randomUreteci.country().currencyCode();
        currency = "EUR";
        Map<String, String> newAccount = new HashMap<>();
        newAccount.put("name", name);
        newAccount.put("iban", iban);
        newAccount.put("integrationCode", integrationCode);
        newAccount.put("currency", currency);
        newAccount.put("schoolId", "646cbb07acf2ee0d37c6d984");
        newAccount.put("active", "true");

        bankID =
                given()
                        .spec(reqSpec)
                        .body(newAccount)
                        .when()
                        .post("school-service/api/bank-accounts/"+this.bankID)
                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");;

        System.out.println("Bank ID: " + bankID);
    }


    @Test(dependsOnMethods = "createBankAccount")
    public void UpdateBankAccount() {
        String newName = "Updated Name " + randomUreteci.name().firstName();
        String newIban = randomUreteci.finance().iban();
        String newIntegrationCode = randomUreteci.country().currencyCode();
        String newCurrency = "USD";
        Map<String, String> updatedBank = new HashMap<>();
        updatedBank.put("id",bankID);
        updatedBank.put("name", newName);
        updatedBank.put("iban", newIban);
        updatedBank.put("integrationCode", newIntegrationCode);
        updatedBank.put("schoolId", "646cbb07acf2ee0d37c6d984");
        updatedBank.put("currency", newCurrency);
        updatedBank.put("deleted","false");
        updatedBank.put("active", "false");

        given()
                .spec(reqSpec)
                .body(updatedBank)
                .when().log().all()
                .put("school-service/api/bank-accounts/")
                .then()
                .log().body()
                .statusCode(200)
        ;

    }

    @Test(dependsOnMethods = "UpdateBankAccount")
    public void deleteBankAccount()
    {
        given()
                .spec(reqSpec)
                .when()
                .delete("/school-service/api/bank-accounts/"+bankID)
                .then()
                .log().body()
                .statusCode(200);


    }
}
