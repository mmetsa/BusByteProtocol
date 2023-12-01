package busbyteprotocol;

import java.nio.ByteBuffer;

public class BusByteHeader {
    private byte opcode;
    private long timestamp;
    private long id;
    private byte version;
    private byte sequence;
    private int checksum;


    public static final int HEADER_SIZE = 23;

    public BusByteHeader(byte opcode, long timestamp, long id, byte version, byte sequence) {
        this.opcode = opcode;
        this.timestamp = timestamp;
        this.id = id;
        this.version = version;
        this.sequence = sequence;
        this.checksum = calculateChecksum();
    }

    public BusByteHeader(byte opcode, long timestamp, long id, byte version, byte sequence, int checksum) {
        this.opcode = opcode;
        this.timestamp = timestamp;
        this.id = id;
        this.version = version;
        this.sequence = sequence;
        if (checksum != calculateChecksum()) {
            throw new IllegalArgumentException("Invalid checksum");
        }
        this.checksum = checksum;
    }

    public byte getOpcode() {
        return opcode;
    }

    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public byte getSequence() {
        return sequence;
    }

    public void setSequence(byte sequence) {
        this.sequence = sequence;
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(23);

        buffer.put(opcode);
        buffer.putLong(timestamp);
        buffer.putLong(id);
        buffer.put(version);
        buffer.put(sequence);
        buffer.putInt(Checksum8.calculateChecksum(buffer.array()));

        return buffer.array();
    }

    private int calculateChecksum() {
        byte[] headerBytes = toByteArrayWithoutChecksum();
        return Checksum8.calculateChecksum(headerBytes);
    }

    private byte[] toByteArrayWithoutChecksum() {
        ByteBuffer buffer = ByteBuffer.allocate(19);
        buffer.put(opcode);
        buffer.putLong(timestamp);
        buffer.putLong(id);
        buffer.put(version);
        buffer.put(sequence);
        return buffer.array();
    }

    public static BusByteHeader fromByteArray(byte[] headerBytes) {
        if (headerBytes.length != HEADER_SIZE) {
            throw new IllegalArgumentException("Invalid header byte array length");
        }

        ByteBuffer buffer = ByteBuffer.wrap(headerBytes);

        byte opcode = buffer.get();
        long timestamp = buffer.getLong();
        long id = buffer.getLong();
        byte version = buffer.get();
        byte sequence = buffer.get();
        int checksum = buffer.getInt();

        return new BusByteHeader(opcode, timestamp, id, version, sequence, checksum);
    }
}
