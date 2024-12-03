/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import java.util.Map;

public class MacroExecutionContext {
    private Map<String, String> params;
    private String body;
    private PageContext pageContext;
    private ContentEntityObject content;

    public MacroExecutionContext(Map<String, String> params, String body, PageContext pageContext) {
        this.params = params;
        this.body = body;
        this.pageContext = pageContext;
        this.content = pageContext.getEntity();
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getBody() {
        return this.body;
    }

    public PageContext getPageContext() {
        return this.pageContext;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }
}

