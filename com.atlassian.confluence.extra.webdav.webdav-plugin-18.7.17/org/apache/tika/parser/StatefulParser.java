/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;

public class StatefulParser
extends ParserDecorator {
    public StatefulParser(Parser parser) {
        super(parser);
    }
}

