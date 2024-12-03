/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import org.xml.sax.ContentHandler;

public interface VerifierHandler
extends ContentHandler {
    public boolean isValid() throws IllegalStateException;
}

