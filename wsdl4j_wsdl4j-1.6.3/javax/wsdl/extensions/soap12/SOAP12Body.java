/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap12;

import java.io.Serializable;
import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAP12Body
extends ExtensibilityElement,
Serializable {
    public void setParts(List var1);

    public List getParts();

    public void setUse(String var1);

    public String getUse();

    public void setEncodingStyle(String var1);

    public String getEncodingStyle();

    public void setNamespaceURI(String var1);

    public String getNamespaceURI();
}

