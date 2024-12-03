/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.B2CConverter
 */
package org.apache.catalina.valves;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;

public class AccessLogValve
extends AbstractAccessLogValve {
    private static final Log log = LogFactory.getLog(AccessLogValve.class);
    private volatile String dateStamp = "";
    private String directory = "logs";
    protected volatile String prefix = "access_log";
    protected boolean rotatable = true;
    protected boolean renameOnRotate = false;
    private boolean buffered = true;
    protected volatile String suffix = "";
    protected PrintWriter writer = null;
    protected SimpleDateFormat fileDateFormatter = null;
    protected File currentLogFile = null;
    private volatile long rotationLastChecked = 0L;
    private boolean checkExists = false;
    protected String fileDateFormat = ".yyyy-MM-dd";
    protected volatile String encoding = null;
    private int maxDays = -1;
    private volatile boolean checkForOldLogs = false;

    public int getMaxDays() {
        return this.maxDays;
    }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isCheckExists() {
        return this.checkExists;
    }

    public void setCheckExists(boolean checkExists) {
        this.checkExists = checkExists;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isRotatable() {
        return this.rotatable;
    }

    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }

    public boolean isRenameOnRotate() {
        return this.renameOnRotate;
    }

    public void setRenameOnRotate(boolean renameOnRotate) {
        this.renameOnRotate = renameOnRotate;
    }

    public boolean isBuffered() {
        return this.buffered;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFileDateFormat() {
        return this.fileDateFormat;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFileDateFormat(String fileDateFormat) {
        String newFormat = fileDateFormat == null ? "" : fileDateFormat;
        this.fileDateFormat = newFormat;
        AccessLogValve accessLogValve = this;
        synchronized (accessLogValve) {
            this.fileDateFormatter = new SimpleDateFormat(newFormat, Locale.US);
            this.fileDateFormatter.setTimeZone(TimeZone.getDefault());
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding != null && encoding.length() > 0 ? encoding : null;
    }

    @Override
    public synchronized void backgroundProcess() {
        if (this.getState().isAvailable() && this.getEnabled() && this.writer != null && this.buffered) {
            this.writer.flush();
        }
        int maxDays = this.maxDays;
        String prefix = this.prefix;
        String suffix = this.suffix;
        if (this.rotatable && this.checkForOldLogs && maxDays > 0) {
            String[] oldAccessLogs;
            long deleteIfLastModifiedBefore = System.currentTimeMillis() - (long)maxDays * 24L * 60L * 60L * 1000L;
            File dir = this.getDirectoryFile();
            if (dir.isDirectory() && (oldAccessLogs = dir.list()) != null) {
                for (String oldAccessLog : oldAccessLogs) {
                    File file;
                    boolean match = false;
                    if (prefix != null && prefix.length() > 0) {
                        if (!oldAccessLog.startsWith(prefix)) continue;
                        match = true;
                    }
                    if (suffix != null && suffix.length() > 0) {
                        if (!oldAccessLog.endsWith(suffix)) continue;
                        match = true;
                    }
                    if (!match || !(file = new File(dir, oldAccessLog)).isFile() || file.lastModified() >= deleteIfLastModifiedBefore || file.delete()) continue;
                    log.warn((Object)sm.getString("accessLogValve.deleteFail", new Object[]{file.getAbsolutePath()}));
                }
            }
            this.checkForOldLogs = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void rotate() {
        long systime;
        if (this.rotatable && (systime = System.currentTimeMillis()) - this.rotationLastChecked > 1000L) {
            AccessLogValve accessLogValve = this;
            synchronized (accessLogValve) {
                if (systime - this.rotationLastChecked > 1000L) {
                    this.rotationLastChecked = systime;
                    String tsDate = this.fileDateFormatter.format(new Date(systime));
                    if (!this.dateStamp.equals(tsDate)) {
                        this.close(true);
                        this.dateStamp = tsDate;
                        this.open();
                    }
                }
            }
        }
    }

    public synchronized boolean rotate(String newFileName) {
        if (this.currentLogFile != null) {
            File holder = this.currentLogFile;
            this.close(false);
            try {
                holder.renameTo(new File(newFileName));
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                log.error((Object)sm.getString("accessLogValve.rotateFail"), e);
            }
            this.dateStamp = this.fileDateFormatter.format(new Date(System.currentTimeMillis()));
            this.open();
            return true;
        }
        return false;
    }

    private File getDirectoryFile() {
        File dir = new File(this.directory);
        if (!dir.isAbsolute()) {
            dir = new File(this.getContainer().getCatalinaBase(), this.directory);
        }
        return dir;
    }

    private File getLogFile(boolean useDateStamp) {
        File pathname;
        File parent;
        File dir = this.getDirectoryFile();
        if (!dir.mkdirs() && !dir.isDirectory()) {
            log.error((Object)sm.getString("accessLogValve.openDirFail", new Object[]{dir}));
        }
        if (!(parent = (pathname = useDateStamp ? new File(dir.getAbsoluteFile(), this.prefix + this.dateStamp + this.suffix) : new File(dir.getAbsoluteFile(), this.prefix + this.suffix)).getParentFile()).mkdirs() && !parent.isDirectory()) {
            log.error((Object)sm.getString("accessLogValve.openDirFail", new Object[]{parent}));
        }
        return pathname;
    }

    private void restore() {
        File newLogFile = this.getLogFile(false);
        File rotatedLogFile = this.getLogFile(true);
        if (rotatedLogFile.exists() && !newLogFile.exists() && !rotatedLogFile.equals(newLogFile)) {
            try {
                if (!rotatedLogFile.renameTo(newLogFile)) {
                    log.error((Object)sm.getString("accessLogValve.renameFail", new Object[]{rotatedLogFile, newLogFile}));
                }
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                log.error((Object)sm.getString("accessLogValve.renameFail", new Object[]{rotatedLogFile, newLogFile}), e);
            }
        }
    }

    private synchronized void close(boolean rename) {
        if (this.writer == null) {
            return;
        }
        this.writer.flush();
        this.writer.close();
        if (rename && this.renameOnRotate) {
            File newLogFile = this.getLogFile(true);
            if (!newLogFile.exists()) {
                try {
                    if (!this.currentLogFile.renameTo(newLogFile)) {
                        log.error((Object)sm.getString("accessLogValve.renameFail", new Object[]{this.currentLogFile, newLogFile}));
                    }
                }
                catch (Throwable e) {
                    ExceptionUtils.handleThrowable((Throwable)e);
                    log.error((Object)sm.getString("accessLogValve.renameFail", new Object[]{this.currentLogFile, newLogFile}), e);
                }
            } else {
                log.error((Object)sm.getString("accessLogValve.alreadyExists", new Object[]{this.currentLogFile, newLogFile}));
            }
        }
        this.writer = null;
        this.dateStamp = "";
        this.currentLogFile = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void log(CharArrayWriter message) {
        AccessLogValve accessLogValve;
        this.rotate();
        if (this.checkExists) {
            accessLogValve = this;
            synchronized (accessLogValve) {
                if (this.currentLogFile != null && !this.currentLogFile.exists()) {
                    try {
                        this.close(false);
                    }
                    catch (Throwable e) {
                        ExceptionUtils.handleThrowable((Throwable)e);
                        log.info((Object)sm.getString("accessLogValve.closeFail"), e);
                    }
                    this.dateStamp = this.fileDateFormatter.format(new Date(System.currentTimeMillis()));
                    this.open();
                }
            }
        }
        try {
            message.write(System.lineSeparator());
            accessLogValve = this;
            synchronized (accessLogValve) {
                if (this.writer != null) {
                    message.writeTo(this.writer);
                    if (!this.buffered) {
                        this.writer.flush();
                    }
                }
            }
        }
        catch (IOException ioe) {
            log.warn((Object)sm.getString("accessLogValve.writeFail", new Object[]{message.toString()}), (Throwable)ioe);
        }
    }

    protected synchronized void open() {
        File pathname = this.getLogFile(this.rotatable && !this.renameOnRotate);
        Charset charset = null;
        if (this.encoding != null) {
            try {
                charset = B2CConverter.getCharset((String)this.encoding);
            }
            catch (UnsupportedEncodingException ex) {
                log.error((Object)sm.getString("accessLogValve.unsupportedEncoding", new Object[]{this.encoding}), (Throwable)ex);
            }
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        try {
            this.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(pathname, true), charset), 128000), false);
            this.currentLogFile = pathname;
        }
        catch (IOException e) {
            this.writer = null;
            this.currentLogFile = null;
            log.error((Object)sm.getString("accessLogValve.openFail", new Object[]{pathname, System.getProperty("user.name")}), (Throwable)e);
        }
        this.checkForOldLogs = true;
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        String format = this.getFileDateFormat();
        this.fileDateFormatter = new SimpleDateFormat(format, Locale.US);
        this.fileDateFormatter.setTimeZone(TimeZone.getDefault());
        this.dateStamp = this.fileDateFormatter.format(new Date(System.currentTimeMillis()));
        if (this.rotatable && this.renameOnRotate) {
            this.restore();
        }
        this.open();
        super.startInternal();
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.close(false);
    }
}

