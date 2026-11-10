package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/* CSV reader and writer. 
 * + fields containing commas or quotes are quoted
 * + files are written to a temporary file first and then renamed
 */
final class Csv {

    private Csv() {
    }

    // joins fields into one CSV line, quoting where needed. 
    // New lines inside a field become spaces
    static String format(String... fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line.append(',');
            }
            String value = fields[i] == null ? "" : fields[i].replace('\r', ' ').replace('\n', ' ');
            if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0) {
                line.append('"').append(value.replace("\"", "\"\"")).append('"');
            } else {
                line.append(value);
            }
        }
        return line.toString();
    }

    // splits one CSV line into fields, understanding quotes and doubled quotes.
    static List<String> parse(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }

    // reads a CSV file, skipping the header row and blank lines.
    static List<List<String>> read(Path file) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        if (!Files.exists(file)) {
            return rows;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        // lines becomes a list containing n strings
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).trim().isEmpty()) {
                rows.add(parse(lines.get(i)));
            }
        }
        return rows;
    }

    // writes a header and rows to a file, replacing it atomically.
    static void write(Path file, String header, List<String> rows) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header);
        lines.addAll(rows);
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
