/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.ParseException
 *  org.apache.hc.core5.http.io.entity.EntityUtils
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

@Contract(threading=ThreadingBehavior.STATELESS)
public class BasicHttpClientResponseHandler
extends AbstractHttpClientResponseHandler<String> {
    @Override
    public String handleEntity(HttpEntity entity) throws IOException {
        try {
            return EntityUtils.toString((HttpEntity)entity);
        }
        catch (ParseException ex) {
            throw new ClientProtocolException(ex);
        }
    }

    @Override
    public String handleResponse(ClassicHttpResponse response) throws IOException {
        return (String)super.handleResponse(response);
    }
}

