/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.search.highlight.GradientFormatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class SpanGradientFormatter
extends GradientFormatter {
    private static final String TEMPLATE = "<span style=\"background: #EEEEEE; color: #000000;\">...</span>";
    private static final int EXTRA = "<span style=\"background: #EEEEEE; color: #000000;\">...</span>".length();

    public SpanGradientFormatter(float maxScore, String minForegroundColor, String maxForegroundColor, String minBackgroundColor, String maxBackgroundColor) {
        super(maxScore, minForegroundColor, maxForegroundColor, minBackgroundColor, maxBackgroundColor);
    }

    @Override
    public String highlightTerm(String originalText, TokenGroup tokenGroup) {
        if (tokenGroup.getTotalScore() == 0.0f) {
            return originalText;
        }
        float score = tokenGroup.getTotalScore();
        if (score == 0.0f) {
            return originalText;
        }
        StringBuilder sb = new StringBuilder(originalText.length() + EXTRA);
        sb.append("<span style=\"");
        if (this.highlightForeground) {
            sb.append("color: ");
            sb.append(this.getForegroundColorString(score));
            sb.append("; ");
        }
        if (this.highlightBackground) {
            sb.append("background: ");
            sb.append(this.getBackgroundColorString(score));
            sb.append("; ");
        }
        sb.append("\">");
        sb.append(originalText);
        sb.append("</span>");
        return sb.toString();
    }
}

