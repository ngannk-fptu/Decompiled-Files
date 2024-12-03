/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

public interface IVerifier
extends VerifierHandler {
    public boolean isValid();

    public Object getCurrentElementType();

    public Datatype[] getLastCharacterType();

    public void setPanicMode(boolean var1);

    public Locator getLocator();

    public ErrorHandler getErrorHandler();

    public void setErrorHandler(ErrorHandler var1);
}

