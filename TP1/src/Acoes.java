import java.util.ArrayList;
import java.util.Scanner;
public class Acoes {
    public Acoes() {};
    Crud crud = new Crud();
    public static void criaContaModel(Scanner scanner) {
        Crud.writeAccount(Crud.geradorDeConta(scanner));
    }

    public static void realizaTranseferencia(Scanner Scanner) {
        Conta origem;
        Conta destino;
        float valor;
        System.out.println("Digite o ID da conta origem:");
        origem = Crud.readById(Integer.parseInt(Scanner.next()));
        System.out.println("Digite o ID da conta destino:");
        destino = Crud.readById(Integer.parseInt(Scanner.next()));
        System.out.println("Digite o valor a ser transferido:");
        valor = Float.parseFloat(Scanner.nextLine());
        if (Crud.transferencia(origem, destino, valor)) {
            System.out.println("Transferencia realizada com sucesso!");
        } else {
            System.out.println("Erro na transferencia");
        }
    }

    public static void lerPorId(Scanner Scanner) {
        System.out.println("Digite o ID da conta a ser lida:");
        Conta conta = Crud.readById(Scanner.nextInt());
        // Conta conta = Crud.readById(Integer.parseInt(Scanner.nextLine()));
        if (conta!=null) {
            System.out.println(conta.toString());
        } else {
            System.out.println("Conta não encontrada!");
        }
    }

    public static void atualizarRegistro(Scanner Scanner) {
        Conta conta = Crud.geradorDeContaUpdate(Scanner);
        if (Crud.update(conta)) {
            System.out.println("Conta atualizada com sucesso");
        } else {
            System.out.println("Erro ao atualizar!");
        }
        
    }

    public static void deletarRegistro(Scanner Scanner) {
        int id = Scanner.nextInt();
        if (Crud.delete(id)) {
            System.out.println("Conta deletada com sucesso");
        } else {
            System.out.println("Erro ao deletar conta");
        }
    }
    public static void criaContasRandom(Scanner scanner) {
        scanner.nextLine();
        System.out.println("Digite o numero de contas a ser criada:");
        int numeroDeContas = Integer.parseInt(scanner.nextLine());
        ArrayList<Conta> contas = new ArrayList<>();
        contas = Crud.createRandomAccounts(numeroDeContas);
        for (Conta conta : contas) {
            Crud.writeAccount(conta);
        }
    }

}
