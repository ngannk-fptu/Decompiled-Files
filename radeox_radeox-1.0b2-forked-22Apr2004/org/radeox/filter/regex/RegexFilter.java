/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter.regex;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.filter.FilterSupport;
import org.radeox.filter.context.FilterContext;
import org.radeox.regex.Compiler;

public abstract class RegexFilter
extends FilterSupport {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$regex$RegexFilter == null ? (class$org$radeox$filter$regex$RegexFilter = RegexFilter.class$("org.radeox.filter.regex.RegexFilter")) : class$org$radeox$filter$regex$RegexFilter));
    protected List pattern = new ArrayList();
    protected List substitute = new ArrayList();
    public static final boolean SINGLELINE = false;
    public static final boolean MULTILINE = true;
    static /* synthetic */ Class class$org$radeox$filter$regex$RegexFilter;

    public RegexFilter() {
    }

    public RegexFilter(String regex, String substitute) {
        this();
        this.addRegex(regex, substitute);
    }

    public RegexFilter(String regex, String substitute, boolean multiline) {
        this.addRegex(regex, substitute, multiline);
    }

    public void clearRegex() {
        this.pattern.clear();
        this.substitute.clear();
    }

    public void addRegex(String regex, String substitute) {
        this.addRegex(regex, substitute, true);
    }

    public void addRegex(String regex, String substitute, boolean multiline) {
        try {
            Compiler compiler = Compiler.create();
            compiler.setMultiline(multiline);
            this.pattern.add(compiler.compile(regex));
            this.substitute.add(substitute);
        }
        catch (Exception e) {
            log.warn((Object)("bad pattern: " + regex + " -> " + substitute + " " + e));
        }
    }

    public abstract String filter(String var1, FilterContext var2);

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

