package huffman.core;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

}
