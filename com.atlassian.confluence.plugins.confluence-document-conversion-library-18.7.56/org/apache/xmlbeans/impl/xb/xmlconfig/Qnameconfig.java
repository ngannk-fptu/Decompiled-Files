/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnametargetlist;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface Qnameconfig
extends XmlObject {
    public static final DocumentFactory<Qnameconfig> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "qnameconfig463ftype");
    public static final SchemaType type = Factory.getType();

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

    public List getTarget();

    public Qnametargetlist xgetTarget();

    public boolean isSetTarget();

    public void setTarget(List var1);

    public void xsetTarget(Qnametargetlist var1);

    public void unsetTarget();
}

