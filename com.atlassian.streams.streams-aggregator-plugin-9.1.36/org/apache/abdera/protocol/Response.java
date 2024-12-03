/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol;

import java.util.Date;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.Message;
import org.apache.abdera.util.EntityTag;

public interface Response
extends Message {
    public EntityTag getEntityTag();

    public ResponseType getType();

    public int getStatus();

    public String getStatusText();

    public Date getLastModified();

    public long getContentLength();

    public String getAllow();

    public IRI getLocation();

    public boolean isPrivate();

    public boolean isPublic();

    public boolean isMustRevalidate();

    public boolean isProxyRevalidate();

    public long getSMaxAge();

    public long getAge();

    public Date getExpires();

    public String[] getNoCacheHeaders();

    public String[] getPrivateHeaders();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ResponseType {
        SUCCESS,
        REDIRECTION,
        CLIENT_ERROR,
        SERVER_ERROR,
        UNKNOWN;


        public static ResponseType select(int status) {
            if (status >= 200 && status < 300) {
                return SUCCESS;
            }
            if (status >= 300 && status < 400) {
                return REDIRECTION;
            }
            if (status >= 400 && status < 500) {
                return CLIENT_ERROR;
            }
            if (status >= 500 && status < 600) {
                return SERVER_ERROR;
            }
            return UNKNOWN;
        }
    }
}

