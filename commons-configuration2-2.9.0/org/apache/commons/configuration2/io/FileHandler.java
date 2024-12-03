/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.io.FileHandlerListener;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.io.InputStreamSupport;
import org.apache.commons.configuration2.io.URLConnectionOptions;
import org.apache.commons.configuration2.io.VerifiableOutputStream;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.apache.commons.configuration2.sync.SynchronizerSupport;
import org.apache.commons.logging.LogFactory;

public class FileHandler {
    private static final String FILE_SCHEME = "file:";
    private static final String FILE_SCHEME_SLASH = "file://";
    private static final SynchronizerSupport DUMMY_SYNC_SUPPORT = new SynchronizerSupport(){

        @Override
        public Synchronizer getSynchronizer() {
            return NoOpSynchronizer.INSTANCE;
        }

        @Override
        public void lock(LockMode mode) {
        }

        @Override
        public void setSynchronizer(Synchronizer sync) {
        }

        @Override
        public void unlock(LockMode mode) {
        }
    };
    private final FileBased content;
    private final AtomicReference<FileLocator> fileLocator;
    private final List<FileHandlerListener> listeners = new CopyOnWriteArrayList<FileHandlerListener>();

    public FileHandler() {
        this(null);
    }

    public FileHandler(FileBased obj) {
        this(obj, FileHandler.emptyFileLocator());
    }

    public FileHandler(FileBased obj, FileHandler c) {
        this(obj, FileHandler.checkSourceHandler(c).getFileLocator());
    }

    private FileHandler(FileBased obj, FileLocator locator) {
        this.content = obj;
        this.fileLocator = new AtomicReference<FileLocator>(locator);
    }

    private static FileHandler checkSourceHandler(FileHandler c) {
        if (c == null) {
            throw new IllegalArgumentException("FileHandler to assign must not be null!");
        }
        return c;
    }

    private static void closeSilent(Closeable cl) {
        try {
            if (cl != null) {
                cl.close();
            }
        }
        catch (IOException e) {
            LogFactory.getLog(FileHandler.class).warn((Object)("Exception when closing " + cl), (Throwable)e);
        }
    }

    private static File createFile(FileLocator loc) {
        if (loc.getFileName() == null && loc.getSourceURL() == null) {
            return null;
        }
        if (loc.getSourceURL() != null) {
            return FileLocatorUtils.fileFromURL(loc.getSourceURL());
        }
        return FileLocatorUtils.getFile(loc.getBasePath(), loc.getFileName());
    }

    private static FileLocator emptyFileLocator() {
        return FileLocatorUtils.fileLocator().create();
    }

    public static FileHandler fromMap(Map<String, ?> map) {
        return new FileHandler(null, FileLocatorUtils.fromMap(map));
    }

    private static String normalizeFileURL(String fileName) {
        if (fileName != null && fileName.startsWith(FILE_SCHEME) && !fileName.startsWith(FILE_SCHEME_SLASH)) {
            fileName = FILE_SCHEME_SLASH + fileName.substring(FILE_SCHEME.length());
        }
        return fileName;
    }

    public void addFileHandlerListener(FileHandlerListener l) {
        if (l == null) {
            throw new IllegalArgumentException("Listener must not be null!");
        }
        this.listeners.add(l);
    }

    private void checkContent() throws ConfigurationException {
        if (this.getContent() == null) {
            throw new ConfigurationException("No content available!");
        }
    }

    private FileLocator checkContentAndGetLocator() throws ConfigurationException {
        this.checkContent();
        return this.getFileLocator();
    }

    public void clearLocation() {
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.basePath(null).fileName(null).sourceURL(null);
            }
        }.update();
    }

    private FileLocator createLocatorWithFileName(String fileName, FileLocator locator) {
        return FileLocatorUtils.fileLocator(locator).sourceURL(null).fileName(fileName).create();
    }

    private SynchronizerSupport fetchSynchronizerSupport() {
        if (this.getContent() instanceof SynchronizerSupport) {
            return (SynchronizerSupport)((Object)this.getContent());
        }
        return DUMMY_SYNC_SUPPORT;
    }

    private void fireLoadedEvent() {
        this.listeners.forEach(l -> l.loaded(this));
    }

    private void fireLoadingEvent() {
        this.listeners.forEach(l -> l.loading(this));
    }

    private void fireLocationChangedEvent() {
        this.listeners.forEach(l -> l.locationChanged(this));
    }

    private void fireSavedEvent() {
        this.listeners.forEach(l -> l.saved(this));
    }

    private void fireSavingEvent() {
        this.listeners.forEach(l -> l.saving(this));
    }

    public String getBasePath() {
        FileLocator locator = this.getFileLocator();
        if (locator.getBasePath() != null) {
            return locator.getBasePath();
        }
        if (locator.getSourceURL() != null) {
            return FileLocatorUtils.getBasePath(locator.getSourceURL());
        }
        return null;
    }

    public final FileBased getContent() {
        return this.content;
    }

    public String getEncoding() {
        return this.getFileLocator().getEncoding();
    }

    public File getFile() {
        return FileHandler.createFile(this.getFileLocator());
    }

    public FileLocator getFileLocator() {
        return this.fileLocator.get();
    }

    public String getFileName() {
        FileLocator locator = this.getFileLocator();
        if (locator.getFileName() != null) {
            return locator.getFileName();
        }
        if (locator.getSourceURL() != null) {
            return FileLocatorUtils.getFileName(locator.getSourceURL());
        }
        return null;
    }

    public FileSystem getFileSystem() {
        return FileLocatorUtils.getFileSystem(this.getFileLocator());
    }

    public FileLocationStrategy getLocationStrategy() {
        return FileLocatorUtils.getLocationStrategy(this.getFileLocator());
    }

    public String getPath() {
        FileLocator locator = this.getFileLocator();
        File file = FileHandler.createFile(locator);
        return FileLocatorUtils.getFileSystem(locator).getPath(file, locator.getSourceURL(), locator.getBasePath(), locator.getFileName());
    }

    public URL getURL() {
        FileLocator locator = this.getFileLocator();
        return locator.getSourceURL() != null ? locator.getSourceURL() : FileLocatorUtils.locate(locator);
    }

    private void injectFileLocator(URL url) {
        if (url == null) {
            this.injectNullFileLocator();
        } else if (this.getContent() instanceof FileLocatorAware) {
            FileLocator locator = this.prepareNullLocatorBuilder().sourceURL(url).create();
            ((FileLocatorAware)((Object)this.getContent())).initFileLocator(locator);
        }
    }

    private void injectNullFileLocator() {
        if (this.getContent() instanceof FileLocatorAware) {
            FileLocator locator = this.prepareNullLocatorBuilder().create();
            ((FileLocatorAware)((Object)this.getContent())).initFileLocator(locator);
        }
    }

    public boolean isLocationDefined() {
        return FileLocatorUtils.isLocationDefined(this.getFileLocator());
    }

    public void load() throws ConfigurationException {
        this.load(this.checkContentAndGetLocator());
    }

    public void load(File file) throws ConfigurationException {
        URL url;
        try {
            url = FileLocatorUtils.toURL(file);
        }
        catch (MalformedURLException e1) {
            throw new ConfigurationException("Cannot create URL from file " + file);
        }
        this.load(url);
    }

    private void load(FileLocator locator) throws ConfigurationException {
        this.load(FileLocatorUtils.locateOrThrow(locator), locator);
    }

    public void load(InputStream in) throws ConfigurationException {
        this.load(in, this.checkContentAndGetLocator());
    }

    private void load(InputStream in, FileLocator locator) throws ConfigurationException {
        this.load(in, locator.getEncoding());
    }

    public void load(InputStream in, String encoding) throws ConfigurationException {
        this.loadFromStream(in, encoding, null);
    }

    public void load(Reader in) throws ConfigurationException {
        this.checkContent();
        this.injectNullFileLocator();
        this.loadFromReader(in);
    }

    public void load(String fileName) throws ConfigurationException {
        this.load(fileName, this.checkContentAndGetLocator());
    }

    private void load(String fileName, FileLocator locator) throws ConfigurationException {
        FileLocator locFileName = this.createLocatorWithFileName(fileName, locator);
        URL url = FileLocatorUtils.locateOrThrow(locFileName);
        this.load(url, locator);
    }

    public void load(URL url) throws ConfigurationException {
        this.load(url, this.checkContentAndGetLocator());
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void load(URL url, FileLocator locator) throws ConfigurationException {
        InputStream in = null;
        try {
            FileSystem fileSystem = FileLocatorUtils.getFileSystem(locator);
            URLConnectionOptions urlConnectionOptions = locator.getURLConnectionOptions();
            in = urlConnectionOptions == null ? fileSystem.getInputStream(url) : fileSystem.getInputStream(url, urlConnectionOptions);
            this.loadFromStream(in, locator.getEncoding(), url);
        }
        catch (ConfigurationException e) {
            try {
                throw e;
                catch (Exception e2) {
                    throw new ConfigurationException("Unable to load the configuration from the URL " + url, e2);
                }
            }
            catch (Throwable throwable) {
                FileHandler.closeSilent(in);
                throw throwable;
            }
        }
        FileHandler.closeSilent(in);
    }

    private void loadFromReader(Reader in) throws ConfigurationException {
        this.fireLoadingEvent();
        try {
            this.getContent().read(in);
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
        finally {
            this.fireLoadedEvent();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadFromStream(InputStream in, String encoding, URL url) throws ConfigurationException {
        this.checkContent();
        SynchronizerSupport syncSupport = this.fetchSynchronizerSupport();
        syncSupport.lock(LockMode.WRITE);
        try {
            this.injectFileLocator(url);
            if (this.getContent() instanceof InputStreamSupport) {
                this.loadFromStreamDirectly(in);
            } else {
                this.loadFromTransformedStream(in, encoding);
            }
        }
        finally {
            syncSupport.unlock(LockMode.WRITE);
        }
    }

    private void loadFromStreamDirectly(InputStream in) throws ConfigurationException {
        try {
            ((InputStreamSupport)((Object)this.getContent())).read(in);
        }
        catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    private void loadFromTransformedStream(InputStream in, String encoding) throws ConfigurationException {
        InputStreamReader reader = null;
        if (encoding != null) {
            try {
                reader = new InputStreamReader(in, encoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", e);
            }
        }
        if (reader == null) {
            reader = new InputStreamReader(in);
        }
        this.loadFromReader(reader);
    }

    public boolean locate() {
        boolean result;
        FileLocator fullLocator;
        FileLocator locator;
        boolean done;
        do {
            if ((fullLocator = FileLocatorUtils.fullyInitializedLocator(locator = this.getFileLocator())) == null) {
                result = false;
                fullLocator = locator;
                continue;
            }
            boolean bl = result = fullLocator != locator || FileLocatorUtils.isFullyInitialized(locator);
        } while (!(done = this.fileLocator.compareAndSet(locator, fullLocator)));
        return result;
    }

    private FileLocator.FileLocatorBuilder prepareNullLocatorBuilder() {
        return FileLocatorUtils.fileLocator(this.getFileLocator()).sourceURL(null).basePath(null).fileName(null);
    }

    public void removeFileHandlerListener(FileHandlerListener l) {
        this.listeners.remove(l);
    }

    public void resetFileSystem() {
        this.setFileSystem(null);
    }

    public void save() throws ConfigurationException {
        this.save(this.checkContentAndGetLocator());
    }

    public void save(File file) throws ConfigurationException {
        this.save(file, this.checkContentAndGetLocator());
    }

    private void save(File file, FileLocator locator) throws ConfigurationException {
        OutputStream out = null;
        try {
            out = FileLocatorUtils.getFileSystem(locator).getOutputStream(file);
            this.saveToStream(out, locator.getEncoding(), file.toURI().toURL());
        }
        catch (MalformedURLException muex) {
            try {
                throw new ConfigurationException(muex);
            }
            catch (Throwable throwable) {
                FileHandler.closeSilent(out);
                throw throwable;
            }
        }
        FileHandler.closeSilent(out);
    }

    private void save(FileLocator locator) throws ConfigurationException {
        if (!FileLocatorUtils.isLocationDefined(locator)) {
            throw new ConfigurationException("No file location has been set!");
        }
        if (locator.getSourceURL() != null) {
            this.save(locator.getSourceURL(), locator);
        } else {
            this.save(locator.getFileName(), locator);
        }
    }

    public void save(OutputStream out) throws ConfigurationException {
        this.save(out, this.checkContentAndGetLocator());
    }

    private void save(OutputStream out, FileLocator locator) throws ConfigurationException {
        this.save(out, locator.getEncoding());
    }

    public void save(OutputStream out, String encoding) throws ConfigurationException {
        this.saveToStream(out, encoding, null);
    }

    public void save(String fileName) throws ConfigurationException {
        this.save(fileName, this.checkContentAndGetLocator());
    }

    private void save(String fileName, FileLocator locator) throws ConfigurationException {
        URL url;
        try {
            url = FileLocatorUtils.getFileSystem(locator).getURL(locator.getBasePath(), fileName);
        }
        catch (MalformedURLException e) {
            throw new ConfigurationException(e);
        }
        if (url == null) {
            throw new ConfigurationException("Cannot locate configuration source " + fileName);
        }
        this.save(url, locator);
    }

    public void save(URL url) throws ConfigurationException {
        this.save(url, this.checkContentAndGetLocator());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void save(URL url, FileLocator locator) throws ConfigurationException {
        OutputStream out;
        block4: {
            out = null;
            try {
                out = FileLocatorUtils.getFileSystem(locator).getOutputStream(url);
                this.saveToStream(out, locator.getEncoding(), url);
                if (!(out instanceof VerifiableOutputStream)) break block4;
                try {
                    ((VerifiableOutputStream)out).verify();
                }
                catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }
            catch (Throwable throwable) {
                FileHandler.closeSilent(out);
                throw throwable;
            }
        }
        FileHandler.closeSilent(out);
    }

    public void save(Writer out) throws ConfigurationException {
        this.checkContent();
        this.injectNullFileLocator();
        this.saveToWriter(out);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void saveToStream(OutputStream out, String encoding, URL url) throws ConfigurationException {
        this.checkContent();
        SynchronizerSupport syncSupport = this.fetchSynchronizerSupport();
        syncSupport.lock(LockMode.WRITE);
        try {
            this.injectFileLocator(url);
            OutputStreamWriter writer = null;
            if (encoding != null) {
                try {
                    writer = new OutputStreamWriter(out, encoding);
                }
                catch (UnsupportedEncodingException e) {
                    throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", e);
                }
            }
            if (writer == null) {
                writer = new OutputStreamWriter(out);
            }
            this.saveToWriter(writer);
        }
        finally {
            syncSupport.unlock(LockMode.WRITE);
        }
    }

    private void saveToWriter(Writer out) throws ConfigurationException {
        this.fireSavingEvent();
        try {
            this.getContent().write(out);
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
        finally {
            this.fireSavedEvent();
        }
    }

    public void setBasePath(String basePath) {
        final String path = FileHandler.normalizeFileURL(basePath);
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.basePath(path);
                builder.sourceURL(null);
            }
        }.update();
    }

    public void setEncoding(final String encoding) {
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.encoding(encoding);
            }
        }.update();
    }

    public void setFile(File file) {
        final String fileName = file.getName();
        final String basePath = file.getParentFile() != null ? file.getParentFile().getAbsolutePath() : null;
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.fileName(fileName).basePath(basePath).sourceURL(null);
            }
        }.update();
    }

    public void setFileLocator(FileLocator locator) {
        if (locator == null) {
            throw new IllegalArgumentException("FileLocator must not be null!");
        }
        this.fileLocator.set(locator);
        this.fireLocationChangedEvent();
    }

    public void setFileName(String fileName) {
        final String name = FileHandler.normalizeFileURL(fileName);
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.fileName(name);
                builder.sourceURL(null);
            }
        }.update();
    }

    public void setFileSystem(final FileSystem fileSystem) {
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.fileSystem(fileSystem);
            }
        }.update();
    }

    public void setLocationStrategy(final FileLocationStrategy strategy) {
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.locationStrategy(strategy);
            }
        }.update();
    }

    public void setPath(String path) {
        this.setFile(new File(path));
    }

    public void setURL(URL url) {
        this.setURL(url, URLConnectionOptions.DEFAULT);
    }

    public void setURL(final URL url, final URLConnectionOptions urlConnectionOptions) {
        new Updater(){

            @Override
            protected void updateBuilder(FileLocator.FileLocatorBuilder builder) {
                builder.sourceURL(url);
                builder.urlConnectionOptions(urlConnectionOptions);
                builder.basePath(null).fileName(null);
            }
        }.update();
    }

    private abstract class Updater {
        private Updater() {
        }

        public void update() {
            FileLocator.FileLocatorBuilder builder;
            FileLocator oldLocator;
            boolean done;
            do {
                oldLocator = (FileLocator)FileHandler.this.fileLocator.get();
                builder = FileLocatorUtils.fileLocator(oldLocator);
                this.updateBuilder(builder);
            } while (!(done = FileHandler.this.fileLocator.compareAndSet(oldLocator, builder.create())));
            FileHandler.this.fireLocationChangedEvent();
        }

        protected abstract void updateBuilder(FileLocator.FileLocatorBuilder var1);
    }
}

