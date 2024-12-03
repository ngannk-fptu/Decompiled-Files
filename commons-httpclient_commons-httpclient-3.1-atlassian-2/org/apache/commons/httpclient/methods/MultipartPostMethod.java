/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.ExpectContinueMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultipartPostMethod
extends ExpectContinueMethod {
    public static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";
    private static final Log LOG = LogFactory.getLog(MultipartPostMethod.class);
    private final List parameters = new ArrayList();

    public MultipartPostMethod() {
    }

    public MultipartPostMethod(String uri) {
        super(uri);
    }

    @Override
    protected boolean hasRequestContent() {
        return true;
    }

    @Override
    public String getName() {
        return "POST";
    }

    public void addParameter(String parameterName, String parameterValue) {
        LOG.trace((Object)"enter addParameter(String parameterName, String parameterValue)");
        StringPart param = new StringPart(parameterName, parameterValue);
        this.parameters.add(param);
    }

    public void addParameter(String parameterName, File parameterFile) throws FileNotFoundException {
        LOG.trace((Object)"enter MultipartPostMethod.addParameter(String parameterName, File parameterFile)");
        FilePart param = new FilePart(parameterName, parameterFile);
        this.parameters.add(param);
    }

    public void addParameter(String parameterName, String fileName, File parameterFile) throws FileNotFoundException {
        LOG.trace((Object)"enter MultipartPostMethod.addParameter(String parameterName, String fileName, File parameterFile)");
        FilePart param = new FilePart(parameterName, fileName, parameterFile);
        this.parameters.add(param);
    }

    public void addPart(Part part) {
        LOG.trace((Object)"enter addPart(Part part)");
        this.parameters.add(part);
    }

    public Part[] getParts() {
        return this.parameters.toArray(new Part[this.parameters.size()]);
    }

    protected void addContentLengthRequestHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter EntityEnclosingMethod.addContentLengthRequestHeader(HttpState, HttpConnection)");
        if (this.getRequestHeader("Content-Length") == null) {
            long len = this.getRequestContentLength();
            this.addRequestHeader("Content-Length", String.valueOf(len));
        }
        this.removeRequestHeader("Transfer-Encoding");
    }

    protected void addContentTypeRequestHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter EntityEnclosingMethod.addContentTypeRequestHeader(HttpState, HttpConnection)");
        if (!this.parameters.isEmpty()) {
            StringBuffer buffer = new StringBuffer(MULTIPART_FORM_CONTENT_TYPE);
            if (Part.getBoundary() != null) {
                buffer.append("; boundary=");
                buffer.append(Part.getBoundary());
            }
            this.setRequestHeader("Content-Type", buffer.toString());
        }
    }

    @Override
    protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter MultipartPostMethod.addRequestHeaders(HttpState state, HttpConnection conn)");
        super.addRequestHeaders(state, conn);
        this.addContentLengthRequestHeader(state, conn);
        this.addContentTypeRequestHeader(state, conn);
    }

    @Override
    protected boolean writeRequestBody(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter MultipartPostMethod.writeRequestBody(HttpState state, HttpConnection conn)");
        OutputStream out = conn.getRequestOutputStream();
        Part.sendParts(out, this.getParts());
        return true;
    }

    protected long getRequestContentLength() throws IOException {
        LOG.trace((Object)"enter MultipartPostMethod.getRequestContentLength()");
        return Part.getLengthOfParts(this.getParts());
    }

    @Override
    public void recycle() {
        LOG.trace((Object)"enter MultipartPostMethod.recycle()");
        super.recycle();
        this.parameters.clear();
    }
}

