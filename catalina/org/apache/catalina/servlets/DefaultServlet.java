/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletResponseWrapper
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.coyote.Constants
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.http.ResponseUtil
 *  org.apache.tomcat.util.http.parser.ContentRange
 *  org.apache.tomcat.util.http.parser.EntityTag
 *  org.apache.tomcat.util.http.parser.Ranges
 *  org.apache.tomcat.util.http.parser.Ranges$Entry
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 *  org.apache.tomcat.util.security.PrivilegedGetTccl
 *  org.apache.tomcat.util.security.PrivilegedSetTccl
 */
package org.apache.catalina.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.webresources.CachedResource;
import org.apache.coyote.Constants;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.http.ResponseUtil;
import org.apache.tomcat.util.http.parser.ContentRange;
import org.apache.tomcat.util.http.parser.EntityTag;
import org.apache.tomcat.util.http.parser.Ranges;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class DefaultServlet
extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected static final StringManager sm = StringManager.getManager(DefaultServlet.class);
    private static final DocumentBuilderFactory factory;
    private static final SecureEntityResolver secureEntityResolver;
    protected static final ArrayList<Range> FULL;
    private static final Range IGNORE;
    protected static final String mimeSeparation = "CATALINA_MIME_BOUNDARY";
    protected static final int BUFFER_SIZE = 4096;
    protected int debug = 0;
    protected int input = 2048;
    protected boolean listings = false;
    protected boolean readOnly = true;
    protected CompressionFormat[] compressionFormats;
    protected int output = 2048;
    protected String localXsltFile = null;
    protected String contextXsltFile = null;
    protected String globalXsltFile = null;
    protected String readmeFile = null;
    protected transient WebResourceRoot resources = null;
    protected String fileEncoding = null;
    private transient Charset fileEncodingCharset = null;
    private BomConfig useBomIfPresent = null;
    protected int sendfileSize = 49152;
    protected boolean useAcceptRanges = true;
    protected boolean showServerInfo = true;
    protected boolean sortListings = false;
    protected transient SortManager sortManager;
    private boolean allowPartialPut = true;

    public void destroy() {
    }

    public void init() throws ServletException {
        if (this.getServletConfig().getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(this.getServletConfig().getInitParameter("debug"));
        }
        if (this.getServletConfig().getInitParameter("input") != null) {
            this.input = Integer.parseInt(this.getServletConfig().getInitParameter("input"));
        }
        if (this.getServletConfig().getInitParameter("output") != null) {
            this.output = Integer.parseInt(this.getServletConfig().getInitParameter("output"));
        }
        this.listings = Boolean.parseBoolean(this.getServletConfig().getInitParameter("listings"));
        if (this.getServletConfig().getInitParameter("readonly") != null) {
            this.readOnly = Boolean.parseBoolean(this.getServletConfig().getInitParameter("readonly"));
        }
        this.compressionFormats = this.parseCompressionFormats(this.getServletConfig().getInitParameter("precompressed"), this.getServletConfig().getInitParameter("gzip"));
        if (this.getServletConfig().getInitParameter("sendfileSize") != null) {
            this.sendfileSize = Integer.parseInt(this.getServletConfig().getInitParameter("sendfileSize")) * 1024;
        }
        this.fileEncoding = this.getServletConfig().getInitParameter("fileEncoding");
        if (this.fileEncoding == null) {
            this.fileEncodingCharset = Charset.defaultCharset();
            this.fileEncoding = this.fileEncodingCharset.name();
        } else {
            try {
                this.fileEncodingCharset = B2CConverter.getCharset((String)this.fileEncoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new ServletException((Throwable)e);
            }
        }
        String useBomIfPresent = this.getServletConfig().getInitParameter("useBomIfPresent");
        if (useBomIfPresent == null) {
            this.useBomIfPresent = BomConfig.TRUE;
        } else {
            for (BomConfig bomConfig : BomConfig.values()) {
                if (!bomConfig.configurationValue.equalsIgnoreCase(useBomIfPresent)) continue;
                this.useBomIfPresent = bomConfig;
                break;
            }
            if (this.useBomIfPresent == null) {
                IllegalArgumentException iae = new IllegalArgumentException(sm.getString("defaultServlet.unknownBomConfig", new Object[]{useBomIfPresent}));
                throw new ServletException((Throwable)iae);
            }
        }
        this.globalXsltFile = this.getServletConfig().getInitParameter("globalXsltFile");
        this.contextXsltFile = this.getServletConfig().getInitParameter("contextXsltFile");
        this.localXsltFile = this.getServletConfig().getInitParameter("localXsltFile");
        this.readmeFile = this.getServletConfig().getInitParameter("readmeFile");
        if (this.getServletConfig().getInitParameter("useAcceptRanges") != null) {
            this.useAcceptRanges = Boolean.parseBoolean(this.getServletConfig().getInitParameter("useAcceptRanges"));
        }
        if (this.input < 256) {
            this.input = 256;
        }
        if (this.output < 256) {
            this.output = 256;
        }
        if (this.debug > 0) {
            this.log("DefaultServlet.init:  input buffer size=" + this.input + ", output buffer size=" + this.output);
        }
        this.resources = (WebResourceRoot)this.getServletContext().getAttribute("org.apache.catalina.resources");
        if (this.resources == null) {
            throw new UnavailableException(sm.getString("defaultServlet.noResources"));
        }
        if (this.getServletConfig().getInitParameter("showServerInfo") != null) {
            this.showServerInfo = Boolean.parseBoolean(this.getServletConfig().getInitParameter("showServerInfo"));
        }
        if (this.getServletConfig().getInitParameter("sortListings") != null) {
            this.sortListings = Boolean.parseBoolean(this.getServletConfig().getInitParameter("sortListings"));
            if (this.sortListings) {
                boolean sortDirectoriesFirst = this.getServletConfig().getInitParameter("sortDirectoriesFirst") != null ? Boolean.parseBoolean(this.getServletConfig().getInitParameter("sortDirectoriesFirst")) : false;
                this.sortManager = new SortManager(sortDirectoriesFirst);
            }
        }
        if (this.getServletConfig().getInitParameter("allowPartialPut") != null) {
            this.allowPartialPut = Boolean.parseBoolean(this.getServletConfig().getInitParameter("allowPartialPut"));
        }
    }

    private CompressionFormat[] parseCompressionFormats(String precompressed, String gzip) {
        ArrayList<CompressionFormat> ret = new ArrayList<CompressionFormat>();
        if (precompressed != null && precompressed.indexOf(61) > 0) {
            for (String pair : precompressed.split(",")) {
                String[] setting = pair.split("=");
                String encoding = setting[0];
                String extension = setting[1];
                ret.add(new CompressionFormat(extension, encoding));
            }
        } else if (precompressed != null) {
            if (Boolean.parseBoolean(precompressed)) {
                ret.add(new CompressionFormat(".br", "br"));
                ret.add(new CompressionFormat(".gz", "gzip"));
            }
        } else if (Boolean.parseBoolean(gzip)) {
            ret.add(new CompressionFormat(".gz", "gzip"));
        }
        return ret.toArray(new CompressionFormat[0]);
    }

    protected String getRelativePath(HttpServletRequest request) {
        return this.getRelativePath(request, false);
    }

    protected String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
        String servletPath;
        String pathInfo;
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
            servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
        } else {
            pathInfo = request.getPathInfo();
            servletPath = request.getServletPath();
        }
        StringBuilder result = new StringBuilder();
        if (servletPath.length() > 0) {
            result.append(servletPath);
        }
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0 && !allowEmptyPath) {
            result.append('/');
        }
        return result.toString();
    }

    protected String getPathPrefix(HttpServletRequest request) {
        return request.getContextPath();
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getDispatcherType() == DispatcherType.ERROR) {
            this.doGet(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.serveResource(request, response, true, this.fileEncoding);
    }

    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean serveContent = DispatcherType.INCLUDE.equals((Object)request.getDispatcherType());
        this.serveResource(request, response, serveContent, this.fileEncoding);
    }

    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Allow", this.determineMethodsAllowed(req));
    }

    protected String determineMethodsAllowed(HttpServletRequest req) {
        StringBuilder allow = new StringBuilder();
        allow.append("OPTIONS, GET, HEAD, POST");
        if (!this.readOnly) {
            allow.append(", PUT, DELETE");
        }
        if (req instanceof RequestFacade && ((RequestFacade)req).getAllowTrace()) {
            allow.append(", TRACE");
        }
        return allow.toString();
    }

    protected void sendNotAllowed(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("Allow", this.determineMethodsAllowed(req));
        resp.sendError(405);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.doGet(request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            this.sendNotAllowed(req, resp);
            return;
        }
        String path = this.getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        Range range = this.parseContentRange(req, resp);
        if (range == null) {
            return;
        }
        Object resourceInputStream = null;
        try {
            if (range == IGNORE) {
                resourceInputStream = req.getInputStream();
            } else {
                File contentFile = this.executePartialPut(req, range, path);
                resourceInputStream = new FileInputStream(contentFile);
            }
            if (this.resources.write(path, (InputStream)resourceInputStream, true)) {
                if (resource.exists()) {
                    resp.setStatus(204);
                } else {
                    resp.setStatus(201);
                }
            } else {
                resp.sendError(409);
            }
        }
        finally {
            if (resourceInputStream != null) {
                try {
                    ((InputStream)resourceInputStream).close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    protected File executePartialPut(HttpServletRequest req, Range range, String path) throws IOException {
        String convertedResourcePath;
        File tempDir = (File)this.getServletContext().getAttribute("javax.servlet.context.tempdir");
        File contentFile = new File(tempDir, convertedResourcePath = path.replace('/', '.'));
        if (contentFile.createNewFile()) {
            contentFile.deleteOnExit();
        }
        try (RandomAccessFile randAccessContentFile = new RandomAccessFile(contentFile, "rw");){
            WebResource oldResource = this.resources.getResource(path);
            if (oldResource.isFile()) {
                try (BufferedInputStream bufOldRevStream = new BufferedInputStream(oldResource.getInputStream(), 4096);){
                    int numBytesRead;
                    byte[] copyBuffer = new byte[4096];
                    while ((numBytesRead = bufOldRevStream.read(copyBuffer)) != -1) {
                        randAccessContentFile.write(copyBuffer, 0, numBytesRead);
                    }
                }
            }
            randAccessContentFile.setLength(range.length);
            randAccessContentFile.seek(range.start);
            byte[] transferBuffer = new byte[4096];
            try (BufferedInputStream requestBufInStream = new BufferedInputStream((InputStream)req.getInputStream(), 4096);){
                int numBytesRead;
                while ((numBytesRead = requestBufInStream.read(transferBuffer)) != -1) {
                    randAccessContentFile.write(transferBuffer, 0, numBytesRead);
                }
            }
        }
        return contentFile;
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            this.sendNotAllowed(req, resp);
            return;
        }
        String path = this.getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.exists()) {
            if (resource.delete()) {
                resp.setStatus(204);
            } else {
                resp.sendError(405);
            }
        } else {
            resp.sendError(404);
        }
    }

    protected boolean checkIfHeaders(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        return this.checkIfMatch(request, response, resource) && this.checkIfModifiedSince(request, response, resource) && this.checkIfNoneMatch(request, response, resource) && this.checkIfUnmodifiedSince(request, response, resource);
    }

    protected String rewriteUrl(String path) {
        return URLEncoder.DEFAULT.encode(path, StandardCharsets.UTF_8);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void serveResource(HttpServletRequest request, HttpServletResponse response, boolean content, String inputEncoding) throws IOException, ServletException {
        boolean conversionRequired;
        boolean outputEncodingSpecified;
        List<PrecompressedResource> precompressedResources;
        String contentType;
        boolean isError;
        boolean serveContent = content;
        String path = this.getRelativePath(request, true);
        if (this.debug > 0) {
            if (serveContent) {
                this.log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers and data");
            } else {
                this.log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers only");
            }
        }
        if (path.length() == 0) {
            this.doDirectoryRedirect(request, response);
            return;
        }
        WebResource resource = this.resources.getResource(path);
        boolean bl = isError = DispatcherType.ERROR == request.getDispatcherType();
        if (!resource.exists()) {
            String requestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (requestUri != null) {
                throw new FileNotFoundException(sm.getString("defaultServlet.missingResource", new Object[]{requestUri}));
            }
            requestUri = request.getRequestURI();
            if (isError) {
                response.sendError(((Integer)request.getAttribute("javax.servlet.error.status_code")).intValue());
                return;
            } else {
                response.sendError(404, sm.getString("defaultServlet.missingResource", new Object[]{requestUri}));
            }
            return;
        }
        if (!resource.canRead()) {
            String requestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (requestUri != null) {
                throw new FileNotFoundException(sm.getString("defaultServlet.missingResource", new Object[]{requestUri}));
            }
            requestUri = request.getRequestURI();
            if (isError) {
                response.sendError(((Integer)request.getAttribute("javax.servlet.error.status_code")).intValue());
                return;
            } else {
                response.sendError(403, requestUri);
            }
            return;
        }
        boolean included = false;
        if (resource.isFile()) {
            boolean bl2 = included = request.getAttribute("javax.servlet.include.context_path") != null;
            if (!(included || isError || this.checkIfHeaders(request, response, resource))) {
                return;
            }
        }
        if ((contentType = resource.getMimeType()) == null) {
            contentType = this.getServletContext().getMimeType(resource.getName());
            resource.setMimeType(contentType);
        }
        String eTag = null;
        String lastModifiedHttp = null;
        if (resource.isFile() && !isError) {
            eTag = this.generateETag(resource);
            lastModifiedHttp = resource.getLastModifiedHttp();
        }
        boolean usingPrecompressedVersion = false;
        if (this.compressionFormats.length > 0 && !included && resource.isFile() && !this.pathEndsWithCompressedExtension(path) && !(precompressedResources = this.getAvailablePrecompressedResources(path)).isEmpty()) {
            ResponseUtil.addVaryFieldName((HttpServletResponse)response, (String)"accept-encoding");
            PrecompressedResource bestResource = this.getBestPrecompressedResource(request, precompressedResources);
            if (bestResource != null) {
                response.addHeader("Content-Encoding", bestResource.format.encoding);
                resource = bestResource.resource;
                usingPrecompressedVersion = true;
            }
        }
        ArrayList<Range> ranges = FULL;
        long contentLength = -1L;
        if (resource.isDirectory()) {
            if (!path.endsWith("/")) {
                this.doDirectoryRedirect(request, response);
                return;
            }
            if (!this.listings) {
                response.sendError(404, sm.getString("defaultServlet.missingResource", new Object[]{request.getRequestURI()}));
                return;
            }
            contentType = "text/html;charset=UTF-8";
        } else {
            if (!isError) {
                if (this.useAcceptRanges) {
                    response.setHeader("Accept-Ranges", "bytes");
                }
                if ((ranges = this.parseRange(request, response, resource)) == null) {
                    return;
                }
                response.setHeader("ETag", eTag);
                response.setHeader("Last-Modified", lastModifiedHttp);
            }
            if ((contentLength = resource.getContentLength()) == 0L) {
                serveContent = false;
            }
        }
        ServletOutputStream ostream = null;
        PrintWriter writer = null;
        if (serveContent) {
            try {
                ostream = response.getOutputStream();
            }
            catch (IllegalStateException e) {
                if (usingPrecompressedVersion || !DefaultServlet.isText(contentType)) throw e;
                writer = response.getWriter();
                ranges = FULL;
            }
        }
        HttpServletResponse r = response;
        long contentWritten = 0L;
        while (r instanceof ServletResponseWrapper) {
            r = ((ServletResponseWrapper)r).getResponse();
        }
        if (r instanceof ResponseFacade) {
            contentWritten = ((ResponseFacade)r).getContentWritten();
        }
        if (contentWritten > 0L) {
            ranges = FULL;
        }
        String outputEncoding = response.getCharacterEncoding();
        Charset charset = B2CConverter.getCharset((String)outputEncoding);
        boolean bl3 = outputEncodingSpecified = outputEncoding != Constants.DEFAULT_BODY_CHARSET.name() && outputEncoding != this.resources.getContext().getResponseCharacterEncoding();
        if (!usingPrecompressedVersion && DefaultServlet.isText(contentType) && outputEncodingSpecified && !charset.equals(this.fileEncodingCharset)) {
            conversionRequired = true;
            ranges = FULL;
        } else {
            conversionRequired = false;
        }
        if (resource.isDirectory() || isError || ranges == FULL) {
            if (contentType != null) {
                if (this.debug > 0) {
                    this.log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                }
                if (response.getContentType() == null) {
                    response.setContentType(contentType);
                }
            }
            if (resource.isFile() && contentLength >= 0L && (!serveContent || ostream != null)) {
                if (this.debug > 0) {
                    this.log("DefaultServlet.serveFile:  contentLength=" + contentLength);
                }
                if (contentWritten == 0L && !conversionRequired) {
                    response.setContentLengthLong(contentLength);
                }
            }
            if (!serveContent) return;
            try {
                response.setBufferSize(this.output);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            InputStream renderResult = null;
            if (ostream == null) {
                if (resource.isDirectory()) {
                    renderResult = this.render(request, this.getPathPrefix(request), resource, inputEncoding);
                } else {
                    renderResult = resource.getInputStream();
                    if (included) {
                        Charset bomCharset;
                        if (!renderResult.markSupported()) {
                            renderResult = new BufferedInputStream(renderResult);
                        }
                        if ((bomCharset = DefaultServlet.processBom(renderResult, this.useBomIfPresent.stripBom)) != null && this.useBomIfPresent.useBomEncoding) {
                            inputEncoding = bomCharset.name();
                        }
                    }
                }
                this.copy(renderResult, writer, inputEncoding);
                return;
            } else {
                if (resource.isDirectory()) {
                    renderResult = this.render(request, this.getPathPrefix(request), resource, inputEncoding);
                } else if (conversionRequired || included) {
                    Charset bomCharset;
                    InputStream source = resource.getInputStream();
                    if (!source.markSupported()) {
                        source = new BufferedInputStream(source);
                    }
                    if ((bomCharset = DefaultServlet.processBom(source, this.useBomIfPresent.stripBom)) != null && this.useBomIfPresent.useBomEncoding) {
                        inputEncoding = bomCharset.name();
                    }
                    if (outputEncodingSpecified) {
                        OutputStreamWriter osw = new OutputStreamWriter((OutputStream)ostream, charset);
                        PrintWriter pw = new PrintWriter(osw);
                        this.copy(source, pw, inputEncoding);
                        pw.flush();
                    } else {
                        renderResult = source;
                    }
                } else if (!this.checkSendfile(request, response, resource, contentLength, null)) {
                    byte[] resourceBody = null;
                    if (resource instanceof CachedResource) {
                        resourceBody = resource.getContent();
                    }
                    if (resourceBody == null) {
                        renderResult = resource.getInputStream();
                    } else {
                        ostream.write(resourceBody);
                    }
                }
                if (renderResult == null) return;
                this.copy(renderResult, ostream);
            }
            return;
        }
        if (ranges == null || ranges.isEmpty()) {
            return;
        }
        response.setStatus(206);
        if (ranges.size() == 1) {
            Range range = ranges.get(0);
            response.addHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + range.length);
            long length = range.end - range.start + 1L;
            response.setContentLengthLong(length);
            if (contentType != null) {
                if (this.debug > 0) {
                    this.log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                }
                response.setContentType(contentType);
            }
            if (!serveContent) return;
            try {
                response.setBufferSize(this.output);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            if (ostream == null) throw new IllegalStateException();
            if (this.checkSendfile(request, response, resource, range.end - range.start + 1L, range)) return;
            this.copy(resource, ostream, range);
            return;
        }
        response.setContentType("multipart/byteranges; boundary=CATALINA_MIME_BOUNDARY");
        if (!serveContent) return;
        try {
            response.setBufferSize(this.output);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
        if (ostream == null) throw new IllegalStateException();
        this.copy(resource, ostream, ranges.iterator(), contentType);
    }

    private static Charset processBom(InputStream is, boolean stripBom) throws IOException {
        byte[] bom = new byte[4];
        is.mark(bom.length);
        int count = is.read(bom);
        if (count < 2) {
            DefaultServlet.skip(is, 0, stripBom);
            return null;
        }
        int b0 = bom[0] & 0xFF;
        int b1 = bom[1] & 0xFF;
        if (b0 == 254 && b1 == 255) {
            DefaultServlet.skip(is, 2, stripBom);
            return StandardCharsets.UTF_16BE;
        }
        if (count == 2 && b0 == 255 && b1 == 254) {
            DefaultServlet.skip(is, 2, stripBom);
            return StandardCharsets.UTF_16LE;
        }
        if (count < 3) {
            DefaultServlet.skip(is, 0, stripBom);
            return null;
        }
        int b2 = bom[2] & 0xFF;
        if (b0 == 239 && b1 == 187 && b2 == 191) {
            DefaultServlet.skip(is, 3, stripBom);
            return StandardCharsets.UTF_8;
        }
        if (count < 4) {
            DefaultServlet.skip(is, 0, stripBom);
            return null;
        }
        int b3 = bom[3] & 0xFF;
        if (b0 == 0 && b1 == 0 && b2 == 254 && b3 == 255) {
            return Charset.forName("UTF-32BE");
        }
        if (b0 == 255 && b1 == 254 && b2 == 0 && b3 == 0) {
            return Charset.forName("UTF-32LE");
        }
        if (b0 == 255 && b1 == 254) {
            DefaultServlet.skip(is, 2, stripBom);
            return StandardCharsets.UTF_16LE;
        }
        DefaultServlet.skip(is, 0, stripBom);
        return null;
    }

    private static void skip(InputStream is, int skip, boolean stripBom) throws IOException {
        block1: {
            is.reset();
            if (!stripBom) break block1;
            while (skip-- > 0 && is.read() >= 0) {
            }
        }
    }

    private static boolean isText(String contentType) {
        return contentType == null || contentType.startsWith("text") || contentType.endsWith("xml") || contentType.contains("/javascript");
    }

    private boolean pathEndsWithCompressedExtension(String path) {
        for (CompressionFormat format : this.compressionFormats) {
            if (!path.endsWith(format.extension)) continue;
            return true;
        }
        return false;
    }

    private List<PrecompressedResource> getAvailablePrecompressedResources(String path) {
        ArrayList<PrecompressedResource> ret = new ArrayList<PrecompressedResource>(this.compressionFormats.length);
        for (CompressionFormat format : this.compressionFormats) {
            WebResource precompressedResource = this.resources.getResource(path + format.extension);
            if (!precompressedResource.exists() || !precompressedResource.isFile()) continue;
            ret.add(new PrecompressedResource(precompressedResource, format));
        }
        return ret;
    }

    private PrecompressedResource getBestPrecompressedResource(HttpServletRequest request, List<PrecompressedResource> precompressedResources) {
        Enumeration headers = request.getHeaders("Accept-Encoding");
        PrecompressedResource bestResource = null;
        double bestResourceQuality = 0.0;
        int bestResourcePreference = Integer.MAX_VALUE;
        while (headers.hasMoreElements()) {
            String header = (String)headers.nextElement();
            block1: for (String preference : header.split(",")) {
                double quality = 1.0;
                int qualityIdx = preference.indexOf(59);
                if (qualityIdx > 0) {
                    int equalsIdx = preference.indexOf(61, qualityIdx + 1);
                    if (equalsIdx == -1) continue;
                    quality = Double.parseDouble(preference.substring(equalsIdx + 1).trim());
                }
                if (!(quality >= bestResourceQuality)) continue;
                String encoding = preference;
                if (qualityIdx > 0) {
                    encoding = encoding.substring(0, qualityIdx);
                }
                if ("identity".equals(encoding = encoding.trim())) {
                    bestResource = null;
                    bestResourceQuality = quality;
                    bestResourcePreference = Integer.MAX_VALUE;
                    continue;
                }
                if ("*".equals(encoding)) {
                    bestResource = precompressedResources.get(0);
                    bestResourceQuality = quality;
                    bestResourcePreference = 0;
                    continue;
                }
                for (int i = 0; i < precompressedResources.size(); ++i) {
                    PrecompressedResource resource = precompressedResources.get(i);
                    if (!encoding.equals(resource.format.encoding)) continue;
                    if (!(quality > bestResourceQuality) && i >= bestResourcePreference) continue block1;
                    bestResource = resource;
                    bestResourceQuality = quality;
                    bestResourcePreference = i;
                    continue block1;
                }
            }
        }
        return bestResource;
    }

    private void doDirectoryRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder location = new StringBuilder(request.getRequestURI());
        location.append('/');
        if (request.getQueryString() != null) {
            location.append('?');
            location.append(request.getQueryString());
        }
        while (location.length() > 1 && location.charAt(1) == '/') {
            location.deleteCharAt(0);
        }
        response.sendRedirect(response.encodeRedirectURL(location.toString()));
    }

    protected Range parseContentRange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contentRangeHeader = request.getHeader("Content-Range");
        if (contentRangeHeader == null) {
            return IGNORE;
        }
        if (!this.allowPartialPut) {
            response.sendError(400);
            return null;
        }
        ContentRange contentRange = ContentRange.parse((StringReader)new StringReader(contentRangeHeader));
        if (contentRange == null) {
            response.sendError(400);
            return null;
        }
        if (!contentRange.getUnits().equals("bytes")) {
            response.sendError(400);
            return null;
        }
        Range range = new Range();
        range.start = contentRange.getStart();
        range.end = contentRange.getEnd();
        range.length = contentRange.getLength();
        if (!range.validate()) {
            response.sendError(400);
            return null;
        }
        return range;
    }

    protected ArrayList<Range> parseRange(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        long fileLength;
        String headerValue = request.getHeader("If-Range");
        if (headerValue != null) {
            long headerValueTime = -1L;
            try {
                headerValueTime = request.getDateHeader("If-Range");
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            String eTag = this.generateETag(resource);
            long lastModified = resource.getLastModified();
            if (headerValueTime == -1L ? !eTag.equals(headerValue.trim()) : Math.abs(lastModified - headerValueTime) > 1000L) {
                return FULL;
            }
        }
        if ((fileLength = resource.getContentLength()) == 0L) {
            return FULL;
        }
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader == null) {
            return FULL;
        }
        Ranges ranges = Ranges.parse((StringReader)new StringReader(rangeHeader));
        if (ranges == null) {
            response.addHeader("Content-Range", "bytes */" + fileLength);
            response.sendError(416);
            return null;
        }
        if (!ranges.getUnits().equals("bytes")) {
            return FULL;
        }
        ArrayList<Range> result = new ArrayList<Range>();
        for (Ranges.Entry entry : ranges.getEntries()) {
            Range currentRange = new Range();
            if (entry.getStart() == -1L) {
                currentRange.start = fileLength - entry.getEnd();
                if (currentRange.start < 0L) {
                    currentRange.start = 0L;
                }
                currentRange.end = fileLength - 1L;
            } else if (entry.getEnd() == -1L) {
                currentRange.start = entry.getStart();
                currentRange.end = fileLength - 1L;
            } else {
                currentRange.start = entry.getStart();
                currentRange.end = entry.getEnd();
            }
            currentRange.length = fileLength;
            if (!currentRange.validate()) {
                response.addHeader("Content-Range", "bytes */" + fileLength);
                response.sendError(416);
                return null;
            }
            result.add(currentRange);
        }
        return result;
    }

    @Deprecated
    protected InputStream render(String contextPath, WebResource resource, String encoding) throws IOException, ServletException {
        return this.render(null, contextPath, resource, encoding);
    }

    protected InputStream render(HttpServletRequest request, String contextPath, WebResource resource, String encoding) throws IOException, ServletException {
        Source xsltSource = this.findXsltSource(resource);
        if (xsltSource == null) {
            return this.renderHtml(request, contextPath, resource, encoding);
        }
        return this.renderXml(request, contextPath, resource, xsltSource, encoding);
    }

    @Deprecated
    protected InputStream renderXml(String contextPath, WebResource resource, Source xsltSource, String encoding) throws ServletException, IOException {
        return this.renderXml(null, contextPath, resource, xsltSource, encoding);
    }

    protected InputStream renderXml(HttpServletRequest request, String contextPath, WebResource resource, Source xsltSource, String encoding) throws IOException, ServletException {
        ClassLoader original;
        PrivilegedGetTccl pa;
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<listing ");
        sb.append(" contextPath='");
        sb.append(contextPath);
        sb.append('\'');
        sb.append(" directory='");
        sb.append(resource.getName());
        sb.append("' ");
        sb.append(" hasParent='").append(!resource.getName().equals("/"));
        sb.append("'>");
        sb.append("<entries>");
        String[] entries = this.resources.list(resource.getWebappPath());
        String rewrittenContextPath = this.rewriteUrl(contextPath);
        String directoryWebappPath = resource.getWebappPath();
        for (String entry : entries) {
            WebResource childResource;
            if (entry.equalsIgnoreCase("WEB-INF") || entry.equalsIgnoreCase("META-INF") || entry.equalsIgnoreCase(this.localXsltFile) || (directoryWebappPath + entry).equals(this.contextXsltFile) || !(childResource = this.resources.getResource(directoryWebappPath + entry)).exists()) continue;
            sb.append("<entry");
            sb.append(" type='").append(childResource.isDirectory() ? "dir" : "file").append('\'');
            sb.append(" urlPath='").append(rewrittenContextPath).append(Escape.xml((String)this.rewriteUrl(directoryWebappPath + entry))).append(childResource.isDirectory() ? "/" : "").append('\'');
            if (childResource.isFile()) {
                sb.append(" size='").append(this.renderSize(childResource.getContentLength())).append('\'');
            }
            sb.append(" date='").append(childResource.getLastModifiedHttp()).append('\'');
            sb.append(" longDate='").append(childResource.getLastModified()).append('\'');
            sb.append('>');
            sb.append(Escape.htmlElementContent((String)entry));
            if (childResource.isDirectory()) {
                sb.append('/');
            }
            sb.append("</entry>");
        }
        sb.append("</entries>");
        String readme = this.getReadme(resource, encoding);
        if (readme != null) {
            sb.append("<readme><![CDATA[");
            sb.append(readme);
            sb.append("]]></readme>");
        }
        sb.append("</listing>");
        Thread currentThread = Thread.currentThread();
        if (Globals.IS_SECURITY_ENABLED) {
            pa = new PrivilegedGetTccl(currentThread);
            original = (ClassLoader)AccessController.doPrivileged(pa);
        } else {
            original = currentThread.getContextClassLoader();
        }
        try {
            if (Globals.IS_SECURITY_ENABLED) {
                pa = new PrivilegedSetTccl(currentThread, DefaultServlet.class.getClassLoader());
                AccessController.doPrivileged(pa);
            } else {
                currentThread.setContextClassLoader(DefaultServlet.class.getClassLoader());
            }
            TransformerFactory tFactory = TransformerFactory.newInstance();
            StreamSource xmlSource = new StreamSource(new StringReader(sb.toString()));
            Transformer transformer = tFactory.newTransformer(xsltSource);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            OutputStreamWriter osWriter = new OutputStreamWriter((OutputStream)stream, StandardCharsets.UTF_8);
            StreamResult out = new StreamResult(osWriter);
            transformer.transform(xmlSource, out);
            osWriter.flush();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream.toByteArray());
            return byteArrayInputStream;
        }
        catch (TransformerException e) {
            throw new ServletException(sm.getString("defaultServlet.xslError"), (Throwable)e);
        }
        finally {
            if (Globals.IS_SECURITY_ENABLED) {
                PrivilegedSetTccl pa2 = new PrivilegedSetTccl(currentThread, original);
                AccessController.doPrivileged(pa2);
            } else {
                currentThread.setContextClassLoader(original);
            }
        }
    }

    @Deprecated
    protected InputStream renderHtml(String contextPath, WebResource resource, String encoding) throws IOException {
        return this.renderHtml(null, contextPath, resource, encoding);
    }

    protected InputStream renderHtml(HttpServletRequest request, String contextPath, WebResource resource, String encoding) throws IOException {
        int slash;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter osWriter = new OutputStreamWriter((OutputStream)stream, StandardCharsets.UTF_8);
        PrintWriter writer = new PrintWriter(osWriter);
        StringBuilder sb = new StringBuilder();
        String directoryWebappPath = resource.getWebappPath();
        WebResource[] entries = this.resources.listResources(directoryWebappPath);
        String rewrittenContextPath = this.rewriteUrl(contextPath);
        sb.append("<!doctype html><html>\r\n");
        sb.append("<head>\r\n");
        sb.append("<title>");
        sb.append(sm.getString("directory.title", new Object[]{directoryWebappPath}));
        sb.append("</title>\r\n");
        sb.append("<style>");
        sb.append("body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}");
        sb.append("</style> ");
        sb.append("</head>\r\n");
        sb.append("<body>");
        sb.append("<h1>");
        sb.append(sm.getString("directory.title", new Object[]{directoryWebappPath}));
        String parentDirectory = directoryWebappPath;
        if (parentDirectory.endsWith("/")) {
            parentDirectory = parentDirectory.substring(0, parentDirectory.length() - 1);
        }
        if ((slash = parentDirectory.lastIndexOf(47)) >= 0) {
            String parent = directoryWebappPath.substring(0, slash);
            sb.append(" - <a href=\"");
            sb.append(rewrittenContextPath);
            if (parent.equals("")) {
                parent = "/";
            }
            sb.append(this.rewriteUrl(parent));
            if (!parent.endsWith("/")) {
                sb.append('/');
            }
            sb.append("\">");
            sb.append("<b>");
            sb.append(sm.getString("directory.parent", new Object[]{parent}));
            sb.append("</b>");
            sb.append("</a>");
        }
        sb.append("</h1>");
        sb.append("<hr class=\"line\">");
        sb.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\">\r\n");
        SortManager.Order order = this.sortListings && null != request ? this.sortManager.getOrder(request.getQueryString()) : null;
        sb.append("<tr>\r\n");
        sb.append("<td align=\"left\"><font size=\"+1\"><strong>");
        if (this.sortListings && null != request) {
            sb.append("<a href=\"?C=N;O=");
            sb.append(this.getOrderChar(order, 'N'));
            sb.append("\">");
            sb.append(sm.getString("directory.filename"));
            sb.append("</a>");
        } else {
            sb.append(sm.getString("directory.filename"));
        }
        sb.append("</strong></font></td>\r\n");
        sb.append("<td align=\"center\"><font size=\"+1\"><strong>");
        if (this.sortListings && null != request) {
            sb.append("<a href=\"?C=S;O=");
            sb.append(this.getOrderChar(order, 'S'));
            sb.append("\">");
            sb.append(sm.getString("directory.size"));
            sb.append("</a>");
        } else {
            sb.append(sm.getString("directory.size"));
        }
        sb.append("</strong></font></td>\r\n");
        sb.append("<td align=\"right\"><font size=\"+1\"><strong>");
        if (this.sortListings && null != request) {
            sb.append("<a href=\"?C=M;O=");
            sb.append(this.getOrderChar(order, 'M'));
            sb.append("\">");
            sb.append(sm.getString("directory.lastModified"));
            sb.append("</a>");
        } else {
            sb.append(sm.getString("directory.lastModified"));
        }
        sb.append("</strong></font></td>\r\n");
        sb.append("</tr>");
        if (null != this.sortManager && null != request) {
            this.sortManager.sort(entries, request.getQueryString());
        }
        boolean shade = false;
        for (WebResource childResource : entries) {
            String filename = childResource.getName();
            if (filename.equalsIgnoreCase("WEB-INF") || filename.equalsIgnoreCase("META-INF") || !childResource.exists()) continue;
            sb.append("<tr");
            if (shade) {
                sb.append(" bgcolor=\"#eeeeee\"");
            }
            sb.append(">\r\n");
            shade = !shade;
            sb.append("<td align=\"left\">&nbsp;&nbsp;\r\n");
            sb.append("<a href=\"");
            sb.append(rewrittenContextPath);
            sb.append(this.rewriteUrl(childResource.getWebappPath()));
            if (childResource.isDirectory()) {
                sb.append('/');
            }
            sb.append("\"><tt>");
            sb.append(Escape.htmlElementContent((String)filename));
            if (childResource.isDirectory()) {
                sb.append('/');
            }
            sb.append("</tt></a></td>\r\n");
            sb.append("<td align=\"right\"><tt>");
            if (childResource.isDirectory()) {
                sb.append("&nbsp;");
            } else {
                sb.append(this.renderSize(childResource.getContentLength()));
            }
            sb.append("</tt></td>\r\n");
            sb.append("<td align=\"right\"><tt>");
            sb.append(childResource.getLastModifiedHttp());
            sb.append("</tt></td>\r\n");
            sb.append("</tr>\r\n");
        }
        sb.append("</table>\r\n");
        sb.append("<hr class=\"line\">");
        String readme = this.getReadme(resource, encoding);
        if (readme != null) {
            sb.append(readme);
            sb.append("<hr class=\"line\">");
        }
        if (this.showServerInfo) {
            sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        }
        sb.append("</body>\r\n");
        sb.append("</html>\r\n");
        writer.write(sb.toString());
        writer.flush();
        return new ByteArrayInputStream(stream.toByteArray());
    }

    protected String renderSize(long size) {
        long leftSide = size / 1024L;
        long rightSide = size % 1024L / 103L;
        if (leftSide == 0L && rightSide == 0L && size > 0L) {
            rightSide = 1L;
        }
        return "" + leftSide + "." + rightSide + " KiB";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String getReadme(WebResource directory, String encoding) {
        if (this.readmeFile != null) {
            WebResource resource = this.resources.getResource(directory.getWebappPath() + this.readmeFile);
            if (resource.isFile()) {
                StringWriter buffer = new StringWriter();
                InputStreamReader reader = null;
                try (InputStream is = resource.getInputStream();){
                    reader = encoding != null ? new InputStreamReader(is, encoding) : new InputStreamReader(is);
                    this.copyRange(reader, new PrintWriter(buffer));
                }
                catch (IOException e) {
                    this.log(sm.getString("defaultServlet.readerCloseFailed"), e);
                }
                finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
                return buffer.toString();
            }
            if (this.debug > 10) {
                this.log("readme '" + this.readmeFile + "' not found");
            }
            return null;
        }
        return null;
    }

    protected Source findXsltSource(WebResource directory) throws IOException {
        File f;
        if (this.localXsltFile != null) {
            InputStream is;
            WebResource resource = this.resources.getResource(directory.getWebappPath() + this.localXsltFile);
            if (resource.isFile() && (is = resource.getInputStream()) != null) {
                if (Globals.IS_SECURITY_ENABLED) {
                    return this.secureXslt(is);
                }
                return new StreamSource(is);
            }
            if (this.debug > 10) {
                this.log("localXsltFile '" + this.localXsltFile + "' not found");
            }
        }
        if (this.contextXsltFile != null) {
            InputStream is = this.getServletContext().getResourceAsStream(this.contextXsltFile);
            if (is != null) {
                if (Globals.IS_SECURITY_ENABLED) {
                    return this.secureXslt(is);
                }
                return new StreamSource(is);
            }
            if (this.debug > 10) {
                this.log("contextXsltFile '" + this.contextXsltFile + "' not found");
            }
        }
        if (this.globalXsltFile != null && (f = this.validateGlobalXsltFile()) != null) {
            long globalXsltFileSize = f.length();
            if (globalXsltFileSize > Integer.MAX_VALUE) {
                this.log("globalXsltFile [" + f.getAbsolutePath() + "] is too big to buffer");
            } else {
                try (FileInputStream fis = new FileInputStream(f);){
                    byte[] b = new byte[(int)f.length()];
                    IOTools.readFully(fis, b);
                    StreamSource streamSource = new StreamSource(new ByteArrayInputStream(b));
                    return streamSource;
                }
            }
        }
        return null;
    }

    private File validateGlobalXsltFile() {
        File homeConf;
        Context context = this.resources.getContext();
        File baseConf = new File(context.getCatalinaBase(), "conf");
        File result = this.validateGlobalXsltFile(baseConf);
        if (result == null && !baseConf.equals(homeConf = new File(context.getCatalinaHome(), "conf"))) {
            result = this.validateGlobalXsltFile(homeConf);
        }
        return result;
    }

    private File validateGlobalXsltFile(File base) {
        File candidate = new File(this.globalXsltFile);
        if (!candidate.isAbsolute()) {
            candidate = new File(base, this.globalXsltFile);
        }
        if (!candidate.isFile()) {
            return null;
        }
        try {
            if (!candidate.getCanonicalFile().toPath().startsWith(base.getCanonicalFile().toPath())) {
                return null;
            }
        }
        catch (IOException ioe) {
            return null;
        }
        String nameLower = candidate.getName().toLowerCase(Locale.ENGLISH);
        if (!nameLower.endsWith(".xslt") && !nameLower.endsWith(".xsl")) {
            return null;
        }
        return candidate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Source secureXslt(InputStream is) {
        DOMSource result = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(secureEntityResolver);
            Document document = builder.parse(is);
            result = new DOMSource(document);
        }
        catch (IOException | ParserConfigurationException | SAXException e) {
            if (this.debug > 0) {
                this.log(e.getMessage(), e);
            }
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException iOException) {}
            }
        }
        return result;
    }

    protected boolean checkSendfile(HttpServletRequest request, HttpServletResponse response, WebResource resource, long length, Range range) {
        String canonicalPath;
        if (this.sendfileSize > 0 && length > (long)this.sendfileSize && Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.sendfile.support")) && request.getClass().getName().equals("org.apache.catalina.connector.RequestFacade") && response.getClass().getName().equals("org.apache.catalina.connector.ResponseFacade") && resource.isFile() && (canonicalPath = resource.getCanonicalPath()) != null) {
            request.setAttribute("org.apache.tomcat.sendfile.filename", (Object)canonicalPath);
            if (range == null) {
                request.setAttribute("org.apache.tomcat.sendfile.start", (Object)0L);
                request.setAttribute("org.apache.tomcat.sendfile.end", (Object)length);
            } else {
                request.setAttribute("org.apache.tomcat.sendfile.start", (Object)range.start);
                request.setAttribute("org.apache.tomcat.sendfile.end", (Object)(range.end + 1L));
            }
            return true;
        }
        return false;
    }

    protected boolean checkIfMatch(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        String headerValue = request.getHeader("If-Match");
        if (headerValue != null) {
            boolean conditionSatisfied;
            if (!headerValue.equals("*")) {
                String resourceETag = this.generateETag(resource);
                if (resourceETag == null) {
                    conditionSatisfied = false;
                } else {
                    Boolean matched = EntityTag.compareEntityTag((StringReader)new StringReader(headerValue), (boolean)false, (String)resourceETag);
                    if (matched == null) {
                        if (this.debug > 10) {
                            this.log("DefaultServlet.checkIfMatch:  Invalid header value [" + headerValue + "]");
                        }
                        response.sendError(400);
                        return false;
                    }
                    conditionSatisfied = matched;
                }
            } else {
                conditionSatisfied = true;
            }
            if (!conditionSatisfied) {
                response.sendError(412);
                return false;
            }
        }
        return true;
    }

    protected boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response, WebResource resource) {
        try {
            long headerValue = request.getDateHeader("If-Modified-Since");
            long lastModified = resource.getLastModified();
            if (headerValue != -1L && request.getHeader("If-None-Match") == null && lastModified < headerValue + 1000L) {
                response.setStatus(304);
                response.setHeader("ETag", this.generateETag(resource));
                return false;
            }
        }
        catch (IllegalArgumentException illegalArgument) {
            return true;
        }
        return true;
    }

    protected boolean checkIfNoneMatch(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        String headerValue = request.getHeader("If-None-Match");
        if (headerValue != null) {
            boolean conditionSatisfied;
            String resourceETag = this.generateETag(resource);
            if (!headerValue.equals("*")) {
                if (resourceETag == null) {
                    conditionSatisfied = false;
                } else {
                    Boolean matched = EntityTag.compareEntityTag((StringReader)new StringReader(headerValue), (boolean)true, (String)resourceETag);
                    if (matched == null) {
                        if (this.debug > 10) {
                            this.log("DefaultServlet.checkIfNoneMatch:  Invalid header value [" + headerValue + "]");
                        }
                        response.sendError(400);
                        return false;
                    }
                    conditionSatisfied = matched;
                }
            } else {
                conditionSatisfied = true;
            }
            if (conditionSatisfied) {
                if ("GET".equals(request.getMethod()) || "HEAD".equals(request.getMethod())) {
                    response.setStatus(304);
                    response.setHeader("ETag", resourceETag);
                } else {
                    response.sendError(412);
                }
                return false;
            }
        }
        return true;
    }

    protected boolean checkIfUnmodifiedSince(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        try {
            long lastModified = resource.getLastModified();
            long headerValue = request.getDateHeader("If-Unmodified-Since");
            if (headerValue != -1L && lastModified >= headerValue + 1000L) {
                response.sendError(412);
                return false;
            }
        }
        catch (IllegalArgumentException illegalArgument) {
            return true;
        }
        return true;
    }

    protected String generateETag(WebResource resource) {
        return resource.getETag();
    }

    protected void copy(InputStream is, ServletOutputStream ostream) throws IOException {
        IOException exception = null;
        BufferedInputStream istream = new BufferedInputStream(is, this.input);
        exception = this.copyRange(istream, ostream);
        ((InputStream)istream).close();
        if (exception != null) {
            throw exception;
        }
    }

    protected void copy(InputStream is, PrintWriter writer, String encoding) throws IOException {
        IOException exception = null;
        InputStreamReader reader = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
        exception = this.copyRange(reader, writer);
        ((Reader)reader).close();
        if (exception != null) {
            throw exception;
        }
    }

    protected void copy(WebResource resource, ServletOutputStream ostream, Range range) throws IOException {
        IOException exception = null;
        InputStream resourceInputStream = resource.getInputStream();
        BufferedInputStream istream = new BufferedInputStream(resourceInputStream, this.input);
        exception = this.copyRange(istream, ostream, range.start, range.end);
        ((InputStream)istream).close();
        if (exception != null) {
            throw exception;
        }
    }

    protected void copy(WebResource resource, ServletOutputStream ostream, Iterator<Range> ranges, String contentType) throws IOException {
        IOException exception = null;
        while (exception == null && ranges.hasNext()) {
            InputStream resourceInputStream = resource.getInputStream();
            try (BufferedInputStream istream = new BufferedInputStream(resourceInputStream, this.input);){
                Range currentRange = ranges.next();
                ostream.println();
                ostream.println("--CATALINA_MIME_BOUNDARY");
                if (contentType != null) {
                    ostream.println("Content-Type: " + contentType);
                }
                ostream.println("Content-Range: bytes " + currentRange.start + "-" + currentRange.end + "/" + currentRange.length);
                ostream.println();
                exception = this.copyRange(istream, ostream, currentRange.start, currentRange.end);
            }
        }
        ostream.println();
        ostream.print("--CATALINA_MIME_BOUNDARY--");
        if (exception != null) {
            throw exception;
        }
    }

    protected IOException copyRange(InputStream istream, ServletOutputStream ostream) {
        IOException exception = null;
        byte[] buffer = new byte[this.input];
        int len = buffer.length;
        try {
            while ((len = istream.read(buffer)) != -1) {
                ostream.write(buffer, 0, len);
            }
        }
        catch (IOException e) {
            exception = e;
            len = -1;
        }
        return exception;
    }

    protected IOException copyRange(Reader reader, PrintWriter writer) {
        IOException exception = null;
        char[] buffer = new char[this.input];
        int len = buffer.length;
        try {
            while ((len = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, len);
            }
        }
        catch (IOException e) {
            exception = e;
            len = -1;
        }
        return exception;
    }

    protected IOException copyRange(InputStream istream, ServletOutputStream ostream, long start, long end) {
        if (this.debug > 10) {
            this.log("Serving bytes:" + start + "-" + end);
        }
        long skipped = 0L;
        try {
            skipped = istream.skip(start);
        }
        catch (IOException e) {
            return e;
        }
        if (skipped < start) {
            return new IOException(sm.getString("defaultServlet.skipfail", new Object[]{skipped, start}));
        }
        IOException exception = null;
        long bytesToRead = end - start + 1L;
        byte[] buffer = new byte[this.input];
        int len = buffer.length;
        while (bytesToRead > 0L && len >= buffer.length) {
            try {
                len = istream.read(buffer);
                if (bytesToRead >= (long)len) {
                    ostream.write(buffer, 0, len);
                    bytesToRead -= (long)len;
                } else {
                    ostream.write(buffer, 0, (int)bytesToRead);
                    bytesToRead = 0L;
                }
            }
            catch (IOException e) {
                exception = e;
                len = -1;
            }
            if (len >= buffer.length) continue;
            break;
        }
        return exception;
    }

    private char getOrderChar(SortManager.Order order, char column) {
        if (column == order.column) {
            if (order.ascending) {
                return 'D';
            }
            return 'A';
        }
        return 'D';
    }

    private static Comparator<WebResource> comparingTrueFirst(Function<WebResource, Boolean> keyExtractor) {
        return (s1, s2) -> {
            Boolean r1 = (Boolean)keyExtractor.apply((WebResource)s1);
            Boolean r2 = (Boolean)keyExtractor.apply((WebResource)s2);
            if (r1.booleanValue()) {
                if (r2.booleanValue()) {
                    return 0;
                }
                return -1;
            }
            if (r2.booleanValue()) {
                return 1;
            }
            return 0;
        };
    }

    static {
        FULL = new ArrayList();
        IGNORE = new Range();
        if (Globals.IS_SECURITY_ENABLED) {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            secureEntityResolver = new SecureEntityResolver();
        } else {
            factory = null;
            secureEntityResolver = null;
        }
    }

    static enum BomConfig {
        TRUE("true", true, true),
        FALSE("false", true, false),
        PASS_THROUGH("pass-through", false, false);

        final String configurationValue;
        final boolean stripBom;
        final boolean useBomEncoding;

        private BomConfig(String configurationValue, boolean stripBom, boolean useBomEncoding) {
            this.configurationValue = configurationValue;
            this.stripBom = stripBom;
            this.useBomEncoding = useBomEncoding;
        }
    }

    protected static class CompressionFormat
    implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String extension;
        public final String encoding;

        public CompressionFormat(String extension, String encoding) {
            this.extension = extension;
            this.encoding = encoding;
        }
    }

    private static class SortManager {
        protected Comparator<WebResource> defaultResourceComparator;
        protected Comparator<WebResource> resourceNameComparator = Comparator.comparing(WebResource::getName);
        protected Comparator<WebResource> resourceNameComparatorAsc = this.resourceNameComparator.reversed();
        protected Comparator<WebResource> resourceSizeComparator = Comparator.comparing(WebResource::getContentLength).thenComparing(this.resourceNameComparator);
        protected Comparator<WebResource> resourceSizeComparatorAsc = this.resourceSizeComparator.reversed();
        protected Comparator<WebResource> resourceLastModifiedComparator = Comparator.comparing(WebResource::getLastModified).thenComparing(this.resourceNameComparator);
        protected Comparator<WebResource> resourceLastModifiedComparatorAsc = this.resourceLastModifiedComparator.reversed();

        SortManager(boolean directoriesFirst) {
            if (directoriesFirst) {
                Comparator dirsFirst = DefaultServlet.comparingTrueFirst(WebResource::isDirectory);
                this.resourceNameComparator = dirsFirst.thenComparing(this.resourceNameComparator);
                this.resourceNameComparatorAsc = dirsFirst.thenComparing(this.resourceNameComparatorAsc);
                this.resourceSizeComparator = dirsFirst.thenComparing(this.resourceSizeComparator);
                this.resourceSizeComparatorAsc = dirsFirst.thenComparing(this.resourceSizeComparatorAsc);
                this.resourceLastModifiedComparator = dirsFirst.thenComparing(this.resourceLastModifiedComparator);
                this.resourceLastModifiedComparatorAsc = dirsFirst.thenComparing(this.resourceLastModifiedComparatorAsc);
            }
            this.defaultResourceComparator = this.resourceNameComparator;
        }

        public void sort(WebResource[] resources, String order) {
            Comparator<WebResource> comparator = this.getComparator(order);
            if (null != comparator) {
                Arrays.sort(resources, comparator);
            }
        }

        public Comparator<WebResource> getComparator(String order) {
            return this.getComparator(this.getOrder(order));
        }

        public Comparator<WebResource> getComparator(Order order) {
            if (null == order) {
                return this.defaultResourceComparator;
            }
            if ('N' == order.column) {
                if (order.ascending) {
                    return this.resourceNameComparatorAsc;
                }
                return this.resourceNameComparator;
            }
            if ('S' == order.column) {
                if (order.ascending) {
                    return this.resourceSizeComparatorAsc;
                }
                return this.resourceSizeComparator;
            }
            if ('M' == order.column) {
                if (order.ascending) {
                    return this.resourceLastModifiedComparatorAsc;
                }
                return this.resourceLastModifiedComparator;
            }
            return this.defaultResourceComparator;
        }

        public Order getOrder(String order) {
            if (null == order || 0 == order.trim().length()) {
                return Order.DEFAULT;
            }
            String[] options = order.split(";");
            if (0 == options.length) {
                return Order.DEFAULT;
            }
            int column = 0;
            boolean ascending = false;
            for (String option : options) {
                if (2 >= (option = option.trim()).length()) continue;
                char opt = option.charAt(0);
                if ('C' == opt) {
                    column = option.charAt(2);
                    continue;
                }
                if ('O' != opt) continue;
                ascending = 'A' == option.charAt(2);
            }
            if (78 == column) {
                if (ascending) {
                    return Order.NAME_ASC;
                }
                return Order.NAME;
            }
            if (83 == column) {
                if (ascending) {
                    return Order.SIZE_ASC;
                }
                return Order.SIZE;
            }
            if (77 == column) {
                if (ascending) {
                    return Order.LAST_MODIFIED_ASC;
                }
                return Order.LAST_MODIFIED;
            }
            return Order.DEFAULT;
        }

        public static class Order {
            final char column;
            final boolean ascending;
            public static final Order NAME = new Order('N', false);
            public static final Order NAME_ASC = new Order('N', true);
            public static final Order SIZE = new Order('S', false);
            public static final Order SIZE_ASC = new Order('S', true);
            public static final Order LAST_MODIFIED = new Order('M', false);
            public static final Order LAST_MODIFIED_ASC = new Order('M', true);
            public static final Order DEFAULT = NAME;

            Order(char column, boolean ascending) {
                this.column = column;
                this.ascending = ascending;
            }
        }
    }

    protected static class Range {
        public long start;
        public long end;
        public long length;

        protected Range() {
        }

        public boolean validate() {
            if (this.end >= this.length) {
                this.end = this.length - 1L;
            }
            return this.start >= 0L && this.end >= 0L && this.start <= this.end && this.length > 0L;
        }
    }

    private static class PrecompressedResource {
        public final WebResource resource;
        public final CompressionFormat format;

        private PrecompressedResource(WebResource resource, CompressionFormat format) {
            this.resource = resource;
            this.format = format;
        }
    }

    private static class SecureEntityResolver
    implements EntityResolver2 {
        private SecureEntityResolver() {
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            throw new SAXException(sm.getString("defaultServlet.blockExternalEntity", new Object[]{publicId, systemId}));
        }

        @Override
        public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
            throw new SAXException(sm.getString("defaultServlet.blockExternalSubset", new Object[]{name, baseURI}));
        }

        @Override
        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
            throw new SAXException(sm.getString("defaultServlet.blockExternalEntity2", new Object[]{name, publicId, baseURI, systemId}));
        }
    }
}

