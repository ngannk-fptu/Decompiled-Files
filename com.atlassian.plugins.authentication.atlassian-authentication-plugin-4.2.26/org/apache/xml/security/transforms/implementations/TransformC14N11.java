/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.transforms.implementations.TransformC14N;

public class TransformC14N11
extends TransformC14N {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2006/12/xml-c14n11";
    }

    @Override
    protected Canonicalizer20010315 getCanonicalizer() {
        return new Canonicalizer11_OmitComments();
    }
}

