/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import org.xml.sax.XMLFilter;

public interface VerifierFilter
extends XMLFilter {
    public boolean isValid() throws IllegalStateException;
}

