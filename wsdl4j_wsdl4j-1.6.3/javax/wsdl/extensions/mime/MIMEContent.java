/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.mime;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;

public interface MIMEContent
extends ExtensibilityElement,
Serializable {
    public void setPart(String var1);

    public String getPart();

    public void setType(String var1);

    public String getType();
}

