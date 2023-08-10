package domain;

import java.util.ArrayList;
import java.util.List;


public class GestaoEspacos {
    private List<Espaco> espacos;

    public GestaoEspacos() {
        espacos = new ArrayList<>();
        inicializarEspacos();
    }

    private void inicializarEspacos() {
        // Adicione aqui os espaços fixos do clube (quadras, piscinas, academia, etc.)
        espacos.add(new Espaco("Quadra de Futebol Indoor", Espaco.Categoria.ESPORTES, 10));
        espacos.add(new Espaco("Quadra de Vôlei de Praia", Espaco.Categoria.ESPORTES, 8));
        espacos.add(new Espaco("Quadra de Beach Tennis", Espaco.Categoria.ESPORTES, 6));
        espacos.add(new Espaco("Campo de Golfe A", Espaco.Categoria.ESPORTES, 18));
        espacos.add(new Espaco("Campo de Golfe B", Espaco.Categoria.ESPORTES, 18));
        espacos.add(new Espaco("Piscina Olímpica A", Espaco.Categoria.RECREACAO, 500));
        espacos.add(new Espaco("Piscina Olímpica B", Espaco.Categoria.RECREACAO, 500));
        espacos.add(new Espaco("Lago com Pedalinhos", Espaco.Categoria.RECREACAO, 20));
        espacos.add(new Espaco("Jardim Botânico", Espaco.Categoria.RELAXAMENTO, 100));
        espacos.add(new Espaco("Academia", Espaco.Categoria.ESPORTES, 50));
        espacos.add(new Espaco("Spá", Espaco.Categoria.RELAXAMENTO, 15));
        espacos.add(new Espaco("Área para Churrasco", Espaco.Categoria.RECREACAO, 30));
        espacos.add(new Espaco("Parque Infantil", Espaco.Categoria.RECREACAO, 50));
    }


    public void adicionarEspaco(Espaco espaco) {
        espacos.add(espaco);
    }

    public List<Espaco> getEspacos() {
        return espacos;
    }


}
