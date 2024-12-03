/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.model.TextCollection;
import com.atlassian.confluence.plugins.highlight.model.TextMatch;
import com.atlassian.confluence.plugins.highlight.model.TextNode;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TextMatcher {
    private static final Logger logger = LoggerFactory.getLogger(TextMatcher.class);

    public TextMatch match(TextSearch textSearch, TextCollection textCollection) {
        String documentText = textCollection.getAggregatedText().replaceAll("\u00a0", " ");
        List<TextNode> textNodes = textCollection.getPositions();
        int startIndex = this.findIndexOfMatch(documentText, textSearch);
        if (startIndex == -1) {
            return null;
        }
        int endIndex = startIndex + textSearch.getText().length();
        int startPosition = this.findNodePositionContainingIndex(startIndex, textNodes, 0);
        int endPosition = this.findNodePositionContainingIndex(endIndex - 1, textNodes, startPosition);
        int firstNodeStartIndex = startIndex - textNodes.get(startPosition).getStartIndex();
        int lastNodeEndIndex = endIndex - textNodes.get(endPosition).getStartIndex();
        return new TextMatch(firstNodeStartIndex, lastNodeEndIndex, textNodes.subList(startPosition, endPosition + 1));
    }

    private int findIndexOfMatch(String src, TextSearch search) {
        int matchIndex;
        int start = 0;
        int matchCount = 0;
        int chosenIndex = -1;
        String text = search.getText();
        int searchMatchIndex = search.getMatchIndex() + 1;
        while (start < src.length() && (matchIndex = src.indexOf(text, start)) != -1) {
            start = matchIndex + 1;
            if (++matchCount != searchMatchIndex) continue;
            chosenIndex = matchIndex;
        }
        logger.debug("Finding index of match: [text search occurrence {} - text search index {}] vs [found occurrence {} - found index {}]", new Object[]{search.getNumMatches(), search.getMatchIndex(), matchCount, chosenIndex});
        return matchCount == search.getNumMatches() ? chosenIndex : -1;
    }

    private int findNodePositionContainingIndex(int index, List<TextNode> textNodes, int startPosition) {
        int position;
        for (position = startPosition + 1; position < textNodes.size() && textNodes.get(position).getStartIndex() <= index; ++position) {
        }
        return position - 1;
    }
}

