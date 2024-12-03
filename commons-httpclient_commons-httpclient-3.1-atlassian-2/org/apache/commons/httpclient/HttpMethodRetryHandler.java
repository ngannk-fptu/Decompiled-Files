/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import org.apache.commons.httpclient.HttpMethod;

public interface HttpMethodRetryHandler {
    public boolean retryMethod(HttpMethod var1, IOException var2, int var3);
}

