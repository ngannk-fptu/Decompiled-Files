/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

public class CTTextListStyleImpl
extends XmlComplexContentImpl
implements CTTextListStyle {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "defPPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl1pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl2pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl3pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl4pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl5pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl6pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl7pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl8pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl9pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst")};

    public CTTextListStyleImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties getDefPPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDefPPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setDefPPr(CTTextParagraphProperties defPPr) {
        this.generatedSetterHelperImpl(defPPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewDefPPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDefPPr() {
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
    public CTTextParagraphProperties getLvl1PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl1PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setLvl1PPr(CTTextParagraphProperties lvl1PPr) {
        this.generatedSetterHelperImpl(lvl1PPr, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl1PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl1PPr() {
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
    public CTTextParagraphProperties getLvl2PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl2PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setLvl2PPr(CTTextParagraphProperties lvl2PPr) {
        this.generatedSetterHelperImpl(lvl2PPr, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl2PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl2PPr() {
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
    public CTTextParagraphProperties getLvl3PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl3PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setLvl3PPr(CTTextParagraphProperties lvl3PPr) {
        this.generatedSetterHelperImpl(lvl3PPr, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl3PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl3PPr() {
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
    public CTTextParagraphProperties getLvl4PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl4PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setLvl4PPr(CTTextParagraphProperties lvl4PPr) {
        this.generatedSetterHelperImpl(lvl4PPr, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl4PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl4PPr() {
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
    public CTTextParagraphProperties getLvl5PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl5PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setLvl5PPr(CTTextParagraphProperties lvl5PPr) {
        this.generatedSetterHelperImpl(lvl5PPr, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl5PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl5PPr() {
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
    public CTTextParagraphProperties getLvl6PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl6PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setLvl6PPr(CTTextParagraphProperties lvl6PPr) {
        this.generatedSetterHelperImpl(lvl6PPr, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl6PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl6PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties getLvl7PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl7PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setLvl7PPr(CTTextParagraphProperties lvl7PPr) {
        this.generatedSetterHelperImpl(lvl7PPr, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl7PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl7PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties getLvl8PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl8PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setLvl8PPr(CTTextParagraphProperties lvl8PPr) {
        this.generatedSetterHelperImpl(lvl8PPr, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl8PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl8PPr() {
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
    public CTTextParagraphProperties getLvl9PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLvl9PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setLvl9PPr(CTTextParagraphProperties lvl9PPr) {
        this.generatedSetterHelperImpl(lvl9PPr, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewLvl9PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLvl9PPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], 0);
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
            target = (CTOfficeArtExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setExtLst(CTOfficeArtExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[10], 0, (short)1);
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
            target = (CTOfficeArtExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
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
            this.get_store().remove_element(PROPERTY_QNAME[10], 0);
        }
    }
}

