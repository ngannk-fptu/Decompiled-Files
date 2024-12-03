/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import java.util.EventListener;
import org.eclipse.jetty.client.api.ContentProvider;

@Deprecated
public interface AsyncContentProvider
extends ContentProvider {
    public void setListener(Listener var1);

    public static interface Listener
    extends EventListener {
        public void onContent();
    }
}

