/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.tika.config.Field;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.BoundedInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.ExternalProcess;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.external.ExternalParser;
import org.apache.tika.utils.FileProcessResult;
import org.apache.tika.utils.ProcessUtils;
import org.apache.tika.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCommandDetector
implements Detector {
    public static Property FILE_MIME = Property.externalText("file:mime");
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCommandDetector.class);
    private static final long DEFAULT_TIMEOUT_MS = 6000L;
    private static final String DEFAULT_FILE_COMMAND_PATH = "file";
    private static boolean HAS_WARNED = false;
    private Boolean hasFileCommand = null;
    private String fileCommandPath = "file";
    private int maxBytes = 1000000;
    private long timeoutMs = 6000L;
    private boolean useMime = false;

    public static boolean checkHasFile() {
        return FileCommandDetector.checkHasFile(DEFAULT_FILE_COMMAND_PATH);
    }

    public static boolean checkHasFile(String fileCommandPath) {
        String[] commandline = new String[]{fileCommandPath, "-v"};
        return ExternalParser.check(commandline, new int[0]);
    }

    /*
     * Loose catch block
     */
    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        if (this.hasFileCommand == null) {
            this.hasFileCommand = FileCommandDetector.checkHasFile(this.fileCommandPath);
        }
        if (!this.hasFileCommand.booleanValue()) {
            if (!HAS_WARNED) {
                LOGGER.warn("'file' command isn't working: '" + this.fileCommandPath + "'");
                HAS_WARNED = true;
            }
            return MediaType.OCTET_STREAM;
        }
        TikaInputStream tis = TikaInputStream.cast(input);
        if (tis != null) {
            return this.detectOnPath(tis.getPath(), metadata);
        }
        input.mark(this.maxBytes);
        try {
            try (TemporaryResources tmp = new TemporaryResources();){
                Path tmpFile = tmp.createTempFile(metadata);
                Files.copy(new BoundedInputStream(this.maxBytes, input), tmpFile, StandardCopyOption.REPLACE_EXISTING);
                MediaType mediaType = this.detectOnPath(tmpFile, metadata);
                return mediaType;
            }
            {
                catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
        finally {
            input.reset();
        }
    }

    private MediaType detectOnPath(Path path, Metadata metadata) throws IOException {
        String[] args = new String[]{ProcessUtils.escapeCommandLine(this.fileCommandPath), "-b", "--mime-type", ProcessUtils.escapeCommandLine(path.toAbsolutePath().toString())};
        ProcessBuilder builder = new ProcessBuilder(args);
        FileProcessResult result = ProcessUtils.execute(builder, this.timeoutMs, 10000, 10000);
        if (result.isTimeout()) {
            metadata.set(ExternalProcess.IS_TIMEOUT, true);
            return MediaType.OCTET_STREAM;
        }
        if (result.getExitValue() != 0) {
            metadata.set(ExternalProcess.EXIT_VALUE, result.getExitValue());
            return MediaType.OCTET_STREAM;
        }
        String mimeString = result.getStdout();
        if (StringUtils.isBlank(mimeString)) {
            return MediaType.OCTET_STREAM;
        }
        metadata.set(FILE_MIME, mimeString);
        if (this.useMime) {
            MediaType mt = MediaType.parse(mimeString);
            if (mt == null) {
                return MediaType.OCTET_STREAM;
            }
            return mt;
        }
        return MediaType.OCTET_STREAM;
    }

    @Field
    public void setFilePath(String fileCommandPath) {
        this.fileCommandPath = fileCommandPath;
        FileCommandDetector.checkHasFile(this.fileCommandPath);
    }

    @Field
    public void setUseMime(boolean useMime) {
        this.useMime = useMime;
    }

    public boolean isUseMime() {
        return this.useMime;
    }

    @Field
    public void setMaxBytes(int maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Field
    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}

