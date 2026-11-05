import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import javax.tools.*;

// Task 14: 14-report/ReportApp.java
// Run: javac 14-report/*.java && java tests/Task14Test.java
public class Task14Test {
    static final String HEADER = "# type;reference;street;zipCode;city;surface;rent;energyClass;extra...\n";

    // writes the content into a temporary file and returns its path
    static String file(String content) {
        try {
            Path p = Files.createTempFile("properties", ".csv");
            p.toFile().deleteOnExit();
            Files.writeString(p, content);
            return p.toString();
        } catch (IOException e) {
            throw new AssertionError("cannot create a temporary file: " + e);
        }
    }

    // the program must fail: nothing on stdout, this exact line on stderr, exit code 1
    static void fails(String expected, String... args) {
        T.Run r = T.run("ReportApp", "", args);
        T.eq("stderr", List.of(expected), r.errLines());
        T.eq("stdout must be empty", "", r.out());
        T.eq("exit code", 1, r.exit());
    }

    static void failsOnLine(String expected, String content) {
        fails(expected, file(content));
    }

    public static void main(String[] args) {
        T.setup("14-report", "ReportApp");

        T.checkRun("wrong number of arguments: usage on stderr, exit code 1", () -> {
            fails("Usage: java ReportApp <file>");
            fails("Usage: java ReportApp <file>", "a.csv", "b.csv");
        });

        T.checkRun("unreadable file: error on stderr, exit code 1", () -> {
            fails("Error: cannot read file: does/not/exist.csv", "does/not/exist.csv");
        });

        T.checkRun("prints the report of a valid file", () -> {
            String path = file(HEADER
                    + "COMMERCIAL;P-003;1 Place Bellecour;69002;Lyon;80;2500;C;bakery\n"
                    + "APARTMENT;P-001;12 Rue de la Paix;75002;Paris;25;950;D;3;true\n"
                    + "HOUSE;P-002;8 Chemin des Vignes;33000;Bordeaux;120;1800;B;400\n");
            T.Run r = T.run("ReportApp", "", path);
            T.eq("stderr", "", r.err());
            T.eq(List.of(
                    "Portfolio report",
                    "================",
                    "Apartment P-001 | 25 m2 | 950 EUR/month | 12 Rue de la Paix, 75002 Paris | floor 3, elevator",
                    "House P-002 | 120 m2 | 1800 EUR/month | 8 Chemin des Vignes, 33000 Bordeaux | land 400 m2",
                    "Commercial space P-003 | 80 m2 | 2500 EUR/month | 1 Place Bellecour, 69002 Lyon | activity: bakery",
                    "================",
                    "Properties: 3",
                    "Total rent: 5250 EUR/month",
                    "Total charges: 670 EUR/month",
                    "Best energy class: B (P-002)",
                    "Most expensive: P-003 (2500 EUR/month)"), r.lines());
            T.eq("exit code", 0, r.exit());
        });

        T.checkRun("ignores comments and blank lines, handles an empty portfolio", () -> {
            T.Run r = T.run("ReportApp", "", file(HEADER + "\n   \n# nothing yet\n"));
            T.eq(List.of(
                    "Portfolio report",
                    "================",
                    "================",
                    "Properties: 0",
                    "Total rent: 0 EUR/month",
                    "Total charges: 0 EUR/month",
                    "Best energy class: none",
                    "Most expensive: none"), r.lines());
            T.eq("exit code", 0, r.exit());
            T.Run spaced = T.run("ReportApp", "", file(HEADER + "\n"
                    + "  HOUSE ; P-002 ; 8 Chemin des Vignes ; 33000 ; Bordeaux ; 120 ; 1800 ; B ; 400  \n"));
            T.eq("fields are trimmed", "House P-002 | 120 m2 | 1800 EUR/month | 8 Chemin des Vignes, 33000 Bordeaux | land 400 m2",
                    spaced.lines().size() > 2 ? spaced.lines().get(2) : spaced.out() + spaced.err());
        });

        T.checkRun("reports format errors with their line number", () -> {
            String ok = "APARTMENT;P-001;12 Rue de la Paix;75002;Paris;25;950;D;3;true\n";
            failsOnLine("Error: line 3: Unknown type: FLAT",
                    HEADER + ok + "FLAT;P-002;3 Rue Lepic;75018;Paris;40;800;E;5;false\n");
            failsOnLine("Error: line 2: Expected 9 fields, got 8",
                    HEADER + "HOUSE;P-002;8 Chemin des Vignes;33000;Bordeaux;120;1800;B\n");
            failsOnLine("Error: line 4: Invalid number: 1800E",
                    HEADER + "\n" + ok + "HOUSE;P-002;8 Chemin des Vignes;33000;Bordeaux;120;1800E;B;400\n");
            failsOnLine("Error: line 2: Invalid boolean: yes",
                    HEADER + "APARTMENT;P-001;12 Rue de la Paix;75002;Paris;25;950;D;3;yes\n");
            failsOnLine("Error: line 2: Invalid energy class: H",
                    HEADER + "APARTMENT;P-001;12 Rue de la Paix;75002;Paris;25;950;H;3;true\n");
        });

        T.checkRun("reports the rules of the model with their line number", () -> {
            String ok = "APARTMENT;P-001;12 Rue de la Paix;75002;Paris;25;950;D;3;true\n";
            failsOnLine("Error: line 2: Surface must be positive: 0",
                    HEADER + "HOUSE;P-002;8 Chemin des Vignes;33000;Bordeaux;0;1800;B;400\n");
            failsOnLine("Error: line 2: Invalid zip code: 3300",
                    HEADER + "HOUSE;P-002;8 Chemin des Vignes;3300;Bordeaux;120;1800;B;400\n");
            failsOnLine("Error: line 2: Floor must not be negative: -2",
                    HEADER + "APARTMENT;P-001;12 Rue de la Paix;75002;Paris;25;950;D;-2;true\n");
            failsOnLine("Error: line 3: Duplicate reference: P-001", HEADER + ok + ok);
        });

        T.done();
    }
}

// ============================================================
// Test harness: DO NOT MODIFY.
// Each test is a small piece of Java code. The harness compiles it
// against YOUR compiled classes (the .class files of the task folder),
// runs it, and prints [OK] / [KO] for each test.
// ============================================================
final class T {
    interface Body {
        void run() throws Throwable;
    }

    record Run(int exit, String out, String err) {
        List<String> lines() {
            return out.lines().toList();
        }

        List<String> errLines() {
            return err.lines().toList();
        }
    }

    private record Check(String name, String code, Body body) {}

    private static final List<Check> CHECKS = new ArrayList<>();
    private static String dir = ".";
    private static String[] classes = {};
    private static int ok = 0;
    private static int ko = 0;

    // the task folder, and the classes the tests need
    static void setup(String taskDir, String... requiredClasses) {
        dir = taskDir;
        classes = requiredClasses;
    }

    // a test written in Java, compiled against your classes
    static void check(String name, String code) {
        CHECKS.add(new Check(name, code, null));
    }

    // a test that runs one of your programs
    static void checkRun(String name, Body body) {
        CHECKS.add(new Check(name, null, body));
    }

    static void done() {
        String missing = null;
        for (String c : classes) {
            if (!new File(dir, c + ".class").isFile()) {
                missing = c + ".class not found in " + dir + "/: did you run javac " + dir + "/*.java?";
                break;
            }
        }
        Map<Integer, String> errors = new HashMap<>();
        Path out = null;
        if (missing == null) {
            try {
                out = compileAll(errors);
            } catch (IOException e) {
                missing = "cannot compile the tests: " + e;
            }
        }
        for (int i = 0; i < CHECKS.size(); i++) {
            Check c = CHECKS.get(i);
            if (c.body() != null) {
                report(c.name(), c.body());
            } else if (missing != null) {
                fail(c.name(), missing);
            } else if (errors.containsKey(i)) {
                fail(c.name(), "does not compile with your classes: " + errors.get(i));
            } else {
                Path classesDir = out;
                int index = i;
                report(c.name(), () -> invoke(classesDir, index));
            }
        }
        System.out.println(ok + "/" + (ok + ko) + " tests passed");
        System.exit(ko == 0 ? 0 : 1);
    }

    private static void report(String name, Body body) {
        try {
            body.run();
            ok++;
            System.out.println("[OK] " + name);
        } catch (Throwable t) {
            fail(name, describe(t));
        }
    }

    private static void fail(String name, String reason) {
        ko++;
        System.out.println("[KO] " + name + " -> " + reason);
    }

    static String describe(Throwable t) {
        if (t instanceof AssertionError) {
            return String.valueOf(t.getMessage());
        }
        return t.getClass().getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage());
    }

    // ---------- compile the tests against the task folder ----------
    private static final String HEADER = """
            import java.util.*;
            import java.util.function.*;
            import java.lang.reflect.*;
            import java.nio.file.*;

            """;

    private static String source(int i) {
        return HEADER + "public class Check" + i + " {\n"
                + "    public static void run() throws Throwable {\n"
                + CHECKS.get(i).code()
                + "\n    }\n" + HELPERS + "}\n";
    }

    private static Path compileAll(Map<Integer, String> errors) throws IOException {
        Path root = Files.createTempDirectory("checks");
        Path src = Files.createDirectories(root.resolve("src"));
        Path out = Files.createDirectories(root.resolve("out"));
        Path empty = Files.createDirectories(root.resolve("empty"));
        List<Integer> todo = new ArrayList<>();
        for (int i = 0; i < CHECKS.size(); i++) {
            if (CHECKS.get(i).code() != null) {
                Files.writeString(src.resolve("Check" + i + ".java"), source(i));
                todo.add(i);
            }
        }
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        if (javac == null) {
            throw new IOException("no Java compiler found: install a JDK, not a JRE");
        }
        // first pass: everything; second pass: only the tests that compiled
        for (int pass = 0; pass < 2 && !todo.isEmpty(); pass++) {
            DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<>();
            try (StandardJavaFileManager fm = javac.getStandardFileManager(diags, Locale.ENGLISH,
                    StandardCharsets.UTF_8)) {
                List<File> files = new ArrayList<>();
                for (int i : todo) {
                    files.add(src.resolve("Check" + i + ".java").toFile());
                }
                List<String> options = List.of("-cp", new File(dir).getAbsolutePath(), "-sourcepath",
                        empty.toString(), "-implicit:none", "-nowarn", "-d", out.toString());
                boolean success = javac.getTask(null, fm, diags, options, null,
                        fm.getJavaFileObjectsFromFiles(files)).call();
                if (success) {
                    return out;
                }
                for (Diagnostic<? extends JavaFileObject> d : diags.getDiagnostics()) {
                    if (d.getKind() != Diagnostic.Kind.ERROR || d.getSource() == null) {
                        continue;
                    }
                    String file = new File(d.getSource().getName()).getName();
                    int i = Integer.parseInt(file.replace("Check", "").replace(".java", ""));
                    String[] parts = d.getMessage(Locale.ENGLISH).strip().split("\\s*\\n\\s*");
                    String message = parts[0];
                    if (parts.length > 1) {
                        message += " (" + String.join(", ", Arrays.copyOfRange(parts, 1, parts.length))
                                .replaceAll("\\s+", " ") + ")";
                    }
                    errors.putIfAbsent(i, message);
                }
                todo.removeAll(errors.keySet());
            }
        }
        return out;
    }

    private static void invoke(Path out, int i) throws Throwable {
        URLClassLoader loader = new URLClassLoader(
                new URL[] {out.toUri().toURL(), new File(dir).toURI().toURL()},
                ClassLoader.getPlatformClassLoader());
        Method run = Class.forName("Check" + i, true, loader).getMethod("run");
        Throwable[] box = new Throwable[1];
        Thread th = new Thread(() -> {
            try {
                run.invoke(null);
            } catch (InvocationTargetException e) {
                box[0] = e.getCause();
            } catch (Throwable t) {
                box[0] = t;
            }
        });
        th.setDaemon(true);
        th.start();
        th.join(10_000);
        if (th.isAlive()) {
            throw new AssertionError("the test does not terminate after 10 s (infinite loop?)");
        }
        if (box[0] instanceof AssertionError) {
            throw box[0];
        }
        if (box[0] != null) {
            throw new AssertionError("your code threw " + describe(box[0]));
        }
    }

    // ---------- helpers available inside every test ----------
    private static final String HELPERS = """

                interface Code {
                    void run() throws Throwable;
                }

                static void eq(Object expected, Object actual) {
                    if (!Objects.deepEquals(expected, actual)) {
                        throw new AssertionError("expected " + show(expected) + ", got " + show(actual));
                    }
                }

                static void eq(String what, Object expected, Object actual) {
                    if (!Objects.deepEquals(expected, actual)) {
                        throw new AssertionError(what + ": expected " + show(expected) + ", got " + show(actual));
                    }
                }

                static void isTrue(String what, boolean condition) {
                    if (!condition) {
                        throw new AssertionError(what);
                    }
                }

                static String article(Class<?> type) {
                    String n = type.getSimpleName();
                    return ("AEIOU".indexOf(n.charAt(0)) >= 0 ? "an " : "a ") + n;
                }

                // the code must throw an exception of this type
                static <X extends Throwable> X expectThrows(Class<X> type, String what, Code code) {
                    try {
                        code.run();
                    } catch (Throwable t) {
                        if (type.isInstance(t)) {
                            return type.cast(t);
                        }
                        throw new AssertionError(what + ": expected " + article(type) + ", got "
                                + t.getClass().getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage()));
                    }
                    throw new AssertionError("expected " + article(type) + " for " + what);
                }

                // the code must throw an exception of this type, with this exact message
                static void expectThrows(Class<? extends Throwable> type, String what, String message, Code code) {
                    eq("message of the exception for " + what, message, expectThrows(type, what, code).getMessage());
                }

                static Field field(Class<?> c, String name) {
                    try {
                        return c.getDeclaredField(name);
                    } catch (NoSuchFieldException e) {
                        throw new AssertionError("field not found: " + name + " in " + c.getSimpleName());
                    }
                }

                // a private instance field of this type
                static void privateField(Class<?> c, String name, Class<?> type) {
                    Field f = field(c, name);
                    isTrue(c.getSimpleName() + "." + name + " must be private", Modifier.isPrivate(f.getModifiers()));
                    isTrue(c.getSimpleName() + "." + name + " must not be static", !Modifier.isStatic(f.getModifiers()));
                    eq("type of " + c.getSimpleName() + "." + name, type, f.getType());
                }

                static Method method(Class<?> c, String name, Class<?>... types) {
                    try {
                        return c.getDeclaredMethod(name, types);
                    } catch (NoSuchMethodException e) {
                        throw new AssertionError("method " + name + " not declared in " + c.getSimpleName());
                    }
                }

                static boolean isPublic(Member m) {
                    return Modifier.isPublic(m.getModifiers());
                }

                static String show(Object o) {
                    if (o == null) {
                        return "null";
                    }
                    if (o instanceof String s) {
                        return "\\"" + s.replace("\\n", "\\\\n") + "\\"";
                    }
                    if (o instanceof Class<?> c) {
                        return c.getSimpleName();
                    }
                    return o + " (" + o.getClass().getSimpleName() + ")";
                }
            """;

    // ---------- run a program (java -cp dir Class args) ----------
    static void needClass(String cls) {
        if (!new File(dir, cls + ".class").isFile()) {
            throw new AssertionError(cls + ".class not found in " + dir + "/: did you run javac " + dir + "/*.java?");
        }
    }

    static Run run(String cls, String stdin, String... args) {
        needClass(cls);
        try {
            List<String> cmd = new ArrayList<>();
            cmd.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
            cmd.add("-cp");
            cmd.add(dir);
            cmd.add(cls);
            cmd.addAll(Arrays.asList(args));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.environment().remove("JAVA_TOOL_OPTIONS");
            pb.environment().remove("JDK_JAVA_OPTIONS");
            pb.environment().remove("_JAVA_OPTIONS");
            Process p = pb.start();
            CompletableFuture<String> out = CompletableFuture.supplyAsync(() -> readAll(p.getInputStream()));
            CompletableFuture<String> err = CompletableFuture.supplyAsync(() -> readAll(p.getErrorStream()));
            try (OutputStream os = p.getOutputStream()) {
                os.write(stdin.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ignored) {
                // the program may have exited without reading its input: not an error
            }
            if (!p.waitFor(10, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                throw new AssertionError("timeout after 10 s (infinite loop? is the program waiting for input?)");
            }
            return new Run(p.exitValue(), out.get(), err.get());
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError("cannot run " + cls + ": " + e);
        }
    }

    private static String readAll(InputStream in) {
        try (in) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    static void eq(String what, Object expected, Object actual) {
        if (!Objects.deepEquals(expected, actual)) {
            throw new AssertionError(what + ": expected " + show(expected) + ", got " + show(actual));
        }
    }

    static void eq(Object expected, Object actual) {
        if (!Objects.deepEquals(expected, actual)) {
            throw new AssertionError("expected " + show(expected) + ", got " + show(actual));
        }
    }

    static String show(Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof String s) {
            return "\"" + s.replace("\n", "\\n") + "\"";
        }
        return o.toString();
    }
}
