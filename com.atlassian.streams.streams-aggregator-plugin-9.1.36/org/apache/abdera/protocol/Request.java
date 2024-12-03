/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol;

import java.util.Date;
import org.apache.abdera.protocol.Message;
import org.apache.abdera.util.EntityTag;

public interface Request
extends Message {
    public String getAccept();

    public String getAcceptCharset();

    public String getAcceptEncoding();

    public String getAcceptLanguage();

    public String getAuthorization();

    public EntityTag[] getIfMatch();

    public Date getIfModifiedSince();

    public EntityTag[] getIfNoneMatch();

    public Date getIfUnmodifiedSince();

    public long getMaxStale();

    public long getMinFresh();

    public boolean isOnlyIfCached();
}

