/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.XmlNMTOKEN;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface Attribute
extends Annotated {
    public static final DocumentFactory<Attribute> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "attribute83a9type");
    public static final SchemaType type = Factory.getType();

    public LocalSimpleType getSimpleType();

    public boolean isSetSimpleType();

    public void setSimpleType(LocalSimpleType var1);

    public LocalSimpleType addNewSimpleType();

    public void unsetSimpleType();

    public String getName();

    public XmlNCName xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlNCName var1);

    public void unsetName();

    public QName getRef();

    public XmlQName xgetRef();

    public boolean isSetRef();

    public void setRef(QName var1);

    public void xsetRef(XmlQName var1);

    public void unsetRef();

    public QName getType();

    public XmlQName xgetType();

    public boolean isSetType();

    public void setType(QName var1);

    public void xsetType(XmlQName var1);

    public void unsetType();

    public Use.Enum getUse();

    public Use xgetUse();

    public boolean isSetUse();

    public void setUse(Use.Enum var1);

    public void xsetUse(Use var1);

    public void unsetUse();

    public String getDefault();

    public XmlString xgetDefault();

    public boolean isSetDefault();

    public void setDefault(String var1);

    public void xsetDefault(XmlString var1);

    public void unsetDefault();

    public String getFixed();

    public XmlString xgetFixed();

    public boolean isSetFixed();

    public void setFixed(String var1);

    public void xsetFixed(XmlString var1);

    public void unsetFixed();

    public FormChoice.Enum getForm();

    public FormChoice xgetForm();

    public boolean isSetForm();

    public void setForm(FormChoice.Enum var1);

    public void xsetForm(FormChoice var1);

    public void unsetForm();

    public static interface Use
    extends XmlNMTOKEN {
        public static final ElementFactory<Use> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "usea41aattrtype");
        public static final SchemaType type = Factory.getType();
        public static final Enum PROHIBITED = Enum.forString("prohibited");
        public static final Enum OPTIONAL = Enum.forString("optional");
        public static final Enum REQUIRED = Enum.forString("required");
        public static final int INT_PROHIBITED = 1;
        public static final int INT_OPTIONAL = 2;
        public static final int INT_REQUIRED = 3;

        public StringEnumAbstractBase getEnumValue();

        public void setEnumValue(StringEnumAbstractBase var1);

        public static final class Enum
        extends StringEnumAbstractBase {
            static final int INT_PROHIBITED = 1;
            static final int INT_OPTIONAL = 2;
            static final int INT_REQUIRED = 3;
            public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("prohibited", 1), new Enum("optional", 2), new Enum("required", 3)});
            private static final long serialVersionUID = 1L;

            public static Enum forString(String s) {
                return (Enum)table.forString(s);
            }

            public static Enum forInt(int i) {
                return (Enum)table.forInt(i);
            }

            private Enum(String s, int i) {
                super(s, i);
            }

            private Object readResolve() {
                return Enum.forInt(this.intValue());
            }
        }
    }
}

