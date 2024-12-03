/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.messaging;

import javax.xml.messaging.Endpoint;

public class URLEndpoint
extends Endpoint {
    public URLEndpoint(String url) {
        super(url);
    }

    public String getURL() {
        return this.id;
    }
}

