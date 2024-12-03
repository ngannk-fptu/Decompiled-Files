/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.Content;
import org.jdom2.IllegalDataException;
import org.jdom2.Parent;
import org.jdom2.Text;
import org.jdom2.Verifier;

public class CDATA
extends Text {
    private static final long serialVersionUID = 200L;

    protected CDATA() {
        super(Content.CType.CDATA);
    }

    public CDATA(String string) {
        super(Content.CType.CDATA);
        this.setText(string);
    }

    public CDATA setText(String str) {
        if (str == null || "".equals(str)) {
            this.value = "";
            return this;
        }
        String reason = Verifier.checkCDATASection(str);
        if (reason != null) {
            throw new IllegalDataException(str, "CDATA section", reason);
        }
        this.value = str;
        return this;
    }

    public void append(String str) {
        if (str == null || "".equals(str)) {
            return;
        }
        String tmpValue = this.value == "" ? str : this.value + str;
        String reason = Verifier.checkCDATASection(tmpValue);
        if (reason != null) {
            throw new IllegalDataException(str, "CDATA section", reason);
        }
        this.value = tmpValue;
    }

    public void append(Text text) {
        if (text == null) {
            return;
        }
        this.append(text.getText());
    }

    public String toString() {
        return new StringBuilder(64).append("[CDATA: ").append(this.getText()).append("]").toString();
    }

    public CDATA clone() {
        return (CDATA)super.clone();
    }

    public CDATA detach() {
        return (CDATA)super.detach();
    }

    protected CDATA setParent(Parent parent) {
        return (CDATA)super.setParent(parent);
    }
}

