package entities;

import exception.ValorInvalidoException;
import entities.enums.CategoriaConta;
import entities.enums.StatusConta;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ContaPagar {
    private static int contadorId = 1;

    private int id;
    private String descricao;
    private double valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private StatusConta status;
    private CategoriaConta categoria;
    private String fornecedor;

    public ContaPagar(String descricao, double valor, LocalDate dataVencimento, CategoriaConta categoria, String fornecedor) {
        if (descricao == null || descricao.isBlank()) {
            throw new ValorInvalidoException("Campo descrição obrigatório!");
        }
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor da conta deve ser positivo!");
        }
        if (dataVencimento == null) {
            throw new ValorInvalidoException("Campo data de vencimento obrigatório!");
        }
        if (fornecedor == null || fornecedor.isBlank()) {
            throw new ValorInvalidoException("Campo fornecedor obrigatório!");
        }

        this.id = contadorId++;
        this.descricao = descricao;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = null;
        this.status = StatusConta.EM_ABERTO;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public StatusConta getStatus() {
        return status;
    }

    public CategoriaConta getCategoria() {
        return categoria;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus (StatusConta status) {
        this.status = status;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new ValorInvalidoException("Campo descrição obrigatório!");
        }
        this.descricao = descricao;
    }

    public void setValor(double valor) {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor da conta deve ser positivo!");
        }
        this.valor = valor;
    }

    public void setCategoria(CategoriaConta categoria) {
        if (categoria == null) {
            throw new ValorInvalidoException("Categoria não pode ser nula!");
        }
        this.categoria = categoria;
    }

    public void setFornecedor(String fornecedor) {
        if(fornecedor == null || fornecedor.isBlank()){
            throw new ValorInvalidoException("Campo fornecedor obrigatório!");
        }
        this.fornecedor = fornecedor;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null) {
            throw new ValorInvalidoException("Data de vencimento não pode ser nula!");
        }
        this.dataVencimento = dataVencimento;
    }

    public void pagarConta(LocalDate dataPagamento) {
        if (dataPagamento == null) {
            throw new ValorInvalidoException("Data de pagamento obrigatória!");
        }
        this.dataPagamento = dataPagamento;
        this.status = StatusConta.PAGA;
    }

    public void estornarConta () {
        this.status = StatusConta.ESTORNO;
    }

    public void atualizarStatusConta(LocalDate dataAtual) {
        if (this.status == StatusConta.PAGA) {
            return;
        }

        if (this.status == StatusConta.ESTORNO) {
            return;
        }

        if (this.dataVencimento.isBefore(dataAtual)) {
            this.status = StatusConta.VENCIDA;
        } else {
            this.status = StatusConta.EM_ABERTO;
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String nomeFornecedor = fornecedor.length() > 15 ? fornecedor.substring(0, 12) + "..." : fornecedor;
        String descricaoReduzida = descricao.length() > 20 ? descricao.substring(0, 20) + "..." : descricao;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| #%d | R$ %.2f | %s | %s | %s | %s | %s |",
                id,
                valor,
                dtf.format(dataVencimento),
                nomeFornecedor,
                status.getDescricao(),
                categoria.getDescricao(),
                descricaoReduzida));

        if (dataPagamento != null) {
            sb.append(String.format(" Data de Pagamento: %s |",dtf.format(dataPagamento)));
        }

        return sb.toString();
    }
}