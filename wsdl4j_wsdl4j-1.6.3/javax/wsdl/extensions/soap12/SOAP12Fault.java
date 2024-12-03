/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap12;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAP12Fault
extends ExtensibilityElement,
Serializable {
    public void setName(String var1);

    public String getName();

    public void setUse(String var1);

    public String getUse();

    public void setEncodingStyle(String var1);

    public String getEncodingStyle();

    public void setNamespaceURI(String var1);

    public String getNamespaceURI();
}

