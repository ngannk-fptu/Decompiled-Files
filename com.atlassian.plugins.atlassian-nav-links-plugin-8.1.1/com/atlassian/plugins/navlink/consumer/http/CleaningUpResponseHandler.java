/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.util.EntityUtils
 */
package com.atlassian.plugins.navlink.consumer.http;

import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class CleaningUpResponseHandler<T>
implements ResponseHandler<T> {
    private final ResponseHandler<T> delegatee;

    public CleaningUpResponseHandler(@Nonnull ResponseHandler<T> delegatee) {
        this.delegatee = (ResponseHandler)Preconditions.checkNotNull(delegatee);
    }

    public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        try {
            Object object = this.delegatee.handleResponse(response);
            return (T)object;
        }
        finally {
            this.cleanUp(response);
        }
    }

    private void cleanUp(@Nullable HttpResponse response) throws IOException {
        if (response != null) {
            EntityUtils.consume((HttpEntity)response.getEntity());
        }
    }
}

