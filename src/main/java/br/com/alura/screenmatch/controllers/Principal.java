package br.com.alura.screenmatch.controllers;

import br.com.alura.screenmatch.DTOs.RequestSerieDTO;
import br.com.alura.screenmatch.DTOs.RequestTemporadaDTO;
import br.com.alura.screenmatch.entities.Episodio;
import br.com.alura.screenmatch.entities.Serie;
import br.com.alura.screenmatch.models.Categoria;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.utils.ConsumoApi;
import br.com.alura.screenmatch.utils.ConverteDados;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=eada6c3";

    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBuscada;

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0){
            var menu = """
                1  - Buscar séries
                2  - Buscar episódios
                3  - Listar séries buscadas
                4  - Buscar série por título
                5  - Buscar série por ator
                6  - Buscar top 5 séries
                7  - Buscar séries por categorias
                8  - Buscar séries filtrando-as
                9  - Buscar episódio por trecho
                10 - Buscar os top episódios de uma série
                11 - Buscar Episodio Depois Após Data
                
                0 - Sair                               \s
                """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeries();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategorias();
                    break;
                case 8:
                    buscarSerieFiltrada();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodioDepoisDeData();
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    public Principal(SerieRepository repositorio){
        this.repositorio = repositorio;
    }

    private void buscarSerieWeb(){
        RequestSerieDTO dados = getDadosSerie();
        Serie serie = new Serie(dados);
        try {
            repositorio.save(serie);
        } catch (DataIntegrityViolationException e){
            System.out.println(new SQLException().getLocalizedMessage());
        }
    }

    private void listarSeries(){
        series =  repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private RequestSerieDTO getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, RequestSerieDTO.class);
    }

    private void buscarEpisodioPorSerie(){
        listarSeries();
        System.out.println("Selecione uma serie pelo nome:");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie =  repositorio.findByTituloContainingIgnoreCase(nomeSerie);


        if (serie.isPresent()){

            //serie.get() porque veio de um optional
            //serie contém apenas a serie que foi pedida pelo usuário
            Serie serieEncontrada = serie.get();

            //pega todas as temporadas e joga em List<DadosTemporada> temporadas
            List<RequestTemporadaDTO> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                RequestTemporadaDTO requestTemporadaDTO = conversor.obterDados(json, RequestTemporadaDTO.class);
                temporadas.add(requestTemporadaDTO);
            }
            //lista aninhada: temporadas -> temporada -> lista DadosTemp -> Lista Ep -> Ep
            List<Episodio> episodios = temporadas.stream()
                    .flatMap( d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                            .toList();

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada");
        }

    }

    private void buscarSeriePorTitulo(){
        System.out.println("Escolha uma série pelo nome: ");
        String nomeSerie = leitura.nextLine();
        serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()){
            System.out.println("Dados da série:\n" + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        series =  repositorio.findAll();
        List<Serie> series = repositorio.findAll();
        Set<String> atores = series.stream()
                .map(Serie::getAtores)
                .flatMap(a -> Arrays.stream(a.split(",")))
                .map(String::trim) // Remove espaços em branco no início e no final de cada nome
                .collect(Collectors.toCollection(TreeSet::new)); // Coleta os nomes em um TreeSet para ordená-los
        atores.forEach(System.out::println);

        System.out.println("Digite o nome do Ator:");
        String nomeAtor = leitura.nextLine();
        System.out.println("Digite a nota mínima da série a ser apresentada:");
        Double notaMinima = leitura.nextDouble();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor,notaMinima);

        if (seriesEncontradas != null){
            System.out.println("Presente em:");
            seriesEncontradas.forEach(s ->
                    System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("Ator não foi encontrado!");
        }
    }

    private void buscarTop5Series(){
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategorias(){
        System.out.println("Gêneros:");
        for (Categoria categoria: Categoria.values()) {
            System.out.println(categoria);
        }
        System.out.println("Insira o gênero:");
        String nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> serieCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria: " + nomeGenero);
        serieCategoria.forEach(System.out::println);
    }

    private void buscarSerieFiltrada(){
        System.out.println("Digite o máximo de temporadas:");
        Integer totalTemporadas = leitura.nextInt();
        System.out.println("Digite a nota mínima:");
        Double notaMinima = leitura.nextDouble();

        List<Serie> seriesFiltradas = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, notaMinima);
        seriesFiltradas.forEach(System.out::println);
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Digite o nome do episódio: ");
        String trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("====================\nSérie: %s \n  Temporada %s \n    Episódio %s - %s\n====================\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> episodiosTop = repositorio.episodioTop(serie);
            episodiosTop.forEach(e ->
                    System.out.printf("====================\nSérie: %s \n  Temporada %s \n    Episódio %s - %s\n====================\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo()));
        }
    }

    private void buscarEpisodioDepoisDeData(){
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            System.out.println("Digite a data mais antiga possível");
            int anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(e ->
                    System.out.printf("====================\nSérie: %s \n  Temporada %s \n    Episódio %s - %s\n====================\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo()));
        }
    }

}