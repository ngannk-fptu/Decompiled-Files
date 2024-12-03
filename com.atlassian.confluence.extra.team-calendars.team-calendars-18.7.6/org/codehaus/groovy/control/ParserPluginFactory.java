/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import org.codehaus.groovy.antlr.AntlrParserPluginFactory;
import org.codehaus.groovy.control.ParserPlugin;

public abstract class ParserPluginFactory {
    @Deprecated
    public static ParserPluginFactory newInstance(boolean useNewParser) {
        return ParserPluginFactory.newInstance();
    }

    public static ParserPluginFactory newInstance() {
        return new AntlrParserPluginFactory();
    }

    public abstract ParserPlugin createParserPlugin();
}

