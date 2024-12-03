/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor;

public class ChangeText {
    private int maxNbCharsPerLine;
    private StringBuilder txt = new StringBuilder();
    public static final String newLine = "<br/>";
    private int charsThisLine = 0;

    public ChangeText(int maxNbCharsPerLine) {
        this.maxNbCharsPerLine = maxNbCharsPerLine;
    }

    public synchronized void addText(String s) {
        if ((s = this.clean(s)).length() + this.charsThisLine > this.maxNbCharsPerLine) {
            this.addTextCarefully(s);
        } else {
            this.addToLine(s);
        }
    }

    private void addToLine(String s) {
        this.txt.append(s);
        this.charsThisLine += s.length();
    }

    public synchronized void addHtml(String s) {
        this.txt.append(s);
        if (s.contains("</li>") || s.contains("</ol>") || s.contains("</ul>")) {
            this.charsThisLine = 0;
        }
    }

    private synchronized void addTextCarefully(String s) {
        int firstSpace = s.indexOf(" ");
        if (firstSpace < 0) {
            if (s.length() < this.maxNbCharsPerLine) {
                if (this.charsThisLine > 0) {
                    this.addNewLine();
                }
                this.addText(s);
            } else {
                this.addNewLine();
                this.addTextBrokenAcrossLines(s);
            }
        } else if (firstSpace + 1 >= this.maxNbCharsPerLine) {
            if (this.charsThisLine > 0) {
                this.addNewLine();
            }
            this.addTextBrokenAcrossLines(s.substring(0, firstSpace + 1));
            if (firstSpace + 1 < s.length()) {
                this.addText(s.substring(firstSpace + 1, s.length()));
            }
        } else if (firstSpace + 1 + this.charsThisLine > this.maxNbCharsPerLine) {
            this.addNewLine();
            this.addText(s);
        } else {
            this.addText(s.substring(0, firstSpace + 1));
            if (firstSpace + 1 < s.length()) {
                this.addTextCarefully(s.substring(firstSpace + 1, s.length()));
            }
        }
    }

    private void addTextBrokenAcrossLines(String s) {
        assert (s.indexOf(32) < 0 && s.length() > this.maxNbCharsPerLine);
        int firstPart = Math.min(s.length(), this.maxNbCharsPerLine - this.charsThisLine);
        this.addText(s.substring(0, firstPart));
        this.addNewLine();
        this.addText(s.substring(firstPart, s.length()));
    }

    public synchronized void addNewLine() {
        this.addHtml(newLine);
        this.charsThisLine = 0;
    }

    public String toString() {
        return this.txt.toString();
    }

    private String clean(String s) {
        return s.replaceAll("\n", "").replaceAll("\r", "").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&#39;").replaceAll("\"", "&#34;");
    }
}

