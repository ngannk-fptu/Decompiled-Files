/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.XMLEvent;

public interface EntityReference
extends XMLEvent {
    public EntityDeclaration getDeclaration();

    public String getName();
}

