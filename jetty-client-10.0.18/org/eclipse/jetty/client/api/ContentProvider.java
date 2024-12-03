/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.api;

import java.nio.ByteBuffer;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.internal.RequestContentAdapter;

@Deprecated
public interface ContentProvider
extends Iterable<ByteBuffer> {
    public static Request.Content toRequestContent(ContentProvider provider) {
        return new RequestContentAdapter(provider);
    }

    public long getLength();

    default public boolean isReproducible() {
        return false;
    }

    @Deprecated
    public static interface Typed
    extends ContentProvider {
        public String getContentType();
    }
}

