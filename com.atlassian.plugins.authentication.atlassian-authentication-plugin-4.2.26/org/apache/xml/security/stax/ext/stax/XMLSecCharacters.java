/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import javax.xml.stream.events.Characters;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface XMLSecCharacters
extends XMLSecEvent,
Characters {
    @Override
    public XMLSecCharacters asCharacters();

    public char[] getText();
}

