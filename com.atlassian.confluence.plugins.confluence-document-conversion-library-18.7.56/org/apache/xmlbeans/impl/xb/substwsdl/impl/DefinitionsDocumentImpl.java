/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.substwsdl.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument;
import org.apache.xmlbeans.impl.xb.substwsdl.TImport;

public class DefinitionsDocumentImpl
extends XmlComplexContentImpl
implements DefinitionsDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "definitions")};

    public DefinitionsDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefinitionsDocument.Definitions getDefinitions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DefinitionsDocument.Definitions target = null;
            target = (DefinitionsDocument.Definitions)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setDefinitions(DefinitionsDocument.Definitions definitions) {
        this.generatedSetterHelperImpl(definitions, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefinitionsDocument.Definitions addNewDefinitions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DefinitionsDocument.Definitions target = null;
            target = (DefinitionsDocument.Definitions)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class DefinitionsImpl
    extends XmlComplexContentImpl
    implements DefinitionsDocument.Definitions {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "import"), new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "types"), new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "message"), new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "binding"), new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "portType"), new QName("http://www.apache.org/internal/xmlbeans/wsdlsubst", "service")};

        public DefinitionsImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<TImport> getImportList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<TImport>(this::getImportArray, this::setImportArray, this::insertNewImport, this::removeImport, this::sizeOfImportArray);
            }
        }

        @Override
        public TImport[] getImportArray() {
            return (TImport[])this.getXmlObjectArray(PROPERTY_QNAME[0], new TImport[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TImport getImportArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TImport target = null;
                target = (TImport)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
        public int sizeOfImportArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setImportArray(TImport[] ximportArray) {
            this.check_orphaned();
            this.arraySetterHelper(ximportArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setImportArray(int i, TImport ximport) {
            this.generatedSetterHelperImpl(ximport, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TImport insertNewImport(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TImport target = null;
                target = (TImport)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TImport addNewImport() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TImport target = null;
                target = (TImport)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeImport(int i) {
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
        public List<XmlObject> getTypesList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<XmlObject>(this::getTypesArray, this::setTypesArray, this::insertNewTypes, this::removeTypes, this::sizeOfTypesArray);
            }
        }

        @Override
        public XmlObject[] getTypesArray() {
            return this.getXmlObjectArray(PROPERTY_QNAME[1], new XmlObject[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject getTypesArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
        public int sizeOfTypesArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[1]);
            }
        }

        @Override
        public void setTypesArray(XmlObject[] typesArray) {
            this.check_orphaned();
            this.arraySetterHelper(typesArray, PROPERTY_QNAME[1]);
        }

        @Override
        public void setTypesArray(int i, XmlObject types) {
            this.generatedSetterHelperImpl(types, PROPERTY_QNAME[1], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject insertNewTypes(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject addNewTypes() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeTypes(int i) {
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
        public List<XmlObject> getMessageList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<XmlObject>(this::getMessageArray, this::setMessageArray, this::insertNewMessage, this::removeMessage, this::sizeOfMessageArray);
            }
        }

        @Override
        public XmlObject[] getMessageArray() {
            return this.getXmlObjectArray(PROPERTY_QNAME[2], new XmlObject[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject getMessageArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
        public int sizeOfMessageArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[2]);
            }
        }

        @Override
        public void setMessageArray(XmlObject[] messageArray) {
            this.check_orphaned();
            this.arraySetterHelper(messageArray, PROPERTY_QNAME[2]);
        }

        @Override
        public void setMessageArray(int i, XmlObject message) {
            this.generatedSetterHelperImpl(message, PROPERTY_QNAME[2], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject insertNewMessage(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject addNewMessage() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeMessage(int i) {
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
        public List<XmlObject> getBindingList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<XmlObject>(this::getBindingArray, this::setBindingArray, this::insertNewBinding, this::removeBinding, this::sizeOfBindingArray);
            }
        }

        @Override
        public XmlObject[] getBindingArray() {
            return this.getXmlObjectArray(PROPERTY_QNAME[3], new XmlObject[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject getBindingArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
        public int sizeOfBindingArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[3]);
            }
        }

        @Override
        public void setBindingArray(XmlObject[] bindingArray) {
            this.check_orphaned();
            this.arraySetterHelper(bindingArray, PROPERTY_QNAME[3]);
        }

        @Override
        public void setBindingArray(int i, XmlObject binding) {
            this.generatedSetterHelperImpl(binding, PROPERTY_QNAME[3], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject insertNewBinding(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject addNewBinding() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeBinding(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[3], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<XmlObject> getPortTypeList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<XmlObject>(this::getPortTypeArray, this::setPortTypeArray, this::insertNewPortType, this::removePortType, this::sizeOfPortTypeArray);
            }
        }

        @Override
        public XmlObject[] getPortTypeArray() {
            return this.getXmlObjectArray(PROPERTY_QNAME[4], new XmlObject[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject getPortTypeArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
        public int sizeOfPortTypeArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[4]);
            }
        }

        @Override
        public void setPortTypeArray(XmlObject[] portTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(portTypeArray, PROPERTY_QNAME[4]);
        }

        @Override
        public void setPortTypeArray(int i, XmlObject portType) {
            this.generatedSetterHelperImpl(portType, PROPERTY_QNAME[4], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject insertNewPortType(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject addNewPortType() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removePortType(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[4], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<XmlObject> getServiceList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<XmlObject>(this::getServiceArray, this::setServiceArray, this::insertNewService, this::removeService, this::sizeOfServiceArray);
            }
        }

        @Override
        public XmlObject[] getServiceArray() {
            return this.getXmlObjectArray(PROPERTY_QNAME[5], new XmlObject[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject getServiceArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
        public int sizeOfServiceArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[5]);
            }
        }

        @Override
        public void setServiceArray(XmlObject[] serviceArray) {
            this.check_orphaned();
            this.arraySetterHelper(serviceArray, PROPERTY_QNAME[5]);
        }

        @Override
        public void setServiceArray(int i, XmlObject service) {
            this.generatedSetterHelperImpl(service, PROPERTY_QNAME[5], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject insertNewService(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlObject addNewService() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlObject target = null;
                target = (XmlObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeService(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[5], i);
            }
        }
    }
}

