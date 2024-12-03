/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser;

import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.i18n.text.io.CompressionUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ParserOptions
extends Cloneable {
    public Object clone() throws CloneNotSupportedException;

    public Factory getFactory();

    public ParserOptions setFactory(Factory var1);

    public String getCharset();

    public ParserOptions setCharset(String var1);

    public ParseFilter getParseFilter();

    public ParserOptions setParseFilter(ParseFilter var1);

    public boolean getAutodetectCharset();

    public ParserOptions setAutodetectCharset(boolean var1);

    public boolean getMustPreserveWhitespace();

    public ParserOptions setMustPreserveWhitespace(boolean var1);

    public boolean getFilterRestrictedCharacters();

    public ParserOptions setFilterRestrictedCharacters(boolean var1);

    public char getFilterRestrictedCharacterReplacement();

    public ParserOptions setFilterRestrictedCharacterReplacement(char var1);

    public CompressionUtil.CompressionCodec[] getCompressionCodecs();

    public ParserOptions setCompressionCodecs(CompressionUtil.CompressionCodec ... var1);

    public ParserOptions registerEntity(String var1, String var2);

    public String resolveEntity(String var1);

    public ParserOptions setResolveEntities(boolean var1);

    public boolean getResolveEntities();

    public ParserOptions setQNameAliasMappingEnabled(boolean var1);

    public boolean isQNameAliasMappingEnabled();

    public Map<QName, QName> getQNameAliasMap();

    public ParserOptions setQNameAliasMap(Map<QName, QName> var1);
}

