/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAPBinding
extends ExtensibilityElement,
Serializable {
    public void setStyle(String var1);

    public String getStyle();

    public void setTransportURI(String var1);

    public String getTransportURI();
}

