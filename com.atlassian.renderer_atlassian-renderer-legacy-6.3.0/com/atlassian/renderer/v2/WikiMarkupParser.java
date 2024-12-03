/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2;

import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;

public class WikiMarkupParser {
    WikiContentHandler wikiContentHandler;
    private MacroManager macroManager;

    public WikiMarkupParser(MacroManager macroManager, WikiContentHandler wikiContentHandler) {
        this.wikiContentHandler = wikiContentHandler;
        this.macroManager = macroManager;
    }

    public String parse(String wiki) {
        StringBuffer out = new StringBuffer(wiki.length());
        if (wiki.indexOf("{") == -1) {
            this.wikiContentHandler.handleText(out, wiki);
            return out.toString();
        }
        int lastStart = 0;
        boolean inEscape = false;
        for (int i = 0; i < wiki.length(); ++i) {
            char c = wiki.charAt(i);
            if (!inEscape) {
                switch (c) {
                    case '\\': {
                        inEscape = true;
                        break;
                    }
                    case '{': {
                        if (wiki.length() > i + 1 && "{*?^_-+~".indexOf(wiki.charAt(i + 1)) != -1) {
                            ++i;
                            break;
                        }
                        this.wikiContentHandler.handleText(out, wiki.substring(lastStart, i));
                        lastStart = i + 1;
                        i = this.handlePotentialMacro(wiki, i, out);
                        lastStart = i + 1;
                    }
                }
                continue;
            }
            inEscape = false;
        }
        if (lastStart < wiki.length()) {
            this.wikiContentHandler.handleText(out, wiki.substring(lastStart));
        }
        return out.toString();
    }

    private int handlePotentialMacro(String wiki, int i, StringBuffer out) {
        MacroTag startTag = MacroTag.makeMacroTag(wiki, i);
        if (startTag != null) {
            Macro macro = this.getMacroByName(startTag.command);
            if (macro == null || macro.hasBody()) {
                this.setEndTagIfPresent(wiki, startTag);
            }
            if (startTag.getEndTag() != null) {
                MacroTag endTag = startTag.getEndTag();
                String body = wiki.substring(startTag.endIndex + 1, endTag.startIndex);
                if ("\n".equals(body) && startTag.isNewlineAfter() && endTag.isNewlineBefore()) {
                    endTag.removeNewlineBefore();
                }
                this.makeMacro(out, startTag, body);
                i = endTag.endIndex;
            } else {
                this.makeMacro(out, startTag, "");
                i = startTag.endIndex;
            }
        } else {
            out.append('{');
        }
        return i;
    }

    private void makeMacro(StringBuffer buffer, MacroTag startTag, String body) {
        this.wikiContentHandler.handleMacro(buffer, startTag, body);
    }

    private Macro getMacroByName(String name) {
        if (name == null) {
            return null;
        }
        return this.macroManager.getEnabledMacro(name.toLowerCase());
    }

    private void setEndTagIfPresent(String wiki, MacroTag startTag) {
        boolean inEscape = false;
        for (int i = startTag.startIndex + startTag.originalText.length(); i < wiki.length(); ++i) {
            char c = wiki.charAt(i);
            if (inEscape) {
                inEscape = false;
                continue;
            }
            if (c == '{') {
                MacroTag endTag = MacroTag.makeMacroTag(wiki, i);
                if (endTag == null || !startTag.command.equals(endTag.command) || endTag.argString.length() != 0) continue;
                startTag.setEndTag(endTag);
                return;
            }
            if (c != '\\') continue;
            inEscape = true;
        }
    }
}

