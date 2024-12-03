/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Target;
import org.xml.sax.SAXException;

public class DeserializerTarget
implements Target {
    public Deserializer target;
    public Object hint;

    public DeserializerTarget(Deserializer target, Object hint) {
        this.target = target;
        this.hint = hint;
    }

    public void set(Object value) throws SAXException {
        if (this.hint != null) {
            this.target.setChildValue(value, this.hint);
        } else {
            this.target.setValue(value);
        }
    }
}

