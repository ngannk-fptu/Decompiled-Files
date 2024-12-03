/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.http;

import aQute.bnd.connection.settings.ConnectionSettings;
import aQute.bnd.http.HttpRequest;
import aQute.bnd.http.HttpRequestException;
import aQute.bnd.http.ProgressWrappingStream;
import aQute.bnd.http.URLCache;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.Registry;
import aQute.bnd.service.progress.ProgressPlugin;
import aQute.bnd.service.url.ProxyHandler;
import aQute.bnd.service.url.State;
import aQute.bnd.service.url.TaggedData;
import aQute.bnd.service.url.URLConnectionHandler;
import aQute.bnd.service.url.URLConnector;
import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import aQute.libg.reporter.ReporterAdapter;
import aQute.service.reporter.Reporter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient
implements Closeable,
URLConnector {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    public static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    private static final ThreadLocal<DateFormat> HTTP_DATE_FORMATTER = new ThreadLocal();
    private static final int HTTP_TEMPORARY_REDIRECT = 307;
    private static final int HTTP_PERMANENT_REDIRECT = 308;
    private final List<ProxyHandler> proxyHandlers = new ArrayList<ProxyHandler>();
    private final List<URLConnectionHandler> connectionHandlers = new ArrayList<URLConnectionHandler>();
    private ThreadLocal<PasswordAuthentication> passwordAuthentication = new ThreadLocal();
    private boolean inited;
    private static JSONCodec codec;
    private URLCache cache = new URLCache(IO.getFile("~/.bnd/urlcache"));
    private Registry registry = null;
    private Reporter reporter;
    private volatile AtomicBoolean offline;

    synchronized void init() {
        if (this.inited) {
            return;
        }
        this.inited = true;
        Authenticator.setDefault(new Authenticator(){

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return (PasswordAuthentication)HttpClient.this.passwordAuthentication.get();
            }
        });
    }

    private static DateFormat httpDateFormat() {
        DateFormat format = HTTP_DATE_FORMATTER.get();
        if (format == null) {
            format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            HTTP_DATE_FORMATTER.set(format);
        }
        return format;
    }

    @Override
    public void close() {
        Authenticator.setDefault(null);
    }

    @Override
    public InputStream connect(URL url) throws Exception {
        return this.build().get(InputStream.class).go(url);
    }

    @Override
    public TaggedData connectTagged(URL url) throws Exception {
        return this.build().get(TaggedData.class).go(url);
    }

    @Override
    public TaggedData connectTagged(URL url, String tag) throws Exception {
        return this.build().get(TaggedData.class).ifNoneMatch(tag).go(url);
    }

    public HttpRequest<Object> build() {
        return new HttpRequest<Object>(this);
    }

    public Object send(HttpRequest<?> request) throws Exception {
        if (this.isOffline() || request.isCache()) {
            return this.doCached(request);
        }
        TaggedData in = this.send0(request);
        if (request.download == TaggedData.class) {
            return in;
        }
        switch (in.getState()) {
            case NOT_FOUND: {
                return null;
            }
            case OTHER: {
                in.throwIt();
                return null;
            }
        }
        return this.convert(request.download, in.getInputStream());
    }

    Object doCached(HttpRequest<?> request) throws Exception, IOException {
        TaggedData tag = this.doCached0(request);
        if (request.download == TaggedData.class) {
            return tag;
        }
        if (request.download == State.class) {
            return tag.getState();
        }
        switch (tag.getState()) {
            case NOT_FOUND: {
                return null;
            }
            case OTHER: {
                throw new HttpRequestException(tag);
            }
        }
        return this.convert(request.download, request.useCacheFile == null ? tag.getFile() : request.useCacheFile, tag);
    }

    TaggedData doCached0(HttpRequest<?> request) throws Exception, IOException {
        logger.debug("cached {}", (Object)request.url);
        URL url = request.url;
        try (URLCache.Info info = this.cache.get(request.useCacheFile, request.url.toURI());){
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                File sourceFile = new File(url.toURI());
                if (!sourceFile.isFile()) {
                    TaggedData taggedData = new TaggedData(url.toURI(), 404, null);
                    return taggedData;
                }
                if (info.file.isFile() && info.file.lastModified() == sourceFile.lastModified() && info.file.length() == sourceFile.length()) {
                    TaggedData taggedData = new TaggedData(url.toURI(), 304, info.file);
                    return taggedData;
                }
                info.update(IO.stream(sourceFile), null, sourceFile.lastModified());
                TaggedData taggedData = new TaggedData(url.toURI(), 200, info.file);
                return taggedData;
            }
            request.useCacheFile = info.file;
            if (info.isPresent()) {
                if (!(this.isOffline() || request.maxStale >= 0L && info.jsonFile.lastModified() + request.maxStale >= System.currentTimeMillis())) {
                    if (info.dto.etag != null) {
                        request.ifNoneMatch(info.getETag());
                    } else {
                        long time = info.file.lastModified();
                        if (time > 0L) {
                            request.ifModifiedSince(time + 1L);
                        }
                    }
                    TaggedData in = this.send0(request);
                    if (in.getState() == State.UPDATED) {
                        info.update(in.getInputStream(), in.getTag(), in.getModified());
                    } else if (in.getState() == State.UNMODIFIED) {
                        info.jsonFile.setLastModified(System.currentTimeMillis());
                    }
                    TaggedData taggedData = in;
                    return taggedData;
                }
                TaggedData in = new TaggedData(request.url.toURI(), 304, info.file);
                return in;
            }
            request.ifMatch = null;
            request.ifNoneMatch = null;
            request.ifModifiedSince = -1L;
            if (this.isOffline()) {
                TaggedData in = new TaggedData(url.toURI(), 404, request.useCacheFile);
                return in;
            }
            TaggedData in = this.send0(request);
            if (in.isOk()) {
                info.update(in.getInputStream(), in.getTag(), in.getModified());
            }
            TaggedData taggedData = in;
            return taggedData;
        }
    }

    public TaggedData send0(final HttpRequest<?> request) throws Exception {
        ProxyHandler.ProxySetup proxy = this.getProxySetup(request.url);
        final URLConnection con = this.getProxiedAndConfiguredConnection(request.url, proxy);
        final HttpURLConnection hcon = (HttpURLConnection)(con instanceof HttpURLConnection ? con : null);
        if (request.ifNoneMatch != null) {
            request.headers.put("If-None-Match", this.entitytag(request.ifNoneMatch));
        }
        if (request.ifMatch != null) {
            request.headers.put("If-Match", "\"" + this.entitytag(request.ifMatch));
        }
        if (request.ifModifiedSince > 0L) {
            request.headers.put("If-Modified-Since", HttpClient.httpDateFormat().format(new Date(request.ifModifiedSince)));
        }
        if (request.ifUnmodifiedSince != 0L) {
            request.headers.put("If-Unmodified-Since", HttpClient.httpDateFormat().format(new Date(request.ifUnmodifiedSince)));
        }
        this.setHeaders(request.headers, con);
        this.configureHttpConnection(request.verb, hcon);
        final ProgressPlugin.Task task = this.getTask(request);
        try {
            TaggedData td = this.connectWithProxy(proxy, new Callable<TaggedData>(){

                @Override
                public TaggedData call() throws Exception {
                    return HttpClient.this.doConnect(request.upload, request.download, con, hcon, request, task);
                }
            });
            logger.debug("result {}", (Object)td);
            return td;
        }
        catch (Throwable t) {
            task.done("Failed " + t, t);
            throw t;
        }
    }

    ProgressPlugin.Task getTask(HttpRequest<?> request) {
        ProgressPlugin.Task task;
        List<ProgressPlugin> progressPlugins;
        String name = (request.upload == null ? "Download " : "Upload ") + request.url;
        int size = 100;
        List<ProgressPlugin> list = progressPlugins = this.registry != null ? this.registry.getPlugins(ProgressPlugin.class) : null;
        if (progressPlugins != null && progressPlugins.size() > 1) {
            final ArrayList<ProgressPlugin.Task> multiplexedTasks = new ArrayList<ProgressPlugin.Task>();
            for (ProgressPlugin progressPlugin : progressPlugins) {
                multiplexedTasks.add(progressPlugin.startTask(name, 100));
            }
            task = new ProgressPlugin.Task(){

                @Override
                public void worked(int units) {
                    for (ProgressPlugin.Task task : multiplexedTasks) {
                        task.worked(units);
                    }
                }

                @Override
                public void done(String message, Throwable e) {
                    for (ProgressPlugin.Task task : multiplexedTasks) {
                        task.done(message, e);
                    }
                }

                @Override
                public boolean isCanceled() {
                    for (ProgressPlugin.Task task : multiplexedTasks) {
                        if (!task.isCanceled()) continue;
                        return true;
                    }
                    return false;
                }
            };
        } else {
            task = progressPlugins != null && progressPlugins.size() == 1 ? progressPlugins.get(0).startTask(name, 100) : new ProgressPlugin.Task(){

                @Override
                public void worked(int units) {
                }

                @Override
                public void done(String message, Throwable e) {
                }

                @Override
                public boolean isCanceled() {
                    return Thread.currentThread().isInterrupted();
                }
            };
        }
        return task;
    }

    private String entitytag(String entity) {
        if (entity == null || entity.isEmpty() || "*".equals(entity)) {
            return entity;
        }
        return entity;
    }

    public ProxyHandler.ProxySetup getProxySetup(URL url) throws Exception {
        this.init();
        for (ProxyHandler proxyHandler : this.getProxyHandlers()) {
            ProxyHandler.ProxySetup setup = proxyHandler.forURL(url);
            if (setup == null) continue;
            logger.debug("Proxy {}", (Object)setup);
            return setup;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T connectWithProxy(ProxyHandler.ProxySetup proxySetup, Callable<T> r) throws Exception {
        if (proxySetup == null) {
            return r.call();
        }
        this.passwordAuthentication.set(proxySetup.authentication);
        try {
            T t = r.call();
            return t;
        }
        finally {
            this.passwordAuthentication.set(null);
        }
    }

    private URLConnection getProxiedAndConfiguredConnection(URL url, ProxyHandler.ProxySetup proxy) throws IOException, Exception {
        URLConnection urlc = proxy != null ? url.openConnection(proxy.proxy) : url.openConnection();
        URLConnectionHandler matching = this.findMatchingHandler(url);
        if (matching == null) {
            return urlc;
        }
        matching.handle(urlc);
        return urlc;
    }

    public URLConnectionHandler findMatchingHandler(URL url) throws Exception {
        for (URLConnectionHandler uRLConnectionHandler : this.getURLConnectionHandlers()) {
            if (uRLConnectionHandler.matches(url)) {
                logger.debug("Decorate {} with handler {}", (Object)url, (Object)uRLConnectionHandler);
                return uRLConnectionHandler;
            }
            logger.debug("No match for {}, handler {}", (Object)url, (Object)uRLConnectionHandler);
        }
        return null;
    }

    private synchronized Collection<? extends URLConnectionHandler> getURLConnectionHandlers() throws Exception {
        if (this.connectionHandlers.isEmpty() && this.registry != null) {
            List<URLConnectionHandler> connectionHandlers = this.registry.getPlugins(URLConnectionHandler.class);
            this.connectionHandlers.addAll(connectionHandlers);
            logger.debug("URL Connection handlers {}", connectionHandlers);
        }
        return this.connectionHandlers;
    }

    private synchronized Collection<? extends ProxyHandler> getProxyHandlers() throws Exception {
        if (this.proxyHandlers.isEmpty() && this.registry != null) {
            List<ProxyHandler> proxyHandlers = this.registry.getPlugins(ProxyHandler.class);
            proxyHandlers.addAll(proxyHandlers);
            logger.debug("Proxy handlers {}", proxyHandlers);
        }
        return this.proxyHandlers;
    }

    private InputStream createProgressWrappedStream(InputStream inputStream, String name, int size, ProgressPlugin.Task task, long timeout) {
        if (this.registry == null) {
            return inputStream;
        }
        return new ProgressWrappingStream(inputStream, name, size, task, timeout);
    }

    private TaggedData doConnect(Object put, Type ref, URLConnection con, HttpURLConnection hcon, HttpRequest<?> request, ProgressPlugin.Task task) throws IOException, Exception {
        if (put != null) {
            task.worked(1);
            this.doOutput(put, con, request);
        } else {
            logger.debug("{} {}", (Object)request.verb, (Object)request.url);
        }
        if (request.timeout > 0L) {
            con.setConnectTimeout((int)request.timeout * 10);
            con.setReadTimeout((int)(5000L > request.timeout ? request.timeout : 5000L));
        } else {
            con.setConnectTimeout(120000);
            con.setReadTimeout(60000);
        }
        try {
            if (hcon == null) {
                try {
                    con.connect();
                    InputStream in = con.getInputStream();
                    return new TaggedData(con, in, request.useCacheFile);
                }
                catch (FileNotFoundException e) {
                    URI uri = con.getURL().toURI();
                    task.done("File not found " + uri, e);
                    return new TaggedData(uri, 404, request.useCacheFile);
                }
            }
            int code = hcon.getResponseCode();
            if (code == -1) {
                System.out.println("WTF?");
            }
            if ((code == 302 || code == 301 || code == 303 || code == 307 || code == 308) && request.redirects-- > 0) {
                String location = hcon.getHeaderField("Location");
                request.url = new URL(location);
                task.done("Redirected " + code + " " + location, null);
                return this.send0(request);
            }
            if (this.isUpdateInfo(con, request, code)) {
                File file = (File)request.upload;
                String etag = con.getHeaderField("ETag");
                try (URLCache.Info info = this.cache.get(file, con.getURL().toURI());){
                    info.update(etag);
                }
            }
            if (code / 100 != 2) {
                task.done("Finished " + code + " " + con.getURL().toURI(), null);
                return new TaggedData(con, null, request.useCacheFile);
            }
            InputStream xin = con.getInputStream();
            InputStream in = this.handleContentEncoding(hcon, xin);
            in = this.createProgressWrappedStream(in, con.toString(), con.getContentLength(), task, request.timeout);
            return new TaggedData(con, in, request.useCacheFile);
        }
        catch (SocketTimeoutException ste) {
            task.done(ste.toString(), null);
            return new TaggedData(request.url.toURI(), 504, request.useCacheFile);
        }
    }

    boolean isUpdateInfo(URLConnection con, HttpRequest<?> request, int code) {
        return request.upload instanceof File && request.updateTag && code == 201 && con.getHeaderField("ETag") != null;
    }

    private Object convert(Type type, File in, TaggedData tag) throws IOException, Exception {
        if (type == TaggedData.class) {
            return tag;
        }
        if (type == File.class) {
            return in;
        }
        try (InputStream fin = IO.stream(in);){
            Object object = this.convert(type, fin);
            return object;
        }
    }

    private Object convert(Type ref, InputStream in) throws IOException, Exception {
        if (ref instanceof Class) {
            Class refc = (Class)ref;
            if (refc == byte[].class) {
                return IO.read(in);
            }
            if (InputStream.class.isAssignableFrom(refc)) {
                return in;
            }
            if (String.class == refc) {
                return IO.collect(in);
            }
        }
        String s = IO.collect(in);
        return codec.dec().from(s).get(ref);
    }

    private InputStream handleContentEncoding(HttpURLConnection con, InputStream in) throws IOException {
        if (con == null) {
            return in;
        }
        String encoding = con.getHeaderField("Content-Encoding");
        if (encoding != null) {
            if (encoding.equalsIgnoreCase("deflate")) {
                in = new InflaterInputStream(in);
                logger.debug("inflate");
            } else if (encoding.equalsIgnoreCase("gzip")) {
                in = new GZIPInputStream(in);
                logger.debug("gzip");
            }
        }
        return in;
    }

    private void doOutput(Object put, URLConnection con, HttpRequest<?> rq) throws IOException, Exception {
        con.setDoOutput(true);
        try (OutputStream out = con.getOutputStream();){
            if (put instanceof InputStream) {
                logger.debug("out {} input stream {}", (Object)rq.verb, (Object)rq.url);
                IO.copy((InputStream)put, out);
            } else if (put instanceof String) {
                logger.debug("out {} string {}", (Object)rq.verb, (Object)rq.url);
                IO.store(put, out);
            } else if (put instanceof byte[]) {
                logger.debug("out {} byte[] {}", (Object)rq.verb, (Object)rq.url);
                IO.copy((byte[])put, out);
            } else if (put instanceof File) {
                logger.debug("out {} file {} {}", new Object[]{rq.verb, put, rq.url});
                IO.copy((File)put, out);
            } else {
                logger.debug("out {} JSON {} {}", new Object[]{rq.verb, put, rq.url});
                codec.enc().to(out).put(put).flush();
            }
        }
    }

    private void configureHttpConnection(String verb, HttpURLConnection hcon) throws ProtocolException {
        if (hcon != null) {
            hcon.setRequestProperty("Accept-Encoding", "deflate, gzip");
            hcon.setInstanceFollowRedirects(false);
            hcon.setRequestMethod(verb);
        }
    }

    private void setHeaders(Map<String, String> headers, URLConnection con) {
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                logger.debug("set header {}={}", (Object)e.getKey(), (Object)e.getValue());
                con.setRequestProperty(e.getKey(), e.getValue());
            }
        }
    }

    public void setCache(File cache) {
        this.cache = new URLCache(cache);
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void addURLConnectionHandler(URLConnectionHandler handler) {
        this.connectionHandlers.add(handler);
    }

    public Reporter getReporter() {
        return this.reporter;
    }

    public void addProxyHandler(ProxyHandler proxyHandler) {
        this.proxyHandlers.add(proxyHandler);
    }

    public void setLog(File log) throws IOException {
        IO.mkdirs(log.getParentFile());
        this.reporter = new ReporterAdapter(IO.writer(log));
    }

    public String getUserFor(String base) throws MalformedURLException, Exception {
        URLConnectionHandler handler = this.findMatchingHandler(new URL(base));
        if (handler == null) {
            return null;
        }
        return handler.toString();
    }

    public String toName(URI url) throws Exception {
        return URLCache.toName(url);
    }

    public File getCacheFileFor(URI url) throws Exception {
        return this.cache.getCacheFileFor(url);
    }

    public void readSettings(Processor processor) throws IOException, Exception {
        try (ConnectionSettings cs = new ConnectionSettings(processor, this);){
            cs.readSettings();
        }
    }

    public URI makeDir(URI uri) throws URISyntaxException {
        if (uri.getPath() != null && uri.getPath().endsWith("/")) {
            String string = uri.toString();
            return new URI(string.substring(0, string.length() - 1));
        }
        return uri;
    }

    public boolean isOffline() {
        AtomicBoolean localOffline = this.offline;
        if (localOffline == null) {
            return false;
        }
        return localOffline.get();
    }

    public void setOffline(AtomicBoolean offline) {
        this.offline = offline;
    }

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        codec = new JSONCodec();
    }
}

