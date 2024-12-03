/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.Options;
import groovyjarjarcommonscli.Parser;

public class BasicParser
extends Parser {
    protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption) {
        return arguments;
    }
}

