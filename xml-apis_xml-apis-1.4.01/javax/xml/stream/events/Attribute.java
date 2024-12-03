/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

public interface Attribute
extends XMLEvent {
    public String getDTDType();

    public QName getName();

    public String getValue();

    public boolean isSpecified();
}

