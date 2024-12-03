/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.io.StringReader;
import org.springframework.ldap.core.DnParser;
import org.springframework.ldap.core.DnParserImpl;

public final class DefaultDnParserFactory {
    private DefaultDnParserFactory() {
    }

    public static DnParser createDnParser(String string) {
        return new DnParserImpl(new StringReader(string));
    }
}

