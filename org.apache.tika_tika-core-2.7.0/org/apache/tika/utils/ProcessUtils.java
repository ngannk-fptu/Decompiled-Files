/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.tika.utils.FileProcessResult;
import org.apache.tika.utils.StreamGobbler;
import org.apache.tika.utils.StringUtils;
import org.apache.tika.utils.SystemUtils;

public class ProcessUtils {
    private static final ConcurrentHashMap<String, Process> PROCESS_MAP = new ConcurrentHashMap();

    private static String register(Process p) {
        String id = UUID.randomUUID().toString();
        PROCESS_MAP.put(id, p);
        return id;
    }

    private static Process release(String id) {
        return PROCESS_MAP.remove(id);
    }

    public static String escapeCommandLine(String arg) {
        if (arg == null) {
            return arg;
        }
        if (arg.contains(" ") && SystemUtils.IS_OS_WINDOWS && !arg.startsWith("\"") && !arg.endsWith("\"")) {
            arg = "\"" + arg + "\"";
        }
        return arg;
    }

    public static String unescapeCommandLine(String arg) {
        if (arg.contains(" ") && SystemUtils.IS_OS_WINDOWS && arg.startsWith("\"") && arg.endsWith("\"")) {
            arg = arg.substring(1, arg.length() - 1);
        }
        return arg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static FileProcessResult execute(ProcessBuilder pb, long timeoutMillis, int maxStdoutBuffer, int maxStdErrBuffer) throws IOException {
        Process p = null;
        String id = null;
        try {
            p = pb.start();
            id = ProcessUtils.register(p);
            long elapsed = -1L;
            long start = System.currentTimeMillis();
            StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(), maxStdoutBuffer);
            StreamGobbler errGobbler = new StreamGobbler(p.getErrorStream(), maxStdErrBuffer);
            Thread outThread = new Thread(outGobbler);
            outThread.start();
            Thread errThread = new Thread(errGobbler);
            errThread.start();
            int exitValue = -1;
            boolean complete = false;
            try {
                complete = p.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
                elapsed = System.currentTimeMillis() - start;
                if (complete) {
                    exitValue = p.exitValue();
                    outThread.join(1000L);
                    errThread.join(1000L);
                } else {
                    p.destroyForcibly();
                    outThread.join(1000L);
                    errThread.join(1000L);
                    boolean completed = p.waitFor(500L, TimeUnit.MILLISECONDS);
                    if (completed) {
                        try {
                            exitValue = p.exitValue();
                        }
                        catch (IllegalThreadStateException illegalThreadStateException) {
                            // empty catch block
                        }
                    }
                }
            }
            catch (InterruptedException e) {
                exitValue = -1000;
            }
            finally {
                outThread.interrupt();
                errThread.interrupt();
            }
            FileProcessResult result = new FileProcessResult();
            result.processTimeMillis = elapsed;
            result.stderrLength = errGobbler.getStreamLength();
            result.stdoutLength = outGobbler.getStreamLength();
            result.isTimeout = !complete;
            result.exitValue = exitValue;
            result.stdout = StringUtils.joinWith("\n", outGobbler.getLines());
            result.stderr = StringUtils.joinWith("\n", errGobbler.getLines());
            result.stdoutTruncated = outGobbler.getIsTruncated();
            result.stderrTruncated = errGobbler.getIsTruncated();
            FileProcessResult fileProcessResult = result;
            return fileProcessResult;
        }
        finally {
            if (p != null) {
                p.destroyForcibly();
            }
            if (id != null) {
                ProcessUtils.release(id);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static FileProcessResult execute(ProcessBuilder pb, long timeoutMillis, Path stdoutRedirect, int maxStdErrBuffer) throws IOException {
        if (!Files.isDirectory(stdoutRedirect.getParent(), new LinkOption[0])) {
            Files.createDirectories(stdoutRedirect.getParent(), new FileAttribute[0]);
        }
        pb.redirectOutput(stdoutRedirect.toFile());
        Process p = null;
        String id = null;
        try {
            p = pb.start();
            id = ProcessUtils.register(p);
            long elapsed = -1L;
            long start = System.currentTimeMillis();
            StreamGobbler errGobbler = new StreamGobbler(p.getErrorStream(), maxStdErrBuffer);
            Thread errThread = new Thread(errGobbler);
            errThread.start();
            int exitValue = -1;
            boolean complete = false;
            try {
                complete = p.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
                elapsed = System.currentTimeMillis() - start;
                if (complete) {
                    exitValue = p.exitValue();
                    errThread.join(1000L);
                } else {
                    p.destroyForcibly();
                    errThread.join(1000L);
                }
            }
            catch (InterruptedException e) {
                exitValue = -1000;
            }
            FileProcessResult result = new FileProcessResult();
            result.processTimeMillis = elapsed;
            result.stderrLength = errGobbler.getStreamLength();
            result.stdoutLength = Files.size(stdoutRedirect);
            result.isTimeout = !complete;
            result.exitValue = exitValue;
            result.stdout = "";
            result.stderr = StringUtils.joinWith("\n", errGobbler.getLines());
            result.stdoutTruncated = false;
            result.stderrTruncated = errGobbler.getIsTruncated();
            FileProcessResult fileProcessResult = result;
            return fileProcessResult;
        }
        finally {
            if (p != null) {
                p.destroyForcibly();
            }
            ProcessUtils.release(id);
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> PROCESS_MAP.forEachValue(1L, Process::destroyForcibly)));
    }
}

