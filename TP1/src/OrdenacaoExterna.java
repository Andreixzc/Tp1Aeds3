import java.io.RandomAccessFile;
import java.util.*;

public class OrdenacaoExterna {
    private static final String nomeArquivo = "output/conta.db";
    private static final String PREFIXO = ".db";
    private static long ptrControl = 4;
    private static int limite;
    private static String nomeArquivoFinal = "";
    private int ram;
    private int caminhos;

    // public static void main(String[] args) {
    //     int ram = 0;
    //     int caminhos = 0;
    //     Scanner scanner = new Scanner(System.in);
    //     System.out.println("Digite o tamanho de registros da ram:");
    //     ram = scanner.nextInt();
    //     System.out.println("Digite o numero de caminhos:");
    //     caminhos = scanner.nextInt();
    //     scanner.close();
    //     limite = dist(ram, caminhos);//Variável que controla até quando devemos rodar a intercalação.
    //     sort(ram,limite,caminhos);

    // }
    public OrdenacaoExterna(){};
    public OrdenacaoExterna(int ram, int caminhos){
        this.ram = ram;
        this.caminhos = caminhos;
        OrdenacaoExterna.limite = distribuicao(ram, caminhos);
    }
    public void intercalacao(){
        boolean isBase = true;
        //ram = 4, caminhos 3, limite 50;
        while (ram < limite) {
                //4 < 50
            intercalacao(ram, caminhos, isBase);
            ram *= caminhos;
            //4 = 4 * 3;
            isBase = !isBase;
        }
        System.out.println("\n---------------");
        System.out.println("Arquivo final: " + nomeArquivoFinal);
        System.out.println("Printando ids das contas \"ordenadas\"\":");
        listAccouts(nomeArquivoFinal);
    }
    public static int distribuicao(int ram, int caminhos) {
        /*Realiza a distribuição de acordo com o tamanho suportado pela ram (tam) e a quantidade de caminhos especificados
         * por parametro.
         */
        int quantidade = 0;
        try {
            List<Conta> contas = new ArrayList<>();
            RandomAccessFile[] temp = new RandomAccessFile[caminhos];
            for (int i = 0; i < caminhos; i++) {
                temp[i] = new RandomAccessFile("output/tmp" + i + PREFIXO, "rw");
            }
            while (ptrControl != -1) {
                for (int i = 0; i < caminhos; i++) {
                    for (int j = 0; j < ram; j++) {
                        var conta = readFile(nomeArquivo);
//                        System.out.println(conta);
                        if (conta != null) {
                            contas.add(conta);
                        }
                    }
                    Collections.sort(contas);
                    for (Conta conta : contas) {
                        byte[] ba = conta.converteContaEmByte();
                        temp[i].writeChar(' ');
                        temp[i].writeInt(ba.length);
                        temp[i].write(ba);
                    }
                    quantidade += contas.size();
                    contas.clear();
                }
            }
            for (var t : temp) {
                t.close();
            }
        } catch (Exception e) {
            System.out.println("Erro dist. " + e.getMessage());
            e.printStackTrace();
        }
        return quantidade;//Retorna a quantidade total de registros do arquivo original
    }
    public static void intercalacao(int ram, int caminhos, boolean isBase) {
        CustomFile[] temp1 = new CustomFile[caminhos];
        for (int i = 0; i < caminhos; i++) {
            temp1[i] = new CustomFile("output/tmp" + (isBase ? i : i + caminhos) + PREFIXO);
            //Abrindo arquivos originais frutos da distribuição.
        }

        CustomFile[] temp2 = new CustomFile[caminhos];
        for (int i = 0; i < caminhos; i++) {
            temp2[i] = new CustomFile("output/tmp" + (isBase ? i + caminhos : i) + PREFIXO);
            //A brindo arquivos temporarios auxiliares para intercalar
        }

        Map<CustomFile, Conta> map = new HashMap<>();
        int tempPos = 0;
        try {
            while (true) {
                for (int i = 0; i < caminhos; i++) {
                    if (map.get(temp1[i]) == null && temp1[i].readRegisterSize < ram) {//Verifico se ainda existem registros pra ler
                        Conta conta = temp1[i].readNext();//Instancio a conta lendo do arquivo temp1[i];
                        if (conta != null) {
                            map.put(temp1[i], conta);//Inserindo no hash map.
                        }
                    }
                }
                if (map.isEmpty()) break;

                var ordered = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();
                var firstConta = ordered.get(0);

                temp2[tempPos].writeConta(firstConta.getValue());
                if (temp2[tempPos].size == limite) {
                    nomeArquivoFinal = temp2[tempPos].fileName;
                    break;
                }
                if (temp2[tempPos].size % (ram * caminhos) == 0) {
                    tempPos++;
                    if (tempPos == caminhos) {
                        tempPos = 0;
                    }
                    for (var t : temp1) { // limpa a quantidade de registros lidos
                        t.readRegisterSize = 0;
                    }
                }
                map.remove(firstConta.getKey());
            }

            for (var t : temp1) t.file.close();
            for (var t : temp2) t.file.close();
        } catch (Exception e) {
            System.out.println("Erro intercalação. " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static Conta readFile(String fileName) {
        char lapide;
        int tamanho;
        byte[] ba;
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            if (ptrControl < file.length() && ptrControl != -1) {
                file.seek(ptrControl);
                lapide = file.readChar();
                tamanho = file.readInt();
                ba = new byte[tamanho];
                file.read(ba);
                ptrControl = file.getFilePointer();
                if (lapide != '*') {
                    return Conta.fromByteArray(ba);
                }
            } else {
                ptrControl = -1;
            }
        } catch (Exception e) {
            System.out.println("Erro readFile. " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static void listAccouts(String nome) {
        byte[] array;
        char lapide;

        try (RandomAccessFile arquivo = new RandomAccessFile(nome, "rw")) {
            while (arquivo.getFilePointer() < arquivo.length()) {
                lapide = arquivo.readChar();
                array = new byte[arquivo.readInt()];
                arquivo.read(array);
                if (lapide != '*') {
                    Conta conta = Conta.fromByteArray(array);
                    System.out.print(conta.idConta + " ");
                    
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar contas. " + e.getMessage());
        }
    }
}