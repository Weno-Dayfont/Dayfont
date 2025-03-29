import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class dfb {
    private static final String MAGIC = "DFBFILE";
    private static final int VERSION = 0x0100; // Version 1.0

    public static void saveBrush(String absolutePath, String brushShape, int brushWidth, int brushHeight) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(absolutePath))) {
            dos.writeBytes(MAGIC); // Magic number
            dos.writeShort(1); // Version
            dos.writeShort(brushShape.length());
            dos.writeBytes(brushShape);
            dos.writeInt(brushWidth);
            dos.writeInt(brushHeight);
            dos.writeByte(0); // Flags
            byte[] bitmapData = new byte[brushWidth * brushHeight];
            dos.writeInt(bitmapData.length);
            dos.write(bitmapData);
        }
    }


    public static Brush loadBrush(String filename) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            byte[] magicBytes = new byte[7];
            dis.readFully(magicBytes);
            String magic = new String(magicBytes);
            if (!magic.equals(MAGIC)) {
                throw new IOException("Invalid file format");
            }

            int version = dis.readUnsignedShort();
            int nameLength = dis.readUnsignedShort();
            byte[] nameBytes = new byte[nameLength];
            dis.readFully(nameBytes);
            String name = new String(nameBytes);
            int width = dis.readInt();
            int height = dis.readInt();
            int flags = dis.readUnsignedByte();
            int dataSize = dis.readInt();
            byte[] bitmapData = new byte[dataSize];
            dis.readFully(bitmapData);

            return new Brush(version, name, width, height, flags, bitmapData);
        }
    }

    public static class Brush {
        public int version;
        public String name;
        public int width;
        public int height;
        public int flags;
        public byte[] bitmapData;

        public Brush(int version, String name, int width, int height, int flags, byte[] bitmapData) {
            this.version = version;
            this.name = name;
            this.width = width;
            this.height = height;
            this.flags = flags;
            this.bitmapData = bitmapData;
        }
    }

    public static void main(String[] args) throws IOException {
        String brushName = "Soft Round";
        int brushWidth = 64;
        int brushHeight = 64;
        byte[] dummyBitmap = new byte[brushWidth * brushHeight];
        for (int i = 0; i < dummyBitmap.length; i++) dummyBitmap[i] = (byte) 128;

        saveBrush("example.dfb", brushName, brushWidth, brushHeight, dummyBitmap, 0);
        Brush brush = loadBrush("example.dfb");
        System.out.println("Loaded brush: " + brush.name + " (" + brush.width + "x" + brush.height + ")");
    }

    private static void saveBrush(String s, String brushName, int brushWidth, int brushHeight, byte[] dummyBitmap, int i) {
    }
}
