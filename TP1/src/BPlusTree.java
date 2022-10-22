import java.io.RandomAccessFile;
import java.util.*;

public class BPlusTree {
  int m;
  nodeInterno raiz;
  nodeFolha firstLeaf;


  private int binarySearch(DictionaryPair[] dps, int numPairs, int t) {
    Comparator<DictionaryPair> c = new Comparator<DictionaryPair>() {
      @Override
      public int compare(DictionaryPair o1, DictionaryPair o2) {
        Integer a = Integer.valueOf(o1.key);
        Integer b = Integer.valueOf(o2.key);
        return a.compareTo(b);
      }
    };
    return Arrays.binarySearch(dps, 0, numPairs, new DictionaryPair(t, 0), c);
  }


  private nodeFolha findnodeFolha(int key) {

    Integer[] keys = this.raiz.keys;
    int i;

    for (i = 0; i < this.raiz.degree - 1; i++) {
      if (key < keys[i]) {
        break;
      }
    }

    Node child = this.raiz.childPointers[i];
    if (child instanceof nodeFolha) {
      return (nodeFolha) child;
    } else {
      return findnodeFolha((nodeInterno) child, key);
    }
  }


  private nodeFolha findnodeFolha(nodeInterno node, int key) {

    Integer[] keys = node.keys;
    int i;

    for (i = 0; i < node.degree - 1; i++) {
      if (key < keys[i]) {
        break;
      }
    }
    Node childNode = node.childPointers[i];
    if (childNode instanceof nodeFolha) {
      return (nodeFolha) childNode;
    } else {
      return findnodeFolha((nodeInterno) node.childPointers[i], key);
    }
  }

  private int getMidpoint() {
    return (int) Math.ceil((this.m + 1) / 2.0) - 1;
  }

  private boolean isEmpty() {
    return firstLeaf == null;
  }

  private int linearNullSearch(DictionaryPair[] dps) {
    for (int i = 0; i < dps.length; i++) {
      if (dps[i] == null) {
        return i;
      }
    }
    return -1;
  }

  private int linearNullSearch(Node[] pointers) {
    for (int i = 0; i < pointers.length; i++) {
      if (pointers[i] == null) {
        return i;
      }
    }
    return -1;
  }


  private void sortDictionary(DictionaryPair[] dictionary) {
    Arrays.sort(dictionary, new Comparator<DictionaryPair>() {
      @Override
      public int compare(DictionaryPair o1, DictionaryPair o2) {
        if (o1 == null && o2 == null) {
          return 0;
        }
        if (o1 == null) {
          return 1;
        }
        if (o2 == null) {
          return -1;
        }
        return o1.compareTo(o2);
      }
    });
  }

  private Node[] splitChildPointers(nodeInterno in, int split) {

    Node[] pointers = in.childPointers;
    Node[] halfPointers = new Node[this.m + 1];

    for (int i = split + 1; i < pointers.length; i++) {
      halfPointers[i - split - 1] = pointers[i];
      in.removePointer(i);
    }

    return halfPointers;
  }

  private DictionaryPair[] splitDictionary(nodeFolha ln, int split) {

    DictionaryPair[] dictionary = ln.dictionary;

    DictionaryPair[] halfDict = new DictionaryPair[this.m];

    for (int i = split; i < dictionary.length; i++) {
      halfDict[i - split] = dictionary[i];
      ln.delete(i);
    }

    return halfDict;
  }

  private void splitnodeInterno(nodeInterno in) {

    nodeInterno parent = in.parent;

    int midpoint = getMidpoint();
    int newParentKey = in.keys[midpoint];
    Integer[] halfKeys = splitKeys(in.keys, midpoint);
    Node[] halfPointers = splitChildPointers(in, midpoint);

    in.degree = linearNullSearch(in.childPointers);

    nodeInterno sibling = new nodeInterno(this.m, halfKeys, halfPointers);
    for (Node pointer : halfPointers) {
      if (pointer != null) {
        pointer.parent = sibling;
      }
    }

    sibling.dir = in.dir;
    if (sibling.dir != null) {
      sibling.dir.esq = sibling;
    }
    in.dir = sibling;
    sibling.esq = in;

    if (parent == null) {

      Integer[] keys = new Integer[this.m];
      keys[0] = newParentKey;
      nodeInterno newraiz = new nodeInterno(this.m, keys);
      newraiz.appendChildPointer(in);
      newraiz.appendChildPointer(sibling);
      this.raiz = newraiz;

      in.parent = newraiz;
      sibling.parent = newraiz;

    } else {

      parent.keys[parent.degree - 1] = newParentKey;
      Arrays.sort(parent.keys, 0, parent.degree);

      int pointerIndex = parent.findIndexOfPointer(in) + 1;
      parent.insertChildPointer(sibling, pointerIndex);
      sibling.parent = parent;
    }
  }

  private Integer[] splitKeys(Integer[] keys, int split) {

    Integer[] halfKeys = new Integer[this.m];

    keys[split] = null;

    for (int i = split + 1; i < keys.length; i++) {
      halfKeys[i - split - 1] = keys[i];
      keys[i] = null;
    }

    return halfKeys;
  }

  public void insert(int key, double value) {
    if (isEmpty()) {
      // System.out.println(key);
      // System.out.println(value);

      nodeFolha ln = new nodeFolha(this.m, new DictionaryPair(key, value));

      this.firstLeaf = ln;

    } else {
      // System.out.println(key);
      // System.out.println(value);
      nodeFolha ln = (this.raiz == null) ? this.firstLeaf : findnodeFolha(key);

      if (!ln.insert(new DictionaryPair(key, value))) {

        ln.dictionary[ln.numPairs] = new DictionaryPair(key, value);
        ln.numPairs++;
        sortDictionary(ln.dictionary);

        int midpoint = getMidpoint();
        DictionaryPair[] halfDict = splitDictionary(ln, midpoint);

        if (ln.parent == null) {

          Integer[] parent_keys = new Integer[this.m];
          parent_keys[0] = halfDict[0].key;
          nodeInterno parent = new nodeInterno(this.m, parent_keys);
          ln.parent = parent;
          parent.appendChildPointer(ln);

        } else {
          int newParentKey = halfDict[0].key;
          ln.parent.keys[ln.parent.degree - 1] = newParentKey;
          Arrays.sort(ln.parent.keys, 0, ln.parent.degree);
        }

        nodeFolha newnodeFolha = new nodeFolha(this.m, halfDict, ln.parent);

        int pointerIndex = ln.parent.findIndexOfPointer(ln) + 1;
        ln.parent.insertChildPointer(newnodeFolha, pointerIndex);

        newnodeFolha.dir = ln.dir;
        if (newnodeFolha.dir != null) {
          newnodeFolha.dir.esq = newnodeFolha;
        }
        ln.dir = newnodeFolha;
        newnodeFolha.esq = ln;

        if (this.raiz == null) {

          this.raiz = ln.parent;

        } else {
          nodeInterno in = ln.parent;
          while (in != null) {
            if (in.isOverfull()) {
              splitnodeInterno(in);
            } else {
              break;
            }
            in = in.parent;
          }
        }
      }
    }
  }

  public Double search(int key) {

    if (isEmpty()) {
      return null;
    }

    nodeFolha ln = (this.raiz == null) ? this.firstLeaf : findnodeFolha(key);

    DictionaryPair[] dps = ln.dictionary;
    int index = binarySearch(dps, ln.numPairs, key);

    if (index < 0) {
      return null;
    } else {
      return dps[index].value;
    }
  }

  public ArrayList<Double> search(int lowerBound, int upperBound) {

    ArrayList<Double> values = new ArrayList<Double>();

    nodeFolha currNode = this.firstLeaf;
    while (currNode != null) {

      DictionaryPair dps[] = currNode.dictionary;
      for (DictionaryPair dp : dps) {

        if (dp == null) {
          break;
        }

        if (lowerBound <= dp.key && dp.key <= upperBound) {
          values.add(dp.value);
        }
      }
      currNode = currNode.dir;

    }

    return values;
  }

  public BPlusTree(int m) {
    this.m = m;
    this.raiz = null;
  }

  public class Node {
    nodeInterno parent;
  }

  private class nodeInterno extends Node {
    int maxDegree;
    int minDegree;
    int degree;
    nodeInterno esq;
    nodeInterno dir;
    Integer[] keys;
    Node[] childPointers;

    private void appendChildPointer(Node pointer) {
      this.childPointers[degree] = pointer;
      this.degree++;
    }

    private int findIndexOfPointer(Node pointer) {
      for (int i = 0; i < childPointers.length; i++) {
        if (childPointers[i] == pointer) {
          return i;
        }
      }
      return -1;
    }

    private void insertChildPointer(Node pointer, int index) {
      for (int i = degree - 1; i >= index; i--) {
        childPointers[i + 1] = childPointers[i];
      }
      this.childPointers[index] = pointer;
      this.degree++;
    }


    private boolean isOverfull() {
      return this.degree == maxDegree + 1;
    }


    private void removePointer(int index) {
      this.childPointers[index] = null;
      this.degree--;
    }


    private nodeInterno(int m, Integer[] keys) {
      this.maxDegree = m;
      this.minDegree = (int) Math.ceil(m / 2.0);
      this.degree = 0;
      this.keys = keys;
      this.childPointers = new Node[this.maxDegree + 1];
    }

    private nodeInterno(int m, Integer[] keys, Node[] pointers) {
      this.maxDegree = m;
      this.minDegree = (int) Math.ceil(m / 2.0);
      this.degree = linearNullSearch(pointers);
      this.keys = keys;
      this.childPointers = pointers;
    }
  }

  public class nodeFolha extends Node {
    int maxNumPairs;
    int minNumPairs;
    int numPairs;
    nodeFolha esq;
    nodeFolha dir;
    DictionaryPair[] dictionary;

    public void delete(int index) {
      this.dictionary[index] = null;
      numPairs--;
    }

    public boolean insert(DictionaryPair dp) {
      if (this.isFull()) {
        return false;
      } else {
        this.dictionary[numPairs] = dp;
        numPairs++;
        Arrays.sort(this.dictionary, 0, numPairs);

        return true;
      }
    }

    public boolean isDeficient() {
      return numPairs < minNumPairs;
    }

    public boolean isFull() {
      return numPairs == maxNumPairs;
    }

    public boolean isLendable() {
      return numPairs > minNumPairs;
    }

    public boolean isMergeable() {
      return numPairs == minNumPairs;
    }

    public nodeFolha(int m, DictionaryPair dp) {
      this.maxNumPairs = m - 1;
      this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
      this.dictionary = new DictionaryPair[m];
      this.numPairs = 0;
      this.insert(dp);
    }

    public nodeFolha(int m, DictionaryPair[] dps, nodeInterno parent) {
      this.maxNumPairs = m - 1;
      this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
      this.dictionary = dps;
      this.numPairs = linearNullSearch(dps);
      this.parent = parent;
    }
  }
  public class DictionaryPair implements Comparable<DictionaryPair> {
    int key;
    double value;

    public DictionaryPair(int key, double value) {
      this.key = key;
      this.value = value;
    }

    public int compareTo(DictionaryPair o) {
      if (key == o.key) {
        return 0;
      } else if (key > o.key) {
        return 1;
      } else {
        return -1;
      }
    }
  }

  public void criaArvore() {
    try {
      // Loopa pelo arquivo de dados inserindo sua chave/valor na árvore:
      RandomAccessFile raf = new RandomAccessFile("output/conta.db", "rw");
      raf.seek(4);
      long pos;
      char lapide;
      int tamanho;
      byte[] ba;
      while (raf.getFilePointer() != -1) {
        pos = raf.getFilePointer();
        lapide = raf.readChar();
        tamanho = raf.readInt();
        ba = new byte[tamanho];
        raf.read(ba);
        if (lapide != '*') {
          Conta conta = new Conta();
          conta.decodificaByteArray(ba);
          System.out.println(
              "Inserindo conta de Id: " + conta.idConta + ", e sua posicao no arquivo eh: " + pos);
          this.insert(conta.idConta, pos);
        }
      }
      raf.close();
    } catch (Exception e) {
    }

  }

  public Conta buscaIndexada(int chave) {
    return buscaIndexada(search(chave));
  }

  public Conta buscaIndexada(double posRegistro) {
    try {
      RandomAccessFile raf = new RandomAccessFile("output/conta.db", "rw");
      long pos = Double.valueOf(posRegistro).longValue();
      char lapide;
      int tamanho;
      byte[] ba;
      raf.seek(pos);
      lapide = raf.readChar();
      tamanho = raf.readInt();
      ba = new byte[tamanho];
      raf.read(ba);
      if (lapide != '*') {
        Conta conta = new Conta();
        conta.decodificaByteArray(ba);
        raf.close();
        System.out.println("Conta encontrada:");
        System.out.println(conta.toString());
        raf.close();
        return conta;
      }

    } catch (Exception e) {
      System.out.println("Erro ao abrir o arquivo");
    }
    return null;
  }
}
