package com.sihirbet.sihirbet.repository;

import com.sihirbet.sihirbet.entity.Ptt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PttRepository extends JpaRepository<Ptt, Long> {

}
