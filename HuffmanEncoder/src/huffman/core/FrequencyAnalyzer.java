package huffman.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FrequencyAnalyzer {

    // Returns character -> count map, sorted by frequency descending
    public static Map<Character, Integer> analyze(String text) {
        Map<Character, Integer> map = new LinkedHashMap<>();
        for (char c : text.toCharArray()) 
            map.merge(c, 1, Integer::sum);

        // Sort by value descending
        List<Map.Entry<Character, Integer>> entries = new ArrayList<>(map.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
        Map<Character, Integer> sorted = new LinkedHashMap<>();
        entries.forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }

    // Shannon entropy
    public static double entropy(Map<Character, Integer> freq) {
        int total = freq.values().stream().mapToInt(Integer::intValue).sum();
        double h = 0;
        for (int f : freq.values()) {
            double p = (double) f / total;
            if (p > 0) h -= p * (Math.log(p) / Math.log(2));
        }
        return h;
    }
}
