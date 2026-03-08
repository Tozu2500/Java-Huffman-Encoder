package huffman.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {

    private HuffmanNode root;
    private final Map<Character, String> codes = new LinkedHashMap<>();

    public HuffmanTree(Map<Character, Integer> freq) {
        if (freq.isEmpty()) return;
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        freq.forEach((ch, f) -> pq.add(new HuffmanNode(ch, f)));

        // Handle single-char edge case
        if (pq.size() == 1) {
            HuffmanNode only = pq.poll();
            root = new HuffmanNode(only.frequency, only, null);
        } else {
            while (pq.size() > 1) {
                HuffmanNode l = pq.poll(), r = pq.poll();
                pq.add(new HuffmanNode(l.frequency + r.frequency, l, r));
            }
            root = pq.poll();
        }
        buildCodes(root, "");
    }

    private void buildCodes(HuffmanNode node, String prefix) {
        if (node == null) return;
        if (node.isLeaf()) {
            codes.put(node.ch, prefix.isEmpty() ? "0" : prefix);
            return;
        }
        buildCodes(node.left, prefix + "0");
        buildCodes(node.right, prefix + "1");
    }

    public HuffmanNode getRoot() {
        return root;
    }

    public Map<Character, String> getCodes() {
        return Collections.unmodifiableMap(codes);
    }

    // Average code length
    public double averageCodeLength(Map<Character, Integer> freq) {
        int total = freq.values().stream().mapToInt(Integer::intValue).sum();
        double sum = 0;
        for (var e : codes.entrySet()) {
            int f = freq.getOrDefault(e.getKey(), 0);
            sum += (double) f / total * e.getValue().length();
        }
        return sum;
    }
}
