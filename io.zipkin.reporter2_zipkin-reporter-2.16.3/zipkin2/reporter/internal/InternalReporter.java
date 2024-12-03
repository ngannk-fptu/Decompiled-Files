/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.reporter.internal;

import zipkin2.reporter.AsyncReporter;

public abstract class InternalReporter {
    public static InternalReporter instance;

    public abstract AsyncReporter.Builder toBuilder(AsyncReporter<?> var1);
}

