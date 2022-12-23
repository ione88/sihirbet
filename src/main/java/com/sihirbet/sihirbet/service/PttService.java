package com.sihirbet.sihirbet.service;

import com.sihirbet.sihirbet.entity.Ptt;
import com.sihirbet.sihirbet.repository.PttRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PttService {

    private PttRepository pttRepository;

    public PttService(PttRepository pttRepository) {
        this.pttRepository = pttRepository;
    }
    
    public void update(Ptt ptt) {
        Optional<Ptt> existPtt = pttRepository.findById(ptt.getId());
        ptt = merge(existPtt, ptt);
        pttRepository.save(ptt);
    }

    private Ptt merge(Optional<Ptt> existPtt, Ptt ptt) {
        if (existPtt.isEmpty()) {
            return ptt;
        }
        existPtt.get().setAdres(ptt.getAdres());
        existPtt.get().setAlici(ptt.getAlici());
        return existPtt.get();
    }

    public List<Long> getAllId() {
        return pttRepository.findAll().stream().filter(ptt -> ptt.getAlici() == null).map(Ptt::getId).toList();
    }
}
