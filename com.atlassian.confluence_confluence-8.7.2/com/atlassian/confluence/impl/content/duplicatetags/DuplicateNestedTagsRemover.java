/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.duplicatetags;

import javax.xml.stream.XMLStreamException;

public interface DuplicateNestedTagsRemover {
    public String cleanQuietly(String var1);

    public String clean(String var1) throws XMLStreamException;
}

