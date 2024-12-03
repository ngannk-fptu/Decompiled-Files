/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface KeyrefDocument
extends XmlObject {
    public static final DocumentFactory<KeyrefDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "keyref45afdoctype");
    public static final SchemaType type = Factory.getType();

    public Keyref getKeyref();

    public void setKeyref(Keyref var1);

    public Keyref addNewKeyref();

    public static interface Keyref
    extends Keybase {
        public static final ElementFactory<Keyref> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "keyref7a1felemtype");
        public static final SchemaType type = Factory.getType();

        public QName getRefer();

        public XmlQName xgetRefer();

        public void setRefer(QName var1);

        public void xsetRefer(XmlQName var1);
    }
}

