/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyContent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyPr;

public class CTRubyImpl
extends XmlComplexContentImpl
implements CTRuby {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rubyPr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rt"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rubyBase")};

    public CTRubyImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRubyPr getRubyPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRubyPr target = null;
            target = (CTRubyPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setRubyPr(CTRubyPr rubyPr) {
        this.generatedSetterHelperImpl(rubyPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRubyPr addNewRubyPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRubyPr target = null;
            target = (CTRubyPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRubyContent getRt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRubyContent target = null;
            target = (CTRubyContent)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setRt(CTRubyContent rt) {
        this.generatedSetterHelperImpl(rt, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRubyContent addNewRt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRubyContent target = null;
            target = (CTRubyContent)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRubyContent getRubyBase() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRubyContent target = null;
            target = (CTRubyContent)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setRubyBase(CTRubyContent rubyBase) {
        this.generatedSetterHelperImpl(rubyBase, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRubyContent addNewRubyBase() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRubyContent target = null;
            target = (CTRubyContent)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }
}

