/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;

public interface OMText
extends OMNode {
    public String getText();

    public char[] getTextCharacters();

    public boolean isCharacters();

    public QName getTextAsQName();

    public OMNamespace getNamespace();

    public Object getDataHandler();

    public boolean isOptimized();

    public void setOptimize(boolean var1);

    public boolean isBinary();

    public void setBinary(boolean var1);

    public String getContentID();

    public void setContentID(String var1);
}

