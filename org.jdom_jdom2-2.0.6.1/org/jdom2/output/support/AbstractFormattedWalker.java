/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jdom2.CDATA;
import org.jdom2.Content;
import org.jdom2.internal.ArrayCopy;
import org.jdom2.output.EscapeStrategy;
import org.jdom2.output.Format;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.Walker;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractFormattedWalker
implements Walker {
    private static final CDATA CDATATOKEN = new CDATA("");
    private static final Iterator<Content> EMPTYIT = new Iterator<Content>(){

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Content next() {
            throw new NoSuchElementException("Cannot call next() on an empty iterator.");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from an empty iterator.");
        }
    };
    private Content pending = null;
    private final Iterator<? extends Content> content;
    private final boolean alltext;
    private final boolean allwhite;
    private final String newlineindent;
    private final String endofline;
    private final EscapeStrategy escape;
    private final FormatStack fstack;
    private boolean hasnext = true;
    private MultiText multitext = null;
    private MultiText pendingmt = null;
    private final MultiText holdingmt = new MultiText();
    private final StringBuilder mtbuffer = new StringBuilder();
    private boolean mtpostpad;
    private boolean mtgottext = false;
    private int mtsize = 0;
    private int mtsourcesize = 0;
    private Content[] mtsource = new Content[8];
    private Content[] mtdata = new Content[8];
    private String[] mttext = new String[8];
    private int mtpos = -1;
    private Boolean mtwasescape;

    public AbstractFormattedWalker(List<? extends Content> xx, FormatStack fstack, boolean doescape) {
        this.fstack = fstack;
        this.content = xx.isEmpty() ? EMPTYIT : xx.iterator();
        this.escape = doescape ? fstack.getEscapeStrategy() : null;
        this.newlineindent = fstack.getPadBetween();
        this.endofline = fstack.getLevelEOL();
        if (!this.content.hasNext()) {
            this.alltext = true;
            this.allwhite = true;
        } else {
            boolean atext = false;
            boolean awhite = false;
            this.pending = this.content.next();
            if (this.isTextLike(this.pending)) {
                this.pendingmt = this.buildMultiText(true);
                this.analyzeMultiText(this.pendingmt, 0, this.mtsourcesize);
                this.pendingmt.done();
                if (this.pending == null) {
                    atext = true;
                    boolean bl = awhite = this.mtsize == 0;
                }
                if (this.mtsize == 0) {
                    this.pendingmt = null;
                }
            }
            this.alltext = atext;
            this.allwhite = awhite;
        }
        this.hasnext = this.pendingmt != null || this.pending != null;
    }

    @Override
    public final Content next() {
        if (!this.hasnext) {
            throw new NoSuchElementException("Cannot walk off end of Content");
        }
        if (this.multitext != null && this.mtpos + 1 >= this.mtsize) {
            this.multitext = null;
            this.resetMultiText();
        }
        if (this.pendingmt != null) {
            if (this.mtwasescape != null && this.fstack.getEscapeOutput() != this.mtwasescape.booleanValue()) {
                this.mtsize = 0;
                this.mtwasescape = this.fstack.getEscapeOutput();
                this.analyzeMultiText(this.pendingmt, 0, this.mtsourcesize);
                this.pendingmt.done();
            }
            this.multitext = this.pendingmt;
            this.pendingmt = null;
        }
        if (this.multitext != null) {
            ++this.mtpos;
            Content ret = this.mttext[this.mtpos] == null ? this.mtdata[this.mtpos] : null;
            this.hasnext = this.mtpos + 1 < this.mtsize || this.pending != null;
            return ret;
        }
        Content ret = this.pending;
        Content content = this.pending = this.content.hasNext() ? this.content.next() : null;
        if (this.pending == null) {
            this.hasnext = false;
        } else if (this.isTextLike(this.pending)) {
            this.pendingmt = this.buildMultiText(false);
            this.analyzeMultiText(this.pendingmt, 0, this.mtsourcesize);
            this.pendingmt.done();
            if (this.mtsize > 0) {
                this.hasnext = true;
            } else if (this.pending != null && this.newlineindent != null) {
                this.resetMultiText();
                this.pendingmt = this.holdingmt;
                this.pendingmt.forceAppend(this.newlineindent);
                this.pendingmt.done();
                this.hasnext = true;
            } else {
                this.pendingmt = null;
                this.hasnext = this.pending != null;
            }
        } else {
            if (this.newlineindent != null) {
                this.resetMultiText();
                this.pendingmt = this.holdingmt;
                this.pendingmt.forceAppend(this.newlineindent);
                this.pendingmt.done();
            }
            this.hasnext = true;
        }
        return ret;
    }

    private void resetMultiText() {
        this.mtsourcesize = 0;
        this.mtpos = -1;
        this.mtsize = 0;
        this.mtgottext = false;
        this.mtpostpad = false;
        this.mtwasescape = null;
        this.mtbuffer.setLength(0);
    }

    protected abstract void analyzeMultiText(MultiText var1, int var2, int var3);

    protected final Content get(int index) {
        return this.mtsource[index];
    }

    @Override
    public final boolean isAllText() {
        return this.alltext;
    }

    @Override
    public final boolean hasNext() {
        return this.hasnext;
    }

    private final MultiText buildMultiText(boolean first) {
        if (!first && this.newlineindent != null) {
            this.mtbuffer.append(this.newlineindent);
        }
        this.mtsourcesize = 0;
        do {
            if (this.mtsourcesize >= this.mtsource.length) {
                this.mtsource = ArrayCopy.copyOf(this.mtsource, this.mtsource.length * 2);
            }
            this.mtsource[this.mtsourcesize++] = this.pending;
            Content content = this.pending = this.content.hasNext() ? this.content.next() : null;
        } while (this.pending != null && this.isTextLike(this.pending));
        this.mtpostpad = this.pending != null;
        this.mtwasescape = this.fstack.getEscapeOutput();
        return this.holdingmt;
    }

    @Override
    public final String text() {
        if (this.multitext == null || this.mtpos >= this.mtsize) {
            return null;
        }
        return this.mttext[this.mtpos];
    }

    @Override
    public final boolean isCDATA() {
        if (this.multitext == null || this.mtpos >= this.mtsize) {
            return false;
        }
        if (this.mttext[this.mtpos] == null) {
            return false;
        }
        return this.mtdata[this.mtpos] == CDATATOKEN;
    }

    @Override
    public final boolean isAllWhitespace() {
        return this.allwhite;
    }

    private final boolean isTextLike(Content c) {
        switch (c.getCType()) {
            case Text: 
            case CDATA: 
            case EntityRef: {
                return true;
            }
        }
        return false;
    }

    static /* synthetic */ Content[] access$102(AbstractFormattedWalker x0, Content[] x1) {
        x0.mtdata = x1;
        return x1;
    }

    static /* synthetic */ String[] access$202(AbstractFormattedWalker x0, String[] x1) {
        x0.mttext = x1;
        return x1;
    }

    protected final class MultiText {
        private MultiText() {
        }

        private void ensurespace() {
            if (AbstractFormattedWalker.this.mtsize >= AbstractFormattedWalker.this.mtdata.length) {
                AbstractFormattedWalker.access$102(AbstractFormattedWalker.this, ArrayCopy.copyOf(AbstractFormattedWalker.this.mtdata, AbstractFormattedWalker.this.mtsize + 1 + AbstractFormattedWalker.this.mtsize / 2));
                AbstractFormattedWalker.access$202(AbstractFormattedWalker.this, ArrayCopy.copyOf(AbstractFormattedWalker.this.mttext, AbstractFormattedWalker.this.mtdata.length));
            }
        }

        private void closeText() {
            if (AbstractFormattedWalker.this.mtbuffer.length() == 0) {
                return;
            }
            this.ensurespace();
            ((AbstractFormattedWalker)AbstractFormattedWalker.this).mtdata[((AbstractFormattedWalker)AbstractFormattedWalker.this).mtsize] = null;
            ((AbstractFormattedWalker)AbstractFormattedWalker.this).mttext[((AbstractFormattedWalker)AbstractFormattedWalker.this).mtsize++] = AbstractFormattedWalker.this.mtbuffer.toString();
            AbstractFormattedWalker.this.mtbuffer.setLength(0);
        }

        public void appendText(Trim trim, String text) {
            int tlen = text.length();
            if (tlen == 0) {
                return;
            }
            String toadd = null;
            switch (trim) {
                case NONE: {
                    toadd = text;
                    break;
                }
                case BOTH: {
                    toadd = Format.trimBoth(text);
                    break;
                }
                case LEFT: {
                    toadd = Format.trimLeft(text);
                    break;
                }
                case RIGHT: {
                    toadd = Format.trimRight(text);
                    break;
                }
                case COMPACT: {
                    toadd = Format.compact(text);
                }
            }
            if (toadd != null) {
                toadd = this.escapeText(toadd);
                AbstractFormattedWalker.this.mtbuffer.append(toadd);
                AbstractFormattedWalker.this.mtgottext = true;
            }
        }

        private String escapeText(String text) {
            if (AbstractFormattedWalker.this.escape == null || !AbstractFormattedWalker.this.fstack.getEscapeOutput()) {
                return text;
            }
            return Format.escapeText(AbstractFormattedWalker.this.escape, AbstractFormattedWalker.this.endofline, text);
        }

        private String escapeCDATA(String text) {
            if (AbstractFormattedWalker.this.escape == null) {
                return text;
            }
            return text;
        }

        public void appendCDATA(Trim trim, String text) {
            this.closeText();
            String toadd = null;
            switch (trim) {
                case NONE: {
                    toadd = text;
                    break;
                }
                case BOTH: {
                    toadd = Format.trimBoth(text);
                    break;
                }
                case LEFT: {
                    toadd = Format.trimLeft(text);
                    break;
                }
                case RIGHT: {
                    toadd = Format.trimRight(text);
                    break;
                }
                case COMPACT: {
                    toadd = Format.compact(text);
                }
            }
            toadd = this.escapeCDATA(toadd);
            this.ensurespace();
            ((AbstractFormattedWalker)AbstractFormattedWalker.this).mtdata[((AbstractFormattedWalker)AbstractFormattedWalker.this).mtsize] = CDATATOKEN;
            ((AbstractFormattedWalker)AbstractFormattedWalker.this).mttext[((AbstractFormattedWalker)AbstractFormattedWalker.this).mtsize++] = toadd;
            AbstractFormattedWalker.this.mtgottext = true;
        }

        private void forceAppend(String text) {
            AbstractFormattedWalker.this.mtgottext = true;
            AbstractFormattedWalker.this.mtbuffer.append(text);
        }

        public void appendRaw(Content c) {
            this.closeText();
            this.ensurespace();
            ((AbstractFormattedWalker)AbstractFormattedWalker.this).mttext[((AbstractFormattedWalker)AbstractFormattedWalker.this).mtsize] = null;
            ((AbstractFormattedWalker)AbstractFormattedWalker.this).mtdata[((AbstractFormattedWalker)AbstractFormattedWalker.this).mtsize++] = c;
            AbstractFormattedWalker.this.mtbuffer.setLength(0);
        }

        public void done() {
            if (AbstractFormattedWalker.this.mtpostpad && AbstractFormattedWalker.this.newlineindent != null) {
                AbstractFormattedWalker.this.mtbuffer.append(AbstractFormattedWalker.this.newlineindent);
            }
            if (AbstractFormattedWalker.this.mtgottext) {
                this.closeText();
            }
            AbstractFormattedWalker.this.mtbuffer.setLength(0);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum Trim {
        LEFT,
        RIGHT,
        BOTH,
        COMPACT,
        NONE;

    }
}

