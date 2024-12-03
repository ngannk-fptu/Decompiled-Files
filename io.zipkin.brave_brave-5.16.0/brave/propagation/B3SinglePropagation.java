/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.Span;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;

@Deprecated
public final class B3SinglePropagation {
    public static final Propagation.Factory FACTORY = B3Propagation.newFactoryBuilder().injectFormat(B3Propagation.Format.SINGLE).injectFormat(Span.Kind.CLIENT, B3Propagation.Format.SINGLE).injectFormat(Span.Kind.SERVER, B3Propagation.Format.SINGLE).build();
}

