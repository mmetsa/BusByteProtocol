package busbyteprotocol;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BusByteMessageTest {

    @Test
    void testToByteArray() {
        // Create a sample BusByteHeader
        BusByteHeader header = new BusByteHeader((byte) 1, 1234567890L, 9876543210L, (byte) 2, (byte) 3);

        // Sample data for testing
        byte[] testData = new byte[]{0x01, 0x02, 0x03}; // Example test data

        // Create a BusByteMessage using the header and test data
        BusByteMessage message = new BusByteMessage(header, testData);

        // Get the expected byte array by combining header bytes and data bytes (excluding the delimiter)
        byte[] expectedByteArray = new byte[header.toByteArray().length + testData.length];
        System.arraycopy(header.toByteArray(), 0, expectedByteArray, 0, header.toByteArray().length);
        System.arraycopy(testData, 0, expectedByteArray, header.toByteArray().length, testData.length);

        System.out.println(Arrays.toString(expectedByteArray));
        System.out.println(Arrays.toString(message.toByteArray()));
        // Validate that the generated byte array (excluding the delimiter) matches the expected byte array
        assertArrayEquals(expectedByteArray, Arrays.copyOf(message.toByteArray(), expectedByteArray.length));
    }

    @Test
    void testParseByteArray() {
        // Create a sample BusByteHeader
        BusByteHeader header = new BusByteHeader((byte) 1, 1234567890L, 9876543210L, (byte) 2, (byte) 3);

        // Sample data for testing
        byte[] testData = new byte[]{0x01, 0x02, 0x03}; // Example test data

        // Create a BusByteMessage using the header and test data
        BusByteMessage message = new BusByteMessage(header, testData);

        // Get the byte array from the message and parse it to reconstruct a new BusByteMessage
        BusByteMessage reconstructedMessage = BusByteMessage.parseByteArray(message.toByteArray());

        // Get the byte array from the reconstructed message
        byte[] reconstructedByteArray = reconstructedMessage.toByteArray();

        // Validate that the reconstructed message's byte array matches the original message's byte array
        assertArrayEquals(message.toByteArray(), reconstructedByteArray);
    }

    @Test
    void testByteStuffing() {
        // Define sample data with a byte sequence that requires byte stuffing
        byte[] originalData = new byte[]{0x01, 0x7E, 0x7D, 0x02};

        // Create a dummy header (for example purposes)
        BusByteHeader dummyHeader = new BusByteHeader((byte) 0, 0, 0, (byte) 0, (byte) 0);

        // Create a BusByteMessage with the sample data and the dummy header
        BusByteMessage message = new BusByteMessage(dummyHeader, originalData);

        // Get the byte-stuffed representation of the message
        byte[] stuffedData = message.toByteArray();

        // Parse the byte-stuffed data back into a BusByteMessage object
        BusByteMessage parsedMessage = BusByteMessage.parseByteArray(stuffedData);

        // Retrieve the data from the parsed message
        byte[] retrievedData = parsedMessage.getMessageBytes();

        // Validate that the retrieved data matches the original (un-stuffed) data
        assertArrayEquals(originalData, retrievedData);
    }
}
