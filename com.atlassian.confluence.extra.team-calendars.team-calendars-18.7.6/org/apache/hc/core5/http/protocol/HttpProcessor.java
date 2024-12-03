/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.protocol;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface HttpProcessor
extends HttpRequestInterceptor,
HttpResponseInterceptor {
}

