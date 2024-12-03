/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.introspect.Annotated;

public class AnnotatedAndMetadata<A extends Annotated, M> {
    public final A annotated;
    public final M metadata;

    public AnnotatedAndMetadata(A ann, M md) {
        this.annotated = ann;
        this.metadata = md;
    }

    public static <A extends Annotated, M> AnnotatedAndMetadata<A, M> of(A ann, M md) {
        return new AnnotatedAndMetadata<A, M>(ann, md);
    }
}

