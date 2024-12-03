/*
 * Decompiled with CFR 0.152.
 */
package brave;

public interface SpanCustomizer {
    public SpanCustomizer name(String var1);

    public SpanCustomizer tag(String var1, String var2);

    public SpanCustomizer annotate(String var1);
}

