import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Crud {
    final static String nomeArquivo = "output/conta.db";
    final static int cesarKey = 3;
    public Crud() {}

    public static Conta geradorDeConta(Scanner scanner){
        Conta conta = new Conta();
        conta.idConta = getLastId();
        System.out.println("Digite seu nome: ");
        conta.nomePessoa = scanner.nextLine();
        System.out.println("Digite seu email: ");
        conta.email = scanner.nextLine();
        System.out.println("Digite seu nome de usuario:");
        conta.nomeUsuario = scanner.nextLine();
        System.out.println("Digite sua senha:");
        conta.senha = scanner.nextLine();
        System.out.println("Digite seu CPF");
        conta.cpf = scanner.nextLine();
        System.out.println("Digite sua cidade:");
        conta.cidade = scanner.nextLine();
        System.out.println("Digite o saldo da conta:");
        conta.saldoConta = Float.parseFloat(scanner.nextLine());
        conta.transferenciasRealizadas = 0;
        return conta;
    }

    public static Conta geradorDeContaUpdate(Scanner scanner){
        Conta conta = new Conta();
        System.out.println("Digite o ID da conta:");
        conta.idConta = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Digite seu nome: ");
        conta.nomePessoa = scanner.nextLine();
        System.out.println("Digite seu email: ");
        conta.email = scanner.nextLine();
        System.out.println("Digite seu nome de usuario:");
        conta.nomeUsuario = scanner.nextLine();
        System.out.println("Digite sua senha:");
        conta.senha = criptografaSenha(scanner.nextLine());
        System.out.println("Digite seu CPF");
        conta.cpf = scanner.nextLine();
        System.out.println("Digite sua cidade:");
        conta.cidade = scanner.nextLine();
        System.out.println("Digite o saldo da conta:");
        conta.saldoConta = Float.parseFloat(scanner.nextLine());
        conta.transferenciasRealizadas = 0;
        return conta;
    }
    public static StringBuilder extraiTexto(){
        StringBuilder content = new StringBuilder();
        byte[] array;
        char lapide;
        try {
            RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
            content.append(arquivo.readInt());
            arquivo.seek(4);
            // System.out.println("Listando os ID's das contas no arquivo:");
            while (arquivo.getFilePointer() != -1) {
                lapide = arquivo.readChar();
                array = new byte[arquivo.readInt()];
                arquivo.read(array);
                if (lapide != '*') {
                    Conta conta = new Conta();
                    conta.decodificaByteArray(array);
                    content.append(conta);
                    // System.out.print(conta.idConta+",");
                }
            }
            arquivo.close();
            return content;
        } catch (Exception e) {
        }
        return content;
    }

    public static void writeAccount(Conta conta) {
        try {
            RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
            byte[] array;
            array = conta.converteContaEmByte();
            arquivo.seek(0);
            arquivo.writeInt(conta.idConta);
            arquivo.seek(arquivo.length());
            arquivo.writeChar(' ');
            arquivo.writeInt(array.length);
            arquivo.write(array);
            arquivo.close();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Erro ao criar conta!");
        }
    }

    public static int getLastId() {
        int id;
        File f = new File(nomeArquivo);
        if (f.exists() && !f.isDirectory()) {
            try {
                RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
                arquivo.seek(0);
                id = arquivo.readInt();
                arquivo.close();
                return ++id;
            } catch (Exception e) {
                System.out.println("Erro ao obter ultimo ID.");
            }
        }
        return 0;
    }

    public static ArrayList<Conta> listAccouts() {
        ArrayList<Conta> contas = new ArrayList<>();
        byte[] array;
        char lapide;
        try {
            RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
            arquivo.seek(4);
            System.out.println("Listando os ID's das contas no arquivo:");
            while (arquivo.getFilePointer() != -1) {
                lapide = arquivo.readChar();
                array = new byte[arquivo.readInt()];
                arquivo.read(array);
                if (lapide != '*') {
                    Conta conta = new Conta();
                    conta.decodificaByteArray(array);
                    contas.add(conta);
                    System.out.print(conta.idConta+",");
                }
            }
            arquivo.close();
        } catch (Exception e) {
        }

        return contas;
    }

    public static Conta readById(int id) {
        try {
            long pos;
            char lapide;
            int tamanho;
            byte[] array;
            Conta conta;
            RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
            arquivo.seek(4);
            while (arquivo.getFilePointer() != -1) {
                pos = arquivo.getFilePointer();
                lapide = arquivo.readChar();
                tamanho = arquivo.readInt();
                array = new byte[tamanho];
                arquivo.read(array);
                if (lapide != '*') {
                    conta = new Conta();
                    conta.decodificaByteArray(array);
                    if (conta.idConta == id) {
                        arquivo.seek(pos);
                        arquivo.close();
                        conta.senha = descriptografaSenha(conta.senha);
                        return conta;
                    }
                }
            }
            arquivo.close();
        } catch (Exception e) {
            System.out.println("Erro ao deletar");
        }
        return null;
    }
    

    public static boolean update(Conta conta) {
        try {
            char lapide;
            long posRegistro;
            int tamanhoReg;
            byte buffer[];
            byte novoRegBuf[];
            RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
            arquivo.seek(4);
            while (arquivo.getFilePointer() != -1) {
                posRegistro = arquivo.getFilePointer();
                lapide = arquivo.readChar();
                tamanhoReg = arquivo.readInt();
                buffer = new byte[tamanhoReg];
                arquivo.read(buffer);
                if (lapide != '*') {
                    Conta conta2 = new Conta();
                    conta2.decodificaByteArray(buffer);
                    if (conta.idConta == conta2.idConta) {
                        novoRegBuf = conta.converteContaEmByte();
                        if (novoRegBuf.length <= buffer.length) {
                            arquivo.seek(posRegistro + 6);
                            arquivo.write(novoRegBuf);
                            return true;
                        } else {
                            arquivo.seek(posRegistro);
                            arquivo.writeChar('*');
                            arquivo.seek(arquivo.length());
                            arquivo.writeChar(' ');
                            arquivo.write(novoRegBuf);
                            return true;
                        }
                    }
                }
                arquivo.close();               
            }
            arquivo.close();
        } catch (Exception e) {
            System.out.println("Erro desgraca!");
        }
        return false;

    }

    public static boolean delete(int id) {
        try {
            long pos;
            char lapide;
            int tamanho;
            byte[] array;
            Conta conta;
            RandomAccessFile arquivo = new RandomAccessFile(nomeArquivo, "rw");
            arquivo.seek(4);
            while (arquivo.getFilePointer() != -1) {
                pos = arquivo.getFilePointer();
                lapide = arquivo.readChar();
                tamanho = arquivo.readInt();
                array = new byte[tamanho];
                arquivo.read(array);
                if (lapide != '*') {
                    conta = new Conta();
                    conta.decodificaByteArray(array);
                    if (conta.idConta == id) {
                        arquivo.seek(pos);
                        arquivo.writeChar('*');
                        arquivo.close();
                        return true;
                    }
                }
            }
            arquivo.close();
            return false;
        } catch (Exception e) {
            System.out.println("Erro ao deletar");
            return false;
        }
    }

    public static boolean transferencia(Conta contaOrigem, Conta contaDestino, Float valor) {
        contaOrigem.transferenciasRealizadas++;
        contaOrigem.saldoConta = contaDestino.saldoConta - valor;
        contaDestino.transferenciasRealizadas++;
        contaDestino.saldoConta = contaDestino.saldoConta + valor;
        return update(contaOrigem) && update(contaDestino);
    }

    public static ArrayList<Conta> createRandomAccounts(int totalContas) {
        ArrayList<Integer> idsRandom = new ArrayList<>();
        ArrayList<Conta> contas = new ArrayList<>();

        for (int i = 0; i < totalContas; i++) {
            idsRandom.add(i);
        }
        Collections.shuffle(idsRandom);

        for (int i = 0; i < totalContas; i++) {
            Conta conta = new Conta(i, "andrei"+i, "mail"+i, "nomeUser"+i, "senha"+i, "123"+i, "bh"+i, 0, 2f);
            contas.add(conta);
        }
        return contas;
    }

    public static Conta createAccount2() {

        Conta conta =
                new Conta(1, "andreiCaralho", "mail", "nomeUser", criptografaSenha("senha"), "123", "ita", 0, 2f);
        return conta;
    }


    public static String criptografaSenha(String str) {
        int aux = 0;
        String aux2 = "";
        for (int i = 0; i < str.length(); i++) {
            aux = (int) str.charAt(i);// converte cada char da string em int
            aux += cesarKey; // Aumenta 3 do char convertido em inteiro
            char b = (char) aux;// converte o int incrementado em char.
            aux2 += String.valueOf(b);// armazena o resultado na variavel aux2
        }
        return aux2;
    }
    public static String descriptografaSenha(String str){
        int aux = 0;
        String aux2 = "";
        for (int i = 0; i < str.length(); i++) {
            aux = (int) str.charAt(i);// converte cada char da string em int
            aux -= cesarKey; // Aumenta 3 do char convertido em inteiro
            char b = (char) aux;// converte o int incrementado em char.
            aux2 += String.valueOf(b);// armazena o resultado na variavel aux2
        }
        return aux2;
    }








}
