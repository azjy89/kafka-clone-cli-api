package tributary.util;

import java.io.IOException;
import java.io.InputStream;

public final class EventLoader {
    public static String loadEventFile(String fileName) throws IOException {
        String path = "testInputs/" + fileName + ".json";
        System.out.println("Loading resource from: " + path);
        InputStream resourceStream = EventLoader.class.getClassLoader().getResourceAsStream(path);
        if (resourceStream == null) {
            System.out.println("Resource not found: " + path);
            throw new IOException("Resource not found: " + path);
        }

        return new String(resourceStream.readAllBytes());
    }
}
