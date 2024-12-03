/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import javax.xml.stream.Location;

public interface XMLStreamLocation2
extends Location {
    public static final XMLStreamLocation2 NOT_AVAILABLE = new XMLStreamLocation2(){

        @Override
        public XMLStreamLocation2 getContext() {
            return null;
        }

        @Override
        public int getCharacterOffset() {
            return -1;
        }

        @Override
        public int getColumnNumber() {
            return -1;
        }

        @Override
        public int getLineNumber() {
            return -1;
        }

        @Override
        public String getPublicId() {
            return null;
        }

        @Override
        public String getSystemId() {
            return null;
        }
    };

    public XMLStreamLocation2 getContext();
}

