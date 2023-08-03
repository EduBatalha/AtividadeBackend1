import java.time.LocalDate;

public class Socio {
    private int numeroCarteirinha;
    private LocalDate dataAssociacao;
    private String nome;
    private String documento;

    public Socio(int numeroCarteirinha, LocalDate dataAssociacao, String nome, String documento) {
        this.numeroCarteirinha = numeroCarteirinha;
        this.dataAssociacao = dataAssociacao;
        this.nome = nome;
        this.documento = documento;
    }

    public int getNumeroCarteirinha() {
        return numeroCarteirinha;
    }

    public LocalDate getDataAssociacao() {
        return dataAssociacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    @Override
    public String toString() {
        return "Número da carteirinha: " + numeroCarteirinha +
                ", Data de associação: " + dataAssociacao +
                ", Nome: " + nome +
                ", Documento: " + documento;
    }
}

