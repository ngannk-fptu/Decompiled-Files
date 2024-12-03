/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.util;

import java.util.Date;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.util.AbstractMessage;
import org.apache.abdera.util.EntityTag;

public abstract class AbstractRequest
extends AbstractMessage
implements Request {
    protected long max_stale = -1L;
    protected long min_fresh = -1L;

    public String getAccept() {
        return this.getHeader("Accept");
    }

    public String getAcceptCharset() {
        return this.getHeader("Accept-Charset");
    }

    public String getAcceptEncoding() {
        return this.getHeader("Accept-Encoding");
    }

    public String getAcceptLanguage() {
        return this.getHeader("Accept-Language");
    }

    public String getAuthorization() {
        return this.getHeader("Authorization");
    }

    public EntityTag[] getIfMatch() {
        return EntityTag.parseTags(this.getHeader("If-Match"));
    }

    public Date getIfModifiedSince() {
        return this.getDateHeader("If-Modified-Since");
    }

    public EntityTag[] getIfNoneMatch() {
        return EntityTag.parseTags(this.getHeader("If-None-Match"));
    }

    public Date getIfUnmodifiedSince() {
        return this.getDateHeader("If-Unmodified-Since");
    }

    public long getMaxStale() {
        return this.max_stale;
    }

    public long getMinFresh() {
        return this.min_fresh;
    }

    public boolean isOnlyIfCached() {
        return this.check(128);
    }

    public AbstractRequest setMaxAge(long max_age) {
        this.max_age = max_age;
        return this;
    }

    public AbstractRequest setMaxStale(long max_stale) {
        this.max_stale = max_stale;
        return this;
    }

    public AbstractRequest setMinFresh(long min_fresh) {
        this.min_fresh = min_fresh;
        return this;
    }

    public AbstractRequest setNoCache(boolean val) {
        this.toggle(val, 1);
        return this;
    }

    public AbstractRequest setNoStore(boolean val) {
        this.toggle(val, 2);
        return this;
    }

    public AbstractRequest setNoTransform(boolean val) {
        this.toggle(val, 4);
        return this;
    }

    public AbstractRequest setOnlyIfCached(boolean val) {
        this.toggle(val, 128);
        return this;
    }
}

