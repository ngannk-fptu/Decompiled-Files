/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.Parent;
import org.jdom2.Verifier;
import org.jdom2.output.Format;

public class Text
extends Content {
    private static final long serialVersionUID = 200L;
    static final String EMPTY_STRING = "";
    protected String value;

    protected Text(Content.CType ctype) {
        super(ctype);
    }

    protected Text() {
        this(Content.CType.Text);
    }

    public Text(String str) {
        this(Content.CType.Text);
        this.setText(str);
    }

    public String getText() {
        return this.value;
    }

    public String getTextTrim() {
        return Format.trimBoth(this.getText());
    }

    public String getTextNormalize() {
        return Text.normalizeString(this.getText());
    }

    public static String normalizeString(String str) {
        if (str == null) {
            return EMPTY_STRING;
        }
        return Format.compact(str);
    }

    public Text setText(String str) {
        if (str == null) {
            this.value = EMPTY_STRING;
            return this;
        }
        String reason = Verifier.checkCharacterData(str);
        if (reason != null) {
            throw new IllegalDataException(str, "character content", reason);
        }
        this.value = str;
        return this;
    }

    public void append(String str) {
        if (str == null) {
            return;
        }
        String reason = Verifier.checkCharacterData(str);
        if (reason != null) {
            throw new IllegalDataException(str, "character content", reason);
        }
        if (str.length() > 0) {
            this.value = this.value + str;
        }
    }

    public void append(Text text) {
        if (text == null) {
            return;
        }
        this.value = this.value + text.getText();
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return new StringBuilder(64).append("[Text: ").append(this.getText()).append("]").toString();
    }

    public Text clone() {
        Text text = (Text)super.clone();
        text.value = this.value;
        return text;
    }

    public Text detach() {
        return (Text)super.detach();
    }

    protected Text setParent(Parent parent) {
        return (Text)super.setParent(parent);
    }

    public Element getParent() {
        return (Element)super.getParent();
    }
}

