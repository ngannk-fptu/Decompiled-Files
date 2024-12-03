/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import javax.xml.stream.events.Attribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

public interface XMLSecAttribute
extends XMLSecEvent,
Attribute,
Comparable<XMLSecAttribute> {
    public XMLSecNamespace getAttributeNamespace();
}

