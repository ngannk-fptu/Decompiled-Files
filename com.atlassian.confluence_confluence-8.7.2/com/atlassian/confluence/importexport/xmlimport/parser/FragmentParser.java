/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import org.xml.sax.ContentHandler;

@Deprecated
public interface FragmentParser<T>
extends ContentHandler {
    public boolean isDone();

    public T build();
}

