/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xml;

import javax.xml.stream.XMLResolver;
import org.xml.sax.EntityResolver;

public interface XMLEntityResolver
extends EntityResolver,
XMLResolver {
    @Deprecated
    public String createDTD();
}

