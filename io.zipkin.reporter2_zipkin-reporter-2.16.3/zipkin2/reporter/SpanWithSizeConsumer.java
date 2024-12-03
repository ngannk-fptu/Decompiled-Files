/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.reporter;

interface SpanWithSizeConsumer<S> {
    public boolean offer(S var1, int var2);
}

