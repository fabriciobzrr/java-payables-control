package repository;

import entities.ContaPagar;
import entities.enums.CategoriaConta;
import entities.enums.StatusConta;
import exception.ContaNaoEncontradaException;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ContaPagarRepository {
    // Onde os dados ficarão salvos enquanto o programa roda
    private List<ContaPagar> contas = new ArrayList<>();

    // Caminho do arquivo onde as contas ficam salvas
    private static final String CAMINHO_ARQUIVO = "dados/contas.csv";

    // Formato de data para salvar no arquivo
    private static final DateTimeFormatter DTF_BRAZIL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Construtor que inicia a lista e carrega os dados
    public ContaPagarRepository() {
        carregarDoArquivo();
    }

    public void adicionarConta(ContaPagar conta) {
        contas.add(conta);
        salvarNoArquivo();
    }

    public List<ContaPagar> listarContas() {
        return new ArrayList<>(contas);
    }

    public ContaPagar buscarContaPorId(int id) {
        return contas.stream()
                .filter(conta -> conta.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de ID " + id + " não encontrada!"));
    }

    public void removerContaPorId(int id) {
        ContaPagar conta = buscarContaPorId(id);
        contas.remove(conta);
        salvarNoArquivo();
    }

    public void atualizarConta(ContaPagar conta) {
        removerContaPorId(conta.getId());
        adicionarConta(conta);
    }

    // Persistência de dados: salvar dados no arquivo .csv
    private void salvarNoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        File diretorio = arquivo.getParentFile();

        if (diretorio != null && !diretorio.exists()) {
            diretorio.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {

            // Cabeçalho do arquivo contendo todas as informações da conta
            bw.write("id;descricao;valor;dataVencimento;dataPagamento;status;categoria;fornecedor");
            bw.newLine();

            // Adicionando os dados das contas nas linhas
            for (ContaPagar conta : contas) {
                bw.write(converterParaCSV(conta));
                bw.newLine();
            }
        } catch (IOException err) {
            System.err.println("Erro ao salvar arquivo: " + err.getMessage());
        }
    }

    // Persistência de dados: carregar dados do arquivo .csv
    private void carregarDoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);

        if (!arquivo.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {
                ContaPagar conta = converterDoCSV(linha);
                if (conta != null) {
                    contas.add(conta);
                }
            }

        } catch (IOException err) {
            System.err.println("Erro ao carregar arquivo: " + err.getMessage());
        }
    }

    // Conversão dados do ContaPagar para texto CSV
    private String converterParaCSV(ContaPagar conta) {
        String dataPagamento = conta.getDataPagamento() != null ? DTF_BRAZIL.format(conta.getDataPagamento()) : "";

        return String.format("%d;%s;%.2f;%s;%s;%s;%s;%s",
                conta.getId(),
                conta.getDescricao(),
                conta.getValor(),
                DTF_BRAZIL.format(conta.getDataVencimento()),
                dataPagamento,
                conta.getStatus().name(),
                conta.getCategoria().name(),
                conta.getFornecedor());
    }

    // Conversão do CSV para ContaPagar
    private ContaPagar converterDoCSV(String linha) {
        try {
            String[] campo = linha.split(";");

            int id = Integer.parseInt(campo[0]);
            String descricao = campo[1];
            String valorStr = campo[2].replace(",", ".");
            double valor = Double.parseDouble(valorStr);

            LocalDate dataVencimento = LocalDate.parse(campo[3], DTF_BRAZIL);
            LocalDate dataPagamento = campo[4].isBlank() ? null : LocalDate.parse(campo[4], DTF_BRAZIL);
            StatusConta status = StatusConta.valueOf(campo[5]);
            CategoriaConta categoria = CategoriaConta.valueOf(campo[6]);
            String fornecedor = campo[7];

            ContaPagar conta = new ContaPagar(descricao, valor, dataVencimento, categoria, fornecedor);

            // Adiciona o ID do CSV ao objeto instanciado da ContaPagar
            conta.setId(id);

            // Adiciona o status atualizado ao objeto instanciado da ContaPagar
            conta.setStatus(status);

            // Adiciona uma data de pagamento ao objeto instanciado se houver
            if (dataPagamento != null) {
                conta.pagarConta(dataPagamento);
            }

            return conta;
        } catch (Exception err) {
            System.err.println("Erro ao converter linha: " + err.getMessage());
            return null;
        }

    }
}
