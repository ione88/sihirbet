package com.sihirbet.sihirbet.service;

import com.sihirbet.sihirbet.entity.Liga;
import com.sihirbet.sihirbet.entity.Region;
import com.sihirbet.sihirbet.repository.LigaRepository;
import com.sihirbet.sihirbet.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigaService {

    private LigaRepository ligaRepository;

    public LigaService(LigaRepository ligaRepository) {
        this.ligaRepository = ligaRepository;
    }
    
    public void update(Liga liga) {
        List<Liga> existLiga = ligaRepository.findByName(liga.getName());
        liga = merge(existLiga, liga);
        ligaRepository.save(liga);
    }

    private Liga merge(List<Liga> existLiga, Liga liga) {
        if (existLiga.isEmpty()) {
            return liga;
        }
        existLiga.get(0).setUrl(liga.getUrl());
        return existLiga.get(0);
    }
}
