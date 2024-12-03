/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.JavaNameList;

public class ExtensionconfigImpl
extends XmlComplexContentImpl
implements Extensionconfig {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "interface"), new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "prePostSet"), new QName("", "for")};

    public ExtensionconfigImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Extensionconfig.Interface> getInterfaceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<Extensionconfig.Interface>(this::getInterfaceArray, this::setInterfaceArray, this::insertNewInterface, this::removeInterface, this::sizeOfInterfaceArray);
        }
    }

    @Override
    public Extensionconfig.Interface[] getInterfaceArray() {
        return (Extensionconfig.Interface[])this.getXmlObjectArray(PROPERTY_QNAME[0], new Extensionconfig.Interface[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Extensionconfig.Interface getInterfaceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Extensionconfig.Interface target = null;
            target = (Extensionconfig.Interface)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfInterfaceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setInterfaceArray(Extensionconfig.Interface[] xinterfaceArray) {
        this.check_orphaned();
        this.arraySetterHelper(xinterfaceArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setInterfaceArray(int i, Extensionconfig.Interface xinterface) {
        this.generatedSetterHelperImpl(xinterface, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Extensionconfig.Interface insertNewInterface(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Extensionconfig.Interface target = null;
            target = (Extensionconfig.Interface)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Extensionconfig.Interface addNewInterface() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Extensionconfig.Interface target = null;
            target = (Extensionconfig.Interface)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInterface(int i) {
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
    public Extensionconfig.PrePostSet getPrePostSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Extensionconfig.PrePostSet target = null;
            target = (Extensionconfig.PrePostSet)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrePostSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setPrePostSet(Extensionconfig.PrePostSet prePostSet) {
        this.generatedSetterHelperImpl(prePostSet, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Extensionconfig.PrePostSet addNewPrePostSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Extensionconfig.PrePostSet target = null;
            target = (Extensionconfig.PrePostSet)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrePostSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getFor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public JavaNameList xgetFor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            JavaNameList target = null;
            target = (JavaNameList)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[2]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFor(Object xfor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setObjectValue(xfor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFor(JavaNameList xfor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            JavaNameList target = null;
            target = (JavaNameList)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (JavaNameList)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.set(xfor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[2]);
        }
    }

    public static class PrePostSetImpl
    extends XmlComplexContentImpl
    implements Extensionconfig.PrePostSet {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "staticHandler")};

        public PrePostSetImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getStaticHandler() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlString xgetStaticHandler() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setStaticHandler(String staticHandler) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                }
                target.setStringValue(staticHandler);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetStaticHandler(XmlString staticHandler) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                if (target == null) {
                    target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                }
                target.set(staticHandler);
            }
        }
    }

    public static class InterfaceImpl
    extends XmlComplexContentImpl
    implements Extensionconfig.Interface {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "staticHandler"), new QName("", "name")};

        public InterfaceImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getStaticHandler() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlString xgetStaticHandler() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setStaticHandler(String staticHandler) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                }
                target.setStringValue(staticHandler);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetStaticHandler(XmlString staticHandler) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                if (target == null) {
                    target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                }
                target.set(staticHandler);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getName() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlString xgetName() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetName() {
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
        public void setName(String name) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.setStringValue(name);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetName(XmlString name) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.set(name);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetName() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[1]);
            }
        }
    }
}

