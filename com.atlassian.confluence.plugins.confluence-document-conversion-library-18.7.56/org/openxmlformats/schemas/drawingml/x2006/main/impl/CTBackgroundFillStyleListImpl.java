/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBackgroundFillStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;

public class CTBackgroundFillStyleListImpl
extends XmlComplexContentImpl
implements CTBackgroundFillStyleList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill")};

    public CTBackgroundFillStyleListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTNoFillProperties> getNoFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTNoFillProperties>(this::getNoFillArray, this::setNoFillArray, this::insertNewNoFill, this::removeNoFill, this::sizeOfNoFillArray);
        }
    }

    @Override
    public CTNoFillProperties[] getNoFillArray() {
        return (CTNoFillProperties[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTNoFillProperties[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNoFillProperties getNoFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNoFillProperties target = null;
            target = (CTNoFillProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfNoFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setNoFillArray(CTNoFillProperties[] noFillArray) {
        this.check_orphaned();
        this.arraySetterHelper(noFillArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setNoFillArray(int i, CTNoFillProperties noFill) {
        this.generatedSetterHelperImpl(noFill, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNoFillProperties insertNewNoFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNoFillProperties target = null;
            target = (CTNoFillProperties)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNoFillProperties addNewNoFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNoFillProperties target = null;
            target = (CTNoFillProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNoFill(int i) {
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
    public List<CTSolidColorFillProperties> getSolidFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSolidColorFillProperties>(this::getSolidFillArray, this::setSolidFillArray, this::insertNewSolidFill, this::removeSolidFill, this::sizeOfSolidFillArray);
        }
    }

    @Override
    public CTSolidColorFillProperties[] getSolidFillArray() {
        return (CTSolidColorFillProperties[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTSolidColorFillProperties[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSolidColorFillProperties getSolidFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSolidColorFillProperties target = null;
            target = (CTSolidColorFillProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfSolidFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setSolidFillArray(CTSolidColorFillProperties[] solidFillArray) {
        this.check_orphaned();
        this.arraySetterHelper(solidFillArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setSolidFillArray(int i, CTSolidColorFillProperties solidFill) {
        this.generatedSetterHelperImpl(solidFill, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSolidColorFillProperties insertNewSolidFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSolidColorFillProperties target = null;
            target = (CTSolidColorFillProperties)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSolidColorFillProperties addNewSolidFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSolidColorFillProperties target = null;
            target = (CTSolidColorFillProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSolidFill(int i) {
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
    public List<CTGradientFillProperties> getGradFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGradientFillProperties>(this::getGradFillArray, this::setGradFillArray, this::insertNewGradFill, this::removeGradFill, this::sizeOfGradFillArray);
        }
    }

    @Override
    public CTGradientFillProperties[] getGradFillArray() {
        return (CTGradientFillProperties[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTGradientFillProperties[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGradientFillProperties getGradFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGradientFillProperties target = null;
            target = (CTGradientFillProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfGradFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setGradFillArray(CTGradientFillProperties[] gradFillArray) {
        this.check_orphaned();
        this.arraySetterHelper(gradFillArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setGradFillArray(int i, CTGradientFillProperties gradFill) {
        this.generatedSetterHelperImpl(gradFill, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGradientFillProperties insertNewGradFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGradientFillProperties target = null;
            target = (CTGradientFillProperties)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGradientFillProperties addNewGradFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGradientFillProperties target = null;
            target = (CTGradientFillProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGradFill(int i) {
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
    public List<CTBlipFillProperties> getBlipFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBlipFillProperties>(this::getBlipFillArray, this::setBlipFillArray, this::insertNewBlipFill, this::removeBlipFill, this::sizeOfBlipFillArray);
        }
    }

    @Override
    public CTBlipFillProperties[] getBlipFillArray() {
        return (CTBlipFillProperties[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTBlipFillProperties[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlipFillProperties getBlipFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlipFillProperties target = null;
            target = (CTBlipFillProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfBlipFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setBlipFillArray(CTBlipFillProperties[] blipFillArray) {
        this.check_orphaned();
        this.arraySetterHelper(blipFillArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setBlipFillArray(int i, CTBlipFillProperties blipFill) {
        this.generatedSetterHelperImpl(blipFill, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlipFillProperties insertNewBlipFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlipFillProperties target = null;
            target = (CTBlipFillProperties)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlipFillProperties addNewBlipFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlipFillProperties target = null;
            target = (CTBlipFillProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBlipFill(int i) {
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
    public List<CTPatternFillProperties> getPattFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPatternFillProperties>(this::getPattFillArray, this::setPattFillArray, this::insertNewPattFill, this::removePattFill, this::sizeOfPattFillArray);
        }
    }

    @Override
    public CTPatternFillProperties[] getPattFillArray() {
        return (CTPatternFillProperties[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTPatternFillProperties[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPatternFillProperties getPattFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPatternFillProperties target = null;
            target = (CTPatternFillProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfPattFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setPattFillArray(CTPatternFillProperties[] pattFillArray) {
        this.check_orphaned();
        this.arraySetterHelper(pattFillArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setPattFillArray(int i, CTPatternFillProperties pattFill) {
        this.generatedSetterHelperImpl(pattFill, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPatternFillProperties insertNewPattFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPatternFillProperties target = null;
            target = (CTPatternFillProperties)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPatternFillProperties addNewPattFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPatternFillProperties target = null;
            target = (CTPatternFillProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePattFill(int i) {
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
    public List<CTGroupFillProperties> getGrpFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGroupFillProperties>(this::getGrpFillArray, this::setGrpFillArray, this::insertNewGrpFill, this::removeGrpFill, this::sizeOfGrpFillArray);
        }
    }

    @Override
    public CTGroupFillProperties[] getGrpFillArray() {
        return (CTGroupFillProperties[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTGroupFillProperties[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupFillProperties getGrpFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupFillProperties target = null;
            target = (CTGroupFillProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfGrpFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setGrpFillArray(CTGroupFillProperties[] grpFillArray) {
        this.check_orphaned();
        this.arraySetterHelper(grpFillArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setGrpFillArray(int i, CTGroupFillProperties grpFill) {
        this.generatedSetterHelperImpl(grpFill, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupFillProperties insertNewGrpFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupFillProperties target = null;
            target = (CTGroupFillProperties)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupFillProperties addNewGrpFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupFillProperties target = null;
            target = (CTGroupFillProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGrpFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }
}

