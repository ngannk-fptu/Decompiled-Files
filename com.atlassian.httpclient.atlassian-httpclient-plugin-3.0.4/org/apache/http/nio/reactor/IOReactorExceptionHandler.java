/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.reactor;

import java.io.IOException;

public interface IOReactorExceptionHandler {
    public boolean handle(IOException var1);

    public boolean handle(RuntimeException var1);
}

