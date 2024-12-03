/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlLanguage;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.apache.xmlbeans.impl.xb.xmlschema.LangAttribute;

public class LangAttributeImpl
extends XmlComplexContentImpl
implements LangAttribute {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/XML/1998/namespace", "lang")};

    public LangAttributeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public LangAttribute.Lang xgetLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            LangAttribute.Lang target = null;
            target = (LangAttribute.Lang)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLang(String lang) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setStringValue(lang);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLang(LangAttribute.Lang lang) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            LangAttribute.Lang target = null;
            target = (LangAttribute.Lang)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (LangAttribute.Lang)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.set(lang);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[0]);
        }
    }

    public static class LangImpl
    extends XmlUnionImpl
    implements LangAttribute.Lang,
    XmlLanguage,
    LangAttribute.Lang.Member {
        private static final long serialVersionUID = 1L;

        public LangImpl(SchemaType sType) {
            super(sType, false);
        }

        protected LangImpl(SchemaType sType, boolean b) {
            super(sType, b);
        }

        public static class MemberImpl
        extends JavaStringEnumerationHolderEx
        implements LangAttribute.Lang.Member {
            private static final long serialVersionUID = 1L;

            public MemberImpl(SchemaType sType) {
                super(sType, false);
            }

            protected MemberImpl(SchemaType sType, boolean b) {
                super(sType, b);
            }
        }
    }
}

