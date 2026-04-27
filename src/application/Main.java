package application;

import entities.ContaPagar;
import entities.enums.CategoriaConta;
import entities.enums.OpcoesMenu;
import entities.enums.StatusConta;
import exception.ContaNaoEncontradaException;
import services.ContaPagarService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DTF_BRAZIL = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ContaPagarService contaPagarService = new ContaPagarService();

    public static void main(String[] args) {
        contaPagarService.atualizarStatusConta(LocalDate.now());

        IO.println("======== SISTEMA DE CONTAS A PAGAR ========");

        int opcao;

        do {
            mostrarMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao)  {
                case 1 -> cadastrarConta();
                case 2 -> pagarConta();
                case 3 -> estornarConta();
                case 4 -> listarContas();
                case 5 -> filtrarPorPeriodo();
                case 6 -> filtrarPorStatus();
                case 7 -> alertarVencidas();
                case 8 -> mostrarResumo();
                case 0 -> IO.println("\nEncerrando aplicação...");
                default -> IO.println("\nOpção inválida! Tente novamente.");
            }
        } while (opcao != 0);

    }

    private static void mostrarMenu() {
        IO.println("\n-------- Menu Principal --------");
        int count = 1;
        for (OpcoesMenu categoria : OpcoesMenu.values()) {
            IO.println(count + " - " + categoria.getDescricao());
            count++;
        }
        IO.println("0 - Encerrar\n");
    }

    // Auxiliares
    private static String lerTexto(String msg) {
        IO.print(msg);

        String texto = sc.nextLine();

        if(texto.isBlank()) {
            IO.println("\nCampo não pode estar vazio!");
            return "";
        }

        return texto;
    }

    private static int lerInteiro(String msg) {
        IO.print(msg);

        while (!sc.hasNextInt()) {
            IO.println("\nValor inválido! Digite um valor válido!");
            IO.print("Digite novamente: ");
            sc.next();
        }

        int valor = sc.nextInt();
        sc.nextLine();

        return valor;
    }

    private static double lerDouble(String msg) {
        IO.print(msg);

        while (!sc.hasNextDouble()) {
            IO.println("\nValor inválido! Digite um valor válido!");
            IO.print("Digite novamente: ");
            sc.next();
        }

        double valor = sc.nextDouble();
        sc.nextLine();

        return valor;
    }

    private static void mostrarCategorias() {
        IO.println("\nEscolha uma categoria:");
        int count = 1;
        for (CategoriaConta categoria : CategoriaConta.values()) {
            IO.println(count + " - " + categoria.getDescricao());
            count++;
        }
    }

    private static LocalDate lerData(String msg, boolean obrigatorio) {
        IO.print(msg);
        String dataTexto = sc.nextLine();
        if (dataTexto.isBlank()) {
            if (obrigatorio) {
                IO.println("Campo data não pode estar vazio!");
                return lerData(msg, obrigatorio);
            } else {
                String data = DTF_BRAZIL.format(LocalDate.now());
                IO.println("Data de pagamento: " + data);
                return LocalDate.now();
            }
        }

        try {
            return LocalDate.parse(dataTexto, DTF_BRAZIL);

        } catch (DateTimeParseException err) {
            IO.println("\nData inválida! Use o formato DD/MM/AAAA!");
            return lerData(msg, obrigatorio);
        }
    }

    private static boolean confirmar(String msg) {
        IO.print(msg);
        String opcao = sc.nextLine();

        if (opcao.isBlank()) {
            IO.println("\nOpção inválida!");
            return false;
        }

        char opcaoSelecionada = opcao.toUpperCase().charAt(0);

        return opcaoSelecionada == 'S';
    }

    // Principais
    public static void cadastrarConta() {
        IO.println("\n-------- Cadastrar Nova Conta --------");

        try {
            String descricao = lerTexto("Descrição: ");
            if (descricao.isBlank()) {
                return;
            }

            double valor = lerDouble("Valor: R$ ");
            if (valor <= 0) {
                IO.println("\nValor não pode ser menor ou igual a zero!");
                return;
            }

            LocalDate dataVencimento = lerData("Data de vencimento (DD/MM/AAAA): ", true);

            mostrarCategorias();

            int opcaoCategoria = lerInteiro("\nEscolha a categoria: ");
            if (opcaoCategoria < 1 || opcaoCategoria > CategoriaConta.values().length) {
                IO.println("\nCategoria inválida! Escolha uma categoria entre 1 e " + CategoriaConta.values().length + "!");
                return;
            }

            CategoriaConta categoria = CategoriaConta.values()[opcaoCategoria - 1];

            String fornecedor = lerTexto("Fornecedor: ");
            if (fornecedor.isBlank()) {
                IO.println("\nCampo fornecedor não pode estar vazio!");
                return;
            }

            ContaPagar contaPagar = new ContaPagar(descricao, valor, dataVencimento, categoria, fornecedor);

            IO.println("\n-------- Resumo da Conta Cadastrada --------");
            IO.println(contaPagar.toString());

            if (!confirmar("\nConfirmar cadastramento de conta? (S/N) ")) {
                IO.println("\nCadastro cancelado!");
                cadastrarConta();
            }

            contaPagarService.cadastrarContaPagar(contaPagar);

            IO.println("\nConta cadastrada com sucesso!");

            if (confirmar("\nDeseja cadastrar uma nova conta? (S/N) ")) {
                cadastrarConta();
            } else {
                IO.println("\nRetornando ao menu principal!");
            }
        } catch (Exception err) {
            IO.println("\nErro ao cadastrar a conta: " + err.getMessage());
        }
    }

    public static void listarContas() {
        List<ContaPagar> contas = contaPagarService.listarContas();
        IO.println("\n-------- Listar Todas as Contas --------");

        if (contas.isEmpty()) {
            IO.println("\nNenhum registro de contas encontrado!");
            return;
        }

        for (ContaPagar conta : contas) {
            IO.println(conta.toString());
        }

        IO.println("\nTotal de registros: " + contas.size());
    }

    public static void pagarConta() {
        IO.println("\n-------- Pagar Conta --------");

        try {
            int id = lerInteiro("\nDigite o ID da conta: ");

            ContaPagar conta = contaPagarService.buscarContaPorId(id);

            if (conta.getStatus() == StatusConta.PAGA) {
                IO.println("Esta conta já está paga!");
                return;
            }

            IO.println("\nDados da conta:");
            IO.println(conta.toString());

            if (!confirmar("\nA conta acima está correta? (S/N) ")) {
                IO.println("\nPagamento cancelado!");
                pagarConta();
            }

            LocalDate dataPagamento = lerData("\nData de pagamento (DD/MM/AAAA) ou Enter para hoje: ", false);

            if (!confirmar("\nDeseja realmente pagar esta conta? (S/N) ")) {
                IO.println("\nPagamento cancelado!");
                return;
            }

            contaPagarService.pagarConta(id, dataPagamento);
            IO.println("\nPagamento realizado com sucesso!");

            if (confirmar("\nDeseja pagar outra conta? (S/N) ")) {
                pagarConta();
            } else {
                IO.println("\nRetornando ao menu principal!");
            }

        } catch (ContaNaoEncontradaException err) {
            IO.println("\nErro ao pagar a conta: " + err.getMessage());
        }
    }

    public static void filtrarPorStatus() {
        IO.println("\n-------- Filtrar Todas as Contas --------");

        List<ContaPagar> contas = contaPagarService.listarContas();

        IO.println("\nEscolha uma opção de status:");
        int count = 1;
        for (StatusConta status : StatusConta.values()) {
            IO.println(count + " - " + status.getDescricao());
            count++;
        }
        IO.println("0 - Voltar");

        int opcaoStatus = lerInteiro("\nEscolha uma opção: ");
        StatusConta status = null;

        switch (opcaoStatus) {
            case 1 -> status = StatusConta.EM_ABERTO;
            case 2 -> status = StatusConta.PAGA;
            case 3 -> status = StatusConta.VENCIDA;
            case 0 -> { return; }
            default -> IO.println("Opção inválida! Digite uma opção entre 1 e " + StatusConta.values().length + " ou 0 para sair!");
        }

        List<ContaPagar> filtradas = contaPagarService.filtrarPorStatus(status);

        if (filtradas.isEmpty()) {
            IO.println("\nNenhum registro de contas " + status.getDescricao().toLowerCase() + "s encontrado!");
            return;
        }

        IO.println("\nContas com status: " + status.getDescricao().toUpperCase());

        for (ContaPagar conta : filtradas) {
            IO.println(conta);
        }

        IO.println("\nTotal de Registros: " + filtradas.size());
    }

    public static void mostrarResumo() {
        IO.println("\n-------- Resumo Financeiro --------");
        double emAberto = contaPagarService.totalContasPendentes();
        double vencidas = contaPagarService.listarContasVencidas().stream()
                .mapToDouble(ContaPagar::getValor).sum();

        double totalContas = emAberto + vencidas;

        IO.println("\nTotal " + StatusConta.EM_ABERTO.getDescricao() + ": R$ " + real(emAberto));
        IO.println("Total de " + StatusConta.VENCIDA.getDescricao() + "s: R$ " + real(vencidas));
        IO.println("Total de contas a pagar: R$ " + real(totalContas));

    }

    private static String real(double valor) {
        return String.format("%.2f", valor);
    }

    public static void filtrarPorPeriodo() {
        IO.println("\n-------- Filtrar Por Período --------");

        LocalDate dataInicial = lerData("Data inicial (DD/MM/AAAA): ", true);
        LocalDate dataFinal = lerData("Data final (DD/MM/AAAA): ", true);

        List<ContaPagar> contasFiltradas = contaPagarService.filtrarPorPeriodo(dataInicial, dataFinal);

        if (contasFiltradas.isEmpty()) {
            IO.println("Nenhuma registro de contas encontrado para o período de " + DTF_BRAZIL.format(dataInicial) + " à " + DTF_BRAZIL.format(dataFinal) + "!");
            return;
        }

        for (ContaPagar conta : contasFiltradas) {
            IO.println(conta);
        }

        IO.println("\nTotal de registros: " + contasFiltradas.size());
    }

    public static void alertarVencidas() {
        IO.println("\n-------- Alerta de Contas Vencidas --------");

        List<ContaPagar> vencidas = contaPagarService.listarContasVencidas();

        if (vencidas.isEmpty()) {
            IO.println("Nenhum registro de contas " + StatusConta.VENCIDA.getDescricao().toLowerCase() + "s encontrado!");
            return;
        }

        for (ContaPagar conta : vencidas) {
            IO.println(conta);
        }

        IO.println("\nTotal de registros: " + vencidas.size());

    }

    public static void estornarConta() {
        IO.println("\n-------- Estornar Conta --------");

        try {
            int id = lerInteiro("\nDigite o ID da conta: ");

            ContaPagar conta = contaPagarService.buscarContaPorId(id);

            if (conta.getStatus() == StatusConta.ESTORNO) {
                IO.println("A conta acima está correta? (S/N) ");
                return;
            }


            IO.println("\nDados da conta:");
            IO.println(conta);

            if(!confirmar("\nDeseja estornar esta conta? (S/N) ")) {
                IO.println("\nEstorno cancelado!");
                estornarConta();
            }

            contaPagarService.estornarConta(id);

            IO.println("Estorno realizado com sucesso!");

        } catch (ContaNaoEncontradaException err) {
            IO.println("\nErro ao pagar a conta: " + err.getMessage());
        }
    }
}