/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public interface SOAPHeaderElement
extends SOAPElement {
    public void setActor(String var1);

    public void setRole(String var1) throws SOAPException;

    public String getActor();

    public String getRole();

    public void setMustUnderstand(boolean var1);

    public boolean getMustUnderstand();

    public void setRelay(boolean var1) throws SOAPException;

    public boolean getRelay();
}

