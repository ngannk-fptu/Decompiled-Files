/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer11_WithComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.transforms.implementations.TransformC14N;

public class TransformC14N11_WithComments
extends TransformC14N {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    }

    @Override
    protected Canonicalizer20010315 getCanonicalizer() {
        return new Canonicalizer11_WithComments();
    }
}

