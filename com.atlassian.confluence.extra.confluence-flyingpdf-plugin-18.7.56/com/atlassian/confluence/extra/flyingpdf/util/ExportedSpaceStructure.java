/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.confluence.extra.flyingpdf.util.PdfNode;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.Validate;

public class ExportedSpaceStructure
implements Serializable {
    private PdfNode tableOfContents;
    private final List<PdfNode> confluencePages;
    private final Map<String, Integer> locationByTitle = new HashMap<String, Integer>();

    public ExportedSpaceStructure(PdfNode tableOfContents, List<PdfNode> confluencePages) {
        this.tableOfContents = tableOfContents;
        this.confluencePages = confluencePages;
        AtomicInteger currentPage = new AtomicInteger(tableOfContents.getRenderedPdfFile().getNumPages() + 1);
        confluencePages.forEach(node -> this.initialise((PdfNode)node, currentPage));
    }

    public void replaceToc(PdfNode newTableOfContents) {
        Validate.isTrue((this.tableOfContents.getRenderedPdfFile().getNumPages() == newTableOfContents.getRenderedPdfFile().getNumPages() ? 1 : 0) != 0, (String)"TOC should be replaced only with TOC of the same size. It is important because TOC size will affect TOC content (page offsets). This is a bit of chicken and egg problem, which makes us generate TOC twice.", (Object[])new Object[0]);
        this.tableOfContents = newTableOfContents;
    }

    private void initialise(PdfNode node, AtomicInteger currentPage) {
        this.locationByTitle.put(node.getPageTitle(), currentPage.get());
        currentPage.addAndGet(node.getRenderedPdfFile().getNumPages());
        for (PdfNode child : node.getChildren()) {
            this.initialise(child, currentPage);
        }
    }

    public PdfNode getTableOfContents() {
        return this.tableOfContents;
    }

    public List<PdfNode> getConfluencePages() {
        return Collections.unmodifiableList(this.confluencePages);
    }

    Integer locationByNode(PdfNode node) {
        if (this.tableOfContents.equals(node)) {
            return 1;
        }
        return this.locationByTitle.get(node.getPageTitle());
    }

    public Integer locationByTitle(String title) {
        return this.locationByTitle.get(title);
    }

    public boolean hasPageTitle(String title) {
        return this.locationByTitle.containsKey(title);
    }

    public Map<String, Integer> locationByTitleMap() {
        return Collections.unmodifiableMap(this.locationByTitle);
    }
}

