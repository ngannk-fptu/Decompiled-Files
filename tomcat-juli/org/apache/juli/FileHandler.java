/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.AccessController;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import org.apache.juli.ClassLoaderLogManager;
import org.apache.juli.OneLineFormatter;

public class FileHandler
extends Handler {
    public static final int DEFAULT_MAX_DAYS = -1;
    public static final int DEFAULT_BUFFER_SIZE = -1;
    private static final ExecutorService DELETE_FILES_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory("FileHandlerLogFilesCleaner-"));
    private volatile String date = "";
    private String directory;
    private String prefix;
    private String suffix;
    private Boolean rotatable;
    private Integer maxDays;
    private volatile PrintWriter writer = null;
    protected final ReadWriteLock writerLock = new ReentrantReadWriteLock();
    private Integer bufferSize;
    private Pattern pattern;

    public FileHandler() {
        this(null, null, null);
    }

    public FileHandler(String directory, String prefix, String suffix) {
        this(directory, prefix, suffix, null);
    }

    public FileHandler(String directory, String prefix, String suffix, Integer maxDays) {
        this(directory, prefix, suffix, maxDays, null, null);
    }

    public FileHandler(String directory, String prefix, String suffix, Integer maxDays, Boolean rotatable, Integer bufferSize) {
        this.directory = directory;
        this.prefix = prefix;
        this.suffix = suffix;
        this.maxDays = maxDays;
        this.rotatable = rotatable;
        this.bufferSize = bufferSize;
        this.configure();
        this.openWriter();
        this.clean();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void publish(LogRecord record) {
        block17: {
            if (!this.isLoggable(record)) {
                return;
            }
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String tsDate = ts.toString().substring(0, 10);
            this.writerLock.readLock().lock();
            try {
                if (this.rotatable.booleanValue() && !this.date.equals(tsDate)) {
                    this.writerLock.readLock().unlock();
                    this.writerLock.writeLock().lock();
                    try {
                        if (!this.date.equals(tsDate)) {
                            this.closeWriter();
                            this.date = tsDate;
                            this.openWriter();
                            this.clean();
                        }
                    }
                    finally {
                        this.writerLock.readLock().lock();
                        this.writerLock.writeLock().unlock();
                    }
                }
                String result = null;
                try {
                    result = this.getFormatter().format(record);
                }
                catch (Exception e) {
                    this.reportError(null, e, 5);
                    this.writerLock.readLock().unlock();
                    return;
                }
                try {
                    if (this.writer != null) {
                        this.writer.write(result);
                        if (this.bufferSize < 0) {
                            this.writer.flush();
                        }
                        break block17;
                    }
                    this.reportError("FileHandler is closed or not yet initialized, unable to log [" + result + "]", null, 1);
                }
                catch (Exception e) {
                    this.reportError(null, e, 1);
                }
            }
            finally {
                this.writerLock.readLock().unlock();
            }
        }
    }

    @Override
    public void close() {
        this.closeWriter();
    }

    protected void closeWriter() {
        this.writerLock.writeLock().lock();
        try {
            if (this.writer == null) {
                return;
            }
            this.writer.write(this.getFormatter().getTail(this));
            this.writer.flush();
            this.writer.close();
            this.writer = null;
            this.date = "";
        }
        catch (Exception e) {
            this.reportError(null, e, 3);
        }
        finally {
            this.writerLock.writeLock().unlock();
        }
    }

    @Override
    public void flush() {
        this.writerLock.readLock().lock();
        try {
            if (this.writer == null) {
                return;
            }
            this.writer.flush();
        }
        catch (Exception e) {
            this.reportError(null, e, 2);
        }
        finally {
            this.writerLock.readLock().unlock();
        }
    }

    private void configure() {
        String formatterName;
        String encoding;
        boolean shouldCheckForRedundantSeparator;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        this.date = ts.toString().substring(0, 10);
        String className = this.getClass().getName();
        ClassLoader cl = ClassLoaderLogManager.getClassLoader();
        if (this.rotatable == null) {
            this.rotatable = Boolean.valueOf(this.getProperty(className + ".rotatable", "true"));
        }
        if (this.directory == null) {
            this.directory = this.getProperty(className + ".directory", "logs");
        }
        if (this.prefix == null) {
            this.prefix = this.getProperty(className + ".prefix", "juli.");
        }
        if (this.suffix == null) {
            this.suffix = this.getProperty(className + ".suffix", ".log");
        }
        boolean bl = shouldCheckForRedundantSeparator = this.rotatable == false && !this.prefix.isEmpty() && !this.suffix.isEmpty();
        if (shouldCheckForRedundantSeparator && this.prefix.charAt(this.prefix.length() - 1) == this.suffix.charAt(0)) {
            this.suffix = this.suffix.substring(1);
        }
        this.pattern = Pattern.compile("^(" + Pattern.quote(this.prefix) + ")\\d{4}-\\d{1,2}-\\d{1,2}(" + Pattern.quote(this.suffix) + ")$");
        if (this.maxDays == null) {
            String sMaxDays = this.getProperty(className + ".maxDays", String.valueOf(-1));
            try {
                this.maxDays = Integer.valueOf(sMaxDays);
            }
            catch (NumberFormatException ignore) {
                this.maxDays = -1;
            }
        }
        if (this.bufferSize == null) {
            String sBufferSize = this.getProperty(className + ".bufferSize", String.valueOf(-1));
            try {
                this.bufferSize = Integer.valueOf(sBufferSize);
            }
            catch (NumberFormatException ignore) {
                this.bufferSize = -1;
            }
        }
        if ((encoding = this.getProperty(className + ".encoding", null)) != null && encoding.length() > 0) {
            try {
                this.setEncoding(encoding);
            }
            catch (UnsupportedEncodingException ignore) {
                // empty catch block
            }
        }
        this.setLevel(Level.parse(this.getProperty(className + ".level", "" + Level.ALL)));
        String filterName = this.getProperty(className + ".filter", null);
        if (filterName != null) {
            try {
                this.setFilter((Filter)cl.loadClass(filterName).getConstructor(new Class[0]).newInstance(new Object[0]));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if ((formatterName = this.getProperty(className + ".formatter", null)) != null) {
            try {
                this.setFormatter((Formatter)cl.loadClass(formatterName).getConstructor(new Class[0]).newInstance(new Object[0]));
            }
            catch (Exception e) {
                this.setFormatter(new OneLineFormatter());
            }
        } else {
            this.setFormatter(new OneLineFormatter());
        }
        this.setErrorManager(new ErrorManager());
    }

    private String getProperty(String name, String defaultValue) {
        String value = LogManager.getLogManager().getProperty(name);
        value = value == null ? defaultValue : value.trim();
        return value;
    }

    protected void open() {
        this.openWriter();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void openWriter() {
        File dir = new File(this.directory);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            this.reportError("Unable to create [" + dir + "]", null, 4);
            this.writer = null;
            return;
        }
        this.writerLock.writeLock().lock();
        FileOutputStream fos = null;
        OutputStream os = null;
        try {
            File pathname = new File(dir.getAbsoluteFile(), this.prefix + (this.rotatable != false ? this.date : "") + this.suffix);
            File parent = pathname.getParentFile();
            if (!parent.mkdirs() && !parent.isDirectory()) {
                this.reportError("Unable to create [" + parent + "]", null, 4);
                this.writer = null;
                return;
            }
            String encoding = this.getEncoding();
            fos = new FileOutputStream(pathname, true);
            os = this.bufferSize > 0 ? new BufferedOutputStream(fos, this.bufferSize) : fos;
            this.writer = new PrintWriter(encoding != null ? new OutputStreamWriter(os, encoding) : new OutputStreamWriter(os), false);
            this.writer.write(this.getFormatter().getHead(this));
        }
        catch (Exception e) {
            this.reportError(null, e, 4);
            this.writer = null;
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        finally {
            this.writerLock.writeLock().unlock();
        }
    }

    private void clean() {
        if (this.maxDays <= 0 || Files.notExists(this.getDirectoryAsPath(), new LinkOption[0])) {
            return;
        }
        DELETE_FILES_SERVICE.submit(() -> {
            try (DirectoryStream<Path> files = this.streamFilesForDelete();){
                for (Path file : files) {
                    Files.delete(file);
                }
            }
            catch (IOException e) {
                this.reportError("Unable to delete log files older than [" + this.maxDays + "] days", null, 0);
            }
        });
    }

    private DirectoryStream<Path> streamFilesForDelete() throws IOException {
        LocalDate maxDaysOffset = LocalDate.now().minus(this.maxDays.intValue(), ChronoUnit.DAYS);
        return Files.newDirectoryStream(this.getDirectoryAsPath(), path -> {
            boolean result = false;
            String date = this.obtainDateFromPath((Path)path);
            if (date != null) {
                try {
                    LocalDate dateFromFile = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(date));
                    result = dateFromFile.isBefore(maxDaysOffset);
                }
                catch (DateTimeException dateTimeException) {
                    // empty catch block
                }
            }
            return result;
        });
    }

    private Path getDirectoryAsPath() {
        return FileSystems.getDefault().getPath(this.directory, new String[0]);
    }

    private String obtainDateFromPath(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            return null;
        }
        String date = fileName.toString();
        if (this.pattern.matcher(date).matches()) {
            date = date.substring(this.prefix.length());
            return date.substring(0, date.length() - this.suffix.length());
        }
        return null;
    }

    protected static final class ThreadFactory
    implements java.util.concurrent.ThreadFactory {
        private final String namePrefix;
        private final boolean isSecurityEnabled;
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public ThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
            SecurityManager s = System.getSecurityManager();
            if (s == null) {
                this.isSecurityEnabled = false;
                this.group = Thread.currentThread().getThreadGroup();
            } else {
                this.isSecurityEnabled = true;
                this.group = s.getThreadGroup();
            }
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement());
            if (this.isSecurityEnabled) {
                AccessController.doPrivileged(() -> {
                    t.setContextClassLoader(ThreadFactory.class.getClassLoader());
                    return null;
                });
            } else {
                t.setContextClassLoader(ThreadFactory.class.getClassLoader());
            }
            t.setDaemon(true);
            return t;
        }
    }
}

