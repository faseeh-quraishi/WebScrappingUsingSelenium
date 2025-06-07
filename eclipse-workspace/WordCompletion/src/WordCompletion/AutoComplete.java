package WordCompletion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AutoComplete {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree(); // Initialize AVL Tree

        // Load and process CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(
                "C:\\Users\\Faseeh\\eclipse-workspace\\WordCompletion\\src\\hp_laptops.csv"))) {

            String line;
            br.readLine(); // Skip header row

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",", -1); // Read all columns in the row

                for (String col : columns) {
                    // Clean text: convert to lowercase and retain only alphanumeric characters and spaces
                    String cleaned = col.toLowerCase().replaceAll("[^a-z0-9 ]", " ");
                    String[] words = cleaned.split("\\s+");

                    for (String word : words) {
                        if (!word.isEmpty()) {
                            word = word.trim();

                            // Optional: remove junk suffixes like "ca" or "â„¢" if they appear at the end
                            if (word.endsWith("ca")) {
                                word = word.substring(0, word.length() - 2);
                            } else if (word.endsWith("â„¢")) {
                                word = word.substring(0, word.length() - 2);
                            }

                            if (!word.isEmpty()) {
                                tree.insert(word); // Insert cleaned word into AVL tree
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        // User interaction loop
        Scanner sc = new Scanner(System.in);
        System.out.println("\nAutocomplete System Ready!");
        System.out.println("Type a prefix to search (or type 'exit' to quit):");

        while (true) {
            System.out.print("\nEnter prefix: ");
            String prefix = sc.nextLine().toLowerCase().trim();

            if (prefix.equals("exit")) {
                System.out.println("Exiting Autocomplete. Goodbye!");
                break;
            }

            if (prefix.isEmpty()) {
                System.out.println("Please enter a valid prefix.");
                continue;
            }

            // Retrieve and display top K suggestions
            int topK = 10;
            List<WordFrequency> suggestions = tree.getSuggestions(prefix, topK);

            if (suggestions.isEmpty()) {
                System.out.println("No suggestions found for: " + prefix);
            } else {
                System.out.println("Top suggestions:");
                for (WordFrequency wf : suggestions) {
                    System.out.println("- " + wf.word + " (freq: " + wf.frequency + ")");
                }
            }
        }

        sc.close(); // Close Scanner
    }
}
