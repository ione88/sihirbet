package com.sihirbet.sihirbet.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Ptt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "alici", nullable = true, length = Integer.MAX_VALUE)
    private String alici;

    @Column(name = "adres", nullable = true, length = Integer.MAX_VALUE)
    private String adres;

}
