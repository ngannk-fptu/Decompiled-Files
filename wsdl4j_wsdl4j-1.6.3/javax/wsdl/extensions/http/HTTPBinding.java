/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.http;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface HTTPBinding
extends ExtensibilityElement,
Serializable {
    public void setVerb(String var1);

    public String getVerb();
}

