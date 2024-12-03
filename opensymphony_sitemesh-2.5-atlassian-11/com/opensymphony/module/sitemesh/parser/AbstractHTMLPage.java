/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.parser.AbstractPage;
import java.io.IOException;
import java.io.Writer;

public abstract class AbstractHTMLPage
extends AbstractPage
implements HTMLPage {
    protected AbstractHTMLPage(SitemeshBuffer sitemeshBuffer) {
        super(sitemeshBuffer);
    }

    public abstract void writeHead(Writer var1) throws IOException;

    public boolean isFrameSet() {
        return this.isPropertySet("frameset") && this.getProperty("frameset").equalsIgnoreCase("true");
    }

    public void setFrameSet(boolean frameset) {
        if (frameset) {
            this.addProperty("frameset", "true");
        } else if (this.isPropertySet("frameset")) {
            this.addProperty("frameset", "false");
        }
    }
}

