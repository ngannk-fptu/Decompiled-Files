/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.config.ConfigurationException;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.diagnostics.DiagnosticsLogWriterImpl;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

final class DiagnosticsLogFile {
    private static final int ONE_MB = 0x100000;
    volatile File file;
    private final Diagnostics diagnostics;
    private final ILogger logger;
    private final String fileName;
    private final DiagnosticsLogWriterImpl logWriter;
    private int index;
    private PrintWriter printWriter;
    private int maxRollingFileCount;
    private int maxRollingFileSizeBytes;

    DiagnosticsLogFile(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
        this.logger = diagnostics.logger;
        this.fileName = diagnostics.baseFileName + "-%03d.log";
        this.logWriter = new DiagnosticsLogWriterImpl(diagnostics.includeEpochTime);
        this.maxRollingFileCount = diagnostics.properties.getInteger(Diagnostics.MAX_ROLLED_FILE_COUNT);
        this.maxRollingFileSizeBytes = Math.round(1048576.0f * diagnostics.properties.getFloat(Diagnostics.MAX_ROLLED_FILE_SIZE_MB));
        this.logger.finest("maxRollingFileSizeBytes:" + this.maxRollingFileSizeBytes + " maxRollingFileCount:" + this.maxRollingFileCount);
    }

    public void write(DiagnosticsPlugin plugin) {
        try {
            if (this.file == null) {
                this.file = this.newFile(this.index);
                this.printWriter = this.newWriter();
                this.renderStaticPlugins();
            }
            this.renderPlugin(plugin);
            this.printWriter.flush();
            if (this.file.length() >= (long)this.maxRollingFileSizeBytes) {
                this.rollover();
            }
        }
        catch (IOException e) {
            this.logger.warning("Failed to write to file:" + this.file.getAbsolutePath(), e);
            this.file = null;
            IOUtil.closeResource(this.printWriter);
            this.printWriter = null;
        }
        catch (RuntimeException e) {
            this.logger.warning("Failed to write file: " + this.file, e);
        }
    }

    private File newFile(int index) {
        this.createDirectoryIfDoesNotExist();
        return new File(this.diagnostics.directory, String.format(this.fileName, index));
    }

    private void createDirectoryIfDoesNotExist() {
        File dir = this.diagnostics.directory;
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new ConfigurationException("Configured path for diagnostics log file '" + dir + "' exists, but it's not a directory");
            }
        } else if (!dir.mkdirs()) {
            throw new ConfigurationException("Error while creating a directory '" + dir + "' for diagnostics log files. Are you having sufficient rights on the filesystem?");
        }
    }

    private void renderStaticPlugins() {
        for (DiagnosticsPlugin plugin : this.diagnostics.staticTasks.get()) {
            this.renderPlugin(plugin);
        }
    }

    private void renderPlugin(DiagnosticsPlugin plugin) {
        this.logWriter.init(this.printWriter);
        plugin.run(this.logWriter);
    }

    private PrintWriter newWriter() throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(this.file, true);
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter((OutputStream)fos, encoder), Short.MAX_VALUE));
    }

    private void rollover() {
        IOUtil.closeResource(this.printWriter);
        this.printWriter = null;
        this.file = null;
        ++this.index;
        File file = this.newFile(this.index - this.maxRollingFileCount);
        IOUtil.deleteQuietly(file);
    }
}

