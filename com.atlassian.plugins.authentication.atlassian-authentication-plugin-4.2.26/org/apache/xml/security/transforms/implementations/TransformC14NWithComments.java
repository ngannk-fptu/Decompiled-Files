/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithComments;
import org.apache.xml.security.transforms.implementations.TransformC14N;

public class TransformC14NWithComments
extends TransformC14N {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    }

    @Override
    protected Canonicalizer20010315 getCanonicalizer() {
        return new Canonicalizer20010315WithComments();
    }
}

