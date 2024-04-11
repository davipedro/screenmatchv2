package br.com.alura.screenmatch.DTOs;

import br.com.alura.screenmatch.models.Categoria;

public record ResponseSerieDTO(Long id,
                                String titulo,
                               Integer totalTemporadas,
                               Double avaliacao,
                               Categoria genero,
                               String atores,
                               String poster,
                               String sinopse){
}
