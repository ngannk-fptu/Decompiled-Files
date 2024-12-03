/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.ext;

import org.xml.sax.Attributes;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.helpers.AttributesImpl;

public class Attributes2Impl
extends AttributesImpl
implements Attributes2 {
    private boolean[] declared;
    private boolean[] specified;

    public Attributes2Impl() {
    }

    public Attributes2Impl(Attributes attributes) {
        super(attributes);
    }

    public boolean isDeclared(int n) {
        if (n < 0 || n >= this.getLength()) {
            throw new ArrayIndexOutOfBoundsException("No attribute at index: " + n);
        }
        return this.declared[n];
    }

    public boolean isDeclared(String string, String string2) {
        int n = this.getIndex(string, string2);
        if (n < 0) {
            throw new IllegalArgumentException("No such attribute: local=" + string2 + ", namespace=" + string);
        }
        return this.declared[n];
    }

    public boolean isDeclared(String string) {
        int n = this.getIndex(string);
        if (n < 0) {
            throw new IllegalArgumentException("No such attribute: " + string);
        }
        return this.declared[n];
    }

    public boolean isSpecified(int n) {
        if (n < 0 || n >= this.getLength()) {
            throw new ArrayIndexOutOfBoundsException("No attribute at index: " + n);
        }
        return this.specified[n];
    }

    public boolean isSpecified(String string, String string2) {
        int n = this.getIndex(string, string2);
        if (n < 0) {
            throw new IllegalArgumentException("No such attribute: local=" + string2 + ", namespace=" + string);
        }
        return this.specified[n];
    }

    public boolean isSpecified(String string) {
        int n = this.getIndex(string);
        if (n < 0) {
            throw new IllegalArgumentException("No such attribute: " + string);
        }
        return this.specified[n];
    }

    public void setAttributes(Attributes attributes) {
        int n = attributes.getLength();
        super.setAttributes(attributes);
        this.declared = new boolean[n];
        this.specified = new boolean[n];
        if (attributes instanceof Attributes2) {
            Attributes2 attributes2 = (Attributes2)attributes;
            int n2 = 0;
            while (n2 < n) {
                this.declared[n2] = attributes2.isDeclared(n2);
                this.specified[n2] = attributes2.isSpecified(n2);
                ++n2;
            }
        } else {
            int n3 = 0;
            while (n3 < n) {
                this.declared[n3] = !"CDATA".equals(attributes.getType(n3));
                this.specified[n3] = true;
                ++n3;
            }
        }
    }

    public void addAttribute(String string, String string2, String string3, String string4, String string5) {
        super.addAttribute(string, string2, string3, string4, string5);
        int n = this.getLength();
        if (n < this.specified.length) {
            boolean[] blArray = new boolean[n];
            System.arraycopy(this.declared, 0, blArray, 0, this.declared.length);
            this.declared = blArray;
            blArray = new boolean[n];
            System.arraycopy(this.specified, 0, blArray, 0, this.specified.length);
            this.specified = blArray;
        }
        this.specified[n - 1] = true;
        this.declared[n - 1] = !"CDATA".equals(string4);
    }

    public void removeAttribute(int n) {
        int n2 = this.getLength() - 1;
        super.removeAttribute(n);
        if (n != n2) {
            System.arraycopy(this.declared, n + 1, this.declared, n, n2 - n);
            System.arraycopy(this.specified, n + 1, this.specified, n, n2 - n);
        }
    }

    public void setDeclared(int n, boolean bl) {
        if (n < 0 || n >= this.getLength()) {
            throw new ArrayIndexOutOfBoundsException("No attribute at index: " + n);
        }
        this.declared[n] = bl;
    }

    public void setSpecified(int n, boolean bl) {
        if (n < 0 || n >= this.getLength()) {
            throw new ArrayIndexOutOfBoundsException("No attribute at index: " + n);
        }
        this.specified[n] = bl;
    }
}

