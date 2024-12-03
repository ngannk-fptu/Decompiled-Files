/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import javax.xml.namespace.QName;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;

public interface OMFactory {
    public OMMetaFactory getMetaFactory();

    public OMDocument createOMDocument();

    public OMElement createOMElement(String var1, OMNamespace var2);

    public OMElement createOMElement(String var1, OMNamespace var2, OMContainer var3) throws OMException;

    public OMSourcedElement createOMElement(OMDataSource var1);

    public OMSourcedElement createOMElement(OMDataSource var1, String var2, OMNamespace var3);

    public OMSourcedElement createOMElement(OMDataSource var1, QName var2);

    public OMElement createOMElement(String var1, String var2, String var3);

    public OMElement createOMElement(QName var1, OMContainer var2);

    public OMElement createOMElement(QName var1);

    public OMNamespace createOMNamespace(String var1, String var2);

    public OMText createOMText(OMContainer var1, String var2);

    public OMText createOMText(OMContainer var1, OMText var2);

    public OMText createOMText(OMContainer var1, QName var2);

    public OMText createOMText(OMContainer var1, String var2, int var3);

    public OMText createOMText(OMContainer var1, char[] var2, int var3);

    public OMText createOMText(OMContainer var1, QName var2, int var3);

    public OMText createOMText(String var1);

    public OMText createOMText(String var1, int var2);

    public OMText createOMText(String var1, String var2, boolean var3);

    public OMText createOMText(Object var1, boolean var2);

    public OMText createOMText(OMContainer var1, String var2, String var3, boolean var4);

    public OMText createOMText(String var1, DataHandlerProvider var2, boolean var3);

    public OMAttribute createOMAttribute(String var1, OMNamespace var2, String var3);

    public OMDocType createOMDocType(OMContainer var1, String var2, String var3, String var4, String var5);

    public OMProcessingInstruction createOMProcessingInstruction(OMContainer var1, String var2, String var3);

    public OMComment createOMComment(OMContainer var1, String var2);

    public OMEntityReference createOMEntityReference(OMContainer var1, String var2);
}

