/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentMigratorToV4Reporter
implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(AttachmentMigratorToV4Reporter.class);
    public static final String V_3_TO_V_4_REPORT_LOG = "v3-to-v4-report.log";
    private OutputStreamWriterFactory factory;
    private OutputStreamWriter writer;
    private File reportFile;

    public AttachmentMigratorToV4Reporter(OutputStreamWriterFactory factory) {
        this.factory = factory;
    }

    public synchronized void initFile(FilesystemPath path) throws IOException {
        this.reportFile = path.path(new String[]{V_3_TO_V_4_REPORT_LOG}).asJavaFile();
        int fileNumber = 1;
        while (this.reportFile.exists()) {
            this.reportFile = path.path(new String[]{"v3-to-v4-report.log." + fileNumber++}).asJavaFile();
        }
        if (!this.reportFile.createNewFile()) {
            String msg = "Could not create a report reportFile " + this.reportFile.getAbsolutePath();
            logger.error(msg);
            throw new IOException(msg);
        }
        this.writer = this.factory.create(this.reportFile);
    }

    public synchronized void reportFailedFile(String path, String reason) {
        this.writeMessage(String.format("%s\t%s", path, reason));
    }

    public synchronized void writeMessage(String message) {
        try {
            this.writer.write(String.format("%s%n", message));
        }
        catch (IOException e) {
            logger.error("Failed to write to the V4 migration report file {}", (Object)this.reportFile.getAbsolutePath());
        }
    }

    @Override
    public synchronized void close() {
        if (this.writer != null) {
            try {
                this.writer.flush();
                this.writer.close();
                this.writer = null;
            }
            catch (IOException e) {
                logger.error("Failed to close V4 migration report file {}", (Object)this.reportFile.getAbsolutePath());
            }
        }
    }

    public static class OutputStreamWriterFactory {
        public OutputStreamWriter create(File file) throws IOException {
            FileOutputStream fileStream = new FileOutputStream(file);
            return new OutputStreamWriter((OutputStream)fileStream, StandardCharsets.UTF_8);
        }
    }
}

