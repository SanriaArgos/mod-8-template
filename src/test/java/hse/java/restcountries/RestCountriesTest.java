package hse.java.restcountries;

import org.junit.jupiter.api.*;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.params.provider.*;
import static io.restassured.RestAssured.*;
import org.junit.jupiter.params.ParameterizedTest;

public class RestCountriesTest {
    private static final String ALL_FIELDS = "name,capital,population,cca2,region,languages,currencies,independent";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://restcountries.com/v3.1";
    }

    @Test
    @Tag("rest-countries-tests-1")
    void allList() {
        given().queryParam("fields", ALL_FIELDS)
                .when().get("/all")
                .then().statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @Tag("rest-countries-tests-2")
    void europeRegion() {
        given().queryParam("fields", "name,region")
                .when().get("/region/europe")
                .then().statusCode(200)
                .body("region", everyItem(equalTo("Europe")))
                .body("name.common", hasItems("Austria", "Serbia", "Hungary"));
    }

    @Test
    @Tag("rest-countries-tests-1")
    void badCountry() {
        when().get("/name/nonexistentcountryxyz")
                .then().statusCode(404)
                .body("status", equalTo(404))
                .body("message", not(emptyString()));
    }

    @ParameterizedTest
    @Tag("rest-countries-tests-2")
    @CsvSource({
            "russia, Moscow",
            "austria, Vienna",
            "serbia, Belgrade",
            "hungary, Budapest"
    })
    void nameCapital(String name, String capital) {
        given().queryParam("fields", "name,capital")
                .when().get("/name/" + name)
                .then().statusCode(200)
                .body("capital.flatten()", hasItem(capital));
    }

    @Test
    @Tag("rest-countries-tests-2")
    void fieldsOnly() {
        given().queryParam("fields", "name,cca2")
                .when().get("/alpha/at")
                .then().statusCode(200)
                .body("name.common", equalTo("Austria"))
                .body("cca2", equalTo("AT"))
                .body("capital", nullValue());
    }

    @Test
    @Tag("rest-countries-tests-1")
    void populationField() {
        given().queryParam("fields", "name,population")
                .when().get("/all")
                .then().statusCode(200)
                .body("population", everyItem(notNullValue()));
    }

    @Test
    @Tag("rest-countries-tests-2")
    void euroCurrency() {
        given().queryParam("fields", "name,currencies")
                .when().get("/currency/eur")
                .then().statusCode(200)
                .body("name.common", hasItem("Austria"));
    }

    @Test
    @Tag("rest-countries-tests-2")
    void alphaKp() {
        given().queryParam("fields", "name,capital,cca2")
                .when().get("/alpha/kp")
                .then().statusCode(200)
                .body("name.common", equalTo("North Korea"))
                .body("cca2", equalTo("KP"))
                .body("capital", hasItem("Pyongyang"));
    }
}