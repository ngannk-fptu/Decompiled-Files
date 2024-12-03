/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.impl.xb.xsdschema.SelectorDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AnnotatedImpl;

public class KeybaseImpl
extends AnnotatedImpl
implements Keybase {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "selector"), new QName("http://www.w3.org/2001/XMLSchema", "field"), new QName("", "name")};

    public KeybaseImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SelectorDocument.Selector getSelector() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SelectorDocument.Selector target = null;
            target = (SelectorDocument.Selector)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSelector(SelectorDocument.Selector selector) {
        this.generatedSetterHelperImpl(selector, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SelectorDocument.Selector addNewSelector() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SelectorDocument.Selector target = null;
            target = (SelectorDocument.Selector)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<FieldDocument.Field> getFieldList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<FieldDocument.Field>(this::getFieldArray, this::setFieldArray, this::insertNewField, this::removeField, this::sizeOfFieldArray);
        }
    }

    @Override
    public FieldDocument.Field[] getFieldArray() {
        return (FieldDocument.Field[])this.getXmlObjectArray(PROPERTY_QNAME[1], new FieldDocument.Field[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldDocument.Field getFieldArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FieldDocument.Field target = null;
            target = (FieldDocument.Field)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfFieldArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setFieldArray(FieldDocument.Field[] fieldArray) {
        this.check_orphaned();
        this.arraySetterHelper(fieldArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setFieldArray(int i, FieldDocument.Field field) {
        this.generatedSetterHelperImpl(field, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldDocument.Field insertNewField(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FieldDocument.Field target = null;
            target = (FieldDocument.Field)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldDocument.Field addNewField() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FieldDocument.Field target = null;
            target = (FieldDocument.Field)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeField(int i) {
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
    public String getName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlNCName xgetName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target;
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setStringValue(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetName(XmlNCName name) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (XmlNCName)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.set(name);
        }
    }
}

