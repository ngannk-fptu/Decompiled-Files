/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Characters;

public abstract class AbstractCharactersEvent
extends AbstractXMLEvent
implements Characters {
    protected String data;

    public AbstractCharactersEvent(String data) {
        this.data = data;
    }

    public AbstractCharactersEvent(String data, Location location) {
        super(location);
        this.data = data;
    }

    public AbstractCharactersEvent(String data, Location location, QName schemaType) {
        super(location, schemaType);
        this.data = data;
    }

    public AbstractCharactersEvent(Characters that) {
        super(that);
        this.data = that.getData();
    }

    public String getData() {
        return this.data;
    }

    public boolean isCharacters() {
        return true;
    }

    public boolean isWhiteSpace() {
        String data = this.getData();
        int s = data.length();
        block3: for (int i = 0; i < s; ++i) {
            switch (data.charAt(i)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    continue block3;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }
}

