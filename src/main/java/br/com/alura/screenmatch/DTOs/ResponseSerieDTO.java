package br.com.alura.screenmatch.DTOs;

import br.com.alura.screenmatch.models.Categoria;

public record ResponseSerieDTO(String titulo,
                               Integer totalTemporadas,
                               Double avaliacao,
                               Categoria genero,
                               String atores,
                               String enderecoPoster,
                               String sinopse){
}
