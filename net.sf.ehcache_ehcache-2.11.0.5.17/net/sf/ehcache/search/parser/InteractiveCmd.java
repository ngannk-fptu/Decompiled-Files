/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import net.sf.ehcache.search.parser.ParseModel;

public class InteractiveCmd {
    private Cmd type;
    private ParseModel qmodel;
    private String ident;

    public InteractiveCmd(ParseModel qmodel) {
        this.type = Cmd.Select;
        this.qmodel = qmodel;
    }

    public InteractiveCmd(Cmd cmd, String ident) {
        this.type = cmd;
        this.ident = ident;
    }

    public Cmd getType() {
        return this.type;
    }

    public ParseModel getParseModel() {
        return this.qmodel;
    }

    public String getIdent() {
        return this.ident;
    }

    public static enum Cmd {
        Select,
        UseCache,
        UseCacheManager;

    }
}

