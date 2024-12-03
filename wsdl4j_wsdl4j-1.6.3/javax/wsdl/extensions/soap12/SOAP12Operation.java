/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap12;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface SOAP12Operation
extends ExtensibilityElement,
Serializable {
    public void setSoapActionURI(String var1);

    public String getSoapActionURI();

    public void setSoapActionRequired(Boolean var1);

    public Boolean getSoapActionRequired();

    public void setStyle(String var1);

    public String getStyle();
}

