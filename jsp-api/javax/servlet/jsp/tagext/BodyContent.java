/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.servlet.jsp.JspWriter;

public abstract class BodyContent
extends JspWriter {
    private final JspWriter enclosingWriter;

    protected BodyContent(JspWriter e) {
        super(-2, false);
        this.enclosingWriter = e;
    }

    @Override
    public void flush() throws IOException {
        throw new IOException("Illegal to flush within a custom tag");
    }

    public void clearBody() {
        try {
            this.clear();
        }
        catch (IOException ex) {
            throw new Error("internal error!;");
        }
    }

    public abstract Reader getReader();

    public abstract String getString();

    public abstract void writeOut(Writer var1) throws IOException;

    public JspWriter getEnclosingWriter() {
        return this.enclosingWriter;
    }
}

