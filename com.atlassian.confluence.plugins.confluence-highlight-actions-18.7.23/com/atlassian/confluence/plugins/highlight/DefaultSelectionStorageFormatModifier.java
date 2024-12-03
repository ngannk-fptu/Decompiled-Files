/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight;

import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.SelectionStorageFormatModifier;
import com.atlassian.confluence.plugins.highlight.model.TableModification;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.service.AppendToSelectionModifier;
import com.atlassian.confluence.plugins.highlight.service.MarkSelectionModifier;
import com.atlassian.confluence.plugins.highlight.service.StripTagModifier;
import com.atlassian.confluence.plugins.highlight.service.TableSelectionModifier;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.SAXException;

@ExportAsService(value={SelectionStorageFormatModifier.class})
@Component
public class DefaultSelectionStorageFormatModifier
implements SelectionStorageFormatModifier {
    private final TableSelectionModifier tableSelectionModifier;
    private final AppendToSelectionModifier appendToSelectionModifier;
    private final StripTagModifier stripTagModifier;
    private final MarkSelectionModifier markSelectionModifier;

    @Autowired
    public DefaultSelectionStorageFormatModifier(TableSelectionModifier tableSelectionModifier, AppendToSelectionModifier appendToSelectionModifier, StripTagModifier stripTagModifier, MarkSelectionModifier markSelectionModifier) {
        this.tableSelectionModifier = tableSelectionModifier;
        this.appendToSelectionModifier = appendToSelectionModifier;
        this.stripTagModifier = stripTagModifier;
        this.markSelectionModifier = markSelectionModifier;
    }

    @Override
    public boolean appendToSelection(long pageId, long lastFetchTime, TextSearch selection, XMLModification xmlInsertion) throws SAXException, SelectionModificationException {
        return this.appendToSelectionModifier.modify(pageId, lastFetchTime, selection, xmlInsertion);
    }

    @Override
    public boolean stripTags(long pageId, long lastFetchTime, NodeFilter nodeFilter) throws SAXException, SelectionModificationException {
        return this.stripTagModifier.modify(pageId, lastFetchTime, nodeFilter);
    }

    @Override
    public boolean appendToColumnTableCells(long pageId, long lastFetchTime, TextSearch selection, TableModification tableModification) throws SAXException, SelectionModificationException {
        return this.tableSelectionModifier.modify(pageId, lastFetchTime, selection, tableModification);
    }

    @Override
    public boolean markSelection(long pageId, long lastFetchTime, TextSearch selection, XMLModification xmlInsertion) throws SAXException, SelectionModificationException {
        return this.markSelectionModifier.modify(pageId, lastFetchTime, selection, xmlInsertion);
    }
}

