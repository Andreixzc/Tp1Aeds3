import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;


public class App {
    public static String nomeArquivo = "output/conta.db";
    static int caminhosDel = 4;
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        OrdenacaoExterna ordenacaoExterna = new OrdenacaoExterna();
        BPlusTree bPlusTree = new BPlusTree(5);
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
                } else if (opcao == 7) {
                    Crud.listAccouts();
                }
            }
        } while (opcao != 0);

        ordenacaoExterna = externalSortMenu(scanner);
        ordenacaoExterna.intercalacao();
        
        bPlusTree.criaArvore();
        bPlusTree.search(3);

        deleteFiles();
    }
   
    public static void deleteFiles(){
        int len = caminhosDel*2+1;
        File[] files = new File[len];
        int j = 0;
        files[0] = new File("output/conta.db");

        for (int i = 1; i <= caminhosDel*2; i++) {
            files[i] = new File("output/tmp"+j+".db");
            j++;
        }
        for (File file : files) {
            file.delete();
        }
        //9

    }

    public static OrdenacaoExterna externalSortMenu(Scanner scanner) {
        int caminhos;
        int ram;
        System.out.println("Ordenacao externa:");
        scanner.nextLine();
        System.out.println("Digite o numero de caminhos:");
        caminhos = Integer.parseInt(scanner.nextLine());
        caminhosDel = caminhos;
        System.out.println("Digite a quantidade de limitacao de memoria ram:");
        ram = Integer.parseInt(scanner.nextLine());
        OrdenacaoExterna ordenacaoExterna = new OrdenacaoExterna(ram, caminhos);
        return ordenacaoExterna;


    }

    public static void menu() {
        System.out.println("\nMENU:");
        System.out.println("1- Criar conta");
        System.out.println("2- Realizar uma transferencia");
        System.out.println("3- Ler um registro por ID");
        System.out.println("4- Atualizar um registro");
        System.out.println("5- Deletar um registro");
        System.out.println(
                "6- Criar N com id''s desordenados:(desconsiderando limitacao na memoria)");
        System.out.println("7- Listar contas");
        System.out.println("0- Sair do menu de crud");
    }

}


