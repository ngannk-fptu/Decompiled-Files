/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import java.io.Closeable;
import java.io.IOException;
import zipkin2.CheckResult;

public abstract class Component
implements Closeable {
    public CheckResult check() {
        return CheckResult.OK;
    }

    @Override
    public void close() throws IOException {
    }
}

