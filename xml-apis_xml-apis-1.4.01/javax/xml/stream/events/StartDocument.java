/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import javax.xml.stream.events.XMLEvent;

public interface StartDocument
extends XMLEvent {
    public boolean encodingSet();

    public String getCharacterEncodingScheme();

    public String getSystemId();

    public String getVersion();

    public boolean isStandalone();

    public boolean standaloneSet();
}

