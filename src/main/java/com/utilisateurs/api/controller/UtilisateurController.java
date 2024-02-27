package com.utilisateurs.api.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utilisateurs.api.model.AuthenticationRequest;
import com.utilisateurs.api.model.AuthenticationResponse;
import com.utilisateurs.api.model.Utilisateurs;
import com.utilisateurs.api.service.UtilisateurService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/")
public class UtilisateurController {
    private final UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService){
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/api/users/generate")
    public ResponseEntity<ByteArrayResource> getAllUtilisateurs(@RequestParam(value = "count", defaultValue = "0") int count){

        List<Utilisateurs> utilisateurs = utilisateurService.getAllUtilisateurs(count);

        byte[] data = utilisateurs.toString().getBytes();

        ByteArrayResource resource = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=myfile.json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return
                ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentLength(data.length)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);

    }



    @PostMapping(value = "/api/users/batch", consumes = { "multipart/form-data" })
    public ResponseEntity<String> addUtilisateursBatch(
            @RequestPart(value = "file" ) MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Please upload a file!", HttpStatus.BAD_REQUEST);
        }

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            List<Utilisateurs> utilisateursList = objectMapper.readValue(file.getBytes(), new TypeReference<List<Utilisateurs>>() {});

            System.out.println("Received user data for " + utilisateursList.size() + " users.");

            List<Utilisateurs> savedUtilisateurs = utilisateurService.addUsers(utilisateursList);

            int difference = utilisateursList.size() - savedUtilisateurs.size();
            JSONObject responseObject = new JSONObject();
            responseObject.put("Nombre d'enregistrements total", savedUtilisateurs.size());
            responseObject.put("Nombre non importés", difference);

            return new ResponseEntity<>(responseObject.toString(), HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to process the file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/api/auth")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(utilisateurService.authenticate(authenticationRequest));
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/api/users/{username}")
    public ResponseEntity<Utilisateurs> getUserByUserName(@PathVariable("username") String username) {

        Utilisateurs utilisateurs = utilisateurService.getUtilisateursByUsername(username);
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }

}
