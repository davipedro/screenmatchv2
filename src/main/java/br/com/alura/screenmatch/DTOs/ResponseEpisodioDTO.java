package br.com.alura.screenmatch.DTOs;

import java.time.LocalDate;

import br.com.alura.screenmatch.entities.Serie;

public record ResponseEpisodioDTO(Serie serie,
                                   String titulo,
                                   Integer numero,
                                   Double avaliacao,
                                   LocalDate dataLancamento) {
}