package busbyteprotocol;

public class Checksum8 {

    public static int calculateChecksum(byte[] bytes) {
        int sum = 0;
        for (byte b : bytes) {
            sum += b & 0xFF; // Convert to unsigned value
        }
        return ~sum & 0xFF;
    }

}
