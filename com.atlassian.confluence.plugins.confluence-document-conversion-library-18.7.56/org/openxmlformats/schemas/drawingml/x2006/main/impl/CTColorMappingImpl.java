/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.STColorSchemeIndex;

public class CTColorMappingImpl
extends XmlComplexContentImpl
implements CTColorMapping {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst"), new QName("", "bg1"), new QName("", "tx1"), new QName("", "bg2"), new QName("", "tx2"), new QName("", "accent1"), new QName("", "accent2"), new QName("", "accent3"), new QName("", "accent4"), new QName("", "accent5"), new QName("", "accent6"), new QName("", "hlink"), new QName("", "folHlink")};

    public CTColorMappingImpl(SchemaType sType) {
        super(sType);
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
            target = (CTOfficeArtExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setExtLst(CTOfficeArtExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[0], 0, (short)1);
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
            target = (CTOfficeArtExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
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
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getBg1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetBg1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBg1(STColorSchemeIndex.Enum bg1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.setEnumValue(bg1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBg1(STColorSchemeIndex bg1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.set(bg1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getTx1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetTx1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTx1(STColorSchemeIndex.Enum tx1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setEnumValue(tx1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTx1(STColorSchemeIndex tx1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.set(tx1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getBg2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetBg2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBg2(STColorSchemeIndex.Enum bg2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.setEnumValue(bg2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBg2(STColorSchemeIndex bg2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.set(bg2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getTx2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetTx2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTx2(STColorSchemeIndex.Enum tx2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.setEnumValue(tx2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTx2(STColorSchemeIndex tx2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.set(tx2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getAccent1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetAccent1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccent1(STColorSchemeIndex.Enum accent1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
            }
            target.setEnumValue(accent1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccent1(STColorSchemeIndex accent1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
            }
            target.set(accent1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getAccent2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetAccent2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccent2(STColorSchemeIndex.Enum accent2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[6]));
            }
            target.setEnumValue(accent2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccent2(STColorSchemeIndex accent2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[6]));
            }
            target.set(accent2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getAccent3() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetAccent3() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccent3(STColorSchemeIndex.Enum accent3) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[7]));
            }
            target.setEnumValue(accent3);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccent3(STColorSchemeIndex accent3) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[7]));
            }
            target.set(accent3);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getAccent4() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[8]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetAccent4() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccent4(STColorSchemeIndex.Enum accent4) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[8]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[8]));
            }
            target.setEnumValue(accent4);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccent4(STColorSchemeIndex accent4) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[8]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[8]));
            }
            target.set(accent4);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getAccent5() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetAccent5() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccent5(STColorSchemeIndex.Enum accent5) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[9]));
            }
            target.setEnumValue(accent5);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccent5(STColorSchemeIndex accent5) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[9]));
            }
            target.set(accent5);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getAccent6() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetAccent6() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccent6(STColorSchemeIndex.Enum accent6) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[10]));
            }
            target.setEnumValue(accent6);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccent6(STColorSchemeIndex accent6) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[10]));
            }
            target.set(accent6);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHlink(STColorSchemeIndex.Enum hlink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
            }
            target.setEnumValue(hlink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHlink(STColorSchemeIndex hlink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
            }
            target.set(hlink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex.Enum getFolHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            return target == null ? null : (STColorSchemeIndex.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorSchemeIndex xgetFolHlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFolHlink(STColorSchemeIndex.Enum folHlink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
            }
            target.setEnumValue(folHlink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFolHlink(STColorSchemeIndex folHlink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorSchemeIndex target = null;
            target = (STColorSchemeIndex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            if (target == null) {
                target = (STColorSchemeIndex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
            }
            target.set(folHlink);
        }
    }
}

