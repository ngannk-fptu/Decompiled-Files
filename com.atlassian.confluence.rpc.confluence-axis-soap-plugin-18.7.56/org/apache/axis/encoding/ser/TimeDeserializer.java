/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.types.Time;

public class TimeDeserializer
extends SimpleDeserializer {
    public TimeDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    public Object makeValue(String source) {
        Time t = new Time(source);
        return t.getAsCalendar();
    }
}

