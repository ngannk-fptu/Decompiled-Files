/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;
import java.util.Map;

public class EntitiesToUnicode {
    @Deprecated
    public static final HashMap<String, Character> map = new HashMap();

    public static Map<String, Character> getMap() {
        return map;
    }

    public static char decodeEntity(String name) {
        if (name.startsWith("#x")) {
            try {
                return (char)Integer.parseInt(name.substring(2), 16);
            }
            catch (NumberFormatException nfe) {
                return '\u0000';
            }
        }
        if (name.startsWith("#")) {
            try {
                return (char)Integer.parseInt(name.substring(1));
            }
            catch (NumberFormatException nfe) {
                return '\u0000';
            }
        }
        Character c = map.get(name);
        if (c == null) {
            return '\u0000';
        }
        return c.charValue();
    }

    public static String decodeString(String s) {
        int pos_amp = s.indexOf(38);
        if (pos_amp == -1) {
            return s;
        }
        StringBuilder buf = new StringBuilder(s.substring(0, pos_amp));
        while (true) {
            int pos_sc;
            if ((pos_sc = s.indexOf(59, pos_amp)) == -1) {
                buf.append(s.substring(pos_amp));
                return buf.toString();
            }
            int pos_a = s.indexOf(38, pos_amp + 1);
            while (pos_a != -1 && pos_a < pos_sc) {
                buf.append(s, pos_amp, pos_a);
                pos_amp = pos_a;
                pos_a = s.indexOf(38, pos_amp + 1);
            }
            char replace = EntitiesToUnicode.decodeEntity(s.substring(pos_amp + 1, pos_sc));
            if (s.length() < pos_sc + 1) {
                return buf.toString();
            }
            if (replace == '\u0000') {
                buf.append(s, pos_amp, pos_sc + 1);
            } else {
                buf.append(replace);
            }
            pos_amp = s.indexOf(38, pos_sc);
            if (pos_amp == -1) {
                buf.append(s.substring(pos_sc + 1));
                return buf.toString();
            }
            buf.append(s, pos_sc + 1, pos_amp);
        }
    }

    static {
        map.put("nbsp", Character.valueOf('\u00a0'));
        map.put("iexcl", Character.valueOf('\u00a1'));
        map.put("cent", Character.valueOf('\u00a2'));
        map.put("pound", Character.valueOf('\u00a3'));
        map.put("curren", Character.valueOf('\u00a4'));
        map.put("yen", Character.valueOf('\u00a5'));
        map.put("brvbar", Character.valueOf('\u00a6'));
        map.put("sect", Character.valueOf('\u00a7'));
        map.put("uml", Character.valueOf('\u00a8'));
        map.put("copy", Character.valueOf('\u00a9'));
        map.put("ordf", Character.valueOf('\u00aa'));
        map.put("laquo", Character.valueOf('\u00ab'));
        map.put("not", Character.valueOf('\u00ac'));
        map.put("shy", Character.valueOf('\u00ad'));
        map.put("reg", Character.valueOf('\u00ae'));
        map.put("macr", Character.valueOf('\u00af'));
        map.put("deg", Character.valueOf('\u00b0'));
        map.put("plusmn", Character.valueOf('\u00b1'));
        map.put("sup2", Character.valueOf('\u00b2'));
        map.put("sup3", Character.valueOf('\u00b3'));
        map.put("acute", Character.valueOf('\u00b4'));
        map.put("micro", Character.valueOf('\u00b5'));
        map.put("para", Character.valueOf('\u00b6'));
        map.put("middot", Character.valueOf('\u00b7'));
        map.put("cedil", Character.valueOf('\u00b8'));
        map.put("sup1", Character.valueOf('\u00b9'));
        map.put("ordm", Character.valueOf('\u00ba'));
        map.put("raquo", Character.valueOf('\u00bb'));
        map.put("frac14", Character.valueOf('\u00bc'));
        map.put("frac12", Character.valueOf('\u00bd'));
        map.put("frac34", Character.valueOf('\u00be'));
        map.put("iquest", Character.valueOf('\u00bf'));
        map.put("Agrave", Character.valueOf('\u00c0'));
        map.put("Aacute", Character.valueOf('\u00c1'));
        map.put("Acirc", Character.valueOf('\u00c2'));
        map.put("Atilde", Character.valueOf('\u00c3'));
        map.put("Auml", Character.valueOf('\u00c4'));
        map.put("Aring", Character.valueOf('\u00c5'));
        map.put("AElig", Character.valueOf('\u00c6'));
        map.put("Ccedil", Character.valueOf('\u00c7'));
        map.put("Egrave", Character.valueOf('\u00c8'));
        map.put("Eacute", Character.valueOf('\u00c9'));
        map.put("Ecirc", Character.valueOf('\u00ca'));
        map.put("Euml", Character.valueOf('\u00cb'));
        map.put("Igrave", Character.valueOf('\u00cc'));
        map.put("Iacute", Character.valueOf('\u00cd'));
        map.put("Icirc", Character.valueOf('\u00ce'));
        map.put("Iuml", Character.valueOf('\u00cf'));
        map.put("ETH", Character.valueOf('\u00d0'));
        map.put("Ntilde", Character.valueOf('\u00d1'));
        map.put("Ograve", Character.valueOf('\u00d2'));
        map.put("Oacute", Character.valueOf('\u00d3'));
        map.put("Ocirc", Character.valueOf('\u00d4'));
        map.put("Otilde", Character.valueOf('\u00d5'));
        map.put("Ouml", Character.valueOf('\u00d6'));
        map.put("times", Character.valueOf('\u00d7'));
        map.put("Oslash", Character.valueOf('\u00d8'));
        map.put("Ugrave", Character.valueOf('\u00d9'));
        map.put("Uacute", Character.valueOf('\u00da'));
        map.put("Ucirc", Character.valueOf('\u00db'));
        map.put("Uuml", Character.valueOf('\u00dc'));
        map.put("Yacute", Character.valueOf('\u00dd'));
        map.put("THORN", Character.valueOf('\u00de'));
        map.put("szlig", Character.valueOf('\u00df'));
        map.put("agrave", Character.valueOf('\u00e0'));
        map.put("aacute", Character.valueOf('\u00e1'));
        map.put("acirc", Character.valueOf('\u00e2'));
        map.put("atilde", Character.valueOf('\u00e3'));
        map.put("auml", Character.valueOf('\u00e4'));
        map.put("aring", Character.valueOf('\u00e5'));
        map.put("aelig", Character.valueOf('\u00e6'));
        map.put("ccedil", Character.valueOf('\u00e7'));
        map.put("egrave", Character.valueOf('\u00e8'));
        map.put("eacute", Character.valueOf('\u00e9'));
        map.put("ecirc", Character.valueOf('\u00ea'));
        map.put("euml", Character.valueOf('\u00eb'));
        map.put("igrave", Character.valueOf('\u00ec'));
        map.put("iacute", Character.valueOf('\u00ed'));
        map.put("icirc", Character.valueOf('\u00ee'));
        map.put("iuml", Character.valueOf('\u00ef'));
        map.put("eth", Character.valueOf('\u00f0'));
        map.put("ntilde", Character.valueOf('\u00f1'));
        map.put("ograve", Character.valueOf('\u00f2'));
        map.put("oacute", Character.valueOf('\u00f3'));
        map.put("ocirc", Character.valueOf('\u00f4'));
        map.put("otilde", Character.valueOf('\u00f5'));
        map.put("ouml", Character.valueOf('\u00f6'));
        map.put("divide", Character.valueOf('\u00f7'));
        map.put("oslash", Character.valueOf('\u00f8'));
        map.put("ugrave", Character.valueOf('\u00f9'));
        map.put("uacute", Character.valueOf('\u00fa'));
        map.put("ucirc", Character.valueOf('\u00fb'));
        map.put("uuml", Character.valueOf('\u00fc'));
        map.put("yacute", Character.valueOf('\u00fd'));
        map.put("thorn", Character.valueOf('\u00fe'));
        map.put("yuml", Character.valueOf('\u00ff'));
        map.put("fnof", Character.valueOf('\u0192'));
        map.put("Alpha", Character.valueOf('\u0391'));
        map.put("Beta", Character.valueOf('\u0392'));
        map.put("Gamma", Character.valueOf('\u0393'));
        map.put("Delta", Character.valueOf('\u0394'));
        map.put("Epsilon", Character.valueOf('\u0395'));
        map.put("Zeta", Character.valueOf('\u0396'));
        map.put("Eta", Character.valueOf('\u0397'));
        map.put("Theta", Character.valueOf('\u0398'));
        map.put("Iota", Character.valueOf('\u0399'));
        map.put("Kappa", Character.valueOf('\u039a'));
        map.put("Lambda", Character.valueOf('\u039b'));
        map.put("Mu", Character.valueOf('\u039c'));
        map.put("Nu", Character.valueOf('\u039d'));
        map.put("Xi", Character.valueOf('\u039e'));
        map.put("Omicron", Character.valueOf('\u039f'));
        map.put("Pi", Character.valueOf('\u03a0'));
        map.put("Rho", Character.valueOf('\u03a1'));
        map.put("Sigma", Character.valueOf('\u03a3'));
        map.put("Tau", Character.valueOf('\u03a4'));
        map.put("Upsilon", Character.valueOf('\u03a5'));
        map.put("Phi", Character.valueOf('\u03a6'));
        map.put("Chi", Character.valueOf('\u03a7'));
        map.put("Psi", Character.valueOf('\u03a8'));
        map.put("Omega", Character.valueOf('\u03a9'));
        map.put("alpha", Character.valueOf('\u03b1'));
        map.put("beta", Character.valueOf('\u03b2'));
        map.put("gamma", Character.valueOf('\u03b3'));
        map.put("delta", Character.valueOf('\u03b4'));
        map.put("epsilon", Character.valueOf('\u03b5'));
        map.put("zeta", Character.valueOf('\u03b6'));
        map.put("eta", Character.valueOf('\u03b7'));
        map.put("theta", Character.valueOf('\u03b8'));
        map.put("iota", Character.valueOf('\u03b9'));
        map.put("kappa", Character.valueOf('\u03ba'));
        map.put("lambda", Character.valueOf('\u03bb'));
        map.put("mu", Character.valueOf('\u03bc'));
        map.put("nu", Character.valueOf('\u03bd'));
        map.put("xi", Character.valueOf('\u03be'));
        map.put("omicron", Character.valueOf('\u03bf'));
        map.put("pi", Character.valueOf('\u03c0'));
        map.put("rho", Character.valueOf('\u03c1'));
        map.put("sigmaf", Character.valueOf('\u03c2'));
        map.put("sigma", Character.valueOf('\u03c3'));
        map.put("tau", Character.valueOf('\u03c4'));
        map.put("upsilon", Character.valueOf('\u03c5'));
        map.put("phi", Character.valueOf('\u03c6'));
        map.put("chi", Character.valueOf('\u03c7'));
        map.put("psi", Character.valueOf('\u03c8'));
        map.put("omega", Character.valueOf('\u03c9'));
        map.put("thetasym", Character.valueOf('\u03d1'));
        map.put("upsih", Character.valueOf('\u03d2'));
        map.put("piv", Character.valueOf('\u03d6'));
        map.put("bull", Character.valueOf('\u2022'));
        map.put("hellip", Character.valueOf('\u2026'));
        map.put("prime", Character.valueOf('\u2032'));
        map.put("Prime", Character.valueOf('\u2033'));
        map.put("oline", Character.valueOf('\u203e'));
        map.put("frasl", Character.valueOf('\u2044'));
        map.put("weierp", Character.valueOf('\u2118'));
        map.put("image", Character.valueOf('\u2111'));
        map.put("real", Character.valueOf('\u211c'));
        map.put("trade", Character.valueOf('\u2122'));
        map.put("alefsym", Character.valueOf('\u2135'));
        map.put("larr", Character.valueOf('\u2190'));
        map.put("uarr", Character.valueOf('\u2191'));
        map.put("rarr", Character.valueOf('\u2192'));
        map.put("darr", Character.valueOf('\u2193'));
        map.put("harr", Character.valueOf('\u2194'));
        map.put("crarr", Character.valueOf('\u21b5'));
        map.put("lArr", Character.valueOf('\u21d0'));
        map.put("uArr", Character.valueOf('\u21d1'));
        map.put("rArr", Character.valueOf('\u21d2'));
        map.put("dArr", Character.valueOf('\u21d3'));
        map.put("hArr", Character.valueOf('\u21d4'));
        map.put("forall", Character.valueOf('\u2200'));
        map.put("part", Character.valueOf('\u2202'));
        map.put("exist", Character.valueOf('\u2203'));
        map.put("empty", Character.valueOf('\u2205'));
        map.put("nabla", Character.valueOf('\u2207'));
        map.put("isin", Character.valueOf('\u2208'));
        map.put("notin", Character.valueOf('\u2209'));
        map.put("ni", Character.valueOf('\u220b'));
        map.put("prod", Character.valueOf('\u220f'));
        map.put("sum", Character.valueOf('\u2211'));
        map.put("minus", Character.valueOf('\u2212'));
        map.put("lowast", Character.valueOf('\u2217'));
        map.put("radic", Character.valueOf('\u221a'));
        map.put("prop", Character.valueOf('\u221d'));
        map.put("infin", Character.valueOf('\u221e'));
        map.put("ang", Character.valueOf('\u2220'));
        map.put("and", Character.valueOf('\u2227'));
        map.put("or", Character.valueOf('\u2228'));
        map.put("cap", Character.valueOf('\u2229'));
        map.put("cup", Character.valueOf('\u222a'));
        map.put("int", Character.valueOf('\u222b'));
        map.put("there4", Character.valueOf('\u2234'));
        map.put("sim", Character.valueOf('\u223c'));
        map.put("cong", Character.valueOf('\u2245'));
        map.put("asymp", Character.valueOf('\u2248'));
        map.put("ne", Character.valueOf('\u2260'));
        map.put("equiv", Character.valueOf('\u2261'));
        map.put("le", Character.valueOf('\u2264'));
        map.put("ge", Character.valueOf('\u2265'));
        map.put("sub", Character.valueOf('\u2282'));
        map.put("sup", Character.valueOf('\u2283'));
        map.put("nsub", Character.valueOf('\u2284'));
        map.put("sube", Character.valueOf('\u2286'));
        map.put("supe", Character.valueOf('\u2287'));
        map.put("oplus", Character.valueOf('\u2295'));
        map.put("otimes", Character.valueOf('\u2297'));
        map.put("perp", Character.valueOf('\u22a5'));
        map.put("sdot", Character.valueOf('\u22c5'));
        map.put("lceil", Character.valueOf('\u2308'));
        map.put("rceil", Character.valueOf('\u2309'));
        map.put("lfloor", Character.valueOf('\u230a'));
        map.put("rfloor", Character.valueOf('\u230b'));
        map.put("lang", Character.valueOf('\u2329'));
        map.put("rang", Character.valueOf('\u232a'));
        map.put("loz", Character.valueOf('\u25ca'));
        map.put("spades", Character.valueOf('\u2660'));
        map.put("clubs", Character.valueOf('\u2663'));
        map.put("hearts", Character.valueOf('\u2665'));
        map.put("diams", Character.valueOf('\u2666'));
        map.put("quot", Character.valueOf('\"'));
        map.put("amp", Character.valueOf('&'));
        map.put("apos", Character.valueOf('\''));
        map.put("lt", Character.valueOf('<'));
        map.put("gt", Character.valueOf('>'));
        map.put("OElig", Character.valueOf('\u0152'));
        map.put("oelig", Character.valueOf('\u0153'));
        map.put("Scaron", Character.valueOf('\u0160'));
        map.put("scaron", Character.valueOf('\u0161'));
        map.put("Yuml", Character.valueOf('\u0178'));
        map.put("circ", Character.valueOf('\u02c6'));
        map.put("tilde", Character.valueOf('\u02dc'));
        map.put("ensp", Character.valueOf('\u2002'));
        map.put("emsp", Character.valueOf('\u2003'));
        map.put("thinsp", Character.valueOf('\u2009'));
        map.put("zwnj", Character.valueOf('\u200c'));
        map.put("zwj", Character.valueOf('\u200d'));
        map.put("lrm", Character.valueOf('\u200e'));
        map.put("rlm", Character.valueOf('\u200f'));
        map.put("ndash", Character.valueOf('\u2013'));
        map.put("mdash", Character.valueOf('\u2014'));
        map.put("lsquo", Character.valueOf('\u2018'));
        map.put("rsquo", Character.valueOf('\u2019'));
        map.put("sbquo", Character.valueOf('\u201a'));
        map.put("ldquo", Character.valueOf('\u201c'));
        map.put("rdquo", Character.valueOf('\u201d'));
        map.put("bdquo", Character.valueOf('\u201e'));
        map.put("dagger", Character.valueOf('\u2020'));
        map.put("Dagger", Character.valueOf('\u2021'));
        map.put("permil", Character.valueOf('\u2030'));
        map.put("lsaquo", Character.valueOf('\u2039'));
        map.put("rsaquo", Character.valueOf('\u203a'));
        map.put("euro", Character.valueOf('\u20ac'));
    }
}

