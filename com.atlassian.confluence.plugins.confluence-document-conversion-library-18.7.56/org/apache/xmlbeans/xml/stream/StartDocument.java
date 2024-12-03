/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.xml.stream;

import org.apache.xmlbeans.xml.stream.XMLEvent;

public interface StartDocument
extends XMLEvent {
    public String getSystemId();

    public String getCharacterEncodingScheme();

    public boolean isStandalone();

    public String getVersion();
}

