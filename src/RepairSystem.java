import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepairSystem {

    // Method to ensure both layer1.txt and layer2.txt have equal rows and columns
    public static void synchronizeLayers(String filePath1, String filePath2) {
        try {
            // Use FileInputStream to load the files from the res folder
            File layer1File = new File("res/" + filePath1);
            File layer2File = new File("res/" + filePath2);

            // Check if the files exist
            if (!layer1File.exists() || !layer2File.exists()) {
                throw new FileNotFoundException("One or both files not found in the 'res/Map/' directory.");
            }

            // Read both files into lists of lines using FileInputStream
            List<String> layer1Lines = readFile(new FileInputStream(layer1File));
            List<String> layer2Lines = readFile(new FileInputStream(layer2File));

            // Determine the maximum number of rows
            int maxRows = Math.max(layer1Lines.size(), layer2Lines.size());

            // Find the maximum number of columns in both layers
            int maxCols = Math.max(findMaxColumns(layer1Lines), findMaxColumns(layer2Lines));

            // Normalize both layers by adding missing rows and columns with "00,"
            List<String> normalizedLayer1 = normalizeLayer(layer1Lines, maxRows, maxCols);
            List<String> normalizedLayer2 = normalizeLayer(layer2Lines, maxRows, maxCols);

            // Write back the updated layers to their respective files
            writeFile("res/" + filePath1, normalizedLayer1);
            writeFile("res/" + filePath2, normalizedLayer2);

        } catch (FileNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }


    // Method to read file lines
    private static List<String> readFile(InputStream inputStream) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    // Method to find the maximum number of columns in a layer (list of lines)
    private static int findMaxColumns(List<String> lines) {
        int maxColumns = 0;
        for (String line : lines) {
            String[] columns = line.replaceAll(" ", "").split(",");
            maxColumns = Math.max(maxColumns, columns.length);
        }
        return maxColumns;
    }

    // Method to normalize a layer by adding missing rows/columns filled with "00,"
    private static List<String> normalizeLayer(List<String> lines, int maxRows, int maxCols) {
        List<String> normalizedLines = new ArrayList<>();

        // Iterate through each row and ensure it has the correct number of columns
        for (int i = 0; i < maxRows; i++) {
            if (i < lines.size()) {
                // Normalize existing lines by adding missing columns
                String[] columns = lines.get(i).replaceAll(" ", "").split(",");
                StringBuilder newRow = new StringBuilder();

                for (int j = 0; j < maxCols; j++) {
                    if (j < columns.length) {
                        newRow.append(columns[j]).append(",");
                    } else {
                        newRow.append("00,"); // Add missing "00," for columns
                    }
                }
                // Ensure there's no trailing extra space
                String rowStr = newRow.toString().trim();
                normalizedLines.add(rowStr.substring(0, rowStr.length() - 1));  // Remove last comma
            } else {
                // Add missing rows filled entirely with "00,"
                String rowStr = "00,".repeat(Math.max(0, maxCols)).trim();
                normalizedLines.add(rowStr.substring(0, rowStr.length() - 1));  // Remove last comma
            }
        }
        return normalizedLines;
    }

    // Method to write the normalized lines back to a file
    private static void writeFile(String filePath, List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
