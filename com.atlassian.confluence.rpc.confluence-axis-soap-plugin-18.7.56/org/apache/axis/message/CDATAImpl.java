/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import org.apache.axis.message.Text;
import org.w3c.dom.CDATASection;

public class CDATAImpl
extends Text
implements CDATASection {
    static final String cdataUC = "<![CDATA[";
    static final String cdataLC = "<![cdata[";

    public CDATAImpl(String text) {
        super(text);
    }

    public boolean isComment() {
        return false;
    }
}

