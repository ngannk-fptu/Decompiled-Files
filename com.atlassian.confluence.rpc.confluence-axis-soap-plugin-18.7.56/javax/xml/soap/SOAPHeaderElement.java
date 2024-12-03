/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.SOAPElement;

public interface SOAPHeaderElement
extends SOAPElement {
    public void setActor(String var1);

    public String getActor();

    public void setMustUnderstand(boolean var1);

    public boolean getMustUnderstand();
}

