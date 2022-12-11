import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

// classe No

class No {
    Character a;
    Integer frequencia;

    No esq = null;
    No dir = null;

    // Construtor
    No(Character a, Integer frequencia) {
        this.a = a;
        this.frequencia = frequencia;
    }

    // Construtor
    public No(Character a, Integer frequencia, No esq, No dir) {
        this.a = a;
        this.frequencia = frequencia;
        this.esq = esq;
        this.dir = dir;
    }
}


// Classe tempo
class Tempo {
    // Atributos
    private double time;

    // Metodos especiais
    public Tempo() {
        this.time = 0.0;
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    // Metodos
    public void start() {
        time = new Date().getTime();
    }

    public void stop() {
        time = ((new Date().getTime()) - time) / 1000;
    }
}


// Classe Huffman
public class Huffman {
    static double decodeTime = 0;
    static final String huffmanPath = "arquivosComprimidos/Contahuffman.txt";
    // Criando arvore Huffman
    public static void criarArvoreHuffman(String texto) {
        // Caso nao haja string
        if (texto == null || texto.length() == 0) {
            return;
        }

        // Verificar frequencia de ce cada caractere e gravar isso em um mapa
        // Criando o mapa
        Map<Character, Integer> frequencia = new HashMap<>();
        // Loop interage com a String e converte o texto em um array de caracteres
        for (char c : texto.toCharArray()) {
            // Salvando o caractere e sua frequencia no mapa atravehs do mehtodo put()
            frequencia.put(c, frequencia.getOrDefault(c, 0) + 1);
        }
        // Cria uma fila de prioridade que guarda os nos da arvore huffman
        // A maior prioridade significa a menor frequencia
        PriorityQueue<No> pq = new PriorityQueue<>(Comparator.comparingInt(l -> l.frequencia));
        // loop no mapa que retorna um set dos mapas contidos nesse mapa
        // loop iterate over the Map and returns a Set view of the mappings contained in this Map
        for (var entry : frequencia.entrySet()) {
            // Cria um no folha e adiciona a fila
            pq.add(new No(entry.getKey(), entry.getValue()));
        }
        // loop enquanto tiver mais de um no na fila
        while (pq.size() != 1) {
            // Removendo os nos que tem maior prioridade, ou seja menor frequencia, da fila
            No esq = pq.poll();
            No dir = pq.poll();
            // Cria um novo no interno com esses 2 nos como filho e com uma frequencia igual ao
            // filho dos dois nos. Adiciona o novo no a fila de prioridade
            // Somamos a frequencia dos nos, direito e esquerda, que deletamos
            int sum = esq.frequencia + dir.frequencia;
            // adicionar um novo no interno na fila de frequencia que eh igual a soma dos dois nos
            // deletados, dir e esq
            pq.add(new No(null, sum, esq, dir));
        }
        // Raiz guarda ponteiro para a raiz da arvore huffman
        No raiz = pq.peek();
        // Passar pela arvore huffman e guardar o codigo huffman em um mapa
        Map<Character, String> huffmanCode = new HashMap<>();

        // iniciar o temporizador
        Tempo time = new Tempo();
        // Iniciar o relogio
        time.start();

        codificarDado(raiz, "", huffmanCode);

        time.stop();
        System.out.println("O tempo para codificar foi de: " + time.getTime());
        decodeTime = time.getTime();

        // Imprimir os codigos dos caracteres
        System.out.println("Codigos Huffman dos caracteres sao: " + huffmanCode);


        // Imprimir arquivo original
        System.out.println("A string inicial eh: " + texto);

        // Criando uma StringBuilder
        StringBuilder sb = new StringBuilder();

        // loop no array de caracteres
        for (char c : texto.toCharArray()) {

            // imrpimir string codificada pelos caracteres
            sb.append(huffmanCode.get(c));
        }
        System.out.println("A string codificada eh: " + sb);


        //Escrevendo os dados em um arquivo comprimido:
        writeToFile(sb,huffmanPath);




        //Extraindo os dados do arquivo codificado para decoficar:
        StringBuilder sb2 = extraiTexto(huffmanPath);


        Tempo time2 = new Tempo();
        time2.start();
        contentDecoded(raiz, sb, sb2);
        time2.stop();
        decodeTime = time2.getTime();

    }
    public static void contentDecoded(No raiz, StringBuilder sb, StringBuilder sb2){
        System.out.print("A string descodificada eh: ");
         if (eFolha(raiz)) {
            // caso especial para entradas como a, aa, aaa, etc.
            while (raiz.frequencia-- > 0) {
                System.out.print(raiz.a);
            }
        } else {
            // Novamente na arvore so que dessa vez descodificar a string codificada
            int index = -1;
            while (index < sb.length() - 1) {
                index = descodificarDado(raiz, index, sb2);
            }
        }
    }

    public static void codificarDado(No raiz, String str, Map<Character, String> huffmanCode) {
        if (raiz == null) {
            return;
        }
        // verificar se o no eh uma folha
        if (eFolha(raiz)) {
            huffmanCode.put(raiz.a, str.length() > 0 ? str : "1");
        }
        codificarDado(raiz.esq, str + '0', huffmanCode);
        codificarDado(raiz.dir, str + '1', huffmanCode);

    }

    // Passar pela arvore huffman e descodificar a funcao string codificada que descodifica o dado
    // codificado
    public static int descodificarDado(No raiz, int index, StringBuilder sb) {
        if (raiz == null) {
            return index;
        }
        if (eFolha(raiz)) {
            System.out.print(raiz.a);
            return index;
        }
        index++;
        raiz = (sb.charAt(index) == '0') ? raiz.esq : raiz.dir;
        index = descodificarDado(raiz, index, sb);


        return index;

    }

    public static boolean eFolha(No raiz) {
        return raiz.esq == null && raiz.dir == null;
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
    public static StringBuilder extraiTexto(String path){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
            return stringBuilder;
        } catch (Exception e) {
            System.out.println(e);
        }     
        return null;
    }

}
