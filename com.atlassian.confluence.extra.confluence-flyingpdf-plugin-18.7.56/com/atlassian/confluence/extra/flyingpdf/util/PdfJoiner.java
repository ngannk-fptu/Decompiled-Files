/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.extra.flyingpdf.util.ExportedSpaceStructure;
import com.atlassian.confluence.extra.flyingpdf.util.PdfNode;
import com.google.common.collect.ImmutableList;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Internal
public class PdfJoiner {
    private final List<Map> finalOutlines = new ArrayList<Map>();
    private final ExportedSpaceStructure structure;
    private final String outputFile;

    public PdfJoiner(String outputFile, ExportedSpaceStructure structure) {
        this.structure = structure;
        this.outputFile = outputFile;
    }

    public int join() throws IOException, DocumentException {
        ImmutableList forest = ImmutableList.builder().add((Object)this.structure.getTableOfContents()).addAll(this.structure.getConfluencePages()).build();
        return this.mergePdf((List<PdfNode>)forest, this.outputFile);
    }

    private int mergePdf(List<PdfNode> forest, String outputFile) throws DocumentException, IOException {
        Document document = new Document();
        PdfCopy writer = new PdfCopy(document, (OutputStream)new FileOutputStream(outputFile));
        document.open();
        int pageCount = 0;
        for (PdfNode node : forest) {
            pageCount += this.process(writer, node, null);
        }
        if (!this.finalOutlines.isEmpty()) {
            writer.setOutlines(this.finalOutlines);
        }
        document.close();
        writer.close();
        return pageCount;
    }

    private int process(PdfCopy writer, PdfNode node, List<Map<String, List>> parentOutlines) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(node.getFilename());
        int numberOfPages = reader.getNumberOfPages();
        List<Map<String, List>> currentOutlines = this.shiftOutlinesAddress(node, parentOutlines, reader);
        for (int i = 1; i <= numberOfPages; ++i) {
            PdfImportedPage page = writer.getImportedPage(reader, i);
            writer.addPage(page);
        }
        writer.freeReader(reader);
        reader.close();
        for (PdfNode child : node.getChildren()) {
            numberOfPages += this.process(writer, child, currentOutlines);
        }
        return numberOfPages;
    }

    private List<Map<String, List>> shiftOutlinesAddress(PdfNode node, List<Map<String, List>> parentBookmarks, PdfReader reader) {
        int location;
        ArrayList<Map<String, List>> currentBookmarks = SimpleBookmark.getBookmark(reader);
        if (currentBookmarks == null) {
            currentBookmarks = new ArrayList<Map<String, List>>();
        }
        if (currentBookmarks.isEmpty()) {
            currentBookmarks.add(new HashMap());
        }
        if ((location = this.structure.locationByNode(node).intValue()) > 1) {
            SimpleBookmark.shiftPageNumbers(currentBookmarks, location - 1, null);
        }
        if (parentBookmarks == null) {
            this.finalOutlines.addAll(currentBookmarks);
        } else {
            Map<String, List> first = parentBookmarks.get(0);
            first.computeIfAbsent("Kids", k -> new ArrayList()).add(currentBookmarks.get(0));
        }
        return currentBookmarks;
    }
}

