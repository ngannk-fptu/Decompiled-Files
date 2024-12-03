/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.protocol;

import java.util.Date;
import javax.activation.MimeType;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.util.ProtocolConstants;

public interface Message
extends ProtocolConstants {
    public String getHeader(String var1);

    public String getDecodedHeader(String var1);

    public Object[] getHeaders(String var1);

    public String[] getDecodedHeaders(String var1);

    public String[] getHeaderNames();

    public String getCacheControl();

    public String getSlug();

    public MimeType getContentType();

    public IRI getContentLocation();

    public String getContentLanguage();

    public Date getDateHeader(String var1);

    public long getMaxAge();

    public boolean isNoCache();

    public boolean isNoStore();

    public boolean isNoTransform();
}

