package com.utilisateurs.api.repository;

import com.utilisateurs.api.model.Utilisateurs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateurs, UUID>{
        Optional<Utilisateurs> findUtilisateursById(UUID id);

        Optional<Utilisateurs> findUtilisateursByEmail( String email);

        Optional<Utilisateurs> findUtilisateursByUsername( String username);

}

