/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.protocol.util;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Rfc2047Helper;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.Message;

public abstract class AbstractMessage
implements Message {
    protected int flags = 0;
    protected long max_age = -1L;

    public String getCacheControl() {
        return this.getHeader("Cache-Control");
    }

    public String getContentLanguage() {
        return this.getHeader("Content-Language");
    }

    public IRI getContentLocation() {
        String value = this.getHeader("Content-Location");
        return value != null ? new IRI(value) : null;
    }

    public MimeType getContentType() {
        try {
            String value = this.getHeader("Content-Type");
            return value != null ? new MimeType(value) : null;
        }
        catch (MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public String getDecodedHeader(String header) {
        return UrlEncoding.decode(Rfc2047Helper.decode(this.getHeader(header)));
    }

    public String[] getDecodedHeaders(String header) {
        Object[] headers = this.getHeaders(header);
        for (int n = 0; n < headers.length; ++n) {
            headers[n] = UrlEncoding.decode(Rfc2047Helper.decode(headers[n].toString()));
        }
        return (String[])headers;
    }

    public String getSlug() {
        return this.getDecodedHeader("Slug");
    }

    protected boolean check(int flag) {
        return (this.flags & flag) == flag;
    }

    protected void toggle(boolean val, int flag) {
        this.flags = val ? (this.flags |= flag) : (this.flags &= ~flag);
    }

    public boolean isNoCache() {
        if (this.check(1)) {
            return true;
        }
        Object[] pragma = this.getHeaders("Pragma");
        if (pragma != null) {
            for (Object o : pragma) {
                String s = (String)o;
                if (!s.equalsIgnoreCase("no-cache")) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isNoStore() {
        return this.check(2);
    }

    public boolean isNoTransform() {
        return this.check(4);
    }

    public long getMaxAge() {
        return this.max_age;
    }
}

