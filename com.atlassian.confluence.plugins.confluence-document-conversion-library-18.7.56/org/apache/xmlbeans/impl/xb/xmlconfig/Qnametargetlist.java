/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface Qnametargetlist
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<Qnametargetlist> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "qnametargetlist16actype");
    public static final SchemaType type = Factory.getType();

    public List getListValue();

    public List xgetListValue();

    public void setListValue(List<?> var1);
}

