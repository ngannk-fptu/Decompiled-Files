/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlObject
extends XmlTokenSource {
    public static final XmlObjectFactory<XmlObject> Factory = new XmlObjectFactory("_BI_anyType");
    public static final SchemaType type = Factory.getType();
    public static final int LESS_THAN = -1;
    public static final int EQUAL = 0;
    public static final int GREATER_THAN = 1;
    public static final int NOT_EQUAL = 2;

    public SchemaType schemaType();

    public boolean validate();

    public boolean validate(XmlOptions var1);

    public XmlObject[] selectPath(String var1);

    public XmlObject[] selectPath(String var1, XmlOptions var2);

    public XmlObject[] execQuery(String var1);

    public XmlObject[] execQuery(String var1, XmlOptions var2);

    public XmlObject changeType(SchemaType var1);

    public XmlObject substitute(QName var1, SchemaType var2);

    public boolean isNil();

    public void setNil();

    public String toString();

    public boolean isImmutable();

    public XmlObject set(XmlObject var1);

    public XmlObject copy();

    public XmlObject copy(XmlOptions var1);

    public boolean valueEquals(XmlObject var1);

    public int valueHashCode();

    public int compareTo(Object var1);

    public int compareValue(XmlObject var1);

    public XmlObject[] selectChildren(QName var1);

    public XmlObject[] selectChildren(String var1, String var2);

    public XmlObject[] selectChildren(QNameSet var1);

    public XmlObject selectAttribute(QName var1);

    public XmlObject selectAttribute(String var1, String var2);

    public XmlObject[] selectAttributes(QNameSet var1);
}

