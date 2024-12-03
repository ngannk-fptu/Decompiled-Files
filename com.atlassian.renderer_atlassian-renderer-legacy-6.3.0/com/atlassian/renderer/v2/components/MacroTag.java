/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.v2.RenderUtils;

public class MacroTag {
    public final int startIndex;
    public final int endIndex;
    public final String originalText;
    public final String command;
    public final String argString;
    private final boolean newlineAfter;
    private boolean newlineBefore;
    private MacroTag endTag;

    public static MacroTag makeMacroTag(String wiki, int startIndex) {
        if (wiki.charAt(startIndex) != '{' || startIndex + 3 > wiki.length()) {
            return null;
        }
        boolean inCommand = true;
        boolean escapeNext = false;
        StringBuffer command = new StringBuffer();
        StringBuffer args = new StringBuffer();
        block6: for (int i = startIndex + 1; i < wiki.length(); ++i) {
            char c = wiki.charAt(i);
            if (!escapeNext) {
                switch (c) {
                    case '}': {
                        return MacroTag.makeMacroTag(wiki, startIndex, i, command.toString(), args.toString());
                    }
                    case '\n': 
                    case '\r': 
                    case '\\': {
                        escapeNext = true;
                        continue block6;
                    }
                    case '{': {
                        return null;
                    }
                    case ':': {
                        if (inCommand) {
                            inCommand = false;
                            continue block6;
                        }
                        args.append(':');
                        continue block6;
                    }
                }
            } else {
                escapeNext = false;
            }
            if (inCommand) {
                command.append(c);
                continue;
            }
            args.append(c);
        }
        return null;
    }

    private static MacroTag makeMacroTag(String wiki, int startIndex, int i, String command, String args) {
        if (RenderUtils.isBlank(command) || command.startsWith("$")) {
            return null;
        }
        String originalText = wiki.substring(startIndex, i + 1);
        if (MacroTag.evenNumberOfBracketsAt(wiki, i)) {
            return null;
        }
        boolean newlineBefore = startIndex > 0 && wiki.charAt(startIndex - 1) == '\n';
        boolean newlineAfter = i + 1 < wiki.length() && wiki.charAt(i + 1) == '\n';
        return new MacroTag(startIndex, originalText, command, args, newlineBefore, newlineAfter);
    }

    private static boolean evenNumberOfBracketsAt(String wiki, int i) {
        int j = 0;
        while (j + i < wiki.length()) {
            if (wiki.charAt(j + i) != '}') {
                return j % 2 == 0;
            }
            ++j;
        }
        return false;
    }

    private MacroTag(int startIndex, String originalText, String command, String argString, boolean newlineBefore, boolean newlineAfter) {
        this.startIndex = startIndex;
        this.endIndex = startIndex + originalText.length() - 1;
        this.originalText = originalText;
        this.command = command;
        this.argString = argString;
        this.newlineBefore = newlineBefore;
        this.newlineAfter = newlineAfter;
    }

    public boolean isNewlineBefore() {
        return this.newlineBefore;
    }

    public void removeNewlineBefore() {
        this.newlineBefore = false;
    }

    public boolean isNewlineAfter() {
        return this.newlineAfter;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MacroTag macroTag = (MacroTag)o;
        if (this.endIndex != macroTag.endIndex) {
            return false;
        }
        if (this.newlineAfter != macroTag.newlineAfter) {
            return false;
        }
        if (this.newlineBefore != macroTag.newlineBefore) {
            return false;
        }
        if (this.startIndex != macroTag.startIndex) {
            return false;
        }
        if (this.argString != null ? !this.argString.equals(macroTag.argString) : macroTag.argString != null) {
            return false;
        }
        if (this.command != null ? !this.command.equals(macroTag.command) : macroTag.command != null) {
            return false;
        }
        if (this.endTag != null ? !this.endTag.equals(macroTag.endTag) : macroTag.endTag != null) {
            return false;
        }
        return !(this.originalText != null ? !this.originalText.equals(macroTag.originalText) : macroTag.originalText != null);
    }

    public int hashCode() {
        int result = this.startIndex;
        result = 31 * result + (this.newlineBefore ? 1 : 0);
        result = 31 * result + (this.newlineAfter ? 1 : 0);
        result = 31 * result + this.endIndex;
        result = 31 * result + (this.originalText != null ? this.originalText.hashCode() : 0);
        result = 31 * result + (this.command != null ? this.command.hashCode() : 0);
        result = 31 * result + (this.argString != null ? this.argString.hashCode() : 0);
        result = 31 * result + (this.endTag != null ? this.endTag.hashCode() : 0);
        return result;
    }

    public void setEndTag(MacroTag endTag) {
        this.endTag = endTag;
    }

    public MacroTag getEndTag() {
        return this.endTag;
    }

    public String toString() {
        return this.originalText;
    }
}

