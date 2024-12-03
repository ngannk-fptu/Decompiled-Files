/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;

public class Canonicalizer11_OmitComments
extends Canonicalizer20010315 {
    public Canonicalizer11_OmitComments() {
        super(false, true);
    }

    @Override
    public final String engineGetURI() {
        return "http://www.w3.org/2006/12/xml-c14n11";
    }
}

