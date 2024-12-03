/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.dom;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode;
import com.benryan.webwork.WordImportInfo;
import com.benryan.webwork.util.PageNames;
import java.util.ArrayList;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportTitleResolver {
    private static final Logger log = LoggerFactory.getLogger(ImportTitleResolver.class);
    PageManager _pageManager;
    String _spaceKey;
    int _conflictMode;
    int _splitLvl;
    HashSet<String> _internalNames;
    ArrayList<Page> _deleteMe;

    public ImportTitleResolver(WordImportInfo info, @ComponentImport PageManager pageManager, String spaceKey) {
        this._pageManager = pageManager;
        this._spaceKey = spaceKey;
        this._conflictMode = info.getConflict();
        this._splitLvl = info.getLvl();
        this._internalNames = new HashSet();
        this._deleteMe = new ArrayList();
    }

    public static String getUniquePageName(String text, HashSet names, @ComponentImport PageManager pageManager, String spaceKey) {
        Object alternateText = text;
        int i = 0;
        while (names.contains(alternateText) || pageManager.getPage(spaceKey, (String)alternateText) != null) {
            alternateText = text + i++;
        }
        return alternateText;
    }

    public void resolveTitle(DocumentTreeNode<Page> node, boolean isRoot) {
        if (node.getLvl() > this._splitLvl) {
            return;
        }
        Object text = PageNames.fixPageTitle(node.getText());
        if (this._internalNames.contains(node.getText())) {
            int i = 0;
            String newTitle = (String)text + i;
            while (this._internalNames.contains(newTitle)) {
                newTitle = (String)text + i;
                ++i;
            }
            text = newTitle;
        }
        node.setText((String)text);
        Page existingPage = this._pageManager.getPage(this._spaceKey, (String)text);
        if (existingPage != null) {
            if (!isRoot || node.getOldPage() == null) {
                switch (this._conflictMode) {
                    case 2: {
                        this._deleteMe.add(existingPage);
                        break;
                    }
                    case 1: {
                        text = ImportTitleResolver.getUniquePageName((String)text, this._internalNames, this._pageManager, this._spaceKey);
                        node.setText((String)text);
                        break;
                    }
                    case 0: {
                        node.setOldPage((Object)existingPage);
                        break;
                    }
                    default: {
                        log.error("Unhandled conflict mode {}", (Object)this._conflictMode);
                        break;
                    }
                }
            } else if (existingPage.getId() != ((Page)node.getOldPage()).getId()) {
                text = ImportTitleResolver.getUniquePageName((String)text, this._internalNames, this._pageManager, this._spaceKey);
                node.setText((String)text);
            }
        }
        this._internalNames.add((String)text);
    }

    public void doDeletes() {
        this._deleteMe.forEach(Page::trash);
    }
}

