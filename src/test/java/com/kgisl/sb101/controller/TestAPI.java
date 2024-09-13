package com.kgisl.sb101.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.kgisl.sb101.entity.Person;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.*;
 
import java.util.HashMap;
import java.util.Map;
 
import static org.junit.jupiter.api.Assertions.*;
 
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAPI {
 
  private Playwright playwright;
  private APIRequestContext request;
  // private static final String BaseURL = "http://localhost:10000/person";
 
  @BeforeAll
  void beforeAll() {
    createPlaywright();
    createAPIRequestContext();
  }
 
  void createPlaywright() {
    playwright = Playwright.create();
  }
 
  void createAPIRequestContext() {
    Map<String, String> headers = new HashMap<>();
    // We set this header per your developer guidelines.
    // headers.put("Accept", "application/json");
    // Add authorization token to all requests.
    // Assuming personal access token available in the environment.
    // headers.put("Authorization", "token " + API_TOKEN);
 
    request = playwright.request().newContext(new APIRequest.NewContextOptions()
        // All requests we send go to this API endpoint.
        .setBaseURL("http://localhost:9090")
        .setExtraHTTPHeaders(headers));
  }
 
  void disposeAPIRequestContext() {
    if (request != null) {
      request.dispose();
      request = null;
    }
  }
 
  void closePlaywright() {
    if (playwright != null) {
      playwright.close();
      playwright = null;
    }
  }
 
  @AfterAll
  void afterAll() {
    disposeAPIRequestContext();
    closePlaywright();
  }
 
  @Test
  void shouldCreatePerson() {
    Map<String, String> data = new HashMap<>();
    data.put("uname", "soji1");
    data.put("email", "sojiemail1@email.com");
 
    APIResponse response = request.post("/person/post", RequestOptions.create().setData(data));
 
    Person person = new Gson().fromJson(response.text(), Person.class);
 
    assertEquals(201, response.status());
    // assertEquals("OK", response.statusText());
   
    assertEquals(person.uname(), "soji1");
    assertEquals(person.email(), "sojiemail1@email.com");
  }
 
  @Test
  void shouldGetAllPersons() {
    // SELECT count(*) FROM PERSON
    // http://localhost:10000/person
    // https://jsonpathfinder.com/
 
    APIResponse response = request.get("/person/get");
 
    JsonArray json = new Gson().fromJson(response.text(), JsonArray.class);
    assertEquals(5, json.size());
    // JsonElement firstValue = json.get(0);
    Person firstPerson = new Gson().fromJson(json.get(0), Person.class);
 
    assertEquals(firstPerson.uname(), "Baraneetharan");
    assertEquals(firstPerson.email(), "baranee@email.com");
  }
 
  @Test
  void shouldGetPerson() {
    // http://localhost:10000/person/5
    // {"id":5,"firstName":"Emily","lastName":"Davis","email":"emily.davis@example.com"}
 
    APIResponse response = request.get("/person/get/3");
 
    Person person = new Gson().fromJson(response.text(), Person.class);
 
    assertEquals(200, response.status());
    // assertEquals("OK", response.statusText());
    assertEquals(person.uname(), "Charlie");    
    assertEquals(person.email(), "charlie@example.com");
  }
 
  @Test
  void shouldDeletePerson() {
    // http://localhost:10000/person/6
      APIResponse response = request.delete("/person/delete/1");
    assertEquals(204, response.status());
  }
 
  @Test
  void shouldUpdatePerson() {
    // {"id":87,"firstName":"Jane","lastName":"Smith","email":"jane.smith@example.com"},{"id":88,"firstName":"Alice","lastName":"Jones","email":"alice.jones@example.com"}
    Map<String, String> data = new HashMap<>();
    data.put("id", "2");
    data.put("uname", "Baraneetharan");
    data.put("email", "baranee@email.com");
 
    APIResponse response = request.put("/person/put/2", RequestOptions.create().setData(data));
    Person person = new Gson().fromJson(response.text(), Person.class);
    // {"id":87,"firstName":"PlaywrightF87","lastName":"PlaywrightL87","email":"email87@email.com"}
    assertEquals(200, response.status());
    // assertEquals("OK", response.statusText());
    assertEquals(person.uname(), "Baraneetharan");
    assertEquals(person.email(), "baranee@email.com");
  }
}