import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import javax.tools.*;

// Task 3: 3-record/Address.java + 3-record/Property.java
// Run: javac 3-record/*.java && java tests/Task03Test.java
public class Task03Test {
    public static void main(String[] args) {
        T.setup("3-record", "Address", "Property");

        T.check("Address is a record with three String components", """
                isTrue("Address must be declared as a record", Address.class.isRecord());
                RecordComponent[] rc = Address.class.getRecordComponents();
                eq("number of components", 3, rc.length);
                eq("first component", "street", rc[0].getName());
                eq("second component", "zipCode", rc[1].getName());
                eq("third component", "city", rc[2].getName());
                for (RecordComponent c : rc) {
                    eq("type of " + c.getName(), String.class, c.getType());
                }
                """);

        T.check("the compact constructor validates the address", """
                expectThrows(IllegalArgumentException.class, "a blank street", "Street must not be blank",
                        () -> new Address("  ", "75002", "Paris"));
                expectThrows(IllegalArgumentException.class, "a null street", "Street must not be blank",
                        () -> new Address(null, "75002", "Paris"));
                expectThrows(IllegalArgumentException.class, "zip code 7500", "Invalid zip code: 7500",
                        () -> new Address("12 Rue de la Paix", "7500", "Paris"));
                expectThrows(IllegalArgumentException.class, "zip code 75A02", "Invalid zip code: 75A02",
                        () -> new Address("12 Rue de la Paix", "75A02", "Paris"));
                expectThrows(IllegalArgumentException.class, "zip code null", "Invalid zip code: null",
                        () -> new Address("12 Rue de la Paix", null, "Paris"));
                expectThrows(IllegalArgumentException.class, "an empty city", "City must not be blank",
                        () -> new Address("12 Rue de la Paix", "75002", ""));
                """);

        T.check("accessors, equals() and toString() of Address", """
                Address a = new Address("12 Rue de la Paix", "75002", "Paris");
                Address b = new Address("12 Rue de la Paix", "75002", "Paris");
                eq("street()", "12 Rue de la Paix", a.street());
                eq("zipCode()", "75002", a.zipCode());
                eq("city()", "Paris", a.city());
                isTrue("two addresses with the same values must be equal", a.equals(b));
                eq("hashCode() of two equal addresses", a.hashCode(), b.hashCode());
                isTrue("different cities: must not be equal", !a.equals(new Address("12 Rue de la Paix", "75002", "Lyon")));
                eq("toString()", "12 Rue de la Paix, 75002 Paris", a.toString());
                """);

        T.check("Property uses an Address", """
                privateField(Property.class, "address", Address.class);
                Address a = new Address("12 Rue de la Paix", "75002", "Paris");
                Property p = new Property("P-001", a, 25, 950);
                isTrue("getAddress() must return the Address given to the constructor", p.getAddress() == a);
                eq("city of the property", "Paris", p.getAddress().city());
                eq("toString()", "P-001 | 25 m2 | 950 EUR/month | 12 Rue de la Paix, 75002 Paris", p.toString());
                expectThrows(IllegalArgumentException.class, "a null address", "Address must not be null",
                        () -> new Property("P-001", null, 25, 950));
                expectThrows(IllegalArgumentException.class, "setAddress(null)", "Address must not be null",
                        () -> p.setAddress(null));
                p.setAddress(new Address("5 Rue Victor Hugo", "69002", "Lyon"));
                eq("city after setAddress()", "Lyon", p.getAddress().city());
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
