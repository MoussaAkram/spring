package com.utilisateurs.api.service;

import com.utilisateurs.api.exeption.UserNotFoundException;
import com.utilisateurs.api.model.AuthenticationRequest;
import com.utilisateurs.api.model.AuthenticationResponse;
import com.utilisateurs.api.model.Role;
import com.utilisateurs.api.model.Utilisateurs;
import com.utilisateurs.api.repository.UtilisateurRepository;
import com.utilisateurs.api.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UtilisateurService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService){
        this.utilisateurRepository = utilisateurRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }
    public Page<Utilisateurs> getAllUsers(Pageable pageable) {
        return utilisateurRepository.findAll(pageable);
    }

    public List<Utilisateurs> getAllUtilisateurs(int count){
        Pageable pageable = PageRequest.of(0, count);
        Page<Utilisateurs> usersPage = getAllUsers(pageable);
        List<Utilisateurs> users = usersPage.getContent();
        return users;
    }

    public List<Utilisateurs> addUsers(List<Utilisateurs> utilisateursList) {
        List<Utilisateurs> successfulUsers = new ArrayList<>();

        utilisateursList.stream()
                .map(this::addSingleUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(successfulUsers::add);

        return successfulUsers;
    }

private Optional<Utilisateurs> addSingleUser(Utilisateurs utilisateurs) {
    String hashedPassword = bCryptPasswordEncoder.encode(utilisateurs.getPassword());

    Optional<Utilisateurs> utilisateursOptional = utilisateurRepository.findUtilisateursByEmail(utilisateurs.getEmail());
    if (utilisateursOptional.isPresent()) {
        return Optional.empty();
    }

    if (utilisateurs.getRole() != Role.admin && utilisateurs.getRole() != Role.user) {
        return Optional.empty();
    }

    if (utilisateurs.getPassword().length() < 6 || utilisateurs.getPassword().length() > 10) {
        return Optional.empty();
    }

    utilisateurs.setPassword(hashedPassword);

    return Optional.of(utilisateurRepository.save(utilisateurs));
}

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = utilisateurRepository.findUtilisateursByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public Utilisateurs getUtilisateursByUsername(String username){

        return utilisateurRepository.findUtilisateursByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Admin by email " + username + " was not found"));
    }

}
