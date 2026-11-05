import java.util.Scanner;

public class Interactive {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter a number:");

            if (!scanner.hasNextLine()) {
                System.err.println("Error: no valid input");
                System.exit(1);
            }

            String line = scanner.nextLine().trim();

            try {
                int n = Integer.parseInt(line);
                long doubled = n * 2L;
                System.out.println("Doubled: " + doubled);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid: \"" + line + "\"");
            }
        }

        scanner.close();
    }
}