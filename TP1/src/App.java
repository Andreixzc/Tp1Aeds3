import java.util.Scanner;

public class App {
    public static String nomeArquivo = "output/conta.db";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;
        do {
            menu();
            System.out.print("\nDigite sua opcao: ");
            opcao = Integer.parseInt(scanner.next());
            if (opcao != 0) {
                if (opcao == 1) {
                    scanner.nextLine();
                    Acoes.criaContaModel(scanner);
                } else if (opcao == 2) {
                    Acoes.realizaTranseferencia(scanner);
                } else if (opcao == 3) {
                    Acoes.lerPorId(scanner);
                } else if (opcao == 4) {
                    Acoes.atualizarRegistro(scanner);
                } else if (opcao == 5) {
                    Acoes.deletarRegistro(scanner);
                } else if (opcao == 6) {
                    Acoes.criaContasRandom(scanner);
                } else if (opcao == 7){
                    Crud.listAccouts();
                }
            }
        } while (opcao != 0);

    }
    public static void menu() {
        System.out.println("\nMENU:");
        System.out.println("1- Criar conta");
        System.out.println("2- Realizar uma transferencia");
        System.out.println("3- Ler um registro por ID");
        System.out.println("4- Atualizar um registro");
        System.out.println("5- Deletar um registro");
        System.out.println("6- Criar N com id''s desordenados:(desconsiderando limitacao na memoria)");
        System.out.println("7- Listar contas");
        System.out.println("0- Sair do programa");
    }

}
