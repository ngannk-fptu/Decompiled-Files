/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BlockExtractingRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

public class MSOfficeDocumentPropertiesRule
extends BlockExtractingRule {
    private final PageBuilder page;
    private boolean inDocumentProperties;

    public MSOfficeDocumentPropertiesRule(PageBuilder page) {
        super(true);
        this.page = page;
    }

    public boolean shouldProcess(String name) {
        return this.inDocumentProperties && name.startsWith("o:") || name.equals("o:documentproperties");
    }

    public void process(Tag tag) {
        if (tag.getName().equals("o:DocumentProperties")) {
            this.inDocumentProperties = tag.getType() == 1;
        } else {
            super.process(tag);
        }
    }

    protected void start(Tag tag) {
    }

    protected void end(Tag tag) {
        String name = tag.getName().substring(2);
        this.page.addProperty("office.DocumentProperties." + name, this.getCurrentBufferContent());
    }
}

