import java.io.*;
import java.util.Scanner;

public class cli {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nDayfont Brush CLI");
            System.out.println("1. Create a new brush");
            System.out.println("2. Load a brush");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createBrush(scanner);
                    break;
                case 2:
                    loadBrush(scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }

    private static void createBrush(Scanner scanner) throws IOException {
        System.out.print("Enter brush name: ");
        String name = scanner.nextLine();

        System.out.print("Enter brush width: ");
        int width = scanner.nextInt();

        System.out.print("Enter brush height: ");
        int height = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter file name to save as (without extension): ");
        String fileName = scanner.nextLine();
        String filePath = fileName + ".dfb";

        dfb.saveBrush(filePath, name, width, height);
        System.out.println("Brush saved successfully to " + filePath);
    }

    private static void loadBrush(Scanner scanner) {
        System.out.print("Enter brush file name (without extension): ");
        String fileName = scanner.nextLine();
        String filePath = fileName.endsWith(".dfb")?fileName: fileName + ".dfb";

        try {
            dfb.Brush brush = dfb.loadBrush(filePath);
            System.out.println("Loaded Brush:");
            System.out.println("Name: " + brush.name);
            System.out.println("Width: " + brush.width);
            System.out.println("Height: " + brush.height);
        } catch (IOException e) {
            System.out.println("Failed to load brush: " + e.getMessage());
        }
    }
}
