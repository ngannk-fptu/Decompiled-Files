/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl;

public class Canonicalizer20010315ExclWithComments
extends Canonicalizer20010315Excl {
    public Canonicalizer20010315ExclWithComments() {
        super(true);
    }

    @Override
    public final String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    }
}

