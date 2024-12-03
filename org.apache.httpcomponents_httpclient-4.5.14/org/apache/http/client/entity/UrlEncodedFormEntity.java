/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.NameValuePair
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.protocol.HTTP
 */
package org.apache.http.client.entity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

public class UrlEncodedFormEntity
extends StringEntity {
    public UrlEncodedFormEntity(List<? extends NameValuePair> parameters, String charset) throws UnsupportedEncodingException {
        super(URLEncodedUtils.format(parameters, charset != null ? charset : HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create((String)"application/x-www-form-urlencoded", (String)charset));
    }

    public UrlEncodedFormEntity(Iterable<? extends NameValuePair> parameters, Charset charset) {
        super(URLEncodedUtils.format(parameters, charset != null ? charset : HTTP.DEF_CONTENT_CHARSET), ContentType.create((String)"application/x-www-form-urlencoded", (Charset)charset));
    }

    public UrlEncodedFormEntity(List<? extends NameValuePair> parameters) throws UnsupportedEncodingException {
        this(parameters, (Charset)null);
    }

    public UrlEncodedFormEntity(Iterable<? extends NameValuePair> parameters) {
        this(parameters, null);
    }
}

