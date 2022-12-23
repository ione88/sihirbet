package com.sihirbet.sihirbet.service;

import com.sihirbet.sihirbet.entity.Region;
import com.sihirbet.sihirbet.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {

    private RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }
    
    public void update(Region region) {
        List<Region> existRegion = regionRepository.findByName(region.getName());
        region = merge(existRegion, region);
        regionRepository.save(region);
    }

    private Region merge(List<Region> existRegion, Region region) {
        if (existRegion.isEmpty()) {
            return region;
        }
        existRegion.get(0).setUrl(region.getUrl());
        return existRegion.get(0);
    }

    public List<String> getAllUrl() {
        return regionRepository.findAll().stream().map(Region::getUrl).toList();
    }
}
