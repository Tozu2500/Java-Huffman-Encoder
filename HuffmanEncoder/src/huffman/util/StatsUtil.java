package huffman.util;

import huffman.core.CompressionResult;

public class StatsUtil {

    public static String formatBits(long bits) {
        if (bits < 1024) return bits + " bits";
        if (bits < 1024 * 1024) return String.format("%.2f Kb", bits / 1024.0);
        return String.format("%.2f Mb", bits / (1024.0 * 1024));
    }

    public static String summaryReport(CompressionResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Huffman Compression Summary\n");
        sb.append(String.format("Input characters       : %,d%n", r.originalText.length()));
        sb.append(String.format("Unique symbols         : %d%n", r.frequencies.size()));
        sb.append(String.format("Shannon entropy        : %.4f bits/symbol%n", r.entropy));
        sb.append(String.format("Avg code length        : %.4f bits/symbol%n", r.avgCodeLength));
        sb.append(String.format("Original size          : %s%n", formatBits(r.originalBits)));
        sb.append(String.format("Compressed size        : %s%n", formatBits(r.compressedBits)));
        sb.append(String.format("Bits saved             : %s%n", formatBits(r.bitsSaved)));
        sb.append(String.format("Compression ratio      : %.2f%%%n", r.compressionRatio * 100));
        sb.append(String.format("Space savings          : %.2f%%%n", r.spaceSavingsPercent()));
        return sb.toString();
    }
}
