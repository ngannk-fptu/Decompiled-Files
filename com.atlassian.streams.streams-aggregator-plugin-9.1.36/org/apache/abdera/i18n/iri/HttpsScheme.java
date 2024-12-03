/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import org.apache.abdera.i18n.iri.HttpScheme;

class HttpsScheme
extends HttpScheme {
    static final String NAME = "https";
    static final int DEFAULT_PORT = 443;

    public HttpsScheme() {
        super(NAME, 443);
    }
}

