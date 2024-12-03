/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPicBullet
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPicBullet;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;

public class CTNumberingImpl
extends XmlComplexContentImpl
implements CTNumbering {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numPicBullet"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "abstractNum"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "num"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numIdMacAtCleanup")};

    public CTNumberingImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTNumPicBullet> getNumPicBulletList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTNumPicBullet>(this::getNumPicBulletArray, this::setNumPicBulletArray, this::insertNewNumPicBullet, this::removeNumPicBullet, this::sizeOfNumPicBulletArray);
        }
    }

    @Override
    public CTNumPicBullet[] getNumPicBulletArray() {
        return (CTNumPicBullet[])this.getXmlObjectArray(PROPERTY_QNAME[0], (XmlObject[])new CTNumPicBullet[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNumPicBullet getNumPicBulletArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNumPicBullet target = null;
            target = (CTNumPicBullet)this.get_store().find_element_user(PROPERTY_QNAME[0], i);
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
    public int sizeOfNumPicBulletArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setNumPicBulletArray(CTNumPicBullet[] numPicBulletArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])numPicBulletArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setNumPicBulletArray(int i, CTNumPicBullet numPicBullet) {
        this.generatedSetterHelperImpl((XmlObject)numPicBullet, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNumPicBullet insertNewNumPicBullet(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNumPicBullet target = null;
            target = (CTNumPicBullet)this.get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNumPicBullet addNewNumPicBullet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNumPicBullet target = null;
            target = (CTNumPicBullet)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNumPicBullet(int i) {
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
    public List<CTAbstractNum> getAbstractNumList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAbstractNum>(this::getAbstractNumArray, this::setAbstractNumArray, this::insertNewAbstractNum, this::removeAbstractNum, this::sizeOfAbstractNumArray);
        }
    }

    @Override
    public CTAbstractNum[] getAbstractNumArray() {
        return (CTAbstractNum[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTAbstractNum[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAbstractNum getAbstractNumArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAbstractNum target = null;
            target = (CTAbstractNum)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfAbstractNumArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setAbstractNumArray(CTAbstractNum[] abstractNumArray) {
        this.check_orphaned();
        this.arraySetterHelper(abstractNumArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setAbstractNumArray(int i, CTAbstractNum abstractNum) {
        this.generatedSetterHelperImpl(abstractNum, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAbstractNum insertNewAbstractNum(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAbstractNum target = null;
            target = (CTAbstractNum)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAbstractNum addNewAbstractNum() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAbstractNum target = null;
            target = (CTAbstractNum)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAbstractNum(int i) {
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
    public List<CTNum> getNumList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTNum>(this::getNumArray, this::setNumArray, this::insertNewNum, this::removeNum, this::sizeOfNumArray);
        }
    }

    @Override
    public CTNum[] getNumArray() {
        return (CTNum[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTNum[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNum getNumArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNum target = null;
            target = (CTNum)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfNumArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setNumArray(CTNum[] numArray) {
        this.check_orphaned();
        this.arraySetterHelper(numArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setNumArray(int i, CTNum num) {
        this.generatedSetterHelperImpl(num, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNum insertNewNum(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNum target = null;
            target = (CTNum)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNum addNewNum() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNum target = null;
            target = (CTNum)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNum(int i) {
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
    public CTDecimalNumber getNumIdMacAtCleanup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNumIdMacAtCleanup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setNumIdMacAtCleanup(CTDecimalNumber numIdMacAtCleanup) {
        this.generatedSetterHelperImpl(numIdMacAtCleanup, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewNumIdMacAtCleanup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNumIdMacAtCleanup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }
}

