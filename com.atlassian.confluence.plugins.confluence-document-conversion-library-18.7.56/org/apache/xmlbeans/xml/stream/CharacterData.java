/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.xml.stream;

import org.apache.xmlbeans.xml.stream.XMLEvent;

public interface CharacterData
extends XMLEvent {
    public String getContent();

    public boolean hasContent();
}

