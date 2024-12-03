/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHolder;
import org.aspectj.bridge.Version;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.Traceable;

public class Dump {
    public static final String DUMP_CONDITION_PROPERTY = "org.aspectj.weaver.Dump.condition";
    public static final String DUMP_DIRECTORY_PROPERTY = "org.aspectj.dump.directory";
    private static final String FILENAME_PREFIX = "ajcore";
    private static final String FILENAME_SUFFIX = "txt";
    public static final String UNKNOWN_FILENAME = "Unknown";
    public static final String DUMP_EXCLUDED = "Excluded";
    public static final String NULL_OR_EMPTY = "Empty";
    private static Class<?> exceptionClass;
    private static IMessage.Kind conditionKind;
    private static File directory;
    private String reason;
    private String fileName;
    private PrintStream print;
    private static String[] savedCommandLine;
    private static List<String> savedFullClasspath;
    private static IMessageHolder savedMessageHolder;
    private static String lastDumpFileName;
    private static boolean preserveOnNextReset;
    private static Trace trace;

    public static void preserveOnNextReset() {
        preserveOnNextReset = true;
    }

    public static void reset() {
        if (preserveOnNextReset) {
            preserveOnNextReset = false;
            return;
        }
        savedMessageHolder = null;
    }

    public static String dump(String reason) {
        String fileName = UNKNOWN_FILENAME;
        try (Dump dump = null;){
            dump = new Dump(reason);
            fileName = dump.getFileName();
            dump.dumpDefault();
        }
        return fileName;
    }

    public static String dumpWithException(Throwable th) {
        return Dump.dumpWithException(savedMessageHolder, th);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String dumpWithException(IMessageHolder messageHolder, Throwable th) {
        if (!Dump.getDumpOnException()) {
            return null;
        }
        if (trace.isTraceEnabled()) {
            trace.enter("dumpWithException", (Object)null, new Object[]{messageHolder, th});
        }
        String fileName = UNKNOWN_FILENAME;
        try (Dump dump = null;){
            dump = new Dump(th.getClass().getName());
            fileName = dump.getFileName();
            dump.dumpException(messageHolder, th);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("dumpWithException", fileName);
        }
        return fileName;
    }

    public static String dumpOnExit() {
        return Dump.dumpOnExit(savedMessageHolder, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String dumpOnExit(IMessageHolder messageHolder, boolean reset) {
        if (!Dump.getDumpOnException()) {
            return null;
        }
        if (trace.isTraceEnabled()) {
            trace.enter("dumpOnExit", null, messageHolder);
        }
        String fileName = UNKNOWN_FILENAME;
        if (!Dump.shouldDumpOnExit(messageHolder)) {
            fileName = DUMP_EXCLUDED;
        } else {
            try (Dump dump = null;){
                dump = new Dump(conditionKind.toString());
                fileName = dump.getFileName();
                dump.dumpDefault(messageHolder);
            }
        }
        if (reset) {
            messageHolder.clearMessages();
        }
        if (trace.isTraceEnabled()) {
            trace.exit("dumpOnExit", fileName);
        }
        return fileName;
    }

    private static boolean shouldDumpOnExit(IMessageHolder messageHolder) {
        boolean result;
        if (trace.isTraceEnabled()) {
            trace.enter("shouldDumpOnExit", null, messageHolder);
        }
        if (trace.isTraceEnabled()) {
            trace.event("shouldDumpOnExit", null, conditionKind);
        }
        boolean bl = result = messageHolder == null || messageHolder.hasAnyMessage(conditionKind, true);
        if (trace.isTraceEnabled()) {
            trace.exit("shouldDumpOnExit", result);
        }
        return result;
    }

    public static void setDumpOnException(boolean b) {
        exceptionClass = b ? Throwable.class : null;
    }

    public static boolean setDumpDirectory(String directoryName) {
        if (trace.isTraceEnabled()) {
            trace.enter("setDumpDirectory", null, directoryName);
        }
        boolean success = false;
        File newDirectory = new File(directoryName);
        if (newDirectory.exists()) {
            directory = newDirectory;
            success = true;
        }
        if (trace.isTraceEnabled()) {
            trace.exit("setDumpDirectory", success);
        }
        return success;
    }

    public static boolean getDumpOnException() {
        return exceptionClass != null;
    }

    public static boolean setDumpOnExit(IMessage.Kind condition) {
        if (trace.isTraceEnabled()) {
            trace.event("setDumpOnExit", null, condition);
        }
        conditionKind = condition;
        return true;
    }

    public static boolean setDumpOnExit(String condition) {
        for (IMessage.Kind kind : IMessage.KINDS) {
            if (!kind.toString().equals(condition)) continue;
            return Dump.setDumpOnExit(kind);
        }
        return false;
    }

    public static IMessage.Kind getDumpOnExit() {
        return conditionKind;
    }

    public static String getLastDumpFileName() {
        return lastDumpFileName;
    }

    public static void saveCommandLine(String[] args) {
        savedCommandLine = new String[args.length];
        System.arraycopy(args, 0, savedCommandLine, 0, args.length);
    }

    public static void saveFullClasspath(List<String> list) {
        savedFullClasspath = list;
    }

    public static void saveMessageHolder(IMessageHolder holder) {
        savedMessageHolder = holder;
    }

    private Dump(String reason) {
        if (trace.isTraceEnabled()) {
            trace.enter("<init>", (Object)this, reason);
        }
        this.reason = reason;
        this.openDump();
        this.dumpAspectJProperties();
        this.dumpDumpConfiguration();
        if (trace.isTraceEnabled()) {
            trace.exit("<init>", this);
        }
    }

    public String getFileName() {
        return this.fileName;
    }

    private void dumpDefault() {
        this.dumpDefault(savedMessageHolder);
    }

    private void dumpDefault(IMessageHolder holder) {
        this.dumpSytemProperties();
        this.dumpCommandLine();
        this.dumpFullClasspath();
        this.dumpCompilerMessages(holder);
    }

    private void dumpException(IMessageHolder messageHolder, Throwable th) {
        this.println("---- Exception Information ---");
        this.println(th);
        this.dumpDefault(messageHolder);
    }

    private void dumpAspectJProperties() {
        this.println("---- AspectJ Properties ---");
        this.println("AspectJ Compiler " + Version.getText() + " built on " + Version.getTimeText());
    }

    private void dumpDumpConfiguration() {
        this.println("---- Dump Properties ---");
        this.println("Dump file: " + this.fileName);
        this.println("Dump reason: " + this.reason);
        this.println("Dump on exception: " + (exceptionClass != null));
        this.println("Dump at exit condition: " + conditionKind);
    }

    private void dumpFullClasspath() {
        this.println("---- Full Classpath ---");
        if (savedFullClasspath != null && savedFullClasspath.size() > 0) {
            for (String fileName : savedFullClasspath) {
                File file = new File(fileName);
                this.println(file);
            }
        } else {
            this.println(NULL_OR_EMPTY);
        }
    }

    private void dumpSytemProperties() {
        this.println("---- System Properties ---");
        Properties props = System.getProperties();
        this.println(props);
    }

    private void dumpCommandLine() {
        this.println("---- Command Line ---");
        this.println(savedCommandLine);
    }

    private void dumpCompilerMessages(IMessageHolder messageHolder) {
        this.println("---- Compiler Messages ---");
        if (messageHolder != null) {
            for (IMessage message : messageHolder.getUnmodifiableListView()) {
                this.println(message.toString());
            }
        } else {
            this.println(NULL_OR_EMPTY);
        }
    }

    private void openDump() {
        if (this.print != null) {
            return;
        }
        Date now = new Date();
        this.fileName = "ajcore." + new SimpleDateFormat("yyyyMMdd").format(now) + "." + new SimpleDateFormat("HHmmss.SSS").format(now) + "." + FILENAME_SUFFIX;
        try {
            File file = new File(directory, this.fileName);
            this.print = new PrintStream(new FileOutputStream(file), true);
            trace.info("Dumping to " + file.getAbsolutePath());
        }
        catch (Exception ex) {
            this.print = System.err;
            trace.info("Dumping to stderr");
            this.fileName = UNKNOWN_FILENAME;
        }
        lastDumpFileName = this.fileName;
    }

    public void close() {
        this.print.close();
    }

    private void println(Object obj) {
        this.print.println(obj);
    }

    private void println(Object[] array) {
        if (array == null) {
            this.println(NULL_OR_EMPTY);
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.print.println(array[i]);
        }
    }

    private void println(Properties props) {
        for (String string : props.keySet()) {
            String value = props.getProperty(string);
            this.print.println(string + "=" + value);
        }
    }

    private void println(Throwable th) {
        th.printStackTrace(this.print);
    }

    private void println(File file) {
        this.print.print(file.getAbsolutePath());
        if (!file.exists()) {
            this.println("(missing)");
        } else if (file.isDirectory()) {
            int count = file.listFiles().length;
            this.println("(" + count + " entries)");
        } else {
            this.println("(" + file.length() + " bytes)");
        }
    }

    private void println(List list) {
        if (list == null || list.isEmpty()) {
            this.println(NULL_OR_EMPTY);
        } else {
            for (Object o : list) {
                if (o instanceof Exception) {
                    this.println((Exception)o);
                    continue;
                }
                this.println(o.toString());
            }
        }
    }

    private static Object formatObj(Object obj) {
        if (obj == null || obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Exception || obj instanceof Character || obj instanceof Class || obj instanceof File || obj instanceof StringBuffer || obj instanceof URL) {
            return obj;
        }
        try {
            if (obj instanceof Traceable) {
                Traceable t = (Traceable)obj;
                return t.toTraceString();
            }
            return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
        }
        catch (Exception ex) {
            return obj.getClass().getName() + "@FFFFFFFF";
        }
    }

    static {
        String directoryName;
        String conditionName;
        conditionKind = IMessage.ABORT;
        directory = new File(".");
        lastDumpFileName = UNKNOWN_FILENAME;
        preserveOnNextReset = false;
        trace = TraceFactory.getTraceFactory().getTrace(Dump.class);
        String exceptionName = System.getProperty("org.aspectj.weaver.Dump.exception", "true");
        if (!exceptionName.equals("false")) {
            Dump.setDumpOnException(true);
        }
        if ((conditionName = System.getProperty(DUMP_CONDITION_PROPERTY)) != null) {
            Dump.setDumpOnExit(conditionName);
        }
        if ((directoryName = System.getProperty(DUMP_DIRECTORY_PROPERTY)) != null) {
            Dump.setDumpDirectory(directoryName);
        }
    }

    public static interface IVisitor {
        public void visitObject(Object var1);

        public void visitList(List var1);
    }

    public static interface INode {
        public void accept(IVisitor var1);
    }
}

