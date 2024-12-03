/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAPAddress
extends ExtensibilityElement,
Serializable {
    public void setLocationURI(String var1);

    public String getLocationURI();
}

