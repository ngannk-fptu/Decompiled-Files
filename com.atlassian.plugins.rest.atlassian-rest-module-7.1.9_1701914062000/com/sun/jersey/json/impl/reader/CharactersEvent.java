/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.json.impl.reader.JsonXmlEvent;
import javax.xml.stream.Location;

public class CharactersEvent
extends JsonXmlEvent {
    public CharactersEvent(String text, Location location) {
        super(4, location);
        this.setText(text);
    }

    public String toString() {
        return "CharactersEvent(" + this.getText() + ")";
    }
}

