/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.filter.CacheFilter;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.LocaleRegexTokenFilter;
import org.radeox.regex.MatchResult;

public class ListFilter
extends LocaleRegexTokenFilter
implements CacheFilter {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$ListFilter == null ? (class$org$radeox$filter$ListFilter = ListFilter.class$("org.radeox.filter.ListFilter")) : class$org$radeox$filter$ListFilter));
    private static final Map openList = new HashMap();
    private static final Map closeList = new HashMap();
    private static final String UL_CLOSE = "</ul>";
    private static final String OL_CLOSE = "</ol>";
    static /* synthetic */ Class class$org$radeox$filter$ListFilter;

    protected String getLocaleKey() {
        return "filter.list";
    }

    protected boolean isSingleLine() {
        return false;
    }

    public ListFilter() {
        openList.put(new Character('-'), "<ul class=\"minus\">");
        openList.put(new Character('*'), "<ul class=\"star\">");
        openList.put(new Character('#'), "<ol>");
        openList.put(new Character('i'), "<ol class=\"roman\">");
        openList.put(new Character('I'), "<ol class=\"ROMAN\">");
        openList.put(new Character('a'), "<ol class=\"alpha\">");
        openList.put(new Character('A'), "<ol class=\"ALPHA\">");
        openList.put(new Character('g'), "<ol class=\"greek\">");
        openList.put(new Character('h'), "<ol class=\"hiragana\">");
        openList.put(new Character('H'), "<ol class=\"HIRAGANA\">");
        openList.put(new Character('k'), "<ol class=\"katakana\">");
        openList.put(new Character('K'), "<ol class=\"KATAKANA\">");
        openList.put(new Character('j'), "<ol class=\"HEBREW\">");
        openList.put(new Character('1'), "<ol>");
        closeList.put(new Character('-'), UL_CLOSE);
        closeList.put(new Character('*'), UL_CLOSE);
        closeList.put(new Character('#'), OL_CLOSE);
        closeList.put(new Character('i'), OL_CLOSE);
        closeList.put(new Character('I'), OL_CLOSE);
        closeList.put(new Character('a'), OL_CLOSE);
        closeList.put(new Character('A'), OL_CLOSE);
        closeList.put(new Character('1'), OL_CLOSE);
        closeList.put(new Character('g'), OL_CLOSE);
        closeList.put(new Character('G'), OL_CLOSE);
        closeList.put(new Character('h'), OL_CLOSE);
        closeList.put(new Character('H'), OL_CLOSE);
        closeList.put(new Character('k'), OL_CLOSE);
        closeList.put(new Character('K'), OL_CLOSE);
        closeList.put(new Character('j'), OL_CLOSE);
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(result.group(0)));
            this.addList(buffer, reader);
        }
        catch (Exception e) {
            log.warn((Object)"ListFilter: unable get list content", (Throwable)e);
        }
    }

    private void addList(StringBuffer buffer, BufferedReader reader) throws IOException {
        char[] lastBullet = new char[]{};
        String line = null;
        while ((line = reader.readLine()) != null) {
            int i;
            int bulletEnd;
            if ((line = line.trim()).length() == 0 || (bulletEnd = line.indexOf(32)) < 1) continue;
            if (line.charAt(bulletEnd - 1) == '.') {
                --bulletEnd;
            }
            char[] bullet = line.substring(0, bulletEnd).toCharArray();
            for (int sharedPrefixEnd = 0; bullet.length > sharedPrefixEnd && lastBullet.length > sharedPrefixEnd && bullet[sharedPrefixEnd] == lastBullet[sharedPrefixEnd]; ++sharedPrefixEnd) {
            }
            for (i = sharedPrefixEnd; i < lastBullet.length; ++i) {
                buffer.append(closeList.get(new Character(lastBullet[i]))).append("\n");
            }
            for (i = sharedPrefixEnd; i < bullet.length; ++i) {
                buffer.append(openList.get(new Character(bullet[i]))).append("\n");
            }
            buffer.append("<li>");
            buffer.append(line.substring(line.indexOf(32) + 1));
            buffer.append("</li>\n");
            lastBullet = bullet;
        }
        for (int i = lastBullet.length - 1; i >= 0; --i) {
            buffer.append(closeList.get(new Character(lastBullet[i])));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

