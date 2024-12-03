/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Document
 *  com.aspose.words.DocumentVisitor
 */
package com.atlassian.plugins.conversion.confluence.importing;

import com.aspose.words.Document;
import com.aspose.words.DocumentVisitor;
import com.atlassian.plugins.conversion.AsposeAware;
import com.atlassian.plugins.conversion.confluence.dom.ImportContext;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.Doc2Wiki;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocSplitter;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeBuilder;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.SplitImportContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WordImport
extends AsposeAware {
    public static <DocumentType> NormalizationResult<DocumentType> parseAndNormalize(InputStream inputStream, String rootPageTitle) throws Exception {
        DocumentTreeNode rootNode = new DocumentTreeNode(rootPageTitle, 0);
        DocumentTreeBuilder builder = new DocumentTreeBuilder(rootNode);
        Document doc = new Document(inputStream);
        doc.accept(builder);
        NormalizationResult result = new NormalizationResult();
        WordImport.normalizeDocTree(rootNode, result);
        result.bookmarks = builder.getBookmarks();
        result.treeRoot = rootNode;
        return result;
    }

    private static <DocumentType> void normalizeDocTree(DocumentTreeNode<DocumentType> root, NormalizationResult<DocumentType> result) {
        result.treeDepth = Math.max(root.getLvl(), result.treeDepth);
        int lvl = root.getLvl() + 1;
        for (DocumentTreeNode<DocumentType> node : root.getChildren()) {
            node.setLvl(lvl);
            result.orderedNodes.add(node);
            WordImport.normalizeDocTree(node, result);
        }
    }

    public static void doImport(InputStream is, ImportContext importContext, boolean doFootnotes) throws Exception {
        Document doc = new Document(is);
        Doc2Wiki<ImportContext> doc2Wiki = new Doc2Wiki<ImportContext>(importContext, doFootnotes);
        doc.accept(doc2Wiki);
    }

    public static void doImportSplit(InputStream is, SplitImportContext importContext, boolean doFootnotes) throws Exception {
        Document doc = new Document(is);
        DocSplitter doc2Wiki = new DocSplitter(importContext, doFootnotes);
        doc.accept((DocumentVisitor)doc2Wiki);
    }

    public static class NormalizationResult<DocumentType> {
        public int treeDepth;
        public final List<DocumentTreeNode<DocumentType>> orderedNodes = new ArrayList<DocumentTreeNode<DocumentType>>();
        public BookmarkInfo bookmarks;
        public DocumentTreeNode<DocumentType> treeRoot;
    }
}

