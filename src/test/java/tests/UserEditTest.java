package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.DataGenerator.generateDatafromCreatedUser;

public class UserEditTest extends BaseTestCase {
    @Test
    public void editUserWithoutAuth(){


        String userId = "101910";
        String newName = "newName";
        Map<String,String> name = new HashMap<>();
        name.put("firstName",newName);

        //EditUserWithoutAuth

        Map<String,String> newData = generateDatafromCreatedUser(name);
        Response editResponse = RestAssured
                .given()
                .body(newData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();


        Assertions.assertResponseCode(editResponse,400);
        Assertions.assertJsonByName(editResponse,"error","Auth token not supplied");
    }
    @Test
    public void editUserWithAuthOtherUser(){
        //GENERATE USER
        Map<String,String> data = generateDatafromCreatedUser();
        Response responseCreatedUser = RestAssured
                .given()
                .body(data)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();
        Assertions.assertResponseCode(responseCreatedUser,200);

        //Login User
        Map<String,String> authData = new HashMap<>();
        authData.put("email", data.get("email"));
        authData.put("password", data.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //Edit User With Auth Other User
        String userId = "101910";
        String newName = "newName";
        Map<String,String> name = new HashMap<>();
        name.put("firstName",newName);

        Response editResponse = RestAssured
                .given()
                .cookie("auth_sid", getCookie(responseGetAuth,"auth_sid"))
                .header("x-csrf-token",getHeader(responseGetAuth,"x-csrf-token"))
                .body(name)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
        Assertions.assertJsonByName(editResponse,"error","This user can only edit their own data.");

    }
    @Test
    public void editUserErrorEmail(){
        //GENERATE USER
        Map<String,String> data = generateDatafromCreatedUser();
        JsonPath responseCreatedUser = RestAssured
                .given()
                .body(data)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId = responseCreatedUser.getString("id");

        //Login User
        Map<String,String> authData = new HashMap<>();
        authData.put("email", data.get("email"));
        authData.put("password", data.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //Edit User With Auth Other User
        String newEmail = "errorexapmle.com";
        Map<String,String> error = new HashMap<>();
        error.put("email",newEmail);

        Response editResponse = RestAssured
                .given()
                .cookie("auth_sid", getCookie(responseGetAuth,"auth_sid"))
                .header("x-csrf-token",getHeader(responseGetAuth,"x-csrf-token"))
                .body(error)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
        Assertions.assertJsonByName(editResponse,"error","Invalid email format");

    }
    @Test
    public void editUserWithCutName(){
        //GENERATE USER
        Map<String,String> data = generateDatafromCreatedUser();
        JsonPath responseCreatedUser = RestAssured
                .given()
                .body(data)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId = responseCreatedUser.getString("id");

        //Login User
        Map<String,String> authData = new HashMap<>();
        authData.put("email", data.get("email"));
        authData.put("password", data.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //Edit User With Auth Other User
        String newName = "T";
        Map<String,String> cutName = new HashMap<>();
        cutName.put("firstName",newName);

        Response editResponse = RestAssured
                .given()
                .cookie("auth_sid", getCookie(responseGetAuth,"auth_sid"))
                .header("x-csrf-token",getHeader(responseGetAuth,"x-csrf-token"))
                .body(cutName)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
        Assertions.assertJsonByName(editResponse,"error","The value for field `firstName` is too short");

    }
}
