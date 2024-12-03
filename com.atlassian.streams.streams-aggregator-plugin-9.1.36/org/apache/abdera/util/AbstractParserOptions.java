/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.parser.ParserOptions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractParserOptions
implements ParserOptions,
Cloneable {
    protected Factory factory = null;
    protected String charset = null;
    protected ParseFilter parseFilter = null;
    protected boolean detect = false;
    protected boolean preserve = true;
    protected boolean filterreserved = false;
    protected char replacement = '\u0000';
    protected CompressionUtil.CompressionCodec[] codecs = null;
    protected boolean resolveentities = true;
    protected Map<String, String> entities = new HashMap<String, String>();
    protected boolean qnamealiasing = false;
    protected Map<QName, QName> aliases = null;

    protected abstract void initFactory();

    protected abstract void checkFactory(Factory var1);

    protected AbstractParserOptions() {
        this.initDefaultEntities();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractParserOptions copy = (AbstractParserOptions)super.clone();
        if (this.parseFilter != null) {
            copy.parseFilter = (ParseFilter)this.parseFilter.clone();
        }
        return copy;
    }

    @Override
    public Factory getFactory() {
        if (this.factory == null) {
            this.initFactory();
        }
        return this.factory;
    }

    @Override
    public ParserOptions setFactory(Factory factory) {
        this.checkFactory(factory);
        this.factory = factory;
        return this;
    }

    @Override
    public String getCharset() {
        return this.charset;
    }

    @Override
    public ParserOptions setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public ParseFilter getParseFilter() {
        return this.parseFilter;
    }

    @Override
    public ParserOptions setParseFilter(ParseFilter parseFilter) {
        this.parseFilter = parseFilter;
        return this;
    }

    @Override
    public boolean getAutodetectCharset() {
        return this.detect;
    }

    @Override
    public ParserOptions setAutodetectCharset(boolean detect) {
        this.detect = detect;
        return this;
    }

    @Override
    public boolean getMustPreserveWhitespace() {
        return this.preserve;
    }

    @Override
    public ParserOptions setMustPreserveWhitespace(boolean preserve) {
        this.preserve = preserve;
        return this;
    }

    @Override
    public boolean getFilterRestrictedCharacters() {
        return this.filterreserved;
    }

    @Override
    public ParserOptions setFilterRestrictedCharacters(boolean filter) {
        this.filterreserved = filter;
        return this;
    }

    @Override
    public char getFilterRestrictedCharacterReplacement() {
        return this.replacement;
    }

    @Override
    public ParserOptions setFilterRestrictedCharacterReplacement(char replacement) {
        this.replacement = replacement;
        return this;
    }

    @Override
    public CompressionUtil.CompressionCodec[] getCompressionCodecs() {
        return this.codecs;
    }

    @Override
    public ParserOptions setCompressionCodecs(CompressionUtil.CompressionCodec ... codecs) {
        this.codecs = codecs;
        return this;
    }

    @Override
    public ParserOptions registerEntity(String name, String value) {
        this.entities.put(name, value);
        return this;
    }

    private void initDefaultEntities() {
        this.registerEntity("quot", "\"");
        this.registerEntity("amp", "&");
        this.registerEntity("lt", "<");
        this.registerEntity("gt", ">");
        this.registerEntity("nbsp", " ");
        this.registerEntity("iexcl", "\u00a1");
        this.registerEntity("cent", "\u00a2");
        this.registerEntity("pound", "\u00a3");
        this.registerEntity("curren", "\u00a4");
        this.registerEntity("yen", "\u00a5");
        this.registerEntity("brvbar", "\u00a6");
        this.registerEntity("sect", "\u00a7");
        this.registerEntity("uml", "\u00a8");
        this.registerEntity("copy", "\u00a9");
        this.registerEntity("ordf", "\u00aa");
        this.registerEntity("laquo", "\u00ab");
        this.registerEntity("not", "\u00ac");
        this.registerEntity("shy", "\u00ad");
        this.registerEntity("reg", "\u00ae");
        this.registerEntity("macr", "\u00af");
        this.registerEntity("deg", "\u00b0");
        this.registerEntity("plusmn", "\u00b1");
        this.registerEntity("sup2", "\u00b2");
        this.registerEntity("sup3", "\u00b3");
        this.registerEntity("acute", "\u00b4");
        this.registerEntity("micro", "\u00b5");
        this.registerEntity("para", "\u00b6");
        this.registerEntity("middot", "\u00b7");
        this.registerEntity("cedil", "\u00b8");
        this.registerEntity("sup1", "\u00b9");
        this.registerEntity("ordm", "\u00ba");
        this.registerEntity("raquo", "\u00bb");
        this.registerEntity("frac14", "\u00bc");
        this.registerEntity("frac12", "\u00bd");
        this.registerEntity("frac34", "\u00be");
        this.registerEntity("iquest", "\u00bf");
        this.registerEntity("Agrave", "\u00c0");
        this.registerEntity("Aacute", "\u00c1");
        this.registerEntity("Acirc", "\u00c2");
        this.registerEntity("Atilde", "\u00c3");
        this.registerEntity("Auml", "\u00c4");
        this.registerEntity("Aring", "\u00c5");
        this.registerEntity("AElig", "\u00c6");
        this.registerEntity("Ccedil", "\u00c7");
        this.registerEntity("Egrave", "\u00c8");
        this.registerEntity("Eacute", "\u00c9");
        this.registerEntity("Ecirc", "\u00ca");
        this.registerEntity("Euml", "\u00cb");
        this.registerEntity("Igrave", "\u00cc");
        this.registerEntity("Iacute", "\u00cd");
        this.registerEntity("Icirc", "\u00ce");
        this.registerEntity("Iuml", "\u00cf");
        this.registerEntity("ETH", "\u00d0");
        this.registerEntity("Ntilde", "\u00d1");
        this.registerEntity("Ograve", "\u00d2");
        this.registerEntity("Oacute", "\u00d3");
        this.registerEntity("Ocirc", "\u00d4");
        this.registerEntity("Otilde", "\u00d5");
        this.registerEntity("Ouml", "\u00d6");
        this.registerEntity("times", "\u00d7");
        this.registerEntity("Oslash", "\u00d8");
        this.registerEntity("Ugrave", "\u00d9");
        this.registerEntity("Uacute", "\u00da");
        this.registerEntity("Ucirc", "\u00db");
        this.registerEntity("Uuml", "\u00dc");
        this.registerEntity("Yacute", "\u00dd");
        this.registerEntity("THORN", "\u00de");
        this.registerEntity("szlig", "\u00df");
        this.registerEntity("agrave", "\u00e0");
        this.registerEntity("aacute", "\u00e1");
        this.registerEntity("acirc", "\u00e2");
        this.registerEntity("atilde", "\u00e3");
        this.registerEntity("auml", "\u00e4");
        this.registerEntity("aring", "\u00e5");
        this.registerEntity("aelig", "\u00e6");
        this.registerEntity("ccedil", "\u00e7");
        this.registerEntity("egrave", "\u00e8");
        this.registerEntity("eacute", "\u00e9");
        this.registerEntity("ecirc", "\u00ea");
        this.registerEntity("euml", "\u00eb");
        this.registerEntity("igrave", "\u00ec");
        this.registerEntity("iacute", "\u00ed");
        this.registerEntity("icirc", "\u00ee");
        this.registerEntity("iuml", "\u00ef");
        this.registerEntity("eth", "\u00f0");
        this.registerEntity("ntilde", "\u00f1");
        this.registerEntity("ograve", "\u00f2");
        this.registerEntity("oacute", "\u00f3");
        this.registerEntity("ocirc", "\u00f4");
        this.registerEntity("otilde", "\u00f5");
        this.registerEntity("ouml", "\u00f6");
        this.registerEntity("divide", "\u00f7");
        this.registerEntity("oslash", "\u00f8");
        this.registerEntity("ugrave", "\u00f9");
        this.registerEntity("uacute", "\u00fa");
        this.registerEntity("ucirc", "\u00fb");
        this.registerEntity("uuml", "\u00fc");
        this.registerEntity("yacute", "\u00fd");
        this.registerEntity("thorn", "\u00fe");
        this.registerEntity("yuml", "\u00ff");
        this.registerEntity("OElig", "\u0152");
        this.registerEntity("oelig", "\u0153");
        this.registerEntity("Scaron", "\u0160");
        this.registerEntity("scaron", "\u0161");
        this.registerEntity("Yuml", "\u0178");
        this.registerEntity("fnof", "\u0192");
        this.registerEntity("circ", "\u02c6");
        this.registerEntity("tilde", "\u02dc");
        this.registerEntity("Alpha", "\u0391");
        this.registerEntity("Beta", "\u0392");
        this.registerEntity("Gamma", "\u0393");
        this.registerEntity("Delta", "\u0394");
        this.registerEntity("Epsilon", "\u0395");
        this.registerEntity("Zeta", "\u0396");
        this.registerEntity("Eta", "\u0397");
        this.registerEntity("Theta", "\u0398");
        this.registerEntity("Iota", "\u0399");
        this.registerEntity("Kappa", "\u039a");
        this.registerEntity("Lambda", "\u039b");
        this.registerEntity("Mu", "\u039c");
        this.registerEntity("Nu", "\u039d");
        this.registerEntity("Xi", "\u039e");
        this.registerEntity("Omicron", "\u039f");
        this.registerEntity("Pi", "\u03a0");
        this.registerEntity("Rho", "\u03a1");
        this.registerEntity("Sigma", "\u03a3");
        this.registerEntity("Tau", "\u03a4");
        this.registerEntity("Upsilon", "\u03a5");
        this.registerEntity("Phi", "\u03a6");
        this.registerEntity("Chi", "\u03a7");
        this.registerEntity("Psi", "\u03a8");
        this.registerEntity("Omega", "\u03a9");
        this.registerEntity("alpha", "\u03b1");
        this.registerEntity("beta", "\u03b2");
        this.registerEntity("gamma", "\u03b3");
        this.registerEntity("delta", "\u03b4");
        this.registerEntity("epsilon", "\u03b5");
        this.registerEntity("zeta", "\u03b6");
        this.registerEntity("eta", "\u03b7");
        this.registerEntity("theta", "\u03b8");
        this.registerEntity("iota", "\u03b9");
        this.registerEntity("kappa", "\u03ba");
        this.registerEntity("lambda", "\u03bb");
        this.registerEntity("mu", "\u03bc");
        this.registerEntity("nu", "\u03bd");
        this.registerEntity("xi", "\u03be");
        this.registerEntity("omicron", "\u03bf");
        this.registerEntity("pi", "\u03c0");
        this.registerEntity("rho", "\u03c1");
        this.registerEntity("sigmaf", "\u03c2");
        this.registerEntity("sigma", "\u03c3");
        this.registerEntity("tau", "\u03c4");
        this.registerEntity("upsilon", "\u03c5");
        this.registerEntity("phi", "\u03c6");
        this.registerEntity("chi", "\u03c7");
        this.registerEntity("psi", "\u03c8");
        this.registerEntity("omega", "\u03c9");
        this.registerEntity("thetasym", "\u03d1");
        this.registerEntity("upsih", "\u03d2");
        this.registerEntity("piv", "\u03d6");
        this.registerEntity("ensp", "\u2002");
        this.registerEntity("emsp", "\u2003");
        this.registerEntity("thinsp", "\u2009");
        this.registerEntity("zwnj", "\u200c");
        this.registerEntity("zwj", "\u200d");
        this.registerEntity("lrm", "\u200e");
        this.registerEntity("rlm", "\u200f");
        this.registerEntity("ndash", "\u2013");
        this.registerEntity("mdash", "\u2014");
        this.registerEntity("lsquo", "\u2018");
        this.registerEntity("rsquo", "\u2019");
        this.registerEntity("sbquo", "\u201a");
        this.registerEntity("ldquo", "\u201c");
        this.registerEntity("rdquo", "\u201d");
        this.registerEntity("bdquo", "\u201e");
        this.registerEntity("dagger", "\u2020");
        this.registerEntity("Dagger", "\u2021");
        this.registerEntity("bull", "\u2022");
        this.registerEntity("hellip", "\u2026");
        this.registerEntity("permil", "\u2030");
        this.registerEntity("prime", "\u2032");
        this.registerEntity("Prime", "\u2033");
        this.registerEntity("lsaquo", "\u2039");
        this.registerEntity("rsaquo", "\u203a");
        this.registerEntity("oline", "\u203e");
        this.registerEntity("frasl", "\u2044");
        this.registerEntity("euro", "\u20ac");
        this.registerEntity("image", "\u2111");
        this.registerEntity("weierp", "\u2118");
        this.registerEntity("real", "\u211c");
        this.registerEntity("trade", "\u2122");
        this.registerEntity("alefsym", "\u2135");
        this.registerEntity("larr", "\u2190");
        this.registerEntity("uarr", "\u2191");
        this.registerEntity("rarr", "\u2192");
        this.registerEntity("darr", "\u2193");
        this.registerEntity("harr", "\u2194");
        this.registerEntity("crarr", "\u21b5");
        this.registerEntity("lArr", "\u21d0");
        this.registerEntity("uArr", "\u21d1");
        this.registerEntity("rArr", "\u21d2");
        this.registerEntity("dArr", "\u21d3");
        this.registerEntity("hArr", "\u21d4");
        this.registerEntity("forall", "\u2200");
        this.registerEntity("part", "\u2202");
        this.registerEntity("exist", "\u2203");
        this.registerEntity("empty", "\u2205");
        this.registerEntity("nabla", "\u2207");
        this.registerEntity("isin", "\u2208");
        this.registerEntity("notin", "\u2209");
        this.registerEntity("ni", "\u220b");
        this.registerEntity("prod", "\u220f");
        this.registerEntity("sum", "\u2211");
        this.registerEntity("minus", "\u2212");
        this.registerEntity("lowast", "\u2217");
        this.registerEntity("radic", "\u221a");
        this.registerEntity("prop", "\u221d");
        this.registerEntity("infin", "\u221e");
        this.registerEntity("ang", "\u2220");
        this.registerEntity("and", "\u2227");
        this.registerEntity("or", "\u2228");
        this.registerEntity("cap", "\u2229");
        this.registerEntity("cup", "\u222a");
        this.registerEntity("int", "\u222b");
        this.registerEntity("there4", "\u2234");
        this.registerEntity("sim", "\u223c");
        this.registerEntity("cong", "\u2245");
        this.registerEntity("asymp", "\u2248");
        this.registerEntity("ne", "\u2260");
        this.registerEntity("equiv", "\u2261");
        this.registerEntity("le", "\u2264");
        this.registerEntity("ge", "\u2265");
        this.registerEntity("sub", "\u2282");
        this.registerEntity("sup", "\u2283");
        this.registerEntity("nsub", "\u2284");
        this.registerEntity("sube", "\u2286");
        this.registerEntity("supe", "\u2287");
        this.registerEntity("oplus", "\u2295");
        this.registerEntity("otimes", "\u2297");
        this.registerEntity("perp", "\u22a5");
        this.registerEntity("sdot", "\u22c5");
        this.registerEntity("lceil", "\u2308");
        this.registerEntity("rceil", "\u2309");
        this.registerEntity("lfloor", "\u230a");
        this.registerEntity("rfloor", "\u230b");
        this.registerEntity("lang", "\u2329");
        this.registerEntity("rang", "\u232a");
        this.registerEntity("loz", "\u25ca");
        this.registerEntity("spades", "\u2660");
        this.registerEntity("clubs", "\u2663");
        this.registerEntity("hearts", "\u2665");
        this.registerEntity("diams", "\u2666");
    }

    @Override
    public String resolveEntity(String name) {
        return this.resolveentities ? this.entities.get(name) : null;
    }

    @Override
    public ParserOptions setResolveEntities(boolean resolve) {
        this.resolveentities = resolve;
        return this;
    }

    @Override
    public boolean getResolveEntities() {
        return this.resolveentities;
    }

    @Override
    public Map<QName, QName> getQNameAliasMap() {
        return this.aliases;
    }

    @Override
    public ParserOptions setQNameAliasMap(Map<QName, QName> map) {
        this.aliases = map;
        return this;
    }

    @Override
    public boolean isQNameAliasMappingEnabled() {
        return this.qnamealiasing;
    }

    @Override
    public ParserOptions setQNameAliasMappingEnabled(boolean enabled) {
        this.qnamealiasing = enabled;
        return this;
    }
}

