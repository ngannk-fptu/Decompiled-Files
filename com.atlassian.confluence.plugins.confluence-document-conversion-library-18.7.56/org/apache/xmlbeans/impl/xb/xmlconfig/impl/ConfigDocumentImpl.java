/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Nsconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnameconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Usertypeconfig;

public class ConfigDocumentImpl
extends XmlComplexContentImpl
implements ConfigDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "config")};

    public ConfigDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ConfigDocument.Config getConfig() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ConfigDocument.Config target = null;
            target = (ConfigDocument.Config)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setConfig(ConfigDocument.Config config) {
        this.generatedSetterHelperImpl(config, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ConfigDocument.Config addNewConfig() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ConfigDocument.Config target = null;
            target = (ConfigDocument.Config)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class ConfigImpl
    extends XmlComplexContentImpl
    implements ConfigDocument.Config {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "namespace"), new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "qname"), new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "extension"), new QName("http://xml.apache.org/xmlbeans/2004/02/xbean/config", "usertype")};

        public ConfigImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<Nsconfig> getNamespaceList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<Nsconfig>(this::getNamespaceArray, this::setNamespaceArray, this::insertNewNamespace, this::removeNamespace, this::sizeOfNamespaceArray);
            }
        }

        @Override
        public Nsconfig[] getNamespaceArray() {
            return (Nsconfig[])this.getXmlObjectArray(PROPERTY_QNAME[0], new Nsconfig[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Nsconfig getNamespaceArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Nsconfig target = null;
                target = (Nsconfig)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
        public int sizeOfNamespaceArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setNamespaceArray(Nsconfig[] namespaceArray) {
            this.check_orphaned();
            this.arraySetterHelper(namespaceArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setNamespaceArray(int i, Nsconfig namespace) {
            this.generatedSetterHelperImpl(namespace, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Nsconfig insertNewNamespace(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Nsconfig target = null;
                target = (Nsconfig)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Nsconfig addNewNamespace() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Nsconfig target = null;
                target = (Nsconfig)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeNamespace(int i) {
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
        public List<Qnameconfig> getQnameList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<Qnameconfig>(this::getQnameArray, this::setQnameArray, this::insertNewQname, this::removeQname, this::sizeOfQnameArray);
            }
        }

        @Override
        public Qnameconfig[] getQnameArray() {
            return (Qnameconfig[])this.getXmlObjectArray(PROPERTY_QNAME[1], new Qnameconfig[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Qnameconfig getQnameArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Qnameconfig target = null;
                target = (Qnameconfig)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
        public int sizeOfQnameArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[1]);
            }
        }

        @Override
        public void setQnameArray(Qnameconfig[] qnameArray) {
            this.check_orphaned();
            this.arraySetterHelper(qnameArray, PROPERTY_QNAME[1]);
        }

        @Override
        public void setQnameArray(int i, Qnameconfig qname) {
            this.generatedSetterHelperImpl(qname, PROPERTY_QNAME[1], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Qnameconfig insertNewQname(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Qnameconfig target = null;
                target = (Qnameconfig)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Qnameconfig addNewQname() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Qnameconfig target = null;
                target = (Qnameconfig)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeQname(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[1], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<Extensionconfig> getExtensionList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<Extensionconfig>(this::getExtensionArray, this::setExtensionArray, this::insertNewExtension, this::removeExtension, this::sizeOfExtensionArray);
            }
        }

        @Override
        public Extensionconfig[] getExtensionArray() {
            return (Extensionconfig[])this.getXmlObjectArray(PROPERTY_QNAME[2], new Extensionconfig[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Extensionconfig getExtensionArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Extensionconfig target = null;
                target = (Extensionconfig)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
        public int sizeOfExtensionArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[2]);
            }
        }

        @Override
        public void setExtensionArray(Extensionconfig[] extensionArray) {
            this.check_orphaned();
            this.arraySetterHelper(extensionArray, PROPERTY_QNAME[2]);
        }

        @Override
        public void setExtensionArray(int i, Extensionconfig extension) {
            this.generatedSetterHelperImpl(extension, PROPERTY_QNAME[2], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Extensionconfig insertNewExtension(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Extensionconfig target = null;
                target = (Extensionconfig)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Extensionconfig addNewExtension() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Extensionconfig target = null;
                target = (Extensionconfig)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeExtension(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[2], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<Usertypeconfig> getUsertypeList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<Usertypeconfig>(this::getUsertypeArray, this::setUsertypeArray, this::insertNewUsertype, this::removeUsertype, this::sizeOfUsertypeArray);
            }
        }

        @Override
        public Usertypeconfig[] getUsertypeArray() {
            return (Usertypeconfig[])this.getXmlObjectArray(PROPERTY_QNAME[3], new Usertypeconfig[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Usertypeconfig getUsertypeArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Usertypeconfig target = null;
                target = (Usertypeconfig)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
        public int sizeOfUsertypeArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[3]);
            }
        }

        @Override
        public void setUsertypeArray(Usertypeconfig[] usertypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(usertypeArray, PROPERTY_QNAME[3]);
        }

        @Override
        public void setUsertypeArray(int i, Usertypeconfig usertype) {
            this.generatedSetterHelperImpl(usertype, PROPERTY_QNAME[3], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Usertypeconfig insertNewUsertype(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Usertypeconfig target = null;
                target = (Usertypeconfig)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Usertypeconfig addNewUsertype() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                Usertypeconfig target = null;
                target = (Usertypeconfig)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeUsertype(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[3], i);
            }
        }
    }
}

