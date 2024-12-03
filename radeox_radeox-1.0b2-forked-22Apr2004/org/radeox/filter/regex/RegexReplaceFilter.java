/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter.regex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.RegexFilter;
import org.radeox.regex.Matcher;
import org.radeox.regex.Pattern;

public class RegexReplaceFilter
extends RegexFilter {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$regex$RegexReplaceFilter == null ? (class$org$radeox$filter$regex$RegexReplaceFilter = RegexReplaceFilter.class$("org.radeox.filter.regex.RegexReplaceFilter")) : class$org$radeox$filter$regex$RegexReplaceFilter));
    static /* synthetic */ Class class$org$radeox$filter$regex$RegexReplaceFilter;

    public RegexReplaceFilter() {
    }

    public RegexReplaceFilter(String regex, String substitute) {
        super(regex, substitute);
    }

    public RegexReplaceFilter(String regex, String substitute, boolean multiline) {
        super(regex, substitute, multiline);
    }

    public String filter(String input, FilterContext context) {
        String result = input;
        int size = this.pattern.size();
        for (int i = 0; i < size; ++i) {
            Pattern p = (Pattern)this.pattern.get(i);
            String s = (String)this.substitute.get(i);
            try {
                Matcher matcher = Matcher.create(result, p);
                result = matcher.substitute(s);
                continue;
            }
            catch (Exception e) {
                log.warn((Object)("Exception for: " + this + " " + e));
                continue;
            }
            catch (Error err) {
                log.warn((Object)("Error for: " + this));
                err.printStackTrace();
            }
        }
        return result;
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

