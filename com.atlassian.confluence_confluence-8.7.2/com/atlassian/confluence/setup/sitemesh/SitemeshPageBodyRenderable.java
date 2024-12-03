/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sitemesh.Page
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.runtime.Renderable
 */
package com.atlassian.confluence.setup.sitemesh;

import com.opensymphony.module.sitemesh.Page;
import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.Renderable;

public class SitemeshPageBodyRenderable
implements Renderable {
    private final Page page;

    public SitemeshPageBodyRenderable(Page page) {
        this.page = page;
    }

    public boolean render(InternalContextAdapter context, Writer writer) throws IOException {
        this.page.writeBody(writer);
        return true;
    }
}

