/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import org.apache.abdera.i18n.iri.HttpScheme;

class FtpScheme
extends HttpScheme {
    static final String NAME = "ftp";
    static final int DEFAULT_PORT = 21;

    public FtpScheme() {
        super(NAME, 21);
    }
}

