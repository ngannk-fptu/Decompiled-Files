/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.CommandLine;
import groovyjarjarcommonscli.Options;
import groovyjarjarcommonscli.ParseException;

public interface CommandLineParser {
    public CommandLine parse(Options var1, String[] var2) throws ParseException;

    public CommandLine parse(Options var1, String[] var2, boolean var3) throws ParseException;
}

