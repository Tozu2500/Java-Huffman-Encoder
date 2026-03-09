package huffman.core;

import java.util.Map;

public class HuffmanEncoder {

    public static String encode(String text, Map<Character, String> codes) {
        StringBuilder sb = new StringBuilder(text.length() * 4);
        for (char c : text.toCharArray()) {
            String code = codes.get(c);
            if (code != null) sb.append(code);
        }
        return sb.toString();
    }

}
