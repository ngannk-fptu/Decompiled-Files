/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.content.render.xhtml.model.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSection;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class PageLayout {
    private final Collection<PageLayoutSection> sections;

    public PageLayout(Collection<PageLayoutSection> sections) {
        this.sections = ImmutableList.copyOf(sections);
    }

    public PageLayout(PageLayoutSection ... sections) {
        this.sections = ImmutableList.copyOf((Object[])sections);
    }

    public Collection<PageLayoutSection> getSections() {
        return this.sections;
    }

    public boolean hasOneSectionAndOneCell() {
        return this.hasOneSection() && this.sections.iterator().next().hasOneCell();
    }

    public boolean hasOneSection() {
        return this.sections.size() == 1;
    }
}

