public class Conditions {

    public static String fizzBuzz(int n) {
        if (n % 3 == 0 && n % 5 == 0) {
            return "FizzBuzz";
        } else if (n % 3 == 0) {
            return "Fizz";
        } else if (n % 5 == 0) {
            return "Buzz";
        } else {
            return Integer.toString(n);
        }
    }

    public static String dayType(int day) {
        String result;
        switch (day) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                result = "weekday";
                break;
            case 6:
            case 7:
                result = "weekend";
                break;
            default:
                result = "invalid";
        }
        return result;
        }
    
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year %400 == 0);
    }

    public static int daysInMonth(int month, int year){
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> isLeapYear(year) ? 29 : 28;
            default -> -1;
        };
    }

    public static String grade(int score) {
        if (score < 0 || score > 100) {
            return "invalid";
        } else if (score >= 90) {
            return "A";
        } else if (score >= 80) {
            return "B";
        } else if (score >= 70) {
            return "C";
        } else if (score >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 15; i++) {
            System.out.println(fizzBuzz(i));
        }
    }
}
