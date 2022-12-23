package com.sihirbet.sihirbet.repository;

import com.sihirbet.sihirbet.entity.Liga;
import com.sihirbet.sihirbet.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigaRepository extends JpaRepository<Liga, Long> {

    List<Liga> findByName(String name);
}
