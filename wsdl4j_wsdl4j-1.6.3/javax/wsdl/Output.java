/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import javax.wsdl.Message;
import javax.wsdl.WSDLElement;

public interface Output
extends WSDLElement {
    public void setName(String var1);

    public String getName();

    public void setMessage(Message var1);

    public Message getMessage();
}

