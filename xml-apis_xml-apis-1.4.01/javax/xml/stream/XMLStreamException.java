/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import javax.xml.stream.Location;

public class XMLStreamException
extends Exception {
    private static final long serialVersionUID = 2018819321811497362L;
    protected Throwable nested;
    protected Location location;

    public XMLStreamException() {
    }

    public XMLStreamException(String string) {
        super(string);
    }

    public XMLStreamException(Throwable throwable) {
        this.nested = throwable;
    }

    public XMLStreamException(String string, Throwable throwable) {
        super(string);
        this.nested = throwable;
    }

    public XMLStreamException(String string, Location location, Throwable throwable) {
        super(string);
        this.location = location;
        this.nested = throwable;
    }

    public XMLStreamException(String string, Location location) {
        super(string);
        this.location = location;
    }

    public Throwable getNestedException() {
        return this.nested;
    }

    public Location getLocation() {
        return this.location;
    }
}

