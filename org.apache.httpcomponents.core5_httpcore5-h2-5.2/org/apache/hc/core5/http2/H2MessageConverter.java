/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpMessage
 */
package org.apache.hc.core5.http2;

import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpMessage;

public interface H2MessageConverter<T extends HttpMessage> {
    public T convert(List<Header> var1) throws HttpException;

    public List<Header> convert(T var1) throws HttpException;
}

