/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;

public class Canonicalizer20010315OmitComments
extends Canonicalizer20010315 {
    public Canonicalizer20010315OmitComments() {
        super(false);
    }

    @Override
    public final String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    }
}

