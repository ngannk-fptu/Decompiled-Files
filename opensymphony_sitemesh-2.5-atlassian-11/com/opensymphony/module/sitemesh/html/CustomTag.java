/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.tokenizer.Parser;
import com.opensymphony.module.sitemesh.html.util.StringSitemeshBuffer;
import java.io.StringWriter;
import java.util.Arrays;

public class CustomTag
implements Tag {
    private String[] attributes = new String[10];
    private int attributeCount = 0;
    private String name;
    private int type;

    public CustomTag(String name, int type) {
        this.setName(name);
        this.setType(type);
    }

    public CustomTag(Tag tag) {
        this.setName(tag.getName());
        this.setType(tag.getType());
        if (tag instanceof Parser.ReusableToken) {
            Parser.ReusableToken orig = (Parser.ReusableToken)tag;
            this.attributeCount = orig.attributeCount;
            this.attributes = new String[this.attributeCount];
            System.arraycopy(orig.attributes, 0, this.attributes, 0, this.attributeCount);
        } else if (tag instanceof CustomTag) {
            CustomTag orig = (CustomTag)tag;
            this.attributeCount = orig.attributeCount;
            this.attributes = new String[this.attributeCount];
            System.arraycopy(orig.attributes, 0, this.attributes, 0, this.attributeCount);
        } else {
            int c = tag.getAttributeCount();
            this.attributes = new String[c * 2];
            for (int i = 0; i < c; ++i) {
                this.attributes[this.attributeCount++] = tag.getAttributeName(i);
                this.attributes[this.attributeCount++] = tag.getAttributeValue(i);
            }
        }
    }

    public String getContents() {
        SitemeshBufferFragment.Builder buffer = SitemeshBufferFragment.builder().setBuffer(new DefaultSitemeshBuffer(new char[0]));
        this.writeTo(buffer, 0);
        return buffer.build().getStringContent();
    }

    public void writeTo(SitemeshBufferFragment.Builder buffer, int position) {
        StringWriter out = new StringWriter();
        if (this.type == 2) {
            out.append("</");
        } else {
            out.append('<');
        }
        out.append(this.name);
        int len = this.attributeCount;
        for (int i = 0; i < len; i += 2) {
            String name = this.attributes[i];
            String value = this.attributes[i + 1];
            if (value == null) {
                out.append(' ').append(name);
                continue;
            }
            out.append(' ').append(name).append("=\"").append(value).append("\"");
        }
        if (this.type == 3) {
            out.append("/>");
        } else {
            out.append('>');
        }
        buffer.insert(position, StringSitemeshBuffer.createBufferFragment(out.toString()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomTag)) {
            return false;
        }
        CustomTag customTag = (CustomTag)o;
        if (this.type != customTag.type) {
            return false;
        }
        if (this.attributes != null ? !Arrays.equals(this.attributes, customTag.attributes) : customTag.attributes != null) {
            return false;
        }
        return !(this.name != null ? !this.name.equals(customTag.name) : customTag.name != null);
    }

    public int hashCode() {
        int result = this.attributes != null ? this.attributes.hashCode() : 0;
        result = 29 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 29 * result + this.type;
        return result;
    }

    public String toString() {
        return this.getContents();
    }

    public int getAttributeCount() {
        return this.attributeCount / 2;
    }

    public int getAttributeIndex(String name, boolean caseSensitive) {
        if (this.attributes == null) {
            return -1;
        }
        int len = this.attributeCount;
        for (int i = 0; i < len; i += 2) {
            String current = this.attributes[i];
            if (!(caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current))) continue;
            return i / 2;
        }
        return -1;
    }

    public String getAttributeName(int index) {
        return this.attributes[index * 2];
    }

    public String getAttributeValue(int index) {
        return this.attributes[index * 2 + 1];
    }

    public String getAttributeValue(String name, boolean caseSensitive) {
        int attributeIndex = this.getAttributeIndex(name, caseSensitive);
        if (attributeIndex == -1) {
            return null;
        }
        return this.attributes[attributeIndex * 2 + 1];
    }

    public boolean hasAttribute(String name, boolean caseSensitive) {
        return this.getAttributeIndex(name, caseSensitive) > -1;
    }

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.type;
    }

    public void setName(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("CustomTag requires a name");
        }
        this.name = name;
    }

    public void setType(int type) {
        if (type != 1 && type != 2 && type != 3) {
            throw new IllegalArgumentException("CustomTag must be of type Tag.OPEN, Tag.CLOSE or Tag.EMPTY - was " + type);
        }
        this.type = type;
    }

    private void growAttributes() {
        int newSize = this.attributes.length == 0 ? 4 : this.attributes.length * 2;
        String[] newAttributes = new String[newSize];
        System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributes.length);
        this.attributes = newAttributes;
    }

    public int addAttribute(String name, String value) {
        if (this.attributeCount == this.attributes.length) {
            this.growAttributes();
        }
        this.attributes[this.attributeCount++] = name;
        this.attributes[this.attributeCount++] = value;
        return this.attributeCount / 2 - 1;
    }

    public void setAttributeValue(String name, boolean caseSensitive, String value) {
        int attributeIndex = this.getAttributeIndex(name, caseSensitive);
        if (attributeIndex == -1) {
            this.addAttribute(name, value);
        } else {
            this.attributes[attributeIndex * 2 + 1] = value;
        }
    }

    public void setAttributeName(int attributeIndex, String name) {
        this.attributes[attributeIndex * 2] = name;
    }

    public void setAttributeValue(int attributeIndex, String value) {
        this.attributes[attributeIndex * 2 + 1] = value;
    }

    public void removeAttribute(int attributeIndex) {
        if (attributeIndex > this.attributeCount / 2) {
            throw new ArrayIndexOutOfBoundsException("Cannot remove attribute at index " + attributeIndex + ", max index is " + this.attributeCount / 2);
        }
        String[] newAttributes = new String[this.attributes.length - 2];
        System.arraycopy(this.attributes, 0, newAttributes, 0, attributeIndex * 2);
        int next = attributeIndex * 2 + 2;
        System.arraycopy(this.attributes, next, newAttributes, attributeIndex * 2, this.attributes.length - next);
        this.attributeCount -= 2;
        this.attributes = newAttributes;
    }

    public void removeAttribute(String name, boolean caseSensitive) {
        int attributeIndex = this.getAttributeIndex(name, caseSensitive);
        if (attributeIndex == -1) {
            throw new IllegalArgumentException("Attribute " + name + " not found");
        }
        this.removeAttribute(attributeIndex);
    }

    public int getPosition() {
        return 0;
    }

    public int getLength() {
        return 0;
    }
}

