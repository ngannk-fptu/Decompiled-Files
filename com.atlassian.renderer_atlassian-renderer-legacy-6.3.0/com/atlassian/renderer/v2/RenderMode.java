/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2;

public class RenderMode {
    public static final long F_ALL = 0x7FFFFFFFEFFFFFFFL;
    public static final long F_NONE = 0L;
    public static final long F_PARAGRAPHS = 1L;
    public static final long F_LINEBREAKS = 2L;
    public static final long F_MACROS = 4L;
    public static final long F_LINKS = 8L;
    public static final long F_PHRASES = 16L;
    public static final long F_IMAGES = 32L;
    public static final long F_TABLES = 64L;
    public static final long F_HTMLESCAPE = 128L;
    public static final long F_FIRST_PARA = 256L;
    public static final long F_LISTS = 512L;
    public static final long F_RESOLVE_TOKENS = 1024L;
    public static final long F_PRESERVE_ENTITIES = 2048L;
    public static final long F_BACKSLASH_ESCAPE = 4096L;
    public static final long F_TEMPLATE = 8192L;
    public static final long F_MACROS_ERR_MSG = 16384L;
    public static final RenderMode ALL = RenderMode.allow(0x7FFFFFFFEFFFFFFFL);
    public static final RenderMode NO_ESCAPE = RenderMode.suppress(128L);
    public static final RenderMode LINKS_ONLY = RenderMode.allow(6280L);
    public static final RenderMode INLINE = RenderMode.allow(15546L);
    public static final RenderMode PHRASES_IMAGES = RenderMode.allow(15536L);
    public static final RenderMode PHRASES_LINKS = RenderMode.allow(15512L);
    public static final RenderMode SIMPLE_TEXT = RenderMode.allow(15763L);
    public static final RenderMode LIST_ITEM = RenderMode.suppress(512L);
    public static final RenderMode TABLE_CELL = RenderMode.suppress(72L);
    public static final RenderMode ALL_WITH_NO_MACRO_ERRORS = RenderMode.suppress(16384L);
    public static final RenderMode MACROS_ONLY = RenderMode.allow(4L);
    public static final RenderMode NO_RENDER = RenderMode.allow(0L);
    private final long flags;
    public static final RenderMode COMPATIBILITY_MODE = RenderMode.suppress(256L);

    public static RenderMode suppress(long flags) {
        return new RenderMode(0x7FFFFFFFEFFFFFFFL & (flags ^ 0xFFFFFFFFFFFFFFFFL));
    }

    public static RenderMode allow(long flags) {
        return new RenderMode(flags);
    }

    public RenderMode and(RenderMode otherMode) {
        return new RenderMode(this.flags & otherMode.flags);
    }

    public RenderMode or(RenderMode otherMode) {
        return new RenderMode(this.flags | otherMode.flags);
    }

    private RenderMode(long flags) {
        this.flags = flags;
    }

    public boolean renderLinebreaks() {
        return this.flagSet(2L);
    }

    public boolean renderLinks() {
        return this.flagSet(8L);
    }

    public boolean renderMacros() {
        return this.flagSet(4L);
    }

    public boolean renderParagraphs() {
        return this.flagSet(1L);
    }

    public boolean renderPhrases() {
        return this.flagSet(16L);
    }

    public boolean renderImages() {
        return this.flagSet(32L);
    }

    public boolean renderTables() {
        return this.flagSet(64L);
    }

    public boolean renderNothing() {
        return this.flags == 0L;
    }

    public boolean htmlEscape() {
        return this.flagSet(128L);
    }

    public boolean backslashEscape() {
        return this.flagSet(4096L);
    }

    public boolean renderFirstParagraph() {
        return this.flagSet(256L);
    }

    public boolean renderTemplate() {
        return this.flagSet(8192L);
    }

    public boolean renderLists() {
        return this.flagSet(512L);
    }

    public boolean resolveTokens() {
        return this.flagSet(1024L);
    }

    public boolean renderMacroErrorMessages() {
        return this.flagSet(16384L);
    }

    public boolean preserveEntities() {
        return this.flagSet(2048L);
    }

    public boolean tokenizes() {
        return this.renderLinks() || this.renderImages() || this.renderMacros() || this.renderPhrases();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RenderMode)) {
            return false;
        }
        return this.flags == ((RenderMode)o).flags;
    }

    public int hashCode() {
        return (int)(this.flags ^ this.flags >>> 32);
    }

    private boolean flagSet(long flag) {
        return (this.flags & flag) == flag;
    }
}

