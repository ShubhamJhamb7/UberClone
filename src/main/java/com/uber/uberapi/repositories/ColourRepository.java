package com.uber.uberapi.repositories;

import com.uber.uberapi.models.Colour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColourRepository extends JpaRepository<Colour, Long> {
}