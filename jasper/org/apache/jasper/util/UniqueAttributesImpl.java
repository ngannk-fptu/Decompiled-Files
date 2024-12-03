/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.util;

import java.util.HashSet;
import java.util.Set;
import org.apache.jasper.compiler.Localizer;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class UniqueAttributesImpl
extends AttributesImpl {
    private static final String IMPORT = "import";
    private static final String PAGE_ENCODING = "pageEncoding";
    private final boolean pageDirective;
    private final Set<String> qNames = new HashSet<String>();

    public UniqueAttributesImpl() {
        this.pageDirective = false;
    }

    public UniqueAttributesImpl(boolean pageDirective) {
        this.pageDirective = pageDirective;
    }

    @Override
    public void clear() {
        this.qNames.clear();
        super.clear();
    }

    @Override
    public void setAttributes(Attributes atts) {
        for (int i = 0; i < atts.getLength(); ++i) {
            if (this.qNames.add(atts.getQName(i))) continue;
            this.handleDuplicate(atts.getQName(i), atts.getValue(i));
        }
        super.setAttributes(atts);
    }

    @Override
    public void addAttribute(String uri, String localName, String qName, String type, String value) {
        if (this.qNames.add(qName)) {
            super.addAttribute(uri, localName, qName, type, value);
        } else {
            this.handleDuplicate(qName, value);
        }
    }

    @Override
    public void setAttribute(int index, String uri, String localName, String qName, String type, String value) {
        this.qNames.remove(super.getQName(index));
        if (this.qNames.add(qName)) {
            super.setAttribute(index, uri, localName, qName, type, value);
        } else {
            this.handleDuplicate(qName, value);
        }
    }

    @Override
    public void removeAttribute(int index) {
        this.qNames.remove(super.getQName(index));
        super.removeAttribute(index);
    }

    @Override
    public void setQName(int index, String qName) {
        this.qNames.remove(super.getQName(index));
        super.setQName(index, qName);
    }

    private void handleDuplicate(String qName, String value) {
        if (this.pageDirective) {
            String v;
            if (IMPORT.equalsIgnoreCase(qName)) {
                int i = super.getIndex(IMPORT);
                String v2 = super.getValue(i);
                super.setValue(i, v2 + "," + value);
                return;
            }
            if (!PAGE_ENCODING.equalsIgnoreCase(qName) && (v = super.getValue(qName)).equals(value)) {
                return;
            }
        }
        throw new IllegalArgumentException(Localizer.getMessage("jsp.error.duplicateqname", qName));
    }
}

