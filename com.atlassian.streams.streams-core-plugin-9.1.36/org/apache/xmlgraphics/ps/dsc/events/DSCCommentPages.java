/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public class DSCCommentPages
extends AbstractDSCComment {
    private int pageCount = -1;

    public DSCCommentPages() {
    }

    public DSCCommentPages(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int count) {
        this.pageCount = count;
    }

    @Override
    public String getName() {
        return "Pages";
    }

    @Override
    public boolean hasValues() {
        return true;
    }

    @Override
    public void parseValue(String value) {
        this.pageCount = Integer.parseInt(value);
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        if (this.getPageCount() > 0) {
            gen.writeDSCComment(this.getName(), this.getPageCount());
        } else {
            gen.writeDSCComment(this.getName(), DSCConstants.ATEND);
        }
    }
}

