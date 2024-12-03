/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.postingshighlight;

import org.apache.lucene.search.postingshighlight.Passage;
import org.apache.lucene.search.postingshighlight.PassageFormatter;

public class DefaultPassageFormatter
extends PassageFormatter {
    protected final String preTag;
    protected final String postTag;
    protected final String ellipsis;
    protected final boolean escape;

    public DefaultPassageFormatter() {
        this("<b>", "</b>", "... ", false);
    }

    public DefaultPassageFormatter(String preTag, String postTag, String ellipsis, boolean escape) {
        if (preTag == null || postTag == null || ellipsis == null) {
            throw new NullPointerException();
        }
        this.preTag = preTag;
        this.postTag = postTag;
        this.ellipsis = ellipsis;
        this.escape = escape;
    }

    @Override
    public String format(Passage[] passages, String content) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (Passage passage : passages) {
            if (passage.startOffset > pos && pos > 0) {
                sb.append(this.ellipsis);
            }
            pos = passage.startOffset;
            for (int i = 0; i < passage.numMatches; ++i) {
                int start = passage.matchStarts[i];
                int end = passage.matchEnds[i];
                if (start > pos) {
                    this.append(sb, content, pos, start);
                }
                if (end <= pos) continue;
                sb.append(this.preTag);
                this.append(sb, content, Math.max(pos, start), end);
                sb.append(this.postTag);
                pos = end;
            }
            this.append(sb, content, pos, Math.max(pos, passage.endOffset));
            pos = passage.endOffset;
        }
        return sb.toString();
    }

    protected void append(StringBuilder dest, String content, int start, int end) {
        if (this.escape) {
            block8: for (int i = start; i < end; ++i) {
                char ch = content.charAt(i);
                switch (ch) {
                    case '&': {
                        dest.append("&amp;");
                        continue block8;
                    }
                    case '<': {
                        dest.append("&lt;");
                        continue block8;
                    }
                    case '>': {
                        dest.append("&gt;");
                        continue block8;
                    }
                    case '\"': {
                        dest.append("&quot;");
                        continue block8;
                    }
                    case '\'': {
                        dest.append("&#x27;");
                        continue block8;
                    }
                    case '/': {
                        dest.append("&#x2F;");
                        continue block8;
                    }
                    default: {
                        if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z') {
                            dest.append(ch);
                            continue block8;
                        }
                        if (ch < '\u00ff') {
                            dest.append("&#");
                            dest.append((int)ch);
                            dest.append(";");
                            continue block8;
                        }
                        dest.append(ch);
                    }
                }
            }
        } else {
            dest.append(content, start, end);
        }
    }
}

