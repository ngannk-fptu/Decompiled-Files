/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap12;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAP12Address
extends ExtensibilityElement,
Serializable {
    public void setLocationURI(String var1);

    public String getLocationURI();
}

