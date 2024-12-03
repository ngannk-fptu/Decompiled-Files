/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedActionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.aspectj.util.FileUtil;

public class LangUtil {
    public static final String EOL;
    public static final String JRT_FS = "jrt-fs.jar";
    private static double vmVersion;

    public static String getVmVersionString() {
        return Double.toString(vmVersion);
    }

    public static double getVmVersion() {
        return vmVersion;
    }

    private static List<Integer> getFirstNumbers(String vm) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(vm, ".-_");
        try {
            result.add(Integer.parseInt(st.nextToken()));
            result.add(Integer.parseInt(st.nextToken()));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return result;
    }

    public static boolean isOnePointThreeVMOrGreater() {
        return 1.3 <= vmVersion;
    }

    public static boolean is1dot4VMOrGreater() {
        return 1.4 <= vmVersion;
    }

    public static boolean is15VMOrGreater() {
        return 1.5 <= vmVersion;
    }

    public static boolean is16VMOrGreater() {
        return 1.6 <= vmVersion;
    }

    public static boolean is17VMOrGreater() {
        return 1.7 <= vmVersion;
    }

    public static boolean is18VMOrGreater() {
        return 1.8 <= vmVersion;
    }

    public static boolean is19VMOrGreater() {
        return 9.0 <= vmVersion;
    }

    public static boolean is10VMOrGreater() {
        return 10.0 <= vmVersion;
    }

    public static boolean is11VMOrGreater() {
        return 11.0 <= vmVersion;
    }

    public static boolean is12VMOrGreater() {
        return 12.0 <= vmVersion;
    }

    public static boolean is13VMOrGreater() {
        return 13.0 <= vmVersion;
    }

    public static boolean is14VMOrGreater() {
        return 14.0 <= vmVersion;
    }

    public static final void throwIaxIfNull(Object o, String name) {
        if (null == o) {
            String message = "null " + (null == name ? "input" : name);
            throw new IllegalArgumentException(message);
        }
    }

    public static final void throwIaxIfNotAssignable(Object[] ra, Class<?> c, String name) {
        LangUtil.throwIaxIfNull(ra, name);
        String label = null == name ? "input" : name;
        for (int i = 0; i < ra.length; ++i) {
            Class<?> actualClass;
            if (null == ra[i]) {
                String m = " null " + label + "[" + i + "]";
                throw new IllegalArgumentException(m);
            }
            if (null == c || c.isAssignableFrom(actualClass = ra[i].getClass())) continue;
            String message = label + " not assignable to " + c.getName();
            throw new IllegalArgumentException(message);
        }
    }

    public static final void throwIaxIfNotAssignable(Object o, Class<?> c, String name) {
        Class<?> actualClass;
        LangUtil.throwIaxIfNull(o, name);
        if (null != c && !c.isAssignableFrom(actualClass = o.getClass())) {
            String message = name + " not assignable to " + c.getName();
            throw new IllegalArgumentException(message);
        }
    }

    public static final void throwIaxIfFalse(boolean test, String message) {
        if (!test) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isEmpty(String s) {
        return null == s || 0 == s.length();
    }

    public static boolean isEmpty(Object[] ra) {
        return null == ra || 0 == ra.length;
    }

    public static boolean isEmpty(byte[] ra) {
        return null == ra || 0 == ra.length;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return null == collection || 0 == collection.size();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || 0 == map.size();
    }

    public static String[] split(String text) {
        return LangUtil.strings(text).toArray(new String[0]);
    }

    public static List<String> commaSplit(String input) {
        return LangUtil.anySplit(input, ",");
    }

    public static String[] splitClasspath(String classpath) {
        if (LangUtil.isEmpty(classpath)) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
        ArrayList<String> result = new ArrayList<String>(st.countTokens());
        while (st.hasMoreTokens()) {
            String entry = st.nextToken();
            if (LangUtil.isEmpty(entry)) continue;
            result.add(entry);
        }
        return result.toArray(new String[0]);
    }

    public static boolean getBoolean(String propertyName, boolean defaultValue) {
        if (null != propertyName) {
            try {
                String value = System.getProperty(propertyName);
                if (null != value) {
                    return Boolean.valueOf(value);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    public static List<String> anySplit(String input, String delim) {
        if (null == input) {
            return Collections.emptyList();
        }
        ArrayList<String> result = new ArrayList<String>();
        if (LangUtil.isEmpty(delim) || -1 == input.indexOf(delim)) {
            result.add(input.trim());
        } else {
            StringTokenizer st = new StringTokenizer(input, delim);
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().trim());
            }
        }
        return result;
    }

    public static List<String> strings(String text) {
        if (LangUtil.isEmpty(text)) {
            return Collections.emptyList();
        }
        ArrayList<String> strings = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(text);
        while (tok.hasMoreTokens()) {
            strings.add(tok.nextToken());
        }
        return strings;
    }

    public static <T> List<T> safeList(List<T> list) {
        return null == list ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public static String[][] copyStrings(String[][] in) {
        String[][] out = new String[in.length][];
        for (int i = 0; i < out.length; ++i) {
            out[i] = new String[in[i].length];
            System.arraycopy(in[i], 0, out[i], 0, out[i].length);
        }
        return out;
    }

    public static String[] extractOptions(String[] args, String[][] options) {
        if (LangUtil.isEmpty(args) || LangUtil.isEmpty((Object[])options)) {
            return args;
        }
        BitSet foundSet = new BitSet();
        String[] result = new String[args.length];
        int resultIndex = 0;
        for (int j = 0; j < args.length; ++j) {
            boolean found = false;
            for (int i = 0; !found && i < options.length; ++i) {
                Object[] option = options[i];
                LangUtil.throwIaxIfFalse(!LangUtil.isEmpty(option), "options");
                Object sought = option[0];
                found = ((String)sought).equals(args[j]);
                if (!found) continue;
                foundSet.set(i);
                int doMore = option.length - 1;
                if (0 >= doMore) continue;
                int MAX = j + doMore;
                if (MAX >= args.length) {
                    String s = "expecting " + doMore + " args after ";
                    throw new IllegalArgumentException(s + args[j]);
                }
                for (int k = 1; k < option.length; ++k) {
                    option[k] = args[++j];
                }
            }
            if (found) continue;
            result[resultIndex++] = args[j];
        }
        for (int i = 0; i < options.length; ++i) {
            if (foundSet.get(i)) continue;
            options[i][0] = null;
        }
        if (resultIndex < args.length) {
            String[] temp = new String[resultIndex];
            System.arraycopy(result, 0, temp, 0, resultIndex);
            args = temp;
        }
        return args;
    }

    public static Object[] safeCopy(Object[] source, Object[] sink) {
        int resultSize;
        Class<Object> sinkType = null == sink ? Object.class : sink.getClass().getComponentType();
        int sourceLength = null == source ? 0 : source.length;
        int sinkLength = null == sink ? 0 : sink.length;
        ArrayList<Object> result = null;
        if (0 == sourceLength) {
            resultSize = 0;
        } else {
            result = new ArrayList<Object>(sourceLength);
            for (int i = 0; i < sourceLength; ++i) {
                if (null == source[i] || !sinkType.isAssignableFrom(source[i].getClass())) continue;
                result.add(source[i]);
            }
            resultSize = result.size();
        }
        if (resultSize != sinkLength) {
            sink = (Object[])Array.newInstance(sinkType, result.size());
        }
        if (0 < resultSize) {
            sink = result.toArray(sink);
        }
        return sink;
    }

    public static String unqualifiedClassName(Class<?> c) {
        if (null == c) {
            return "null";
        }
        String name = c.getName();
        int loc = name.lastIndexOf(".");
        if (-1 != loc) {
            name = name.substring(1 + loc);
        }
        return name;
    }

    public static String unqualifiedClassName(Object o) {
        return LangUtil.unqualifiedClassName(null == o ? null : o.getClass());
    }

    public static String replace(String in, String sought, String replace) {
        int loc;
        if (LangUtil.isEmpty(in) || LangUtil.isEmpty(sought)) {
            return in;
        }
        StringBuffer result = new StringBuffer();
        int len = sought.length();
        int start = 0;
        while (-1 != (loc = in.indexOf(sought, start))) {
            result.append(in.substring(start, loc));
            if (!LangUtil.isEmpty(replace)) {
                result.append(replace);
            }
            start = loc + len;
        }
        result.append(in.substring(start));
        return result.toString();
    }

    public static String toSizedString(long i, int width) {
        String result = "" + i;
        int size = result.length();
        if (width > size) {
            String pad = "                                              ";
            int padLength = "                                              ".length();
            if (width > padLength) {
                width = padLength;
            }
            int topad = width - size;
            result = "                                              ".substring(0, topad) + result;
        }
        return result;
    }

    public static String renderExceptionShort(Throwable e) {
        if (null == e) {
            return "(Throwable) null";
        }
        return "(" + LangUtil.unqualifiedClassName(e) + ") " + e.getMessage();
    }

    public static String renderException(Throwable t) {
        return LangUtil.renderException(t, true);
    }

    public static String renderException(Throwable t, boolean elide) {
        if (null == t) {
            return "null throwable";
        }
        t = LangUtil.unwrapException(t);
        StringBuffer stack = LangUtil.stackToString(t, false);
        if (elide) {
            LangUtil.elideEndingLines(StringChecker.TEST_PACKAGES, stack, 100);
        }
        return stack.toString();
    }

    static void elideEndingLines(StringChecker checker, StringBuffer stack, int maxLines) {
        String line;
        if (null == checker || null == stack || 0 == stack.length()) {
            return;
        }
        LinkedList<String> lines = new LinkedList<String>();
        StringTokenizer st = new StringTokenizer(stack.toString(), "\n\r");
        while (st.hasMoreTokens() && 0 < --maxLines) {
            lines.add(st.nextToken());
        }
        st = null;
        int elided = 0;
        while (!lines.isEmpty() && checker.acceptString(line = (String)lines.getLast())) {
            ++elided;
            lines.removeLast();
        }
        if (elided > 0 || maxLines < 1) {
            int EOL_LEN = EOL.length();
            int totalLength = 0;
            while (!lines.isEmpty()) {
                totalLength += EOL_LEN + ((String)lines.getFirst()).length();
                lines.removeFirst();
            }
            if (stack.length() > totalLength) {
                stack.setLength(totalLength);
                if (elided > 0) {
                    stack.append("    (... " + elided + " lines...)");
                }
            }
        }
    }

    public static StringBuffer stackToString(Throwable throwable, boolean skipMessage) {
        if (null == throwable) {
            return new StringBuffer();
        }
        StringWriter buf = new StringWriter();
        PrintWriter writer = new PrintWriter(buf);
        if (!skipMessage) {
            writer.println(throwable.getMessage());
        }
        throwable.printStackTrace(writer);
        try {
            buf.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return buf.getBuffer();
    }

    public static Throwable unwrapException(Throwable t) {
        Throwable current = t;
        Throwable next = null;
        while (current != null) {
            if (current instanceof InvocationTargetException) {
                next = ((InvocationTargetException)current).getTargetException();
            } else if (current instanceof ClassNotFoundException) {
                next = ((ClassNotFoundException)current).getException();
            } else if (current instanceof ExceptionInInitializerError) {
                next = ((ExceptionInInitializerError)current).getException();
            } else if (current instanceof PrivilegedActionException) {
                next = ((PrivilegedActionException)current).getException();
            } else if (current instanceof SQLException) {
                next = ((SQLException)current).getNextException();
            }
            if (null == next) break;
            current = next;
            next = null;
        }
        return current;
    }

    public static <T> List<T> arrayAsList(T[] array) {
        if (null == array || 1 > array.length) {
            return Collections.emptyList();
        }
        ArrayList<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(array));
        return list;
    }

    public static String makeClasspath(String bootclasspath, String classpath, String classesDir, String outputJar) {
        StringBuffer sb = new StringBuffer();
        LangUtil.addIfNotEmpty(bootclasspath, sb, File.pathSeparator);
        LangUtil.addIfNotEmpty(classpath, sb, File.pathSeparator);
        if (!LangUtil.addIfNotEmpty(classesDir, sb, File.pathSeparator)) {
            LangUtil.addIfNotEmpty(outputJar, sb, File.pathSeparator);
        }
        return sb.toString();
    }

    private static boolean addIfNotEmpty(String input, StringBuffer sink, String delimiter) {
        if (LangUtil.isEmpty(input) || null == sink) {
            return false;
        }
        sink.append(input);
        if (!LangUtil.isEmpty(delimiter)) {
            sink.append(delimiter);
        }
        return true;
    }

    public static ProcessController makeProcess(ProcessController controller, String classpath, String mainClass, String[] args) {
        File java = LangUtil.getJavaExecutable();
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(java.getAbsolutePath());
        cmd.add("-classpath");
        cmd.add(classpath);
        cmd.add(mainClass);
        if (!LangUtil.isEmpty(args)) {
            cmd.addAll(Arrays.asList(args));
        }
        String[] command = cmd.toArray(new String[0]);
        if (null == controller) {
            controller = new ProcessController();
        }
        controller.init(command, mainClass);
        return controller;
    }

    public static File getJavaExecutable() {
        File binDir;
        String javaHome = null;
        File result = null;
        try {
            javaHome = System.getProperty("java.home");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (null != javaHome && (binDir = new File(javaHome, "bin")).isDirectory() && binDir.canRead()) {
            String exec;
            String[] execs;
            String[] stringArray = execs = new String[]{"java", "java.exe"};
            int n = stringArray.length;
            for (int i = 0; i < n && !(result = new File(binDir, exec = stringArray[i])).canRead(); ++i) {
            }
        }
        return result;
    }

    public static boolean sleepUntil(long time) {
        if (time == 0L) {
            return true;
        }
        if (time < 0L) {
            throw new IllegalArgumentException("negative: " + time);
        }
        long curTime = System.currentTimeMillis();
        for (int i = 0; i < 100 && curTime < time; ++i) {
            try {
                Thread.sleep(time - curTime);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            curTime = System.currentTimeMillis();
        }
        return curTime >= time;
    }

    public static String getJrtFsFilePath() {
        return LangUtil.getJavaHome() + File.separator + "lib" + File.separator + JRT_FS;
    }

    public static String getJavaHome() {
        return System.getProperty("java.home");
    }

    static {
        block11: {
            StringWriter buf = new StringWriter();
            PrintWriter writer = new PrintWriter(buf);
            writer.println("");
            String eol = "\n";
            try {
                buf.close();
                StringBuffer sb = buf.getBuffer();
                if (sb != null) {
                    eol = buf.toString();
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            EOL = eol;
            try {
                String vm = System.getProperty("java.version");
                if (vm == null) {
                    vm = System.getProperty("java.runtime.version");
                }
                if (vm == null) {
                    vm = System.getProperty("java.vm.version");
                }
                if (vm == null) {
                    new RuntimeException("System properties appear damaged, cannot find: java.version/java.runtime.version/java.vm.version").printStackTrace(System.err);
                    vmVersion = 1.5;
                    break block11;
                }
                try {
                    List<Integer> numbers = LangUtil.getFirstNumbers(vm);
                    if (numbers.get(0) == 1) {
                        vmVersion = (double)numbers.get(0).intValue() + (double)numbers.get(1).intValue() / 10.0;
                        break block11;
                    }
                    vmVersion = numbers.get(0).intValue();
                }
                catch (Throwable t) {
                    vmVersion = 1.5;
                }
            }
            catch (Throwable t) {
                new RuntimeException("System properties appear damaged, cannot find: java.version/java.runtime.version/java.vm.version", t).printStackTrace(System.err);
                vmVersion = 1.5;
            }
        }
    }

    public static class ProcessController {
        private String[] command;
        private String[] envp;
        private String label;
        private boolean init;
        private boolean started;
        private boolean completed;
        private boolean userStopped;
        private Process process;
        private FileUtil.Pipe errStream;
        private FileUtil.Pipe outStream;
        private FileUtil.Pipe inStream;
        private ByteArrayOutputStream errSnoop;
        private ByteArrayOutputStream outSnoop;
        private int result;
        private Thrown thrown;

        public final void reinit() {
            if (!this.init) {
                throw new IllegalStateException("must init(..) before reinit()");
            }
            if (this.started && !this.completed) {
                throw new IllegalStateException("not completed - do stop()");
            }
            this.started = false;
            this.completed = false;
            this.result = Integer.MIN_VALUE;
            this.thrown = null;
            this.process = null;
            this.errStream = null;
            this.outStream = null;
            this.inStream = null;
        }

        public final void init(String classpath, String mainClass, String[] args) {
            this.init(LangUtil.getJavaExecutable(), classpath, mainClass, args);
        }

        public final void init(File java, String classpath, String mainClass, String[] args) {
            LangUtil.throwIaxIfNull(java, "java");
            LangUtil.throwIaxIfNull(mainClass, "mainClass");
            LangUtil.throwIaxIfNull(args, "args");
            ArrayList<String> cmd = new ArrayList<String>();
            cmd.add(java.getAbsolutePath());
            cmd.add("-classpath");
            cmd.add(classpath);
            cmd.add(mainClass);
            if (!LangUtil.isEmpty(args)) {
                cmd.addAll(Arrays.asList(args));
            }
            this.init(cmd.toArray(new String[0]), mainClass);
        }

        public final void init(String[] command, String label) {
            this.command = (String[])LangUtil.safeCopy(command, new String[0]);
            if (1 > this.command.length) {
                throw new IllegalArgumentException("empty command");
            }
            this.label = LangUtil.isEmpty(label) ? command[0] : label;
            this.init = true;
            this.reinit();
        }

        public final void setEnvp(String[] envp) {
            this.envp = (String[])LangUtil.safeCopy(envp, new String[0]);
            if (1 > this.envp.length) {
                throw new IllegalArgumentException("empty envp");
            }
        }

        public final void setErrSnoop(ByteArrayOutputStream snoop) {
            this.errSnoop = snoop;
            if (null != this.errStream) {
                this.errStream.setSnoop(this.errSnoop);
            }
        }

        public final void setOutSnoop(ByteArrayOutputStream snoop) {
            this.outSnoop = snoop;
            if (null != this.outStream) {
                this.outStream.setSnoop(this.outSnoop);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final Thread start() {
            if (!this.init) {
                throw new IllegalStateException("not initialized");
            }
            ProcessController processController = this;
            synchronized (processController) {
                if (this.started) {
                    throw new IllegalStateException("already started");
                }
                this.started = true;
            }
            try {
                this.process = Runtime.getRuntime().exec(this.command);
            }
            catch (IOException e) {
                this.stop(e, Integer.MIN_VALUE);
                return null;
            }
            this.errStream = new FileUtil.Pipe(this.process.getErrorStream(), System.err);
            if (null != this.errSnoop) {
                this.errStream.setSnoop(this.errSnoop);
            }
            this.outStream = new FileUtil.Pipe(this.process.getInputStream(), System.out);
            if (null != this.outSnoop) {
                this.outStream.setSnoop(this.outSnoop);
            }
            this.inStream = new FileUtil.Pipe(System.in, this.process.getOutputStream());
            Runnable processRunner = new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    Throwable thrown = null;
                    int result = Integer.MIN_VALUE;
                    try {
                        new Thread(errStream).start();
                        new Thread(outStream).start();
                        new Thread(inStream).start();
                        process.waitFor();
                        result = process.exitValue();
                    }
                    catch (Throwable e) {
                        thrown = e;
                    }
                    finally {
                        this.stop(thrown, result);
                    }
                }
            };
            Thread result = new Thread(processRunner, this.label);
            result.start();
            return result;
        }

        public final synchronized void stop() {
            if (this.completed) {
                return;
            }
            this.userStopped = true;
            this.stop(null, Integer.MIN_VALUE);
        }

        public final String[] getCommand() {
            Object[] toCopy = this.command;
            if (LangUtil.isEmpty(toCopy)) {
                return new String[0];
            }
            String[] result = new String[toCopy.length];
            System.arraycopy(toCopy, 0, result, 0, result.length);
            return result;
        }

        public final boolean completed() {
            return this.completed;
        }

        public final boolean started() {
            return this.started;
        }

        public final boolean userStopped() {
            return this.userStopped;
        }

        public final Thrown getThrown() {
            return this.makeThrown(null);
        }

        public final int getResult() {
            return this.result;
        }

        protected void doCompleting(Thrown thrown, int result) {
        }

        private final synchronized void stop(Throwable thrown, int result) {
            if (this.completed) {
                throw new IllegalStateException("already completed");
            }
            if (null != this.thrown) {
                throw new IllegalStateException("already set thrown: " + thrown);
            }
            this.thrown = this.makeThrown(thrown);
            if (null != this.process) {
                this.process.destroy();
            }
            if (null != this.inStream) {
                this.inStream.halt(false, true);
                this.inStream = null;
            }
            if (null != this.outStream) {
                this.outStream.halt(true, true);
                this.outStream = null;
            }
            if (null != this.errStream) {
                this.errStream.halt(true, true);
                this.errStream = null;
            }
            if (Integer.MIN_VALUE != result) {
                this.result = result;
            }
            this.completed = true;
            this.doCompleting(this.thrown, result);
        }

        private final synchronized Thrown makeThrown(Throwable processThrown) {
            if (null != this.thrown) {
                return this.thrown;
            }
            return new Thrown(processThrown, null == this.outStream ? null : this.outStream.getThrown(), null == this.errStream ? null : this.errStream.getThrown(), null == this.inStream ? null : this.inStream.getThrown());
        }

        public static class Thrown {
            public final Throwable fromProcess;
            public final Throwable fromErrPipe;
            public final Throwable fromOutPipe;
            public final Throwable fromInPipe;
            public final boolean thrown;

            private Thrown(Throwable fromProcess, Throwable fromOutPipe, Throwable fromErrPipe, Throwable fromInPipe) {
                this.fromProcess = fromProcess;
                this.fromErrPipe = fromErrPipe;
                this.fromOutPipe = fromOutPipe;
                this.fromInPipe = fromInPipe;
                this.thrown = null != fromProcess || null != fromInPipe || null != fromOutPipe || null != fromErrPipe;
            }

            public String toString() {
                StringBuffer sb = new StringBuffer();
                this.append(sb, this.fromProcess, "process");
                this.append(sb, this.fromOutPipe, " stdout");
                this.append(sb, this.fromErrPipe, " stderr");
                this.append(sb, this.fromInPipe, "  stdin");
                if (0 == sb.length()) {
                    return "Thrown (none)";
                }
                return sb.toString();
            }

            private void append(StringBuffer sb, Throwable thrown, String label) {
                if (null != thrown) {
                    sb.append("from " + label + ": ");
                    sb.append(LangUtil.renderExceptionShort(thrown));
                    sb.append(EOL);
                }
            }
        }
    }

    public static class StringChecker {
        static StringChecker TEST_PACKAGES = new StringChecker(new String[]{"org.aspectj.testing", "org.eclipse.jdt.internal.junit", "junit.framework.", "org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner"});
        String[] infixes;

        StringChecker(String[] infixes) {
            this.infixes = infixes;
        }

        public boolean acceptString(String input) {
            boolean result = false;
            if (!LangUtil.isEmpty(input)) {
                for (int i = 0; !result && i < this.infixes.length; ++i) {
                    result = -1 != input.indexOf(this.infixes[i]);
                }
            }
            return result;
        }
    }
}

