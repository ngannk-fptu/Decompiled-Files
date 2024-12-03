/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.Attr;
import org.w3c.tidy.AttrCheckImpl;
import org.w3c.tidy.Attribute;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.DOMAttrImpl;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyUtils;

public class AttVal
implements Cloneable {
    protected AttVal next;
    protected Attribute dict;
    protected Node asp;
    protected Node php;
    protected int delim;
    protected String attribute;
    protected String value;
    protected Attr adapter;

    public AttVal() {
    }

    public AttVal(AttVal next, Attribute dict, int delim, String attribute, String value) {
        this.next = next;
        this.dict = dict;
        this.delim = delim;
        this.attribute = attribute;
        this.value = value;
    }

    public AttVal(AttVal next, Attribute dict, Node asp, Node php, int delim, String attribute, String value) {
        this.next = next;
        this.dict = dict;
        this.asp = asp;
        this.php = php;
        this.delim = delim;
        this.attribute = attribute;
        this.value = value;
    }

    protected Object clone() {
        AttVal av = null;
        try {
            av = (AttVal)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        if (this.next != null) {
            av.next = (AttVal)this.next.clone();
        }
        if (this.asp != null) {
            av.asp = (Node)this.asp.clone();
        }
        if (this.php != null) {
            av.php = (Node)this.php.clone();
        }
        return av;
    }

    public boolean isBoolAttribute() {
        Attribute attr = this.dict;
        return attr != null && attr.getAttrchk() == AttrCheckImpl.BOOL;
    }

    void checkLowerCaseAttrValue(Lexer lexer, Node node) {
        if (this.value == null) {
            return;
        }
        String lowercase = this.value.toLowerCase();
        if (!this.value.equals(lowercase)) {
            if (lexer.isvoyager) {
                lexer.report.attrError(lexer, node, this, (short)70);
            }
            if (lexer.isvoyager || lexer.configuration.lowerLiterals) {
                this.value = lowercase;
            }
        }
    }

    public Attribute checkAttribute(Lexer lexer, Node node) {
        TagTable tt = lexer.configuration.tt;
        Attribute attr = this.dict;
        if (attr != null) {
            if (TidyUtils.toBoolean(attr.getVersions() & 0x20)) {
                if (!lexer.configuration.xmlTags && !lexer.configuration.xmlOut) {
                    lexer.report.attrError(lexer, node, this, (short)57);
                }
            } else if (attr != AttributeTable.attrTitle || node.tag != tt.tagA && node.tag != tt.tagLink) {
                lexer.constrainVersion(attr.getVersions());
            }
            if (attr.getAttrchk() != null) {
                attr.getAttrchk().check(lexer, node, this);
            } else if (TidyUtils.toBoolean(this.dict.getVersions() & 0x1C0)) {
                lexer.report.attrError(lexer, node, this, (short)53);
            }
        } else if (!(lexer.configuration.xmlTags || node.tag == null || this.asp != null || node.tag != null && TidyUtils.toBoolean(node.tag.versions & 0x1C0))) {
            lexer.report.attrError(lexer, node, this, (short)48);
        }
        return attr;
    }

    protected Attr getAdapter() {
        if (this.adapter == null) {
            this.adapter = new DOMAttrImpl(this);
        }
        return this.adapter;
    }

    public Node getAsp() {
        return this.asp;
    }

    public void setAsp(Node asp) {
        this.asp = asp;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public int getDelim() {
        return this.delim;
    }

    public void setDelim(int delim) {
        this.delim = delim;
    }

    public Attribute getDict() {
        return this.dict;
    }

    public void setDict(Attribute dict) {
        this.dict = dict;
    }

    public AttVal getNext() {
        return this.next;
    }

    public void setNext(AttVal next) {
        this.next = next;
    }

    public Node getPhp() {
        return this.php;
    }

    public void setPhp(Node php) {
        this.php = php;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

