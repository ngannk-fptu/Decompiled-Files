/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml.controls;

import org.w3c.dom.Element;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.simple.xhtml.controls.AbstractControl;

public class TextControl
extends AbstractControl {
    public static final int DEFAULT_SIZE = 20;
    public static final int DEFAULT_ROWS = 3;
    private boolean _password;
    private boolean _readonly;
    private boolean _multiline;
    private int _size;
    private int _rows;
    private int _maxlength;

    public TextControl(XhtmlForm form, Element e) {
        super(form, e);
        boolean bl = this._readonly = e.getAttribute("readonly").length() > 0;
        if (e.getNodeName().equalsIgnoreCase("textarea")) {
            this._multiline = true;
            this._password = false;
            this._size = TextControl.getIntAttribute(e, "cols", 20);
            this._rows = TextControl.getIntAttribute(e, "rows", 3);
            this._maxlength = -1;
            this.setInitialValue(TextControl.collectText(e));
        } else {
            this._multiline = false;
            this._password = e.getAttribute("type").equalsIgnoreCase("password");
            this._size = TextControl.getIntAttribute(e, "size", 20);
            this._rows = 1;
            this._maxlength = TextControl.getIntAttribute(e, "maxlength", -1);
        }
    }

    public boolean isMultiLine() {
        return this._multiline;
    }

    public boolean isPassword() {
        return this._password;
    }

    public boolean isReadOnly() {
        return this._readonly;
    }

    public int getSize() {
        return this._size;
    }

    public int getRows() {
        return this._rows;
    }

    public int getMaxLength() {
        return this._maxlength;
    }
}

