/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.xml;

import java.io.IOException;
import java.io.StringReader;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EmptyStringEntityResolver
implements EntityResolver {
    public static final EmptyStringEntityResolver INSTANCE = new EmptyStringEntityResolver();

    @Override
    public InputSource resolveEntity(@Nullable String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(new StringReader(""));
    }
}

