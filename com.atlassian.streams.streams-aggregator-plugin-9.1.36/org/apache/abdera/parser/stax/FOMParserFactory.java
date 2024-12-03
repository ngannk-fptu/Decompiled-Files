/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.AbstractParser;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMParserFactory
implements ParserFactory {
    private final Abdera abdera;
    private final Map<String, NamedParser> parsers;

    public FOMParserFactory() {
        this(new Abdera());
    }

    public FOMParserFactory(Abdera abdera) {
        this.abdera = abdera;
        HashMap p = this.getAbdera().getConfiguration().getNamedParsers();
        this.parsers = p != null ? p : new HashMap();
    }

    protected Abdera getAbdera() {
        return this.abdera;
    }

    @Override
    public <T extends Parser> T getParser() {
        return (T)this.getAbdera().getParser();
    }

    @Override
    public <T extends Parser> T getParser(String name) {
        T parser;
        Object object = parser = name != null ? (Parser)this.getParsers().get(name.toLowerCase()) : this.getParser();
        if (parser instanceof AbstractParser) {
            ((AbstractParser)parser).setAbdera(this.abdera);
        }
        return parser;
    }

    private Map<String, NamedParser> getParsers() {
        return this.parsers;
    }
}

