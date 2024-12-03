/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import org.apache.abdera.i18n.iri.AbstractScheme;

public class DefaultScheme
extends AbstractScheme {
    public DefaultScheme(String name) {
        super(name, -1);
    }

    public DefaultScheme(String name, int port) {
        super(name, port);
    }
}

