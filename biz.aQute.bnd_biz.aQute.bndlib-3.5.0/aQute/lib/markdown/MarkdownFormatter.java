/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.markdown;

import java.util.Formatter;

public class MarkdownFormatter {
    private Formatter f;

    public MarkdownFormatter(Appendable out) {
        this.f = new Formatter(out);
    }

    public MarkdownFormatter format(String format, Object ... args) {
        this.f = this.f.format(format, args);
        return this;
    }

    public MarkdownFormatter h1(String format, Object ... args) {
        this.f = this.f.format("# " + format + " #%n", args);
        return this;
    }

    public MarkdownFormatter h2(String format, Object ... args) {
        this.f = this.f.format("## " + format + " ##%n", args);
        return this;
    }

    public MarkdownFormatter h3(String format, Object ... args) {
        this.f = this.f.format("### " + format + " ###%n", args);
        return this;
    }

    public MarkdownFormatter list(String format, Object ... args) {
        this.f = this.f.format("+ " + format + "%n", args);
        return this;
    }

    public String toString() {
        return this.f.toString();
    }

    public MarkdownFormatter code(String format, Object ... args) {
        this.f = this.f.format("\t" + format + "%n", args);
        return this;
    }

    public MarkdownFormatter inlineCode(String format, Object ... args) {
        this.f = this.f.format("`" + format + "`", args);
        return this;
    }

    public MarkdownFormatter endP() {
        this.f = this.f.format("%n%n", new Object[0]);
        return this;
    }

    public MarkdownFormatter flush() {
        this.f.flush();
        return this;
    }
}

