package br.com.alura.screenmatch.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequestSerieDTO(@JsonAlias("Title") String titulo,
                              @JsonAlias("totalSeasons") Integer totalTemporadas,
                              @JsonAlias("imdbRating") String avaliacao,
                              @JsonAlias("Genre") String genero,
                              @JsonAlias("Actors") String atores,
                              @JsonAlias("Poster") String enderecoPoster,
                              @JsonAlias("Plot") String sinopse) {
}