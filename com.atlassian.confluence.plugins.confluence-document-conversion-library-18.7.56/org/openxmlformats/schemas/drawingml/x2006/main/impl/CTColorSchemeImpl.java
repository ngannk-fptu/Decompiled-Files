/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public class CTColorSchemeImpl
extends XmlComplexContentImpl
implements CTColorScheme {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "dk1"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lt1"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "dk2"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lt2"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent1"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent2"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent3"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent4"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent5"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent6"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hlink"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "folHlink"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst"), new QName("", "name")};

    public CTColorSchemeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getDk1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setDk1(CTColor dk1) {
        this.generatedSetterHelperImpl(dk1, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewDk1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getLt1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setLt1(CTColor lt1) {
        this.generatedSetterHelperImpl(lt1, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewLt1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getDk2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setDk2(CTColor dk2) {
        this.generatedSetterHelperImpl(dk2, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewDk2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getLt2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setLt2(CTColor lt2) {
        this.generatedSetterHelperImpl(lt2, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewLt2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getAccent1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAccent1(CTColor accent1) {
        this.generatedSetterHelperImpl(accent1, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewAccent1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getAccent2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAccent2(CTColor accent2) {
        this.generatedSetterHelperImpl(accent2, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewAccent2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getAccent3() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAccent3(CTColor accent3) {
        this.generatedSetterHelperImpl(accent3, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewAccent3() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getAccent4() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAccent4(CTColor accent4) {
        this.generatedSetterHelperImpl(accent4, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewAccent4() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getAccent5() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAccent5(CTColor accent5) {
        this.generatedSetterHelperImpl(accent5, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewAccent5() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getAccent6() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAccent6(CTColor accent6) {
        this.generatedSetterHelperImpl(accent6, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewAccent6() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setHlink(CTColor hlink) {
        this.generatedSetterHelperImpl(hlink, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getFolHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setFolHlink(CTColor folHlink) {
        this.generatedSetterHelperImpl(folHlink, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewFolHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOfficeArtExtensionList getExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOfficeArtExtensionList target = null;
            target = (CTOfficeArtExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setExtLst(CTOfficeArtExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOfficeArtExtensionList addNewExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOfficeArtExtensionList target = null;
            target = (CTOfficeArtExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], 0);
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
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
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
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
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
            }
            target.set(name);
        }
    }
}

