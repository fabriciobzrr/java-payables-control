package entities.enums;

public enum CategoriaConta {
    AGUA_ESGOTO("Água e Esgoto"),
    ALIMENTACAO("Alimentação"),
    ALUGUEL("Aluguel"),
    COMBUSTIVEL("Combustível"),
    COMISSAO("Comissão de Vendas"),
    ENCARGOS("Encargos"),
    ENERGIA_ELETRICA("Energia Elétrica"),
    FRETES("Fretes"),
    INTERNET_TELEFONIA("Internet e Telefonia"),
    MANUTENCAO_REPAROS("Manutenção e Reparos"),
    OUTROS("Outros"),
    PAGAMENTO_FORNECEDORES("Pagamento de Fornecedores"),
    PUBLICIDADE_PROPAGANDA("Publicidade e Propaganda"),
    SALARIOS("Salários"),
    SERVICOS_TERCEIROS("Serviços de Terceiros"),
    TAXAS_JUROS_MULTAS("Taxas, Juros e Multas"),
    TRANSPORTE("Transporte");

    private String descricao;

    CategoriaConta (String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
