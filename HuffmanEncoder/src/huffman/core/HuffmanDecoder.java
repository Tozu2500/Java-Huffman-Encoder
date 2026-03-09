package huffman.core;

public class HuffmanDecoder {

    public static String decode(String bits, HuffmanNode root) {
        if (root == null || bits == null || bits.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        HuffmanNode cur = root;
        for (char b : bits.toCharArray()) {
            if (root.isLeaf()) { // Single char tree
                sb.append(root.ch);
                continue;
            }
            cur = (b == '0') ? cur.left : cur.right;
            if (cur == null) return "[DECODE ERROR]";
            if (cur.isLeaf()) {
                sb.append(cur.ch);
                cur = root;
            }
        }
        return sb.toString();
    }
}
