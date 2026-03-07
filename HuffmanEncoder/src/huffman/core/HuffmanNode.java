package huffman.core;

public class HuffmanNode implements Comparable<HuffmanNode> {

    public final char ch;
    public final int frequency;
    public HuffmanNode left, right;

    // Leaf node
    public HuffmanNode(char ch, int frequency) {
        this.ch = ch;
        this.frequency = frequency;
    }

    // Internal node
    public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
        this.ch = '\0';
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return Integer.compare(this.frequency, o.frequency);
    }
}
