/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.powerassert;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.powerassert.SourceTextNotAvailableException;

public class SourceText {
    private final int firstLine;
    private String normalizedText;
    private final List<Integer> lineOffsets = new ArrayList<Integer>();
    private final List<Integer> textOffsets = new ArrayList<Integer>();

    public SourceText(AssertStatement stat, SourceUnit sourceUnit, Janitor janitor) {
        if (!SourceText.hasPlausibleSourcePosition(stat)) {
            throw new SourceTextNotAvailableException(stat, sourceUnit, "Invalid source position");
        }
        this.firstLine = stat.getLineNumber();
        this.textOffsets.add(0);
        StringBuilder normalizedTextBuffer = new StringBuilder();
        for (int line = stat.getLineNumber(); line <= stat.getLastLineNumber(); ++line) {
            String lineText = sourceUnit.getSample(line, 0, janitor);
            if (lineText == null) {
                throw new SourceTextNotAvailableException(stat, sourceUnit, "SourceUnit.getSample() returned null");
            }
            if (line == stat.getLastLineNumber()) {
                lineText = lineText.substring(0, stat.getLastColumnNumber() - 1);
            }
            if (line == stat.getLineNumber()) {
                lineText = lineText.substring(stat.getColumnNumber() - 1);
                this.lineOffsets.add(stat.getColumnNumber() - 1);
            } else {
                this.lineOffsets.add(SourceText.countLeadingWhitespace(lineText));
            }
            lineText = lineText.trim();
            if (line != stat.getLastLineNumber() && lineText.length() > 0) {
                lineText = lineText + ' ';
            }
            normalizedTextBuffer.append(lineText);
            this.textOffsets.add(normalizedTextBuffer.length());
        }
        this.normalizedText = normalizedTextBuffer.toString();
    }

    public String getNormalizedText() {
        return this.normalizedText;
    }

    public int getNormalizedColumn(int line, int column) {
        int deltaLine = line - this.firstLine;
        if (deltaLine < 0 || deltaLine >= this.lineOffsets.size()) {
            return -1;
        }
        int deltaColumn = column - this.lineOffsets.get(deltaLine);
        if (deltaColumn < 0) {
            return -1;
        }
        return this.textOffsets.get(deltaLine) + deltaColumn;
    }

    private static boolean hasPlausibleSourcePosition(ASTNode node) {
        return node.getLineNumber() > 0 && node.getColumnNumber() > 0 && node.getLastLineNumber() >= node.getLineNumber() && node.getLastColumnNumber() > (node.getLineNumber() == node.getLastLineNumber() ? node.getColumnNumber() : 0);
    }

    private static int countLeadingWhitespace(String lineText) {
        int result;
        for (result = 0; result < lineText.length() && Character.isWhitespace(lineText.charAt(result)); ++result) {
        }
        return result;
    }
}

