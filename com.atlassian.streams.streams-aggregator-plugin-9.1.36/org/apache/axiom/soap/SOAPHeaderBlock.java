/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;

public interface SOAPHeaderBlock
extends OMSourcedElement {
    public static final String ROLE_PROPERTY = "org.apache.axiom.soap.SOAPHeader.ROLE";
    public static final String RELAY_PROPERTY = "org.apache.axiom.soap.SOAPHeader.RELAY";
    public static final String MUST_UNDERSTAND_PROPERTY = "org.apache.axiom.soap.SOAPHeader.MUST_UNDERSTAND";

    public void setRole(String var1);

    public String getRole();

    public void setMustUnderstand(boolean var1);

    public void setMustUnderstand(String var1) throws SOAPProcessingException;

    public boolean getMustUnderstand() throws SOAPProcessingException;

    public boolean isProcessed();

    public void setProcessed();

    public void setRelay(boolean var1);

    public boolean getRelay();

    public SOAPVersion getVersion();
}

