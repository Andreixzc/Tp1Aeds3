import java.io.IOException;
import java.io.RandomAccessFile;

public class CustomFile {
    public RandomAccessFile file;
    public int size;//Controla a quantidade de registros que foram escritos nesse arquivo.
    public int readRegisterSize;//Variavel que serve pra controlar o numero de registros lidos, pra não ultrapassar o limite da ram.
    public String fileName;//Nome do arquivo

    public CustomFile(String path) {
        try {
            fileName = path;
            file = new RandomAccessFile(path, "rw");
            size = 0;
            readRegisterSize = 0;
        } catch (Exception e) {
            System.out.println("Erro ao abrir arquivo " + path);
        }
    }

    public Conta readNext() {
        byte[] ba;
        try {
            if (file.getFilePointer() < file.length()) { //
                file.readChar(); // lê a lapide
                ba = new byte[file.readInt()]; // lê o tamanho
                file.read(ba);
                readRegisterSize++;
                return Conta.fromByteArray(ba);
            }
        } catch (Exception e) {
            System.out.println("Erro readNext. " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void writeConta(Conta conta) throws IOException {
        byte[] ba = conta.converteContaEmByte();
        file.writeChar(' ');
        file.writeInt(ba.length);
        file.write(ba);
        size++;
    }
}
