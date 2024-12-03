/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface StreamingResponseBody {
    public void writeTo(OutputStream var1) throws IOException;
}

