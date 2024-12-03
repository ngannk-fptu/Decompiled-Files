/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.Target;
import org.xml.sax.SAXException;

public class CallbackTarget
implements Target {
    public Callback target;
    public Object hint;

    public CallbackTarget(Callback target, Object hint) {
        this.target = target;
        this.hint = hint;
    }

    public void set(Object value) throws SAXException {
        this.target.setValue(value, this.hint);
    }
}

