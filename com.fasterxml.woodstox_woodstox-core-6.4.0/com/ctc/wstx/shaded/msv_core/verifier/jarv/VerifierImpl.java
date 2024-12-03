/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import com.ctc.wstx.shaded.msv_core.verifier.IVerifier;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;

class VerifierImpl
extends com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl.VerifierImpl {
    private final IVerifier verifier;

    VerifierImpl(IVerifier verifier, XMLReader reader) throws VerifierConfigurationException {
        this.verifier = verifier;
        this.reader = reader;
    }

    protected void prepareXMLReader() {
    }

    public void setErrorHandler(ErrorHandler handler) {
        super.setErrorHandler(handler);
        this.verifier.setErrorHandler(handler);
    }

    public VerifierHandler getVerifierHandler() {
        return this.verifier;
    }
}

