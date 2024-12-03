/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface NotationDocument
extends XmlObject {
    public static final DocumentFactory<NotationDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "notation3381doctype");
    public static final SchemaType type = Factory.getType();

    public Notation getNotation();

    public void setNotation(Notation var1);

    public Notation addNewNotation();

    public static interface Notation
    extends Annotated {
        public static final ElementFactory<Notation> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "notation8b1felemtype");
        public static final SchemaType type = Factory.getType();

        public String getName();

        public XmlNCName xgetName();

        public void setName(String var1);

        public void xsetName(XmlNCName var1);

        public String getPublic();

        public Public xgetPublic();

        public boolean isSetPublic();

        public void setPublic(String var1);

        public void xsetPublic(Public var1);

        public void unsetPublic();

        public String getSystem();

        public XmlAnyURI xgetSystem();

        public boolean isSetSystem();

        public void setSystem(String var1);

        public void xsetSystem(XmlAnyURI var1);

        public void unsetSystem();
    }
}

