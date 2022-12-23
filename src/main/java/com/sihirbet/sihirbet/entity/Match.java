package com.sihirbet.sihirbet.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class Match {
    private Team home;
    private Team away;
    private int time;
}
