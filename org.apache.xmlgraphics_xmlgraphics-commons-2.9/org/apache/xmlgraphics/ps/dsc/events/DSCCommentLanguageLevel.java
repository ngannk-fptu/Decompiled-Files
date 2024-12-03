/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public class DSCCommentLanguageLevel
extends AbstractDSCComment {
    private int level;

    public DSCCommentLanguageLevel() {
    }

    public DSCCommentLanguageLevel(int level) {
        this.level = level;
    }

    public int getLanguageLevel() {
        return this.level;
    }

    @Override
    public String getName() {
        return "LanguageLevel";
    }

    @Override
    public boolean hasValues() {
        return true;
    }

    @Override
    public void parseValue(String value) {
        this.level = Integer.parseInt(value);
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        if (this.level <= 0) {
            throw new IllegalStateException("Language Level was not properly set");
        }
        gen.writeDSCComment(this.getName(), this.getLanguageLevel());
    }
}

