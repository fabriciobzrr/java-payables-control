package entities.enums;

public enum OpcoesMenu {
    CADASTRAR_CONTA("Cadastrar nova conta"),
    PAGAR_CONTA("Pagar uma conta"),
    ESTORNAR_CONTA("Estornar conta"),
    LISTAR_CONTAS("Listar todas as contas"),
    FILTRAR_POR_PERIODO("Filtrar por periodo"),
    FILTRAR_POR_STATUS("Filtrar contas por status"),
    ALERTA_VENCIDAS("Alerta de contas vencidas"),
    MOSTRAR_RESUMO("Mostrar resumo financeiro");

    private String descricao;

    OpcoesMenu(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
