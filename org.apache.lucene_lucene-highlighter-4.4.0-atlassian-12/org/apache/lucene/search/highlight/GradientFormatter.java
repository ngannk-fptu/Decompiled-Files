/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class GradientFormatter
implements Formatter {
    private float maxScore;
    int fgRMin;
    int fgGMin;
    int fgBMin;
    int fgRMax;
    int fgGMax;
    int fgBMax;
    protected boolean highlightForeground;
    int bgRMin;
    int bgGMin;
    int bgBMin;
    int bgRMax;
    int bgGMax;
    int bgBMax;
    protected boolean highlightBackground;
    private static char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public GradientFormatter(float maxScore, String minForegroundColor, String maxForegroundColor, String minBackgroundColor, String maxBackgroundColor) {
        boolean bl = this.highlightForeground = minForegroundColor != null && maxForegroundColor != null;
        if (this.highlightForeground) {
            if (minForegroundColor.length() != 7) {
                throw new IllegalArgumentException("minForegroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            if (maxForegroundColor.length() != 7) {
                throw new IllegalArgumentException("minForegroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            this.fgRMin = GradientFormatter.hexToInt(minForegroundColor.substring(1, 3));
            this.fgGMin = GradientFormatter.hexToInt(minForegroundColor.substring(3, 5));
            this.fgBMin = GradientFormatter.hexToInt(minForegroundColor.substring(5, 7));
            this.fgRMax = GradientFormatter.hexToInt(maxForegroundColor.substring(1, 3));
            this.fgGMax = GradientFormatter.hexToInt(maxForegroundColor.substring(3, 5));
            this.fgBMax = GradientFormatter.hexToInt(maxForegroundColor.substring(5, 7));
        }
        boolean bl2 = this.highlightBackground = minBackgroundColor != null && maxBackgroundColor != null;
        if (this.highlightBackground) {
            if (minBackgroundColor.length() != 7) {
                throw new IllegalArgumentException("minBackgroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            if (maxBackgroundColor.length() != 7) {
                throw new IllegalArgumentException("minBackgroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            this.bgRMin = GradientFormatter.hexToInt(minBackgroundColor.substring(1, 3));
            this.bgGMin = GradientFormatter.hexToInt(minBackgroundColor.substring(3, 5));
            this.bgBMin = GradientFormatter.hexToInt(minBackgroundColor.substring(5, 7));
            this.bgRMax = GradientFormatter.hexToInt(maxBackgroundColor.substring(1, 3));
            this.bgGMax = GradientFormatter.hexToInt(maxBackgroundColor.substring(3, 5));
            this.bgBMax = GradientFormatter.hexToInt(maxBackgroundColor.substring(5, 7));
        }
        this.maxScore = maxScore;
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
        StringBuilder sb = new StringBuilder();
        sb.append("<font ");
        if (this.highlightForeground) {
            sb.append("color=\"");
            sb.append(this.getForegroundColorString(score));
            sb.append("\" ");
        }
        if (this.highlightBackground) {
            sb.append("bgcolor=\"");
            sb.append(this.getBackgroundColorString(score));
            sb.append("\" ");
        }
        sb.append(">");
        sb.append(originalText);
        sb.append("</font>");
        return sb.toString();
    }

    protected String getForegroundColorString(float score) {
        int rVal = this.getColorVal(this.fgRMin, this.fgRMax, score);
        int gVal = this.getColorVal(this.fgGMin, this.fgGMax, score);
        int bVal = this.getColorVal(this.fgBMin, this.fgBMax, score);
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(GradientFormatter.intToHex(rVal));
        sb.append(GradientFormatter.intToHex(gVal));
        sb.append(GradientFormatter.intToHex(bVal));
        return sb.toString();
    }

    protected String getBackgroundColorString(float score) {
        int rVal = this.getColorVal(this.bgRMin, this.bgRMax, score);
        int gVal = this.getColorVal(this.bgGMin, this.bgGMax, score);
        int bVal = this.getColorVal(this.bgBMin, this.bgBMax, score);
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(GradientFormatter.intToHex(rVal));
        sb.append(GradientFormatter.intToHex(gVal));
        sb.append(GradientFormatter.intToHex(bVal));
        return sb.toString();
    }

    private int getColorVal(int colorMin, int colorMax, float score) {
        if (colorMin == colorMax) {
            return colorMin;
        }
        float scale = Math.abs(colorMin - colorMax);
        float relScorePercent = Math.min(this.maxScore, score) / this.maxScore;
        float colScore = scale * relScorePercent;
        return Math.min(colorMin, colorMax) + (int)colScore;
    }

    private static String intToHex(int i) {
        return "" + hexDigits[(i & 0xF0) >> 4] + hexDigits[i & 0xF];
    }

    public static final int hexToInt(String hex) {
        int len = hex.length();
        if (len > 16) {
            throw new NumberFormatException();
        }
        int l = 0;
        for (int i = 0; i < len; ++i) {
            l <<= 4;
            int c = Character.digit(hex.charAt(i), 16);
            if (c < 0) {
                throw new NumberFormatException();
            }
            l |= c;
        }
        return l;
    }
}

