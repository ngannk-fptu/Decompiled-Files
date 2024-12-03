/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.ltgfmt;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface FileDesc
extends XmlObject {
    public static final DocumentFactory<FileDesc> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "filedesc9392type");
    public static final SchemaType type = Factory.getType();

    public Code getCode();

    public boolean isSetCode();

    public void setCode(Code var1);

    public Code addNewCode();

    public void unsetCode();

    public String getTsDir();

    public XmlToken xgetTsDir();

    public boolean isSetTsDir();

    public void setTsDir(String var1);

    public void xsetTsDir(XmlToken var1);

    public void unsetTsDir();

    public String getFolder();

    public XmlToken xgetFolder();

    public boolean isSetFolder();

    public void setFolder(String var1);

    public void xsetFolder(XmlToken var1);

    public void unsetFolder();

    public String getFileName();

    public XmlToken xgetFileName();

    public boolean isSetFileName();

    public void setFileName(String var1);

    public void xsetFileName(XmlToken var1);

    public void unsetFileName();

    public Role.Enum getRole();

    public Role xgetRole();

    public boolean isSetRole();

    public void setRole(Role.Enum var1);

    public void xsetRole(Role var1);

    public void unsetRole();

    public boolean getValidity();

    public XmlBoolean xgetValidity();

    public boolean isSetValidity();

    public void setValidity(boolean var1);

    public void xsetValidity(XmlBoolean var1);

    public void unsetValidity();

    public static interface Role
    extends XmlToken {
        public static final ElementFactory<Role> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "role21a8attrtype");
        public static final SchemaType type = Factory.getType();
        public static final Enum SCHEMA = Enum.forString("schema");
        public static final Enum INSTANCE = Enum.forString("instance");
        public static final Enum RESOURCE = Enum.forString("resource");
        public static final int INT_SCHEMA = 1;
        public static final int INT_INSTANCE = 2;
        public static final int INT_RESOURCE = 3;

        public StringEnumAbstractBase getEnumValue();

        public void setEnumValue(StringEnumAbstractBase var1);

        public static final class Enum
        extends StringEnumAbstractBase {
            static final int INT_SCHEMA = 1;
            static final int INT_INSTANCE = 2;
            static final int INT_RESOURCE = 3;
            public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("schema", 1), new Enum("instance", 2), new Enum("resource", 3)});
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

