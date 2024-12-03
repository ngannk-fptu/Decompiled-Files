/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import javax.xml.stream.events.XMLEvent;

public interface EntityDeclaration
extends XMLEvent {
    public String getPublicId();

    public String getSystemId();

    public String getName();

    public String getNotationName();

    public String getReplacementText();

    public String getBaseURI();
}

