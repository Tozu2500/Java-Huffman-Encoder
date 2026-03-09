package huffman.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/* 
  Binary .huff format:
  - Header: "HUFF"
  - int: number of code table entries
  - for each: char (2 bytes) + int codeLen + bits packed as bytes
  - int: total bit count of payload
  - byte[]: packed bit payload
*/
public class HuffmanSerializer {

    private static final String MAGIC = "HUFF";

    public static void save(File f, CompressionResult r) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)))) {
            dos.writeBytes(MAGIC);
            dos.writeInt(r.codes.size());
            for (var e : r.codes.entrySet()) {
                dos.writeChar(e.getKey());
                dos.writeUTF(e.getValue());
            }

            String bits = r.encodedBits;
            dos.writeInt(bits.length());

            // Pack bits to bytes
            int byteLen = (bits.length() + 7) / 8;
            byte[] packed = new byte[byteLen];
            for (int i = 0; i < bits.length(); i++) {
                if (bits.charAt(i) == '1') {
                    packed[i / 8] |= (byte)(0x80 >> (i % 8));
                }
            }
            dos.write(packed);
        }
    }

    public static record LoadResult(Map<Character, String> codes, String bits) {}

    public static LoadResult load(File f) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            byte[] magic = new byte[4];
            dis.readFully(magic);

            if (!new String(magic).equals(MAGIC)) throw new IOException("Not a valid .huff file");
            int codeCount = dis.readInt();

            Map<Character, String> codes = new LinkedHashMap<>();
            for (int i = 0; i < codeCount; i++) {
                char ch = dis.readChar();
                String code = dis.readUTF();
                codes.put(ch, code);
            }

            int bitLen = dis.readInt();
            int byteLen = (bitLen + 7) / 8;
            byte[] packed = new byte[byteLen];
            dis.readFully(packed);
            
            StringBuilder bits = new StringBuilder(bitLen);
            
            for (int i = 0; i < bitLen; i++) {
                bits.append(((packed[i / 8] >> (7 - i % 8)) & 1) == 1 ? '1' : '0');
            }

            return new LoadResult(codes, bits.toString());
        }
    }
}
