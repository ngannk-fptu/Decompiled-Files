/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import org.xml.sax.Attributes;

public class AttributesImpl
implements Attributes {
    int length;
    String[] data;

    public AttributesImpl() {
        this.length = 0;
        this.data = null;
    }

    public AttributesImpl(Attributes attributes) {
        this.setAttributes(attributes);
    }

    public int getLength() {
        return this.length;
    }

    public String getURI(int n) {
        if (n >= 0 && n < this.length) {
            return this.data[n * 5];
        }
        return null;
    }

    public String getLocalName(int n) {
        if (n >= 0 && n < this.length) {
            return this.data[n * 5 + 1];
        }
        return null;
    }

    public String getQName(int n) {
        if (n >= 0 && n < this.length) {
            return this.data[n * 5 + 2];
        }
        return null;
    }

    public String getType(int n) {
        if (n >= 0 && n < this.length) {
            return this.data[n * 5 + 3];
        }
        return null;
    }

    public String getValue(int n) {
        if (n >= 0 && n < this.length) {
            return this.data[n * 5 + 4];
        }
        return null;
    }

    public int getIndex(String string, String string2) {
        int n = this.length * 5;
        int n2 = 0;
        while (n2 < n) {
            if (this.data[n2].equals(string) && this.data[n2 + 1].equals(string2)) {
                return n2 / 5;
            }
            n2 += 5;
        }
        return -1;
    }

    public int getIndex(String string) {
        int n = this.length * 5;
        int n2 = 0;
        while (n2 < n) {
            if (this.data[n2 + 2].equals(string)) {
                return n2 / 5;
            }
            n2 += 5;
        }
        return -1;
    }

    public String getType(String string, String string2) {
        int n = this.length * 5;
        int n2 = 0;
        while (n2 < n) {
            if (this.data[n2].equals(string) && this.data[n2 + 1].equals(string2)) {
                return this.data[n2 + 3];
            }
            n2 += 5;
        }
        return null;
    }

    public String getType(String string) {
        int n = this.length * 5;
        int n2 = 0;
        while (n2 < n) {
            if (this.data[n2 + 2].equals(string)) {
                return this.data[n2 + 3];
            }
            n2 += 5;
        }
        return null;
    }

    public String getValue(String string, String string2) {
        int n = this.length * 5;
        int n2 = 0;
        while (n2 < n) {
            if (this.data[n2].equals(string) && this.data[n2 + 1].equals(string2)) {
                return this.data[n2 + 4];
            }
            n2 += 5;
        }
        return null;
    }

    public String getValue(String string) {
        int n = this.length * 5;
        int n2 = 0;
        while (n2 < n) {
            if (this.data[n2 + 2].equals(string)) {
                return this.data[n2 + 4];
            }
            n2 += 5;
        }
        return null;
    }

    public void clear() {
        if (this.data != null) {
            int n = 0;
            while (n < this.length * 5) {
                this.data[n] = null;
                ++n;
            }
        }
        this.length = 0;
    }

    public void setAttributes(Attributes attributes) {
        this.clear();
        this.length = attributes.getLength();
        if (this.length > 0) {
            this.data = new String[this.length * 5];
            int n = 0;
            while (n < this.length) {
                this.data[n * 5] = attributes.getURI(n);
                this.data[n * 5 + 1] = attributes.getLocalName(n);
                this.data[n * 5 + 2] = attributes.getQName(n);
                this.data[n * 5 + 3] = attributes.getType(n);
                this.data[n * 5 + 4] = attributes.getValue(n);
                ++n;
            }
        }
    }

    public void addAttribute(String string, String string2, String string3, String string4, String string5) {
        this.ensureCapacity(this.length + 1);
        this.data[this.length * 5] = string;
        this.data[this.length * 5 + 1] = string2;
        this.data[this.length * 5 + 2] = string3;
        this.data[this.length * 5 + 3] = string4;
        this.data[this.length * 5 + 4] = string5;
        ++this.length;
    }

    public void setAttribute(int n, String string, String string2, String string3, String string4, String string5) {
        if (n >= 0 && n < this.length) {
            this.data[n * 5] = string;
            this.data[n * 5 + 1] = string2;
            this.data[n * 5 + 2] = string3;
            this.data[n * 5 + 3] = string4;
            this.data[n * 5 + 4] = string5;
        } else {
            this.badIndex(n);
        }
    }

    public void removeAttribute(int n) {
        if (n >= 0 && n < this.length) {
            if (n < this.length - 1) {
                System.arraycopy(this.data, (n + 1) * 5, this.data, n * 5, (this.length - n - 1) * 5);
            }
            n = (this.length - 1) * 5;
            this.data[n++] = null;
            this.data[n++] = null;
            this.data[n++] = null;
            this.data[n++] = null;
            this.data[n] = null;
            --this.length;
        } else {
            this.badIndex(n);
        }
    }

    public void setURI(int n, String string) {
        if (n >= 0 && n < this.length) {
            this.data[n * 5] = string;
        } else {
            this.badIndex(n);
        }
    }

    public void setLocalName(int n, String string) {
        if (n >= 0 && n < this.length) {
            this.data[n * 5 + 1] = string;
        } else {
            this.badIndex(n);
        }
    }

    public void setQName(int n, String string) {
        if (n >= 0 && n < this.length) {
            this.data[n * 5 + 2] = string;
        } else {
            this.badIndex(n);
        }
    }

    public void setType(int n, String string) {
        if (n >= 0 && n < this.length) {
            this.data[n * 5 + 3] = string;
        } else {
            this.badIndex(n);
        }
    }

    public void setValue(int n, String string) {
        if (n >= 0 && n < this.length) {
            this.data[n * 5 + 4] = string;
        } else {
            this.badIndex(n);
        }
    }

    private void ensureCapacity(int n) {
        int n2;
        if (n <= 0) {
            return;
        }
        if (this.data == null || this.data.length == 0) {
            n2 = 25;
        } else {
            if (this.data.length >= n * 5) {
                return;
            }
            n2 = this.data.length;
        }
        while (n2 < n * 5) {
            n2 *= 2;
        }
        String[] stringArray = new String[n2];
        if (this.length > 0) {
            System.arraycopy(this.data, 0, stringArray, 0, this.length * 5);
        }
        this.data = stringArray;
    }

    private void badIndex(int n) throws ArrayIndexOutOfBoundsException {
        String string = "Attempt to modify attribute at illegal index: " + n;
        throw new ArrayIndexOutOfBoundsException(string);
    }
}

