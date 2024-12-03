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
import org.radeox.regex.MatchResult;
import org.radeox.regex.Matcher;
import org.radeox.regex.Pattern;
import org.radeox.regex.Substitution;

public abstract class RegexTokenFilter
extends RegexFilter {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$regex$RegexTokenFilter == null ? (class$org$radeox$filter$regex$RegexTokenFilter = RegexTokenFilter.class$("org.radeox.filter.regex.RegexTokenFilter")) : class$org$radeox$filter$regex$RegexTokenFilter));
    static /* synthetic */ Class class$org$radeox$filter$regex$RegexTokenFilter;

    public RegexTokenFilter() {
    }

    public RegexTokenFilter(String regex, boolean multiline) {
        super(regex, "", multiline);
    }

    public RegexTokenFilter(String regex) {
        super(regex, "");
    }

    protected void setUp(FilterContext context) {
    }

    public abstract void handleMatch(StringBuffer var1, MatchResult var2, FilterContext var3);

    public String filter(String input, final FilterContext context) {
        this.setUp(context);
        String result = null;
        int size = this.pattern.size();
        for (int i = 0; i < size; ++i) {
            Pattern p = (Pattern)this.pattern.get(i);
            try {
                Matcher m = Matcher.create(input, p);
                result = m.substitute(new Substitution(){

                    public void handleMatch(StringBuffer buffer, MatchResult result) {
                        RegexTokenFilter.this.handleMatch(buffer, result, context);
                    }
                });
            }
            catch (Exception e) {
                log.warn((Object)("<span class=\"error\">Exception</span>: " + this), (Throwable)e);
            }
            catch (Error err) {
                log.warn((Object)("<span class=\"error\">Error</span>: " + this + ": " + err));
                err.printStackTrace();
            }
            input = result;
        }
        return input;
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

