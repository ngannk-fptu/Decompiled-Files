/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight;

import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.model.TableModification;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.SAXException;

public interface SelectionStorageFormatModifier {
    public boolean appendToSelection(long var1, long var3, TextSearch var5, XMLModification var6) throws SAXException, SelectionModificationException;

    public boolean stripTags(long var1, long var3, NodeFilter var5) throws SAXException, SelectionModificationException;

    public boolean appendToColumnTableCells(long var1, long var3, TextSearch var5, TableModification var6) throws SAXException, SelectionModificationException;

    public boolean markSelection(long var1, long var3, TextSearch var5, XMLModification var6) throws SAXException, SelectionModificationException;
}

