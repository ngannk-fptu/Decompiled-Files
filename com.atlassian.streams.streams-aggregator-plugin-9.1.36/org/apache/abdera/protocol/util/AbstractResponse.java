/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.util;

import java.util.Date;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.util.AbstractMessage;
import org.apache.abdera.util.EntityTag;

public abstract class AbstractResponse
extends AbstractMessage
implements Response {
    protected String[] nocache_headers = null;
    protected String[] private_headers = null;
    protected long smax_age = -1L;

    public long getAge() {
        String value = this.getHeader("Age");
        try {
            return value != null ? Long.parseLong(value) : -1L;
        }
        catch (NumberFormatException e) {
            return -1L;
        }
    }

    public String getAllow() {
        return this.getHeader("Allow");
    }

    public long getContentLength() {
        String value = this.getHeader("Content-Length");
        try {
            return value != null ? Long.parseLong(value) : -1L;
        }
        catch (NumberFormatException e) {
            return -1L;
        }
    }

    public EntityTag getEntityTag() {
        String etag = this.getHeader("ETag");
        return etag != null ? EntityTag.parse(this.getHeader("ETag")) : null;
    }

    public Date getExpires() {
        return this.getDateHeader("Expires");
    }

    public Date getLastModified() {
        return this.getDateHeader("Last-Modified");
    }

    public IRI getLocation() {
        String l = this.getHeader("Location");
        return l != null ? new IRI(l) : null;
    }

    public String[] getNoCacheHeaders() {
        return this.nocache_headers;
    }

    public String[] getPrivateHeaders() {
        return this.private_headers;
    }

    public long getSMaxAge() {
        return this.smax_age;
    }

    public Response.ResponseType getType() {
        return Response.ResponseType.select(this.getStatus());
    }

    public boolean isMustRevalidate() {
        return this.check(32);
    }

    public boolean isPrivate() {
        return this.check(16);
    }

    public boolean isProxyRevalidate() {
        return this.check(64);
    }

    public boolean isPublic() {
        return this.check(8);
    }

    public AbstractResponse setMaxAge(long max_age) {
        this.max_age = max_age;
        return this;
    }

    public AbstractResponse setMustRevalidate(boolean val) {
        this.toggle(val, 32);
        return this;
    }

    public AbstractResponse setProxyRevalidate(boolean val) {
        this.toggle(val, 64);
        return this;
    }

    public AbstractResponse setNoCache(boolean val) {
        this.toggle(val, 1);
        return this;
    }

    public AbstractResponse setNoStore(boolean val) {
        this.toggle(val, 2);
        return this;
    }

    public AbstractResponse setNoTransform(boolean val) {
        this.toggle(val, 4);
        return this;
    }

    public AbstractResponse setPublic(boolean val) {
        this.toggle(val, 8);
        return this;
    }

    public AbstractResponse setPrivate(boolean val) {
        if (val) {
            this.flags |= 0x10;
        } else if (this.isPrivate()) {
            this.flags ^= 0x10;
        }
        return this;
    }

    public AbstractResponse setPrivateHeaders(String ... headers) {
        this.private_headers = headers;
        return this;
    }

    public AbstractResponse setNoCacheHeaders(String ... headers) {
        this.nocache_headers = headers;
        return this;
    }
}

