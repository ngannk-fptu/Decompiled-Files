/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xmlconfig.JavaNameList;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface Extensionconfig
extends XmlObject {
    public static final DocumentFactory<Extensionconfig> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "extensionconfig2ac2type");
    public static final SchemaType type = Factory.getType();

    public List<Interface> getInterfaceList();

    public Interface[] getInterfaceArray();

    public Interface getInterfaceArray(int var1);

    public int sizeOfInterfaceArray();

    public void setInterfaceArray(Interface[] var1);

    public void setInterfaceArray(int var1, Interface var2);

    public Interface insertNewInterface(int var1);

    public Interface addNewInterface();

    public void removeInterface(int var1);

    public PrePostSet getPrePostSet();

    public boolean isSetPrePostSet();

    public void setPrePostSet(PrePostSet var1);

    public PrePostSet addNewPrePostSet();

    public void unsetPrePostSet();

    public Object getFor();

    public JavaNameList xgetFor();

    public boolean isSetFor();

    public void setFor(Object var1);

    public void xsetFor(JavaNameList var1);

    public void unsetFor();

    public static interface PrePostSet
    extends XmlObject {
        public static final ElementFactory<PrePostSet> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "prepostset5c9delemtype");
        public static final SchemaType type = Factory.getType();

        public String getStaticHandler();

        public XmlString xgetStaticHandler();

        public void setStaticHandler(String var1);

        public void xsetStaticHandler(XmlString var1);
    }

    public static interface Interface
    extends XmlObject {
        public static final ElementFactory<Interface> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "interface02a7elemtype");
        public static final SchemaType type = Factory.getType();

        public String getStaticHandler();

        public XmlString xgetStaticHandler();

        public void setStaticHandler(String var1);

        public void xsetStaticHandler(XmlString var1);

        public String getName();

        public XmlString xgetName();

        public boolean isSetName();

        public void setName(String var1);

        public void xsetName(XmlString var1);

        public void unsetName();
    }
}

