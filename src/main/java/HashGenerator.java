
    import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

    public class HashGenerator {
        public static void main(String[] args) {
            if (args.length != 2) {
                System.out.println("Usage: java -jar test.jar <roll_number> <json_file_path>");
                return;
            }

            String rollNumber = args[0].toLowerCase().replaceAll("\\s+", "");
            String filePath = args[1];

            try {
                // Parse JSON file
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(new File(filePath));

                // Get the "destination" value
                String destinationValue = findDestination(rootNode);
                if (destinationValue == null) {
                    System.out.println("Key 'destination' not found in the JSON file.");
                    return;
                }

                // Generate random string
                String randomString = generateRandomString(8);

                // Concatenate rollNumber, destinationValue, and randomString
                String dataToHash = rollNumber + destinationValue + randomString;

                // Generate MD5 hash
                String hash = generateMD5Hash(dataToHash);

                // Output result
                System.out.println(hash + ";" + randomString);
            } catch (IOException e) {
                System.err.println("Error reading the JSON file: " + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Error generating MD5 hash: " + e.getMessage());
            }
        }

        private static String findDestination(JsonNode node) {
            if (node.isObject()) {
                for (var entry : node.fields()) {
                    if (entry.getKey().equals("destination")) {
                        return entry.getValue().asText();
                    }
                    String value = findDestination(entry.getValue());
                    if (value != null) {
                        return value;
                    }
                }
            } else if (node.isArray()) {
                for (JsonNode child : node) {
                    String value = findDestination(child);
                    if (value != null) {
                        return value;
                    }
                }
            }
            return null;
        }

        private static String generateRandomString(int length) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(characters.charAt(random.nextInt(characters.length())));
            }
            return sb.toString();
        }

        private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
    }


