package br.com.alura.screenmatch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.screenmatch.DTOs.ResponseSerieDTO;
import br.com.alura.screenmatch.service.SerieService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/series")
public class SerieController {

    private final SerieService serieService;

    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }
    
    @GetMapping
    public List<ResponseSerieDTO> getSeries() {
        return serieService.getSerie();
    }
    
    @GetMapping("/top5")
    public List<ResponseSerieDTO> getTop5() {
        return serieService.getTop5();
    }
    
    @GetMapping("/lancamentos")
    public List<ResponseSerieDTO> getNewEpisodes() {
        return serieService.getLeatestEpisodes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseSerieDTO> getSerieById(@PathVariable Long id) {
        var serie = serieService.getSerieById(id);

        return ResponseEntity.ok(serie);
    }
    
    
}
