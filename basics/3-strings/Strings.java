public class Strings {

    /**
     * Met la première lettre en majuscule et le reste en minuscule.
     */
    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
    /**
     * Compte le nombre de mots en ignorant les espaces superflus.
     */
    public static int countWords(String s) {
        if (s == null) {
            return 0;
        }
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        // split("\\s+") découpe selon un ou plusieurs espaces consécutifs
        String[] words = trimmed.split("\\s+");
        return words.length;
    }

    /**
     * Extrait les initiales de chaque mot en majuscules.
     */
    public static String initials(String s) {
        if (s == null || s.trim().isEmpty()) {
            return "";
        }
        String trimmed = s.trim();
        String[] words = trimmed.split("\\s+");
        String result = "";

        for (String word : words) {
            if (!word.isEmpty()) {
                result += word.substring(0, 1).toUpperCase();
            }
        }
        return result;
    }

    /**
     * Vérifie si la chaine est un palindrome (ignore la casse et les espaces).
     */
    public static boolean isPalindrome(String s) {
        if (s == null) {
            return false;
        }
        // Nettoie la chaine : tout en miniscule et supprime les espaces
        String clean = s.toLowerCase().replace(" ", "");

        int left = 0;
        int right = clean.length() - 1;

        while (left < right) {
            if (clean.charAt(left) != clean.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    /**
     * Extrait le domaine d'une adresse email (la partie après le '@').
     */
    public static String extractDomain(String email) {
        if (email == null) {
            return "";
        }
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return "";
        }
        return email.substring(atIndex + 1);
    }

    public static void main(String[] args) {
        String testString = "Hello world";
        System.out.println("Capitalized: " + capitalize(testString));
        System.out.println("Word Count: " + countWords(testString));
        System.out.println("Initials: " + initials(testString));
        System.out.println("Is Palindrome: " + isPalindrome(testString));
        System.out.println("Extracted Domain: " + extractDomain("user@example.com"));
    }
}