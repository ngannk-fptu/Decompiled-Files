/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;

public interface OMFactoryEx
extends OMFactory {
    public OMDocument createOMDocument(OMXMLParserWrapper var1);

    public OMElement createOMElement(String var1, OMContainer var2, OMXMLParserWrapper var3);

    public OMText createOMText(OMContainer var1, Object var2, boolean var3, boolean var4);

    public OMText createOMText(OMContainer var1, String var2, int var3, boolean var4);

    public OMComment createOMComment(OMContainer var1, String var2, boolean var3);

    public OMDocType createOMDocType(OMContainer var1, String var2, String var3, String var4, String var5, boolean var6);

    public OMProcessingInstruction createOMProcessingInstruction(OMContainer var1, String var2, String var3, boolean var4);

    public OMEntityReference createOMEntityReference(OMContainer var1, String var2, String var3, boolean var4);

    public OMNode importNode(OMNode var1);
}

