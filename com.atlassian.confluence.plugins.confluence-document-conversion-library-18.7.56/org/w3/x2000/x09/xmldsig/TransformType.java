/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface TransformType
extends XmlObject {
    public static final DocumentFactory<TransformType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "transformtype550btype");
    public static final SchemaType type = Factory.getType();

    public List<String> getXPathList();

    public String[] getXPathArray();

    public String getXPathArray(int var1);

    public List<XmlString> xgetXPathList();

    public XmlString[] xgetXPathArray();

    public XmlString xgetXPathArray(int var1);

    public int sizeOfXPathArray();

    public void setXPathArray(String[] var1);

    public void setXPathArray(int var1, String var2);

    public void xsetXPathArray(XmlString[] var1);

    public void xsetXPathArray(int var1, XmlString var2);

    public void insertXPath(int var1, String var2);

    public void addXPath(String var1);

    public XmlString insertNewXPath(int var1);

    public XmlString addNewXPath();

    public void removeXPath(int var1);

    public String getAlgorithm();

    public XmlAnyURI xgetAlgorithm();

    public void setAlgorithm(String var1);

    public void xsetAlgorithm(XmlAnyURI var1);
}

