/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.ChunkedOutputStream;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.ExpectContinueMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class EntityEnclosingMethod
extends ExpectContinueMethod {
    public static final long CONTENT_LENGTH_AUTO = -2L;
    public static final long CONTENT_LENGTH_CHUNKED = -1L;
    private static final Log LOG = LogFactory.getLog(EntityEnclosingMethod.class);
    private InputStream requestStream = null;
    private String requestString = null;
    private RequestEntity requestEntity;
    private int repeatCount = 0;
    private long requestContentLength = -2L;
    private boolean chunked = false;

    public EntityEnclosingMethod() {
        this.setFollowRedirects(false);
    }

    public EntityEnclosingMethod(String uri) {
        super(uri);
        this.setFollowRedirects(false);
    }

    @Override
    protected boolean hasRequestContent() {
        LOG.trace((Object)"enter EntityEnclosingMethod.hasRequestContent()");
        return this.requestEntity != null || this.requestStream != null || this.requestString != null;
    }

    protected void clearRequestBody() {
        LOG.trace((Object)"enter EntityEnclosingMethod.clearRequestBody()");
        this.requestStream = null;
        this.requestString = null;
        this.requestEntity = null;
    }

    protected byte[] generateRequestBody() {
        LOG.trace((Object)"enter EntityEnclosingMethod.renerateRequestBody()");
        return null;
    }

    protected RequestEntity generateRequestEntity() {
        byte[] requestBody = this.generateRequestBody();
        if (requestBody != null) {
            this.requestEntity = new ByteArrayRequestEntity(requestBody);
        } else if (this.requestStream != null) {
            this.requestEntity = new InputStreamRequestEntity(this.requestStream, this.requestContentLength);
            this.requestStream = null;
        } else if (this.requestString != null) {
            String charset = this.getRequestCharSet();
            try {
                this.requestEntity = new StringRequestEntity(this.requestString, null, charset);
            }
            catch (UnsupportedEncodingException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn((Object)(charset + " not supported"));
                }
                try {
                    this.requestEntity = new StringRequestEntity(this.requestString, null, null);
                }
                catch (UnsupportedEncodingException ignore) {
                    // empty catch block
                }
            }
        }
        return this.requestEntity;
    }

    @Override
    public boolean getFollowRedirects() {
        return false;
    }

    @Override
    public void setFollowRedirects(boolean followRedirects) {
        if (followRedirects) {
            throw new IllegalArgumentException("Entity enclosing requests cannot be redirected without user intervention");
        }
        super.setFollowRedirects(false);
    }

    public void setRequestContentLength(int length) {
        LOG.trace((Object)"enter EntityEnclosingMethod.setRequestContentLength(int)");
        this.requestContentLength = length;
    }

    @Override
    public String getRequestCharSet() {
        if (this.getRequestHeader("Content-Type") == null) {
            if (this.requestEntity != null) {
                return this.getContentCharSet(new Header("Content-Type", this.requestEntity.getContentType()));
            }
            return super.getRequestCharSet();
        }
        return super.getRequestCharSet();
    }

    public void setRequestContentLength(long length) {
        LOG.trace((Object)"enter EntityEnclosingMethod.setRequestContentLength(int)");
        this.requestContentLength = length;
    }

    public void setContentChunked(boolean chunked) {
        this.chunked = chunked;
    }

    protected long getRequestContentLength() {
        LOG.trace((Object)"enter EntityEnclosingMethod.getRequestContentLength()");
        if (!this.hasRequestContent()) {
            return 0L;
        }
        if (this.chunked) {
            return -1L;
        }
        if (this.requestEntity == null) {
            this.requestEntity = this.generateRequestEntity();
        }
        return this.requestEntity == null ? 0L : this.requestEntity.getContentLength();
    }

    @Override
    protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
        RequestEntity requestEntity;
        LOG.trace((Object)"enter EntityEnclosingMethod.addRequestHeaders(HttpState, HttpConnection)");
        super.addRequestHeaders(state, conn);
        this.addContentLengthRequestHeader(state, conn);
        if (this.getRequestHeader("Content-Type") == null && (requestEntity = this.getRequestEntity()) != null && requestEntity.getContentType() != null) {
            this.setRequestHeader("Content-Type", requestEntity.getContentType());
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void addContentLengthRequestHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter EntityEnclosingMethod.addContentLengthRequestHeader(HttpState, HttpConnection)");
        if (this.getRequestHeader("content-length") != null || this.getRequestHeader("Transfer-Encoding") != null) return;
        long len = this.getRequestContentLength();
        if (len < 0L) {
            if (!this.getEffectiveVersion().greaterEquals(HttpVersion.HTTP_1_1)) throw new ProtocolException(this.getEffectiveVersion() + " does not support chunk encoding");
            this.addRequestHeader("Transfer-Encoding", "chunked");
            return;
        } else {
            this.addRequestHeader("Content-Length", String.valueOf(len));
        }
    }

    public void setRequestBody(InputStream body) {
        LOG.trace((Object)"enter EntityEnclosingMethod.setRequestBody(InputStream)");
        this.clearRequestBody();
        this.requestStream = body;
    }

    public void setRequestBody(String body) {
        LOG.trace((Object)"enter EntityEnclosingMethod.setRequestBody(String)");
        this.clearRequestBody();
        this.requestString = body;
    }

    @Override
    protected boolean writeRequestBody(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter EntityEnclosingMethod.writeRequestBody(HttpState, HttpConnection)");
        if (!this.hasRequestContent()) {
            LOG.debug((Object)"Request body has not been specified");
            return true;
        }
        if (this.requestEntity == null) {
            this.requestEntity = this.generateRequestEntity();
        }
        if (this.requestEntity == null) {
            LOG.debug((Object)"Request body is empty");
            return true;
        }
        long contentLength = this.getRequestContentLength();
        if (this.repeatCount > 0 && !this.requestEntity.isRepeatable()) {
            throw new ProtocolException("Unbuffered entity enclosing request can not be repeated.");
        }
        ++this.repeatCount;
        OutputStream outstream = conn.getRequestOutputStream();
        if (contentLength < 0L) {
            outstream = new ChunkedOutputStream(outstream);
        }
        this.requestEntity.writeRequest(outstream);
        if (outstream instanceof ChunkedOutputStream) {
            ((ChunkedOutputStream)outstream).finish();
        }
        outstream.flush();
        LOG.debug((Object)"Request body sent");
        return true;
    }

    @Override
    public void recycle() {
        LOG.trace((Object)"enter EntityEnclosingMethod.recycle()");
        this.clearRequestBody();
        this.requestContentLength = -2L;
        this.repeatCount = 0;
        this.chunked = false;
        super.recycle();
    }

    public RequestEntity getRequestEntity() {
        return this.generateRequestEntity();
    }

    public void setRequestEntity(RequestEntity requestEntity) {
        this.clearRequestBody();
        this.requestEntity = requestEntity;
    }
}

