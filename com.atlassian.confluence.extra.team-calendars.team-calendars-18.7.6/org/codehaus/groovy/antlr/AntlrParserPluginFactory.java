/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import org.codehaus.groovy.antlr.AntlrParserPlugin;
import org.codehaus.groovy.control.ParserPlugin;
import org.codehaus.groovy.control.ParserPluginFactory;

public class AntlrParserPluginFactory
extends ParserPluginFactory {
    @Override
    public ParserPlugin createParserPlugin() {
        return new AntlrParserPlugin();
    }
}

