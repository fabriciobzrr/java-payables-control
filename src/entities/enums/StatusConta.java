package entities.enums;

public enum StatusConta {
    EM_ABERTO("Em Aberto"),
    PAGA("Paga"),
    VENCIDA("Vencida"),
    ESTORNO("Estornada");

    private String descricao;

    StatusConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}