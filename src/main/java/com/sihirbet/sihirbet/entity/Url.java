package com.sihirbet.sihirbet.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Url {
    private String baseUrl;
    @Override
    public String toString() {
        return String.format(baseUrl);
    }
}
