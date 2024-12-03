/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.renderer.PageContext;
import java.util.Objects;

public class PageTemplateContext
extends PageContext {
    private PageTemplate template;

    public PageTemplateContext() {
    }

    public PageTemplateContext(PageTemplate template) {
        super(template.getSpace() != null ? template.getSpace().getKey() : null);
        this.template = template;
    }

    public PageTemplate getTemplate() {
        return this.template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageTemplateContext)) {
            return false;
        }
        PageTemplateContext context = (PageTemplateContext)((Object)o);
        return super.equals(o) && this.template.equals(context.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.template);
    }

    @Override
    public ContentEntityObject getEntity() {
        return new SpaceContentEntityObject(){
            {
                this.setSpace(PageTemplateContext.this.template.getSpace());
                this.setBodyAsString(PageTemplateContext.this.template.getContent());
            }

            @Override
            public String getType() {
                return "page";
            }

            @Override
            public String getUrlPath() {
                return "";
            }
        };
    }
}

