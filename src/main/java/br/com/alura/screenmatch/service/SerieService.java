package br.com.alura.screenmatch.service;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import br.com.alura.screenmatch.DTOs.ResponseSerieDTO;
import br.com.alura.screenmatch.entities.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;

@RestController
public class SerieService {
    
    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public List<ResponseSerieDTO> getSerie(){
        var series = serieRepository.findAll();
        return dataConverter(series);
    }

    public List<ResponseSerieDTO> getTop5() {
        var series = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        return dataConverter(series);
    }

    public List<ResponseSerieDTO> getLeatestEpisodes() {
        return dataConverter(serieRepository.episodiosMaisRecentes());
    }

    public ResponseSerieDTO getSerieById(Long id) {
        Serie serie = serieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Série não encontrada"));
        
        return new ResponseSerieDTO(
                serie.getTitulo(),
                serie.getTotalTemporadas(),
                serie.getAvaliacao(),
                serie.getGenero(),
                serie.getAtores(),
                serie.getPoster(),
                serie.getSinopse()
            );
    }

    private List<ResponseSerieDTO> dataConverter(List<Serie> series){
        return series
            .stream()
            .map(s -> new ResponseSerieDTO(
                s.getTitulo(),
                s.getTotalTemporadas(),
                s.getAvaliacao(),
                s.getGenero(),
                s.getAtores(),
                s.getPoster(),
                s.getSinopse()
        )).toList();
    }
}
