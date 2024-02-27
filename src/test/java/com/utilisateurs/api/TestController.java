package com.utilisateurs.api;


import com.utilisateurs.api.controller.UtilisateurController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class TestController {
    @Autowired
    private UtilisateurController utilisateurController;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(utilisateurController);
    }

    @Test
    public void test(){

    }
}
