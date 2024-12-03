/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationSet;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AnnotatedImpl;

public class ComplexTypeImpl
extends AnnotatedImpl
implements ComplexType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "simpleContent"), new QName("http://www.w3.org/2001/XMLSchema", "complexContent"), new QName("http://www.w3.org/2001/XMLSchema", "group"), new QName("http://www.w3.org/2001/XMLSchema", "all"), new QName("http://www.w3.org/2001/XMLSchema", "choice"), new QName("http://www.w3.org/2001/XMLSchema", "sequence"), new QName("http://www.w3.org/2001/XMLSchema", "attribute"), new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup"), new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute"), new QName("", "name"), new QName("", "mixed"), new QName("", "abstract"), new QName("", "final"), new QName("", "block")};

    public ComplexTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SimpleContentDocument.SimpleContent getSimpleContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleContentDocument.SimpleContent target = null;
            target = (SimpleContentDocument.SimpleContent)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSimpleContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setSimpleContent(SimpleContentDocument.SimpleContent simpleContent) {
        this.generatedSetterHelperImpl(simpleContent, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SimpleContentDocument.SimpleContent addNewSimpleContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleContentDocument.SimpleContent target = null;
            target = (SimpleContentDocument.SimpleContent)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSimpleContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ComplexContentDocument.ComplexContent getComplexContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ComplexContentDocument.ComplexContent target = null;
            target = (ComplexContentDocument.ComplexContent)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetComplexContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setComplexContent(ComplexContentDocument.ComplexContent complexContent) {
        this.generatedSetterHelperImpl(complexContent, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ComplexContentDocument.ComplexContent addNewComplexContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ComplexContentDocument.ComplexContent target = null;
            target = (ComplexContentDocument.ComplexContent)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetComplexContent() {
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
    public GroupRef getGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setGroup(GroupRef group) {
        this.generatedSetterHelperImpl(group, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GroupRef addNewGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public All getAll() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            All target = null;
            target = (All)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAll() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setAll(All all) {
        this.generatedSetterHelperImpl(all, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public All addNewAll() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            All target = null;
            target = (All)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAll() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ExplicitGroup getChoice() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetChoice() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setChoice(ExplicitGroup choice) {
        this.generatedSetterHelperImpl(choice, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ExplicitGroup addNewChoice() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetChoice() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ExplicitGroup getSequence() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSequence() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setSequence(ExplicitGroup sequence) {
        this.generatedSetterHelperImpl(sequence, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ExplicitGroup addNewSequence() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSequence() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Attribute> getAttributeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<Attribute>(this::getAttributeArray, this::setAttributeArray, this::insertNewAttribute, this::removeAttribute, this::sizeOfAttributeArray);
        }
    }

    @Override
    public Attribute[] getAttributeArray() {
        return (Attribute[])this.getXmlObjectArray(PROPERTY_QNAME[6], new Attribute[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Attribute getAttributeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfAttributeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setAttributeArray(Attribute[] attributeArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setAttributeArray(int i, Attribute attribute) {
        this.generatedSetterHelperImpl(attribute, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Attribute insertNewAttribute(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Attribute addNewAttribute() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAttribute(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<AttributeGroupRef> getAttributeGroupList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<AttributeGroupRef>(this::getAttributeGroupArray, this::setAttributeGroupArray, this::insertNewAttributeGroup, this::removeAttributeGroup, this::sizeOfAttributeGroupArray);
        }
    }

    @Override
    public AttributeGroupRef[] getAttributeGroupArray() {
        return (AttributeGroupRef[])this.getXmlObjectArray(PROPERTY_QNAME[7], new AttributeGroupRef[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AttributeGroupRef getAttributeGroupArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
    public int sizeOfAttributeGroupArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setAttributeGroupArray(AttributeGroupRef[] attributeGroupArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeGroupArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setAttributeGroupArray(int i, AttributeGroupRef attributeGroup) {
        this.generatedSetterHelperImpl(attributeGroup, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AttributeGroupRef insertNewAttributeGroup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AttributeGroupRef addNewAttributeGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAttributeGroup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Wildcard getAnyAttribute() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAnyAttribute() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setAnyAttribute(Wildcard anyAttribute) {
        this.generatedSetterHelperImpl(anyAttribute, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Wildcard addNewAnyAttribute() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAnyAttribute() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], 0);
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
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
            target = (XmlNCName)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
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
            return this.get_store().find_attribute_user(PROPERTY_QNAME[9]) != null;
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[9]));
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
            target = (XmlNCName)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            if (target == null) {
                target = (XmlNCName)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[9]));
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
            this.get_store().remove_attribute(PROPERTY_QNAME[9]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getMixed() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[10]));
            }
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetMixed() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(PROPERTY_QNAME[10]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMixed() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[10]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMixed(boolean mixed) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[10]));
            }
            target.setBooleanValue(mixed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMixed(XmlBoolean mixed) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[10]));
            }
            target.set(mixed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMixed() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[10]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getAbstract() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[11]));
            }
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetAbstract() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(PROPERTY_QNAME[11]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAbstract() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[11]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAbstract(boolean xabstract) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
            }
            target.setBooleanValue(xabstract);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAbstract(XmlBoolean xabstract) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
            }
            target.set(xabstract);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAbstract() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[11]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getFinal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DerivationSet xgetFinal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFinal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[12]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFinal(Object xfinal) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
            }
            target.setObjectValue(xfinal);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFinal(DerivationSet xfinal) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            if (target == null) {
                target = (DerivationSet)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
            }
            target.set(xfinal);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFinal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[12]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getBlock() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DerivationSet xgetBlock() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBlock() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[13]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBlock(Object block) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
            }
            target.setObjectValue(block);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBlock(DerivationSet block) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (DerivationSet)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
            }
            target.set(block);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBlock() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[13]);
        }
    }
}

