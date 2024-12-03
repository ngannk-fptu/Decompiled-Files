/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xmlconfig.NamespaceList;
import org.apache.xmlbeans.impl.xb.xmlconfig.NamespacePrefixList;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface Nsconfig
extends XmlObject {
    public static final DocumentFactory<Nsconfig> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "nsconfigaebatype");
    public static final SchemaType type = Factory.getType();

    public String getPackage();

    public XmlString xgetPackage();

    public boolean isSetPackage();

    public void setPackage(String var1);

    public void xsetPackage(XmlString var1);

    public void unsetPackage();

    public String getPrefix();

    public XmlString xgetPrefix();

    public boolean isSetPrefix();

    public void setPrefix(String var1);

    public void xsetPrefix(XmlString var1);

    public void unsetPrefix();

    public String getSuffix();

    public XmlString xgetSuffix();

    public boolean isSetSuffix();

    public void setSuffix(String var1);

    public void xsetSuffix(XmlString var1);

    public void unsetSuffix();

    public Object getUri();

    public NamespaceList xgetUri();

    public boolean isSetUri();

    public void setUri(Object var1);

    public void xsetUri(NamespaceList var1);

    public void unsetUri();

    public List getUriprefix();

    public NamespacePrefixList xgetUriprefix();

    public boolean isSetUriprefix();

    public void setUriprefix(List var1);

    public void xsetUriprefix(NamespacePrefixList var1);

    public void unsetUriprefix();
}

