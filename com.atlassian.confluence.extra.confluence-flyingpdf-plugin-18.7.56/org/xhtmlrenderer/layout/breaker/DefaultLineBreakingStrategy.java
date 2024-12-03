/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout.breaker;

import java.text.BreakIterator;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.breaker.BreakPoint;
import org.xhtmlrenderer.layout.breaker.BreakPointsProvider;
import org.xhtmlrenderer.layout.breaker.LineBreakingStrategy;
import org.xhtmlrenderer.layout.breaker.UrlAwareLineBreakIterator;

public class DefaultLineBreakingStrategy
implements LineBreakingStrategy {
    @Override
    public BreakPointsProvider getBreakPointsProvider(String text, String lang, CalculatedStyle style) {
        final UrlAwareLineBreakIterator i = new UrlAwareLineBreakIterator();
        ((BreakIterator)i).setText(text);
        return new BreakPointsProvider(){

            @Override
            public BreakPoint next() {
                int next = i.next();
                if (next < 0) {
                    return BreakPoint.getDonePoint();
                }
                return new BreakPoint(next);
            }
        };
    }
}

