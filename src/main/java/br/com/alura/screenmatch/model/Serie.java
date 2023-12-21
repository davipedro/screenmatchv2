package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.repository.SerieRepository;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Entity
@Table (name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = Optional.ofNullable(dadosSerie.avaliacao())
                .map(Double::parseDouble)
                .orElse(null);
        this.genero = Optional.ofNullable(dadosSerie.genero())
                .map(g -> Categoria.fromString(g.split(",")[0].trim()))
                .orElse(null);
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.enderecoPoster();
        this.sinopse = dadosSerie.sinopse();
        //this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse()).trim(); - chave expirada
    }

    public Serie() {
    }

    public List<Episodio> getEpisodios(){
        return episodios;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public String getAtores() {
        return atores;
    }

    public String getPoster() {
        return poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setEpisodios(List<Episodio> episodios) {
        //mapeia de qual serie é cada episodio
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return String.format("================================================="
                + "\n%22s" + getTitulo() + "\n%22s" +  getTotalTemporadas() + "\n%22s" +
                        getAvaliacao() + "\n%22s" + getGenero() + "\n%22s" + getAtores() +
                        "\n%22s"+ getPoster() + "\n%22s" + getSinopse() + "\n%22s" + getEpisodios() +
                "\n=================================================", "Título: ",
                "Total de Temporadas: ", "Avaliação: ", "Gênero: " , "Atores: ",
                "Poster: ", "Sinopse: ", "Episodios");
    }
}
