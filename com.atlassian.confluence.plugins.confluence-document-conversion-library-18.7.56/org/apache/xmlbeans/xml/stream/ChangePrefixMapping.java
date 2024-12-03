/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.xml.stream;

import org.apache.xmlbeans.xml.stream.XMLEvent;

public interface ChangePrefixMapping
extends XMLEvent {
    public String getOldNamespaceUri();

    public String getNewNamespaceUri();

    public String getPrefix();
}

