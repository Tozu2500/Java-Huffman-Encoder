package huffman.core;

import java.util.Map;

public class CompressionResult {

    public final String originalText;
    public final String encodedBits;
    public final Map<Character, String> codes;
    public final Map<Character, Integer> frequencies;
    public final HuffmanNode treeRoot;
    public final double compressionRatio;
    public final double entropy;
    public final double avgCodeLength;
    public final long originalBits;
    public final long compressedBits;
    public final long bitsSaved;

    public CompressionResult(String originalText, String encodedBits,
                            Map<Character, String> codes, Map<Character, Integer> frequencies,
                            HuffmanNode treeRoot, double entropy, double avgCodeLength) {
        this.originalText = originalText;
        this.encodedBits = encodedBits;
        this.codes = codes;
        this.frequencies = frequencies;
        this.treeRoot = treeRoot;
        this.entropy = entropy;
        this.avgCodeLength = avgCodeLength;
        this.originalBits = (long) originalText.length() * 8;
        this.compressedBits = encodedBits.length();
        this.bitsSaved = originalBits - compressedBits;
        this.compressionRatio = originalBits == 0 ? 1.0 : (double) compressedBits / originalBits;
    }

    public double spaceSavingsPercent() {
        if (originalBits == 0) return 0;
        return (1.0 - compressionRatio) * 100.0;
    }
}
