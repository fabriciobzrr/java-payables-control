package services;

import entities.ContaPagar;
import entities.enums.StatusConta;
import repository.ContaPagarRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ContaPagarService {
    private ContaPagarRepository contaPagarRepository;

    public ContaPagarService() {
        this.contaPagarRepository = new ContaPagarRepository();
    }

    // CRUD - CREATE, READ, UPDATE, DELETE

    public void cadastrarContaPagar(ContaPagar conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta não pode ser nula!");
        }

        if (conta.getValor() <= 0) {
            throw new IllegalArgumentException("Valor da conta não pode ser negativo!");
        }


        contaPagarRepository.adicionarConta(conta);
    }

    public List<ContaPagar> listarContas() {
        return contaPagarRepository.listarContas();
    }

    public ContaPagar buscarContaPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return contaPagarRepository.buscarContaPorId(id);
    }

    public void pagarConta(int id, LocalDate dataPagamento) {
        ContaPagar conta = buscarContaPorId(id);

        if (conta.getStatus() == StatusConta.PAGA) {
            throw new IllegalStateException("Conta já está paga!");
        }

        conta.pagarConta(dataPagamento);

        contaPagarRepository.atualizarConta(conta);
    }

    public void estornarConta (int id) {
        ContaPagar conta = buscarContaPorId(id);
        if (conta.getStatus() == StatusConta.ESTORNO) {
            throw new IllegalStateException("Conta já está estornada!");
        }

        conta.estornarConta();

        contaPagarRepository.atualizarConta(conta);
    }

    // FILTROS

    // Filtrar por Status
    public List<ContaPagar> filtrarPorStatus(StatusConta status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo!");
        }

        return contaPagarRepository.listarContas().stream()
                .filter(conta -> conta.getStatus() == status)
                .collect(Collectors.toList());
    }

    // Filtrar por período
    public List<ContaPagar> filtrarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas!");
        }

        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("A data inicial não pode ser após a da data final!");
        }

        return contaPagarRepository.listarContas().stream()
                .filter(conta -> {
                    LocalDate vencimento = conta.getDataVencimento();
                    return !vencimento.isBefore(dataInicial) && !vencimento.isAfter(dataFinal);
                })
                .collect(Collectors.toList());
    }

    // Contas vencidas
    public List<ContaPagar> listarContasVencidas() {
        return filtrarPorStatus(StatusConta.VENCIDA);
    }

    // CÁLCULOS

    // Total contas em aberto
    public double totalContasPendentes() {
        return filtrarPorStatus(StatusConta.EM_ABERTO).stream()
                .mapToDouble(ContaPagar::getValor)
                .sum();
    }

    // Atualizar status de todas as contas
    public void atualizarStatusConta(LocalDate dataAtual) {
        if (dataAtual == null) {
            throw new IllegalArgumentException("Data atual não pode ser nula!");
        }

        for (ContaPagar conta : contaPagarRepository.listarContas()) {
            StatusConta statusAntigo =  conta.getStatus();
            conta.atualizarStatusConta(dataAtual);

            if (statusAntigo != conta.getStatus()) {
                contaPagarRepository.atualizarConta(conta);
            }
        }
    }

}