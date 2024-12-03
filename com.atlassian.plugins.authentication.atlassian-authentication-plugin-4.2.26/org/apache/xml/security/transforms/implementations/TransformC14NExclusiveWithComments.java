/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusive;

public class TransformC14NExclusiveWithComments
extends TransformC14NExclusive {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    }

    @Override
    protected Canonicalizer20010315Excl getCanonicalizer() {
        return new Canonicalizer20010315ExclWithComments();
    }
}

