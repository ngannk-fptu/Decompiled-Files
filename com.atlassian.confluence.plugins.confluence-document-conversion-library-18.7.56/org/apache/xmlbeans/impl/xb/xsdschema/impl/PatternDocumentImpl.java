/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.PatternDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.NoFixedFacetImpl;

public class PatternDocumentImpl
extends XmlComplexContentImpl
implements PatternDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "pattern")};

    public PatternDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PatternDocument.Pattern getPattern() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PatternDocument.Pattern target = null;
            target = (PatternDocument.Pattern)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setPattern(PatternDocument.Pattern pattern) {
        this.generatedSetterHelperImpl(pattern, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PatternDocument.Pattern addNewPattern() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PatternDocument.Pattern target = null;
            target = (PatternDocument.Pattern)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class PatternImpl
    extends NoFixedFacetImpl
    implements PatternDocument.Pattern {
        private static final long serialVersionUID = 1L;

        public PatternImpl(SchemaType sType) {
            super(sType);
        }
    }
}

