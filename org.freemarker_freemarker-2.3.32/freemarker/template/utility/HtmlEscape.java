/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class HtmlEscape
implements TemplateTransformModel {
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] QUOT = "&quot;".toCharArray();

    @Override
    public Writer getWriter(final Writer out, Map args) {
        return new Writer(){

            @Override
            public void write(int c) throws IOException {
                switch (c) {
                    case 60: {
                        out.write(LT, 0, 4);
                        break;
                    }
                    case 62: {
                        out.write(GT, 0, 4);
                        break;
                    }
                    case 38: {
                        out.write(AMP, 0, 5);
                        break;
                    }
                    case 34: {
                        out.write(QUOT, 0, 6);
                        break;
                    }
                    default: {
                        out.write(c);
                    }
                }
            }

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                int lastoff = off;
                int lastpos = off + len;
                block6: for (int i = off; i < lastpos; ++i) {
                    switch (cbuf[i]) {
                        case '<': {
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(LT, 0, 4);
                            lastoff = i + 1;
                            continue block6;
                        }
                        case '>': {
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(GT, 0, 4);
                            lastoff = i + 1;
                            continue block6;
                        }
                        case '&': {
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(AMP, 0, 5);
                            lastoff = i + 1;
                            continue block6;
                        }
                        case '\"': {
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(QUOT, 0, 6);
                            lastoff = i + 1;
                        }
                    }
                }
                int remaining = lastpos - lastoff;
                if (remaining > 0) {
                    out.write(cbuf, lastoff, remaining);
                }
            }

            @Override
            public void flush() throws IOException {
                out.flush();
            }

            @Override
            public void close() {
            }
        };
    }
}

