package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.sql.SQLException;
import java.util.*;

import org.springframework.dao.DataIntegrityViolationException;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=eada6c3";

    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0){
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                
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
        DadosSerie dados = getDadosSerie();
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

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie(){
        listarSeries();
        System.out.println("Selecione uma serie pelo nome:");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie =  series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();


        if (serie.isPresent()){

            //serie.get() porque veio de um optional
            //serie contém apenas a serie que foi pedida pelo usuário
            Serie serieEncontrada = serie.get();

            //pega todas as temporadas e joga em List<DadosTemporada> temporadas
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
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
}