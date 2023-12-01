package busbyteprotocol;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class BusByteMessage {

    private final BusByteHeader header;
    private final byte[] data;

    private static final byte DELIMITER = 0x7E;
    private static final byte ESCAPE = 0x7D;
    private static final byte ESCAPE_MASK = 0x20;

    // Constructor
    public BusByteMessage(BusByteHeader header, byte[] data) {
        this.header = header;
        this.data = data;
    }

    public byte[] toByteArray() {
        byte[] headerBytes = header.toByteArray();
        byte[] stuffedData = byteStuff(data);

        byte[] messageBytes = new byte[headerBytes.length + stuffedData.length + 1];
        System.arraycopy(headerBytes, 0, messageBytes, 0, headerBytes.length);

        System.arraycopy(stuffedData, 0, messageBytes, headerBytes.length, stuffedData.length);

        messageBytes[messageBytes.length - 1] = DELIMITER;

        return messageBytes;
    }

    private byte[] byteStuff(byte[] input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte b : input) {
            if (b == DELIMITER || b == ESCAPE) {
                output.write(ESCAPE);
                output.write((byte) (b ^ ESCAPE_MASK));
            } else {
                output.write(b);
            }
        }
        return output.toByteArray();
    }

    private static byte[] removeByteStuff(byte[] input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean isStuffed = false;
        for (byte b : input) {
            if (isStuffed) {
                output.write((byte) (b ^ ESCAPE_MASK));
                isStuffed = false;
            } else if (b == ESCAPE) {
                isStuffed = true;
            } else {
                output.write(b);
            }
        }
        return output.toByteArray();
    }

    public byte[] getMessageBytes() {
        byte[] messageBytes = new byte[data.length];

        // Copy data bytes to the message bytes after the header
        System.arraycopy(data, 0, messageBytes, 0, data.length);

        return messageBytes;
    }

    public static BusByteMessage parseByteArray(byte[] messageBytes) {
        int delimiterIndex = -1;
        for (int i = messageBytes.length - 1; i >= 0; i--) {
            if (messageBytes[i] == DELIMITER) {
                delimiterIndex = i;
                break;
            }
        }

        if (delimiterIndex == -1) {
            throw new IllegalArgumentException("Delimiter not found in the message");
        }

        byte[] headerBytes = Arrays.copyOfRange(messageBytes, 0, BusByteHeader.HEADER_SIZE);
        byte[] dataWithDelimiter = Arrays.copyOfRange(messageBytes, BusByteHeader.HEADER_SIZE, messageBytes.length);

        byte[] dataBytes = removeDelimiter(dataWithDelimiter);

        byte[] dataWithoutStuffing = removeByteStuff(dataBytes);

        // Parse the header bytes and create a BusByteHeader object
        BusByteHeader header = BusByteHeader.fromByteArray(headerBytes);

        return new BusByteMessage(header, dataWithoutStuffing);
    }

    private static byte[] removeDelimiter(byte[] input) {
        return Arrays.copyOf(input, input.length - 1);
    }
}
