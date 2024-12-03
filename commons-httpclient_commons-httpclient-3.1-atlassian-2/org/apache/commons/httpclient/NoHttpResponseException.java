/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import org.apache.commons.httpclient.util.ExceptionUtil;

public class NoHttpResponseException
extends IOException {
    public NoHttpResponseException() {
    }

    public NoHttpResponseException(String message) {
        super(message);
    }

    public NoHttpResponseException(String message, Throwable cause) {
        super(message);
        ExceptionUtil.initCause(this, cause);
    }
}

