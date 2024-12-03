/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.mime;

import java.io.Serializable;
import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.mime.MIMEPart;

public interface MIMEMultipartRelated
extends ExtensibilityElement,
Serializable {
    public void addMIMEPart(MIMEPart var1);

    public MIMEPart removeMIMEPart(MIMEPart var1);

    public List getMIMEParts();
}

