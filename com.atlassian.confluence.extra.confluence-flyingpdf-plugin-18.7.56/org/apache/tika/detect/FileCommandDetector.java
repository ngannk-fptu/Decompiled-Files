/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.config.Field;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.BoundedInputStream;
import org.apache.tika.io.IOExceptionWithCause;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.external.ExternalParser;
import org.apache.tika.utils.ProcessUtils;

public class FileCommandDetector
implements Detector {
    private static final Logger LOGGER = Logger.getLogger(FileCommandDetector.class.getName());
    private static boolean HAS_WARNED = false;
    private static final long DEFAULT_TIMEOUT_MS = 6000L;
    private static String DEFAULT_FILE_COMMAND_PATH = "file";
    private Boolean hasFileCommand = null;
    private String fileCommandPath = DEFAULT_FILE_COMMAND_PATH;
    private int maxBytes = 1000000;
    private long timeoutMs = 6000L;

    public static boolean checkHasFile() {
        return FileCommandDetector.checkHasFile(DEFAULT_FILE_COMMAND_PATH);
    }

    public static boolean checkHasFile(String fileCommandPath) {
        String[] commandline = new String[]{fileCommandPath, "-v"};
        return ExternalParser.check(commandline, new int[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        if (this.hasFileCommand == null) {
            this.hasFileCommand = FileCommandDetector.checkHasFile(this.fileCommandPath);
        }
        if (!this.hasFileCommand.booleanValue()) {
            if (!HAS_WARNED) {
                LOGGER.log(Level.WARNING, "'file' command isn't working: '" + this.fileCommandPath + "'");
                HAS_WARNED = true;
            }
            return MediaType.OCTET_STREAM;
        }
        TikaInputStream tis = TikaInputStream.cast(input);
        if (tis != null) {
            return this.detectOnPath(tis.getPath());
        }
        input.mark(this.maxBytes);
        TemporaryResources tmp = new TemporaryResources();
        try {
            Path tmpFile = tmp.createTempFile();
            Files.copy(new BoundedInputStream(this.maxBytes, input), tmpFile, StandardCopyOption.REPLACE_EXISTING);
            MediaType mediaType = this.detectOnPath(tmpFile);
            return mediaType;
        }
        finally {
            tmp.close();
            input.reset();
        }
    }

    private MediaType detectOnPath(Path path) throws IOException {
        String[] args = new String[]{ProcessUtils.escapeCommandLine(this.fileCommandPath), "-b", "--mime-type", ProcessUtils.escapeCommandLine(path.toAbsolutePath().toString())};
        ProcessBuilder builder = new ProcessBuilder(args);
        Process process = builder.start();
        StringStreamGobbler errorGobbler = new StringStreamGobbler(process.getErrorStream());
        StringStreamGobbler outGobbler = new StringStreamGobbler(process.getInputStream());
        Thread errorThread = new Thread(errorGobbler);
        Thread outThread = new Thread(outGobbler);
        errorThread.start();
        outThread.start();
        process.getErrorStream();
        process.getInputStream();
        boolean finished = false;
        try {
            finished = process.waitFor(this.timeoutMs, TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IOExceptionWithCause(new TimeoutException("timed out"));
            }
            int exitValue = process.exitValue();
            if (exitValue != 0) {
                throw new IOExceptionWithCause(new RuntimeException("bad exit value"));
            }
            errorThread.join();
            outThread.join();
        }
        catch (InterruptedException exitValue) {
            // empty catch block
        }
        MediaType mt = MediaType.parse(outGobbler.toString().trim());
        if (mt == null) {
            return MediaType.OCTET_STREAM;
        }
        return mt;
    }

    @Field
    public void setFilePath(String fileCommandPath) {
        this.fileCommandPath = fileCommandPath;
        FileCommandDetector.checkHasFile(this.fileCommandPath);
    }

    @Field
    public void setMaxBytes(int maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Field
    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    private static class StringStreamGobbler
    implements Runnable {
        private final BufferedReader reader;
        private final StringBuilder sb = new StringBuilder();

        public StringStreamGobbler(InputStream is) {
            this.reader = new BufferedReader(new InputStreamReader((InputStream)new BufferedInputStream(is), StandardCharsets.UTF_8));
        }

        @Override
        public void run() {
            String line = null;
            try {
                while ((line = this.reader.readLine()) != null) {
                    this.sb.append(line);
                    this.sb.append("\n");
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }

        public void stopGobblingAndDie() {
            IOUtils.closeQuietly(this.reader);
        }

        public String toString() {
            return this.sb.toString();
        }
    }
}

