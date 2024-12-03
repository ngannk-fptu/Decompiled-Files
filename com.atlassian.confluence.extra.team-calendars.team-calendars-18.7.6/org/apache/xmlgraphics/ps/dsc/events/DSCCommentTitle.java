/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public class DSCCommentTitle
extends AbstractDSCComment {
    private String title;

    public DSCCommentTitle() {
    }

    public DSCCommentTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String getName() {
        return "Title";
    }

    @Override
    public boolean hasValues() {
        return true;
    }

    @Override
    public void parseValue(String value) {
        List params = this.splitParams(value);
        Iterator iter = params.iterator();
        this.title = (String)iter.next();
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.writeDSCComment(this.getName(), this.getTitle());
    }
}

