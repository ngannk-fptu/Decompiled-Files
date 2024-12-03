/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;

public interface OMNamedInformationItem
extends OMInformationItem {
    public String getLocalName();

    public void setLocalName(String var1);

    public OMNamespace getNamespace();

    public QName getQName();

    public String getPrefix();

    public String getNamespaceURI();
}

