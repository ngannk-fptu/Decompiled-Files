/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import javax.xml.stream.Location;

public interface XMLStreamLocation2
extends Location {
    public static final XMLStreamLocation2 NOT_AVAILABLE = new XMLStreamLocation2(){

        public XMLStreamLocation2 getContext() {
            return null;
        }

        public int getCharacterOffset() {
            return -1;
        }

        public int getColumnNumber() {
            return -1;
        }

        public int getLineNumber() {
            return -1;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return null;
        }
    };

    public XMLStreamLocation2 getContext();
}

