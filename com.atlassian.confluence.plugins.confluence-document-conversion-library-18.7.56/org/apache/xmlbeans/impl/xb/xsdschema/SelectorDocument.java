/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SelectorDocument
extends XmlObject {
    public static final DocumentFactory<SelectorDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "selectorcb44doctype");
    public static final SchemaType type = Factory.getType();

    public Selector getSelector();

    public void setSelector(Selector var1);

    public Selector addNewSelector();

    public static interface Selector
    extends Annotated {
        public static final ElementFactory<Selector> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "selector233felemtype");
        public static final SchemaType type = Factory.getType();

        public String getXpath();

        public Xpath xgetXpath();

        public void setXpath(String var1);

        public void xsetXpath(Xpath var1);

        public static interface Xpath
        extends XmlToken {
            public static final ElementFactory<Xpath> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "xpath6f9aattrtype");
            public static final SchemaType type = Factory.getType();
        }
    }
}

