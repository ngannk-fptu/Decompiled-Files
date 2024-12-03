/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.net.URL;

public interface AsyncCallback {
    public void handleResult(Object var1, URL var2, String var3);

    public void handleError(Exception var1, URL var2, String var3);
}

