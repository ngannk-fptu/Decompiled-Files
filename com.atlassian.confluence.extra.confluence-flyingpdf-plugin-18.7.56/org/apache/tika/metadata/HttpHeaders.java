/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public interface HttpHeaders {
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_MD5 = "Content-MD5";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final Property LAST_MODIFIED = Property.internalDate("Last-Modified");
    public static final String LOCATION = "Location";
}

