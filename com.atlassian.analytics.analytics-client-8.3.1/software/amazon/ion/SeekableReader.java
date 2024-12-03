/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.Span;
import software.amazon.ion.SpanProvider;

public interface SeekableReader
extends SpanProvider {
    public void hoist(Span var1);
}

