/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAPOperation
extends ExtensibilityElement,
Serializable {
    public void setSoapActionURI(String var1);

    public String getSoapActionURI();

    public void setStyle(String var1);

    public String getStyle();
}

