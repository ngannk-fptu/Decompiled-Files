/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.email.Header;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.URLResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

public class Get
extends Task {
    private static final int NUMBER_RETRIES = 3;
    private static final int DOTS_PER_LINE = 50;
    private static final int BIG_BUFFER_SIZE = 102400;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final int REDIRECT_LIMIT = 25;
    private static final int HTTP_MOVED_TEMP = 307;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String DEFAULT_AGENT_PREFIX = "Apache Ant";
    private static final String GZIP_CONTENT_ENCODING = "gzip";
    private final Resources sources = new Resources();
    private File destination;
    private boolean verbose = false;
    private boolean quiet = false;
    private boolean useTimestamp = false;
    private boolean ignoreErrors = false;
    private String uname = null;
    private String pword = null;
    private boolean authenticateOnRedirect = false;
    private long maxTime = 0L;
    private int numberRetries = 3;
    private boolean skipExisting = false;
    private boolean httpUseCaches = true;
    private boolean tryGzipEncoding = false;
    private Mapper mapperElement = null;
    private String userAgent = System.getProperty("ant.http.agent", "Apache Ant/" + Main.getShortAntVersion());
    private Map<String, String> headers = new LinkedHashMap<String, String>();

    @Override
    public void execute() throws BuildException {
        this.checkAttributes();
        for (Resource r : this.sources) {
            URLProvider up = r.as(URLProvider.class);
            URL source = up.getURL();
            File dest = this.destination;
            if (this.destination.isDirectory()) {
                if (this.mapperElement == null) {
                    int slash;
                    String path = source.getPath();
                    if (path.endsWith("/")) {
                        path = path.substring(0, path.length() - 1);
                    }
                    if ((slash = path.lastIndexOf(47)) > -1) {
                        path = path.substring(slash + 1);
                    }
                    dest = new File(this.destination, path);
                } else {
                    FileNameMapper mapper = this.mapperElement.getImplementation();
                    String[] d = mapper.mapFileName(source.toString());
                    if (d == null) {
                        this.log("skipping " + r + " - mapper can't handle it", 1);
                        continue;
                    }
                    if (d.length == 0) {
                        this.log("skipping " + r + " - mapper returns no file name", 1);
                        continue;
                    }
                    if (d.length > 1) {
                        this.log("skipping " + r + " - mapper returns multiple file names", 1);
                        continue;
                    }
                    dest = new File(this.destination, d[0]);
                }
            }
            int logLevel = 2;
            VerboseProgress progress = null;
            if (this.verbose) {
                progress = new VerboseProgress(System.out);
            }
            try {
                this.doGet(source, dest, 2, progress);
            }
            catch (IOException ioe) {
                this.log("Error getting " + source + " to " + dest);
                if (this.ignoreErrors) continue;
                throw new BuildException(ioe, this.getLocation());
            }
        }
    }

    @Deprecated
    public boolean doGet(int logLevel, DownloadProgress progress) throws IOException {
        this.checkAttributes();
        return this.doGet(this.sources.iterator().next().as(URLProvider.class).getURL(), this.destination, logLevel, progress);
    }

    public boolean doGet(URL source, File dest, int logLevel, DownloadProgress progress) throws IOException {
        if (dest.exists() && this.skipExisting) {
            this.log("Destination already exists (skipping): " + dest.getAbsolutePath(), logLevel);
            return true;
        }
        if (progress == null) {
            progress = new NullProgress();
        }
        this.log("Getting: " + source, logLevel);
        this.log("To: " + dest.getAbsolutePath(), logLevel);
        long timestamp = 0L;
        boolean hasTimestamp = false;
        if (this.useTimestamp && dest.exists()) {
            timestamp = dest.lastModified();
            if (this.verbose) {
                Date t = new Date(timestamp);
                this.log("local file date : " + t.toString(), logLevel);
            }
            hasTimestamp = true;
        }
        GetThread getThread = new GetThread(source, dest, hasTimestamp, timestamp, progress, logLevel, this.userAgent);
        getThread.setDaemon(true);
        this.getProject().registerThreadTask(getThread, this);
        getThread.start();
        try {
            getThread.join(this.maxTime * 1000L);
        }
        catch (InterruptedException ie) {
            this.log("interrupted waiting for GET to finish", 3);
        }
        if (getThread.isAlive()) {
            String msg = "The GET operation took longer than " + this.maxTime + " seconds, stopping it.";
            if (this.ignoreErrors) {
                this.log(msg);
            }
            getThread.closeStreams();
            if (!this.ignoreErrors) {
                throw new BuildException(msg);
            }
            return false;
        }
        return getThread.wasSuccessful();
    }

    @Override
    public void log(String msg, int msgLevel) {
        if (!this.quiet || msgLevel <= 0) {
            super.log(msg, msgLevel);
        }
    }

    private void checkAttributes() {
        if (this.userAgent == null || this.userAgent.trim().isEmpty()) {
            throw new BuildException("userAgent may not be null or empty");
        }
        if (this.sources.size() == 0) {
            throw new BuildException("at least one source is required", this.getLocation());
        }
        for (Resource r : this.sources) {
            URLProvider up = r.as(URLProvider.class);
            if (up != null) continue;
            throw new BuildException("Only URLProvider resources are supported", this.getLocation());
        }
        if (this.destination == null) {
            throw new BuildException("dest attribute is required", this.getLocation());
        }
        if (this.destination.exists() && this.sources.size() > 1 && !this.destination.isDirectory()) {
            throw new BuildException("The specified destination is not a directory", this.getLocation());
        }
        if (this.destination.exists() && !this.destination.canWrite()) {
            throw new BuildException("Can't write to " + this.destination.getAbsolutePath(), this.getLocation());
        }
        if (this.sources.size() > 1 && !this.destination.exists()) {
            this.destination.mkdirs();
        }
    }

    public void setSrc(URL u) {
        this.add(new URLResource(u));
    }

    public void add(ResourceCollection rc) {
        this.sources.add(rc);
    }

    public void setDest(File dest) {
        this.destination = dest;
    }

    public void setVerbose(boolean v) {
        this.verbose = v;
    }

    public void setQuiet(boolean v) {
        this.quiet = v;
    }

    public void setIgnoreErrors(boolean v) {
        this.ignoreErrors = v;
    }

    public void setUseTimestamp(boolean v) {
        this.useTimestamp = v;
    }

    public void setUsername(String u) {
        this.uname = u;
    }

    public void setPassword(String p) {
        this.pword = p;
    }

    public void setAuthenticateOnRedirect(boolean v) {
        this.authenticateOnRedirect = v;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public void setRetries(int r) {
        if (r <= 0) {
            this.log("Setting retries to " + r + " will make the task not even try to reach the URI at all", 1);
        }
        this.numberRetries = r;
    }

    public void setSkipExisting(boolean s) {
        this.skipExisting = s;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setHttpUseCaches(boolean httpUseCache) {
        this.httpUseCaches = httpUseCache;
    }

    public void setTryGzipEncoding(boolean b) {
        this.tryGzipEncoding = b;
    }

    public void addConfiguredHeader(Header header) {
        if (header != null) {
            String key = StringUtils.trimToNull(header.getName());
            String value = StringUtils.trimToNull(header.getValue());
            if (key != null && value != null) {
                this.headers.put(key, value);
            }
        }
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        this.mapperElement = new Mapper(this.getProject());
        return this.mapperElement;
    }

    public void add(FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }

    public static boolean isMoved(int responseCode) {
        return responseCode == 301 || responseCode == 302 || responseCode == 303 || responseCode == 307;
    }

    public static class VerboseProgress
    implements DownloadProgress {
        private int dots = 0;
        PrintStream out;

        public VerboseProgress(PrintStream out) {
            this.out = out;
        }

        @Override
        public void beginDownload() {
            this.dots = 0;
        }

        @Override
        public void onTick() {
            this.out.print(".");
            if (this.dots++ > 50) {
                this.out.flush();
                this.dots = 0;
            }
        }

        @Override
        public void endDownload() {
            this.out.println();
            this.out.flush();
        }
    }

    public static interface DownloadProgress {
        public void beginDownload();

        public void onTick();

        public void endDownload();
    }

    public static class NullProgress
    implements DownloadProgress {
        @Override
        public void beginDownload() {
        }

        @Override
        public void onTick() {
        }

        @Override
        public void endDownload() {
        }
    }

    private class GetThread
    extends Thread {
        private final URL source;
        private final File dest;
        private final boolean hasTimestamp;
        private final long timestamp;
        private final DownloadProgress progress;
        private final int logLevel;
        private boolean success = false;
        private IOException ioexception = null;
        private BuildException exception = null;
        private InputStream is = null;
        private OutputStream os = null;
        private URLConnection connection;
        private int redirections = 0;
        private String userAgent = null;

        GetThread(URL source, File dest, boolean h, long t, DownloadProgress p, int l, String userAgent) {
            this.source = source;
            this.dest = dest;
            this.hasTimestamp = h;
            this.timestamp = t;
            this.progress = p;
            this.logLevel = l;
            this.userAgent = userAgent;
        }

        @Override
        public void run() {
            try {
                this.success = this.get();
            }
            catch (IOException ioex) {
                this.ioexception = ioex;
            }
            catch (BuildException bex) {
                this.exception = bex;
            }
        }

        private boolean get() throws IOException, BuildException {
            this.connection = this.openConnection(this.source, Get.this.uname, Get.this.pword);
            if (this.connection == null) {
                return false;
            }
            boolean downloadSucceeded = this.downloadFile();
            if (downloadSucceeded && Get.this.useTimestamp) {
                this.updateTimeStamp();
            }
            return downloadSucceeded;
        }

        private boolean redirectionAllowed(URL aSource, URL aDest) {
            if (!(aSource.getProtocol().equals(aDest.getProtocol()) || Get.HTTP.equals(aSource.getProtocol()) && Get.HTTPS.equals(aDest.getProtocol()))) {
                String message = "Redirection detected from " + aSource.getProtocol() + " to " + aDest.getProtocol() + ". Protocol switch unsafe, not allowed.";
                if (Get.this.ignoreErrors) {
                    Get.this.log(message, this.logLevel);
                    return false;
                }
                throw new BuildException(message);
            }
            ++this.redirections;
            if (this.redirections > 25) {
                String message = "More than 25 times redirected, giving up";
                if (Get.this.ignoreErrors) {
                    Get.this.log("More than 25 times redirected, giving up", this.logLevel);
                    return false;
                }
                throw new BuildException("More than 25 times redirected, giving up");
            }
            return true;
        }

        private URLConnection openConnection(URL aSource, String uname, String pword) throws IOException {
            URLConnection connection = aSource.openConnection();
            if (this.hasTimestamp) {
                connection.setIfModifiedSince(this.timestamp);
            }
            connection.addRequestProperty("User-Agent", this.userAgent);
            if (uname != null || pword != null) {
                String up = uname + ":" + pword;
                Base64Converter encoder = new Base64Converter();
                String string = encoder.encode(up.getBytes());
                connection.setRequestProperty("Authorization", "Basic " + string);
            }
            if (Get.this.tryGzipEncoding) {
                connection.setRequestProperty("Accept-Encoding", Get.GZIP_CONTENT_ENCODING);
            }
            for (Map.Entry entry : Get.this.headers.entrySet()) {
                Get.this.log(String.format("Adding header '%s' ", entry.getKey()));
                connection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
            }
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection)connection).setInstanceFollowRedirects(false);
                connection.setUseCaches(Get.this.httpUseCaches);
            }
            try {
                connection.connect();
            }
            catch (NullPointerException e) {
                throw new BuildException("Failed to parse " + this.source.toString(), e);
            }
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection)connection;
                int n = httpConnection.getResponseCode();
                if (Get.isMoved(n)) {
                    String newLocation = httpConnection.getHeaderField("Location");
                    String message = aSource + (n == 301 ? " permanently" : "") + " moved to " + newLocation;
                    Get.this.log(message, this.logLevel);
                    URL newURL = new URL(aSource, newLocation);
                    if (!this.redirectionAllowed(aSource, newURL)) {
                        return null;
                    }
                    return this.openConnection(newURL, Get.this.authenticateOnRedirect ? uname : null, Get.this.authenticateOnRedirect ? pword : null);
                }
                long lastModified = httpConnection.getLastModified();
                if (n == 304 || lastModified != 0L && this.hasTimestamp && this.timestamp >= lastModified) {
                    Get.this.log("Not modified - so not downloaded", this.logLevel);
                    return null;
                }
                if (n == 401) {
                    String message = "HTTP Authorization failure";
                    if (Get.this.ignoreErrors) {
                        Get.this.log("HTTP Authorization failure", this.logLevel);
                        return null;
                    }
                    throw new BuildException("HTTP Authorization failure");
                }
            }
            return connection;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean downloadFile() throws IOException {
            for (int i = 0; i < Get.this.numberRetries; ++i) {
                try {
                    this.is = this.connection.getInputStream();
                    break;
                }
                catch (IOException ex) {
                    Get.this.log("Error opening connection " + ex, this.logLevel);
                    continue;
                }
            }
            if (this.is == null) {
                Get.this.log("Can't get " + this.source + " to " + this.dest, this.logLevel);
                if (Get.this.ignoreErrors) {
                    return false;
                }
                throw new BuildException("Can't get " + this.source + " to " + this.dest, Get.this.getLocation());
            }
            if (Get.this.tryGzipEncoding && Get.GZIP_CONTENT_ENCODING.equals(this.connection.getContentEncoding())) {
                this.is = new GZIPInputStream(this.is);
            }
            this.os = Files.newOutputStream(this.dest.toPath(), new OpenOption[0]);
            this.progress.beginDownload();
            boolean finished = false;
            try {
                int length;
                byte[] buffer = new byte[102400];
                while (!this.isInterrupted() && (length = this.is.read(buffer)) >= 0) {
                    this.os.write(buffer, 0, length);
                    this.progress.onTick();
                }
                finished = !this.isInterrupted();
            }
            finally {
                FileUtils.close(this.os);
                FileUtils.close(this.is);
                if (!finished) {
                    this.dest.delete();
                }
            }
            this.progress.endDownload();
            return true;
        }

        private void updateTimeStamp() {
            long remoteTimestamp = this.connection.getLastModified();
            if (Get.this.verbose) {
                Date t = new Date(remoteTimestamp);
                Get.this.log("last modified = " + t.toString() + (remoteTimestamp == 0L ? " - using current time instead" : ""), this.logLevel);
            }
            if (remoteTimestamp != 0L) {
                FILE_UTILS.setFileLastModified(this.dest, remoteTimestamp);
            }
        }

        boolean wasSuccessful() throws IOException, BuildException {
            if (this.ioexception != null) {
                throw this.ioexception;
            }
            if (this.exception != null) {
                throw this.exception;
            }
            return this.success;
        }

        void closeStreams() {
            this.interrupt();
            FileUtils.close(this.os);
            FileUtils.close(this.is);
            if (!this.success && this.dest.exists()) {
                this.dest.delete();
            }
        }
    }

    protected static class Base64Converter
    extends org.apache.tools.ant.util.Base64Converter {
        protected Base64Converter() {
        }
    }
}

