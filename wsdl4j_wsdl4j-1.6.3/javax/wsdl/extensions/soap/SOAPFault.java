/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap;

import java.io.Serializable;
import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAPFault
extends ExtensibilityElement,
Serializable {
    public void setName(String var1);

    public String getName();

    public void setUse(String var1);

    public String getUse();

    public void setEncodingStyles(List var1);

    public List getEncodingStyles();

    public void setNamespaceURI(String var1);

    public String getNamespaceURI();
}

