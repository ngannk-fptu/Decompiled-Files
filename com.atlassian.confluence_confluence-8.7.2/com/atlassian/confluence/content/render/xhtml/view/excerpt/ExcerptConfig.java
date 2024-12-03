/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.content.render.xhtml.view.excerpt;

import com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptState;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;

public class ExcerptConfig {
    static final Set<String> BLOCK_ELEMENT_SET = ImmutableSet.of((Object)"p", (Object)"pre", (Object)"blockquote", (Object)"h1", (Object)"h2", (Object)"h3", (Object[])new String[]{"h4", "h5", "h6", "li", "tr"});
    static final Set<String> CONTAINER_ELEMENT_SET = ImmutableSet.of((Object)"div", (Object)"ul", (Object)"ol", (Object)"table", (Object)"tbody", (Object)"thead", (Object[])new String[0]);
    private final int maxBlocks;
    private final int minBlocks;
    private final int minCharCount;
    private final int maxCharCount;
    private final ImmutableSet<String> excludedLastHtmlElements;
    private final ImmutableSet<String> excludedHtmlElements;
    private final MacroDefinitionUpdater macroDefinitionUpdater;
    private final boolean ignoreUserDefinedExcerpt;

    public static Builder builder() {
        return new Builder();
    }

    protected ExcerptConfig(Builder builder) {
        this.maxBlocks = builder.maxBlocks;
        this.minBlocks = builder.minBlocks;
        this.minCharCount = builder.minCharCount;
        this.maxCharCount = builder.maxCharCount;
        this.excludedLastHtmlElements = builder.excludedLastHtmlElement.build();
        this.excludedHtmlElements = builder.excludeHtmlElements.build();
        this.macroDefinitionUpdater = builder.macroDefinitionUpdater;
        this.ignoreUserDefinedExcerpt = builder.ignoreUserDefinedExcerpt;
    }

    protected Set<String> getContainerElementSet() {
        return Sets.difference(CONTAINER_ELEMENT_SET, this.excludedHtmlElements);
    }

    protected Set<String> getBlockElementSet() {
        return Sets.difference(BLOCK_ELEMENT_SET, this.excludedHtmlElements);
    }

    public int getMaxBlocks() {
        return this.maxBlocks;
    }

    public int getMinBlocks() {
        return this.minBlocks;
    }

    public int getMinCharCount() {
        return this.minCharCount;
    }

    public int getMaxCharCount() {
        return this.maxCharCount;
    }

    public Set<String> getExcludedHtmlElements() {
        return this.excludedHtmlElements;
    }

    public Set<String> getExcludedLastHtmlElements() {
        return this.excludedLastHtmlElements;
    }

    public MacroDefinitionUpdater getMacroDefinitionUpdater() {
        return this.macroDefinitionUpdater;
    }

    public boolean ignoreUserDefinedExcerpt() {
        return this.ignoreUserDefinedExcerpt;
    }

    boolean canContinue(ExcerptState excerptState) {
        if (this.anyMaximumReached(excerptState)) {
            return false;
        }
        return !this.allMinimumsExceeded(excerptState) || this.getExcludedLastHtmlElements().contains(excerptState.getLastTag());
    }

    private boolean allMinimumsExceeded(ExcerptState excerptState) {
        if (this.getMinBlocks() == 0 && this.getMinCharCount() == 0) {
            return false;
        }
        return !(this.getMinBlocks() != 0 && excerptState.getBlocks() < this.getMinBlocks() || this.getMinCharCount() != 0 && excerptState.getChars() < this.getMinCharCount());
    }

    private boolean anyMaximumReached(ExcerptState excerptState) {
        return this.getMaxBlocks() > 0 && excerptState.getBlocks() >= this.getMaxBlocks() || this.getMaxCharCount() > 0 && excerptState.getChars() >= this.getMaxCharCount();
    }

    public static class Builder {
        private int maxBlocks;
        private int minBlocks;
        private int minCharCount;
        private int maxCharCount;
        private ImmutableSet.Builder<String> excludedLastHtmlElement = new ImmutableSet.Builder();
        private ImmutableSet.Builder<String> excludeHtmlElements = new ImmutableSet.Builder();
        private MacroDefinitionUpdater macroDefinitionUpdater;
        private boolean ignoreUserDefinedExcerpt = true;

        public ExcerptConfig build() {
            return new ExcerptConfig(this);
        }

        Builder() {
        }

        public Builder maxBlocks(int maxBlocks) {
            this.maxBlocks = maxBlocks;
            return this;
        }

        public Builder minBlocks(int minBlocks) {
            this.minBlocks = minBlocks;
            return this;
        }

        public Builder minCharCount(int minCharCount) {
            this.minCharCount = minCharCount;
            return this;
        }

        public Builder maxCharCount(int maxCharCount) {
            this.maxCharCount = maxCharCount;
            return this;
        }

        public Builder excludedLastHtmlElement(Iterable<String> excludedLastHtmlElement) {
            this.excludedLastHtmlElement.addAll(excludedLastHtmlElement);
            return this;
        }

        public Builder excludeHtmlElements(Iterable<String> exclude) {
            this.excludeHtmlElements.addAll(exclude);
            return this;
        }

        public Builder macroDefinitionUpdater(MacroDefinitionUpdater macroDefinitionUpdater) {
            this.macroDefinitionUpdater = macroDefinitionUpdater;
            return this;
        }

        public Builder ignoreUserDefinedExcerpt(boolean ignoreUserDefinedExcerpt) {
            this.ignoreUserDefinedExcerpt = ignoreUserDefinedExcerpt;
            return this;
        }
    }
}

