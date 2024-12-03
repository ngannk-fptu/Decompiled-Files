/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.ProtocolException
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.RedirectStrategy
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.DefaultRedirectStrategy
 *  org.apache.http.protocol.BasicHttpContext
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.EntityUtils
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 *  org.joda.time.ReadableDuration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.core.install;

import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.marketplace.client.impl.CommonsHttpTransport;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.impl.Uris;
import com.atlassian.upm.core.install.AccessDeniedException;
import com.atlassian.upm.core.install.InvalidFileURIException;
import com.atlassian.upm.core.install.RelativeURIException;
import com.atlassian.upm.core.install.UnsupportedProtocolException;
import com.atlassian.upm.core.pac.ClientContextFactory;
import com.atlassian.upm.core.pac.MarketplaceClientConfiguration;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public final class PluginDownloadServiceImpl
implements PluginDownloadService,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(PluginDownloadServiceImpl.class);
    private static final String[] DEFAULT_ACCEPTABLE_MEDIA_TYPES = new String[]{"application/octet-stream", "application/java-archive", "*/*"};
    private static final int CONNECTION_TIMEOUT_MILLIS = 30000;
    private static final int READ_TIMEOUT_MILLIS = 30000;
    private static final Duration PROGRESS_TRACKER_STATUS_UPDATE_FREQUENCY = new Duration(100L);
    private static final int DOWNLOAD_BUFFER_SIZE = 4096;
    private static final String URI_ATTR = "upm.download.uri";
    public static final int MAX_FOLLOW_REDIRECTS = 6;
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final Pattern CONTENT_DISPOSITION_FILENAME_REGEX = Pattern.compile("filename=\"([^\"]*)\"");
    private final HttpClient httpClient;
    private final ClientContextFactory clientContextFactory;

    public PluginDownloadServiceImpl(ClientContextFactory clientContextFactory) {
        this.clientContextFactory = Objects.requireNonNull(clientContextFactory, "clientContextFactory");
        HttpConfiguration config = MarketplaceClientConfiguration.httpConfigurationFromSystemProperties().connectTimeoutMillis(30000).readTimeoutMillis(30000).maxRedirects(UpmFugueConverters.fugueSome(6)).build();
        this.httpClient = CommonsHttpTransport.httpClientBuilder(config, UpmFugueConverters.fugueNone(URI.class), CommonsHttpTransport.CachingBehavior.NO_CACHING).setRedirectStrategy((RedirectStrategy)new DownloadRedirectStrategy()).build();
    }

    @Override
    public PluginDownloadService.DownloadResult downloadPlugin(URI uri, Option<String> displayName, PluginDownloadService.ProgressTracker progressTracker) throws ResponseException, FileNotFoundException {
        return this.downloadPlugin(uri, displayName, Option.none(String[].class), progressTracker);
    }

    @Override
    public PluginDownloadService.DownloadResult downloadPlugin(URI uri, Option<String> displayName, String[] acceptHeaders, PluginDownloadService.ProgressTracker progressTracker) throws ResponseException, FileNotFoundException {
        return this.downloadPlugin(uri, displayName, Option.some(acceptHeaders), progressTracker);
    }

    public void destroy() throws Exception {
        if (this.httpClient instanceof Closeable) {
            ((Closeable)this.httpClient).close();
        }
    }

    private PluginDownloadService.DownloadResult downloadPlugin(URI uri, Option<String> displayName, Option<String[]> acceptHeaders, PluginDownloadService.ProgressTracker progressTracker) throws ResponseException, FileNotFoundException {
        log.info("Downloading plugin artifact from [" + uri + "]...");
        PluginDownloadServiceImpl.checkIfURIIsSupported(uri);
        if (Uris.hasFileScheme(uri)) {
            try {
                File f = new File(uri);
                if (!f.exists() || !f.isFile()) {
                    throw new FileNotFoundException(f.getName() + " is not a valid file");
                }
                if (!f.canRead()) {
                    throw new AccessDeniedException("Cannot read " + f.getName());
                }
                return new PluginDownloadService.DownloadResult(f, displayName.getOrElse(f.getName()), Option.none(String.class));
            }
            catch (IllegalArgumentException iae) {
                throw new InvalidFileURIException("URI is not a valid download URI");
            }
        }
        Option<String> acceptHeader = acceptHeaders.flatMap(headers -> Option.some(StringUtils.join((Object[])headers, (String)", ")));
        HttpGet method = new HttpGet(uri);
        method.addHeader("X-Pac-Client-Info", this.clientContextFactory.getClientContext().toString());
        method.addHeader("Accept", (String)acceptHeader.getOrElse(StringUtils.join((Object[])DEFAULT_ACCEPTABLE_MEDIA_TYPES, (String)", ")));
        BasicHttpContext context = new BasicHttpContext();
        context.setAttribute(URI_ATTR, (Object)uri);
        try {
            HttpResponse response = this.httpClient.execute((HttpUriRequest)method, (HttpContext)context);
            URI newUri = (URI)context.getAttribute(URI_ATTR);
            if (!uri.equals(newUri)) {
                progressTracker.redirectedTo((URI)context.getAttribute(URI_ATTR));
            }
            if (response.getStatusLine().getStatusCode() == 401) {
                this.consumeResponse(response);
                throw new AccessDeniedException("Requires Authorization.");
            }
            if (response.getStatusLine().getStatusCode() != 200) {
                String message = response.getStatusLine().getStatusCode() + " " + IOUtils.toString((InputStream)response.getEntity().getContent(), (Charset)StandardCharsets.UTF_8);
                this.consumeResponse(response);
                throw new ResponseException("Failed to download plugin: " + message);
            }
            PluginDownloadService.DownloadResult downloadResult = this.copyToFile(response, newUri, displayName, progressTracker);
            return downloadResult;
        }
        catch (IOException ioe) {
            throw new ResponseException(ioe.getMessage(), (Throwable)ioe);
        }
        finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }

    private void consumeResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                EntityUtils.consume((HttpEntity)entity);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    /*
     * Exception decompiling
     */
    private PluginDownloadService.DownloadResult copyToFile(HttpResponse response, URI uri, Option<String> displayName, PluginDownloadService.ProgressTracker progressTracker) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private String getFileNameFromResponse(HttpResponse response, URI uri) {
        Matcher filenameMatcher;
        Header contentDispositionHeader = response.getLastHeader(CONTENT_DISPOSITION);
        if (contentDispositionHeader != null && (filenameMatcher = CONTENT_DISPOSITION_FILENAME_REGEX.matcher(contentDispositionHeader.getValue())).find()) {
            return filenameMatcher.group(1);
        }
        String path = uri.getPath();
        return path.substring(path.lastIndexOf(47) + 1);
    }

    private Option<String> getContentTypeFromResponse(HttpResponse response) {
        Header h = response.getEntity().getContentType();
        return h == null ? Option.none(String.class) : Option.option(h.getValue());
    }

    private String coerceFileExtensionForContentType(String filename, HttpResponse response) {
        Header contentTypeHeader = response.getLastHeader("Content-Type");
        if (contentTypeHeader != null) {
            String contentType = contentTypeHeader.getValue().toLowerCase();
            if (contentType.contains(";")) {
                contentType = contentType.substring(0, contentType.indexOf(";"));
            }
            if (contentType.equals("application/java-archive")) {
                return this.coerceFileExtension(filename, "jar");
            }
            if (contentType.endsWith("/xml")) {
                return this.coerceFileExtension(filename, "xml");
            }
        }
        return filename;
    }

    private String coerceFileExtension(String filename, String extension) {
        if (filename.contains(".")) {
            return filename.substring(0, filename.lastIndexOf(".") + 1) + extension;
        }
        return filename + "." + extension;
    }

    private void copy(InputStream in, FileOutputStream out, Long totalSize, String displayName, PluginDownloadService.ProgressTracker progressTracker) throws IOException {
        int n;
        DateTime nextUpdate = new DateTime();
        byte[] buffer = new byte[4096];
        long count = 0L;
        while (-1 != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
            count += (long)n;
            if (!nextUpdate.isBeforeNow()) continue;
            progressTracker.notify(new PluginDownloadService.Progress(count, totalSize, Option.some(displayName)));
            nextUpdate = new DateTime().plus((ReadableDuration)PROGRESS_TRACKER_STATUS_UPDATE_FREQUENCY);
        }
    }

    private static void checkIfURIIsSupported(URI uri) throws ResponseException {
        if (!uri.isAbsolute()) {
            throw new RelativeURIException("URI must be absolute");
        }
        if (!(Uris.hasHttpScheme(uri) || Uris.hasHttpsScheme(uri) || Uris.hasFileScheme(uri))) {
            throw new UnsupportedProtocolException("URI scheme '" + uri.getScheme() + "' is not supported");
        }
        if (Uris.hasFileScheme(uri) && (uri.getPath() == null || uri.getAuthority() != null || uri.getFragment() != null || uri.getQuery() != null)) {
            throw new InvalidFileURIException("URI is not a valid download URI");
        }
    }

    private static class DownloadRedirectStrategy
    implements RedirectStrategy {
        private final RedirectStrategy defaultStrategy = new DefaultRedirectStrategy();

        private DownloadRedirectStrategy() {
        }

        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            HttpUriRequest method = this.defaultStrategy.getRedirect(request, response, context);
            try {
                PluginDownloadServiceImpl.checkIfURIIsSupported(method.getURI());
            }
            catch (ResponseException e) {
                throw new ProtocolException(e.getMessage());
            }
            context.setAttribute(PluginDownloadServiceImpl.URI_ATTR, (Object)method.getURI());
            method.removeHeaders("X-Pac-Client-Info");
            method.addHeader("Accept", StringUtils.join((Object[])DEFAULT_ACCEPTABLE_MEDIA_TYPES, (String)", "));
            return method;
        }

        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
            switch (response.getStatusLine().getStatusCode()) {
                case 301: 
                case 302: {
                    return true;
                }
            }
            return false;
        }
    }
}

