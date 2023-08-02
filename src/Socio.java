public class Socio {
    private int numeroCarteirinha;
    private String dataAssociacao;
    private String nome;
    private String documento;

    public Socio(int numeroCarteirinha, String dataAssociacao, String nome, String documento) {
        this.numeroCarteirinha = numeroCarteirinha;
        this.dataAssociacao = dataAssociacao;
        this.nome = nome;
        this.documento = documento;
    }

    public int getNumeroCarteirinha() {
        return numeroCarteirinha;
    }

    public String getDataAssociacao() {
        return dataAssociacao;
    }

    public String getNome() {
        return nome;
    }

    public String getDocumento() {
        return documento;
    }

    @Override
    public String toString() {
        return "Número da carteirinha: " + numeroCarteirinha +
                ", Data de associação: " + dataAssociacao +
                ", Nome: " + nome +
                ", Documento: " + documento;
    }
}

