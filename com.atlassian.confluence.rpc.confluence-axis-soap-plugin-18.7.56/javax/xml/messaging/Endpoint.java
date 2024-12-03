/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.messaging;

public class Endpoint {
    protected String id;

    public Endpoint(String uri) {
        this.id = uri;
    }

    public String toString() {
        return this.id;
    }
}

