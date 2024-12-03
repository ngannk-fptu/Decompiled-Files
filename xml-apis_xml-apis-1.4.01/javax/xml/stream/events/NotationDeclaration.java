/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import javax.xml.stream.events.XMLEvent;

public interface NotationDeclaration
extends XMLEvent {
    public String getName();

    public String getPublicId();

    public String getSystemId();
}

