package br.com.alura.screenmatch.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequestTemporadaDTO(@JsonAlias("Season") Integer numero,
                                  @JsonAlias("Episodes") List<RequestEpisodioDTO> episodios) {
}