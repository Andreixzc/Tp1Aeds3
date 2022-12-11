import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;


public class App {
    public static String nomeArquivo = "output/conta.db";
    static int caminhosDel = 4;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        KMP_String_Matching kmp_String_Matching = new KMP_String_Matching();
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
                } else if (opcao == 8) {
                    ordenacaoExterna = externalSortMenu(scanner);
                    ordenacaoExterna.intercalacao();
                } else if (opcao == 9) {
                    bPlusTree.criaArvore();
                    bPlusTree.search(3);
                } else if (opcao == 10) {
                    Huffman.criarArvoreHuffman(Crud.extraiTexto().toString());
                } else if (opcao == 11) {
                    lzwAux();
                } else if (opcao == 12) {
                    scanner.nextLine();
                    System.out.println("Digite o padrao a ser pesquisado:");
                    String str = scanner.nextLine();
                    kmp_String_Matching.KMPSearch(str,Crud.extraiTexto().toString());
                }
            }
        } while (opcao != 0);
        deleteFiles();
    }

    public static void deleteFiles() {
        int len = caminhosDel * 2 + 1;
        File[] files = new File[len];
        int j = 0;
        files[0] = new File("output/conta.db");

        for (int i = 1; i <= caminhosDel * 2; i++) {
            files[i] = new File("output/tmp" + j + ".db");
            j++;
        }
        for (File file : files) {
            file.delete();
        }

    }

    public static String extraiTexto(String pathFile){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathFile));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            System.out.println(e);
        }     
        return "";
    }

    public static void lzwAux(){
        StringBuilder compressed = new StringBuilder();
        lzw lzw_compression = new lzw();
        String OriginalString = Crud.extraiTexto().toString();//Extraindo todo o conteudo do arquivo para a string
        compressed = lzw_compression.lzw_compress(OriginalString);
        System.out.println("O conteudo comprimido eh "+ compressed);
        writeToFile(compressed, "arquivosComprimidos/ContasLzw.txt");//Armazenando arquivo comprimido
        String decompressed = lzw_compression.lzw_extract(compressed.toString());
        System.out.println("A string decodificada eh:"+decompressed);
    }
    public static boolean writeToFile(StringBuilder sb, String path){
        try {
            File file = new File(path);
            BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.append(sb);
        } finally {
            if (writer != null)
                writer.close();
        }

        } catch (Exception e) {
            return false;
        }
        return true;
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
        System.out.println("8 - Realizar intercalacao externa:");
        System.out.println("9- Inserir base de dados em na em uma bTree:");
        System.out.println("10- Compressao e descompressao huffman:");
        System.out.println("11- Compressao LZW:");
        System.out.println("12- Busca de padroes:");
        System.out.println("0- Finalizar");
    }

}


