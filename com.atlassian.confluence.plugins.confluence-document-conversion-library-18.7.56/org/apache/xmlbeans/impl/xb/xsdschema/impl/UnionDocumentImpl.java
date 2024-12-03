/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlListImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.UnionDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AnnotatedImpl;

public class UnionDocumentImpl
extends XmlComplexContentImpl
implements UnionDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "union")};

    public UnionDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UnionDocument.Union getUnion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            UnionDocument.Union target = null;
            target = (UnionDocument.Union)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setUnion(UnionDocument.Union union) {
        this.generatedSetterHelperImpl(union, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UnionDocument.Union addNewUnion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            UnionDocument.Union target = null;
            target = (UnionDocument.Union)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class UnionImpl
    extends AnnotatedImpl
    implements UnionDocument.Union {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "simpleType"), new QName("", "memberTypes")};

        public UnionImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<LocalSimpleType> getSimpleTypeList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<LocalSimpleType>(this::getSimpleTypeArray, this::setSimpleTypeArray, this::insertNewSimpleType, this::removeSimpleType, this::sizeOfSimpleTypeArray);
            }
        }

        @Override
        public LocalSimpleType[] getSimpleTypeArray() {
            return (LocalSimpleType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new LocalSimpleType[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LocalSimpleType getSimpleTypeArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int sizeOfSimpleTypeArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setSimpleTypeArray(LocalSimpleType[] simpleTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(simpleTypeArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setSimpleTypeArray(int i, LocalSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LocalSimpleType insertNewSimpleType(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LocalSimpleType addNewSimpleType() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeSimpleType(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[0], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List getMemberTypes() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                return target == null ? null : target.getListValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public UnionDocument.Union.MemberTypes xgetMemberTypes() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                UnionDocument.Union.MemberTypes target = null;
                target = (UnionDocument.Union.MemberTypes)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetMemberTypes() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setMemberTypes(List memberTypes) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.setListValue(memberTypes);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetMemberTypes(UnionDocument.Union.MemberTypes memberTypes) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                UnionDocument.Union.MemberTypes target = null;
                target = (UnionDocument.Union.MemberTypes)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (UnionDocument.Union.MemberTypes)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.set(memberTypes);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetMemberTypes() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[1]);
            }
        }

        public static class MemberTypesImpl
        extends XmlListImpl
        implements UnionDocument.Union.MemberTypes {
            private static final long serialVersionUID = 1L;

            public MemberTypesImpl(SchemaType sType) {
                super(sType, false);
            }

            protected MemberTypesImpl(SchemaType sType, boolean b) {
                super(sType, b);
            }
        }
    }
}

