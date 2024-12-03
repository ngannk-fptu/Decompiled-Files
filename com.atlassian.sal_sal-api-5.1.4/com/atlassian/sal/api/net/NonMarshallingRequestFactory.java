/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;

public interface NonMarshallingRequestFactory<T extends Request<?, ?>>
extends RequestFactory<T> {
}

