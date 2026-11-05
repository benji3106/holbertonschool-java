import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import javax.tools.*;

// Task 1: 1-encapsulation/Property.java
// Run: javac 1-encapsulation/*.java && java tests/Task01Test.java
public class Task01Test {
    public static void main(String[] args) {
        T.setup("1-encapsulation", "Property");

        T.check("private fields, reference is final and has no setter", """
                privateField(Property.class, "reference", String.class);
                privateField(Property.class, "address", String.class);
                privateField(Property.class, "surface", int.class);
                privateField(Property.class, "monthlyRent", int.class);
                isTrue("reference must be final", Modifier.isFinal(field(Property.class, "reference").getModifiers()));
                for (Method m : Property.class.getDeclaredMethods()) {
                    isTrue("there must be no setReference() method", !m.getName().equals("setReference"));
                }
                """);

        T.check("the constructor rejects invalid values", """
                expectThrows(IllegalArgumentException.class, "reference null", "Invalid reference: null",
                        () -> new Property(null, "a", 25, 950));
                expectThrows(IllegalArgumentException.class, "reference P-1", "Invalid reference: P-1",
                        () -> new Property("P-1", "a", 25, 950));
                expectThrows(IllegalArgumentException.class, "reference p-001", "Invalid reference: p-001",
                        () -> new Property("p-001", "a", 25, 950));
                expectThrows(IllegalArgumentException.class, "reference P-0001", "Invalid reference: P-0001",
                        () -> new Property("P-0001", "a", 25, 950));
                expectThrows(IllegalArgumentException.class, "a blank address", "Address must not be blank",
                        () -> new Property("P-001", "   ", 25, 950));
                expectThrows(IllegalArgumentException.class, "a null address", "Address must not be blank",
                        () -> new Property("P-001", null, 25, 950));
                expectThrows(IllegalArgumentException.class, "a surface of 0", "Surface must be positive: 0",
                        () -> new Property("P-001", "a", 0, 950));
                expectThrows(IllegalArgumentException.class, "a rent of -1", "Rent must not be negative: -1",
                        () -> new Property("P-001", "a", 25, -1));
                """);

        T.check("setSurface() rejects zero and negative values", """
                Property p = new Property("P-001", "12 Rue de la Paix, 75002 Paris", 25, 950);
                expectThrows(IllegalArgumentException.class, "setSurface(0)", "Surface must be positive: 0",
                        () -> p.setSurface(0));
                expectThrows(IllegalArgumentException.class, "setSurface(-20)", "Surface must be positive: -20",
                        () -> p.setSurface(-20));
                eq("the surface is unchanged after an invalid call", 25, p.getSurface());
                p.setSurface(30);
                eq("getSurface() after setSurface(30)", 30, p.getSurface());
                """);

        T.check("setMonthlyRent() and setAddress() validate their value", """
                Property p = new Property("P-001", "12 Rue de la Paix, 75002 Paris", 25, 950);
                expectThrows(IllegalArgumentException.class, "setMonthlyRent(-5)", "Rent must not be negative: -5",
                        () -> p.setMonthlyRent(-5));
                expectThrows(IllegalArgumentException.class, "setAddress(\\"\\")", "Address must not be blank",
                        () -> p.setAddress(""));
                eq("the rent is unchanged after an invalid call", 950, p.getMonthlyRent());
                eq("the address is unchanged after an invalid call", "12 Rue de la Paix, 75002 Paris", p.getAddress());
                p.setMonthlyRent(1000);
                p.setAddress("5 Rue Victor Hugo, 69002 Lyon");
                eq("getMonthlyRent() after setMonthlyRent(1000)", 1000, p.getMonthlyRent());
                eq("getAnnualRent() after setMonthlyRent(1000)", 12000, p.getAnnualRent());
                eq("getAddress() after setAddress()", "5 Rue Victor Hugo, 69002 Lyon", p.getAddress());
                """);

        T.check("boundary values are accepted", """
                Property p = new Property("P-999", "a", 1, 0);
                eq("getReference()", "P-999", p.getReference());
                eq("a surface of 1", 1, p.getSurface());
                eq("a rent of 0", 0, p.getMonthlyRent());
                p.setMonthlyRent(0);
                p.setSurface(1);
                eq("setMonthlyRent(0)", 0, p.getMonthlyRent());
                """);

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
