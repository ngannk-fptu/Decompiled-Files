/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import javax.xml.namespace.QName;

public interface ExtensibilityElement {
    public void setElementType(QName var1);

    public QName getElementType();

    public void setRequired(Boolean var1);

    public Boolean getRequired();
}

