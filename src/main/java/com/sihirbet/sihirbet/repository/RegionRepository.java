package com.sihirbet.sihirbet.repository;

import com.sihirbet.sihirbet.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    List<Region> findByName(String name);
}
