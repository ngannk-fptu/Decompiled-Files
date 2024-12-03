/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface Usertypeconfig
extends XmlObject {
    public static final DocumentFactory<Usertypeconfig> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "usertypeconfig7bbatype");
    public static final SchemaType type = Factory.getType();

    public String getStaticHandler();

    public XmlString xgetStaticHandler();

    public void setStaticHandler(String var1);

    public void xsetStaticHandler(XmlString var1);

    public QName getName();

    public XmlQName xgetName();

    public boolean isSetName();

    public void setName(QName var1);

    public void xsetName(XmlQName var1);

    public void unsetName();

    public String getJavaname();

    public XmlString xgetJavaname();

    public boolean isSetJavaname();

    public void setJavaname(String var1);

    public void xsetJavaname(XmlString var1);

    public void unsetJavaname();
}

