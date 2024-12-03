/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mail.converters.basic;

import com.atlassian.mail.converters.HtmlConverter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlToTextConverter
implements HtmlConverter {
    private static final Logger log = LoggerFactory.getLogger(HtmlToTextConverter.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String convert(@Nonnull String html) throws IOException {
        StringWriter out = new StringWriter();
        try {
            this.convert(new StringReader(html), out);
            String string = out.toString();
            return string;
        }
        finally {
            IOUtils.closeQuietly((Writer)out);
        }
    }

    private void convert(Reader reader, Writer writer) throws IOException {
        HTMLCallbackHandler handler = new HTMLCallbackHandler(writer);
        new ParserDelegator().parse(reader, handler, true);
    }

    private class HTMLCallbackHandler
    extends HTMLEditorKit.ParserCallback {
        Writer out;
        boolean started = false;
        boolean inBody = false;
        boolean inList = false;
        boolean firstTD = true;
        int listCount = -1;
        List<String> links = new ArrayList<String>();
        private static final String NEWLINE = "\n";
        private static final String TAB = "\t";
        private static final String STAR = "*";
        private static final String SPACE = " ";
        private static final String PERIOD = ".";
        private static final String OPEN_BRACKET = "[";
        private static final String CLOSE_BRACKET = "]";
        private static final String DASH_LINE = "----------------------------------------------------------------------------------------";

        public HTMLCallbackHandler(Writer writer) {
            this.out = writer;
        }

        @Override
        public void handleStartTag(HTML.Tag tag, MutableAttributeSet set, int position) {
            try {
                if (this.inBody && this.started && tag.equals(HTML.Tag.P)) {
                    this.out.write("\n\n");
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.OL) || tag.equals(HTML.Tag.UL)) {
                    this.inList = true;
                    this.out.write("\n\n");
                    if (tag.equals(HTML.Tag.OL)) {
                        this.listCount = 1;
                    }
                } else if (this.inBody && this.started && this.inList && tag.equals(HTML.Tag.LI)) {
                    this.out.write(NEWLINE);
                    if (this.listCount != -1) {
                        this.out.write(this.listCount + PERIOD + SPACE);
                        ++this.listCount;
                    } else {
                        this.out.write(STAR);
                    }
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.TABLE)) {
                    this.out.write(NEWLINE);
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.TR)) {
                    this.out.write(NEWLINE);
                    this.firstTD = true;
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.TD) || tag.equals(HTML.Tag.TH)) {
                    if (!this.firstTD) {
                        this.out.write(TAB);
                    } else {
                        this.firstTD = false;
                    }
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.PRE)) {
                    this.out.write(NEWLINE);
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.IMG)) {
                    this.handleLink((String)set.getAttribute(HTML.Attribute.SRC));
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.A)) {
                    this.handleLink((String)set.getAttribute(HTML.Attribute.HREF));
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.HR)) {
                    this.out.write("\n----------------------------------------------------------------------------------------");
                } else if (this.inBody && this.started && tag.equals(HTML.Tag.H1) || tag.equals(HTML.Tag.H2) || tag.equals(HTML.Tag.H3) || tag.equals(HTML.Tag.H4) || tag.equals(HTML.Tag.H5) || tag.equals(HTML.Tag.H6)) {
                    this.out.write(NEWLINE);
                } else if (tag.equals(HTML.Tag.BODY)) {
                    this.inBody = true;
                }
            }
            catch (IOException e) {
                log.warn("IO error converting HTML to text", (Throwable)e);
            }
        }

        private void handleLink(String src) throws IOException {
            if (src != null) {
                this.links.add(src);
                this.out.write(OPEN_BRACKET + this.links.size() + CLOSE_BRACKET);
            }
        }

        @Override
        public void handleEndTag(HTML.Tag tag, int position) {
            if (this.inBody && this.started && tag.equals(HTML.Tag.OL) || tag.equals(HTML.Tag.UL)) {
                this.inList = false;
                if (tag.equals(HTML.Tag.OL)) {
                    this.listCount = -1;
                }
            } else if (tag.equals(HTML.Tag.BODY)) {
                if (this.links.size() != 0) {
                    try {
                        this.out.write("\n----------------------------------------------------------------------------------------\n");
                        for (int i = 0; i < this.links.size(); ++i) {
                            String src = this.links.get(i);
                            this.out.write(OPEN_BRACKET + (i + 1) + CLOSE_BRACKET + SPACE + src);
                            if (i + 1 >= this.links.size()) continue;
                            this.out.write(NEWLINE);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                this.inBody = false;
            }
        }

        @Override
        public void handleText(char[] aChar, int position) {
            try {
                if (this.inBody) {
                    this.out.write(aChar);
                    this.started = true;
                }
            }
            catch (IOException e) {
                log.warn("IO error converting HTML to text", (Throwable)e);
            }
        }

        @Override
        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
            try {
                if (this.inBody && this.started && tag.equals(HTML.Tag.BR)) {
                    this.out.write(NEWLINE);
                }
            }
            catch (IOException e) {
                log.warn("IO error converting HTML to text", (Throwable)e);
            }
        }
    }
}

