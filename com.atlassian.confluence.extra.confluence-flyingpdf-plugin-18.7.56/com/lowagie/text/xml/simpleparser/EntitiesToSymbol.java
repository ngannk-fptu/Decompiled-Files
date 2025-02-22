/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import java.util.HashMap;
import java.util.Map;

public class EntitiesToSymbol {
    @Deprecated
    public static final HashMap<String, Character> map = new HashMap(300);

    public static Map<String, Character> getMap() {
        return map;
    }

    public static Chunk get(String e, Font font) {
        char s = EntitiesToSymbol.getCorrespondingSymbol(e);
        if (s == '\u0000') {
            try {
                return new Chunk(String.valueOf((char)Integer.parseInt(e)), font);
            }
            catch (Exception exception) {
                return new Chunk(e, font);
            }
        }
        Font symbol = new Font(3, font.getSize(), font.getStyle(), font.getColor());
        return new Chunk(String.valueOf(s), symbol);
    }

    public static char getCorrespondingSymbol(String name) {
        Character symbol = map.get(name);
        if (symbol == null) {
            return '\u0000';
        }
        return symbol.charValue();
    }

    static {
        map.put("169", Character.valueOf('\u00e3'));
        map.put("172", Character.valueOf('\u00d8'));
        map.put("174", Character.valueOf('\u00d2'));
        map.put("177", Character.valueOf('\u00b1'));
        map.put("215", Character.valueOf('\u00b4'));
        map.put("247", Character.valueOf('\u00b8'));
        map.put("8230", Character.valueOf('\u00bc'));
        map.put("8242", Character.valueOf('\u00a2'));
        map.put("8243", Character.valueOf('\u00b2'));
        map.put("8260", Character.valueOf('\u00a4'));
        map.put("8364", Character.valueOf('\u00f0'));
        map.put("8465", Character.valueOf('\u00c1'));
        map.put("8472", Character.valueOf('\u00c3'));
        map.put("8476", Character.valueOf('\u00c2'));
        map.put("8482", Character.valueOf('\u00d4'));
        map.put("8501", Character.valueOf('\u00c0'));
        map.put("8592", Character.valueOf('\u00ac'));
        map.put("8593", Character.valueOf('\u00ad'));
        map.put("8594", Character.valueOf('\u00ae'));
        map.put("8595", Character.valueOf('\u00af'));
        map.put("8596", Character.valueOf('\u00ab'));
        map.put("8629", Character.valueOf('\u00bf'));
        map.put("8656", Character.valueOf('\u00dc'));
        map.put("8657", Character.valueOf('\u00dd'));
        map.put("8658", Character.valueOf('\u00de'));
        map.put("8659", Character.valueOf('\u00df'));
        map.put("8660", Character.valueOf('\u00db'));
        map.put("8704", Character.valueOf('\"'));
        map.put("8706", Character.valueOf('\u00b6'));
        map.put("8707", Character.valueOf('$'));
        map.put("8709", Character.valueOf('\u00c6'));
        map.put("8711", Character.valueOf('\u00d1'));
        map.put("8712", Character.valueOf('\u00ce'));
        map.put("8713", Character.valueOf('\u00cf'));
        map.put("8717", Character.valueOf('\''));
        map.put("8719", Character.valueOf('\u00d5'));
        map.put("8721", Character.valueOf('\u00e5'));
        map.put("8722", Character.valueOf('-'));
        map.put("8727", Character.valueOf('*'));
        map.put("8729", Character.valueOf('\u00b7'));
        map.put("8730", Character.valueOf('\u00d6'));
        map.put("8733", Character.valueOf('\u00b5'));
        map.put("8734", Character.valueOf('\u00a5'));
        map.put("8736", Character.valueOf('\u00d0'));
        map.put("8743", Character.valueOf('\u00d9'));
        map.put("8744", Character.valueOf('\u00da'));
        map.put("8745", Character.valueOf('\u00c7'));
        map.put("8746", Character.valueOf('\u00c8'));
        map.put("8747", Character.valueOf('\u00f2'));
        map.put("8756", Character.valueOf('\\'));
        map.put("8764", Character.valueOf('~'));
        map.put("8773", Character.valueOf('@'));
        map.put("8776", Character.valueOf('\u00bb'));
        map.put("8800", Character.valueOf('\u00b9'));
        map.put("8801", Character.valueOf('\u00ba'));
        map.put("8804", Character.valueOf('\u00a3'));
        map.put("8805", Character.valueOf('\u00b3'));
        map.put("8834", Character.valueOf('\u00cc'));
        map.put("8835", Character.valueOf('\u00c9'));
        map.put("8836", Character.valueOf('\u00cb'));
        map.put("8838", Character.valueOf('\u00cd'));
        map.put("8839", Character.valueOf('\u00ca'));
        map.put("8853", Character.valueOf('\u00c5'));
        map.put("8855", Character.valueOf('\u00c4'));
        map.put("8869", Character.valueOf('^'));
        map.put("8901", Character.valueOf('\u00d7'));
        map.put("8992", Character.valueOf('\u00f3'));
        map.put("8993", Character.valueOf('\u00f5'));
        map.put("9001", Character.valueOf('\u00e1'));
        map.put("9002", Character.valueOf('\u00f1'));
        map.put("913", Character.valueOf('A'));
        map.put("914", Character.valueOf('B'));
        map.put("915", Character.valueOf('G'));
        map.put("916", Character.valueOf('D'));
        map.put("917", Character.valueOf('E'));
        map.put("918", Character.valueOf('Z'));
        map.put("919", Character.valueOf('H'));
        map.put("920", Character.valueOf('Q'));
        map.put("921", Character.valueOf('I'));
        map.put("922", Character.valueOf('K'));
        map.put("923", Character.valueOf('L'));
        map.put("924", Character.valueOf('M'));
        map.put("925", Character.valueOf('N'));
        map.put("926", Character.valueOf('X'));
        map.put("927", Character.valueOf('O'));
        map.put("928", Character.valueOf('P'));
        map.put("929", Character.valueOf('R'));
        map.put("931", Character.valueOf('S'));
        map.put("932", Character.valueOf('T'));
        map.put("933", Character.valueOf('U'));
        map.put("934", Character.valueOf('F'));
        map.put("935", Character.valueOf('C'));
        map.put("936", Character.valueOf('Y'));
        map.put("937", Character.valueOf('W'));
        map.put("945", Character.valueOf('a'));
        map.put("946", Character.valueOf('b'));
        map.put("947", Character.valueOf('g'));
        map.put("948", Character.valueOf('d'));
        map.put("949", Character.valueOf('e'));
        map.put("950", Character.valueOf('z'));
        map.put("951", Character.valueOf('h'));
        map.put("952", Character.valueOf('q'));
        map.put("953", Character.valueOf('i'));
        map.put("954", Character.valueOf('k'));
        map.put("955", Character.valueOf('l'));
        map.put("956", Character.valueOf('m'));
        map.put("957", Character.valueOf('n'));
        map.put("958", Character.valueOf('x'));
        map.put("959", Character.valueOf('o'));
        map.put("960", Character.valueOf('p'));
        map.put("961", Character.valueOf('r'));
        map.put("962", Character.valueOf('V'));
        map.put("963", Character.valueOf('s'));
        map.put("964", Character.valueOf('t'));
        map.put("965", Character.valueOf('u'));
        map.put("966", Character.valueOf('f'));
        map.put("967", Character.valueOf('c'));
        map.put("9674", Character.valueOf('\u00e0'));
        map.put("968", Character.valueOf('y'));
        map.put("969", Character.valueOf('w'));
        map.put("977", Character.valueOf('J'));
        map.put("978", Character.valueOf('\u00a1'));
        map.put("981", Character.valueOf('j'));
        map.put("982", Character.valueOf('v'));
        map.put("9824", Character.valueOf('\u00aa'));
        map.put("9827", Character.valueOf('\u00a7'));
        map.put("9829", Character.valueOf('\u00a9'));
        map.put("9830", Character.valueOf('\u00a8'));
        map.put("Alpha", Character.valueOf('A'));
        map.put("Beta", Character.valueOf('B'));
        map.put("Chi", Character.valueOf('C'));
        map.put("Delta", Character.valueOf('D'));
        map.put("Epsilon", Character.valueOf('E'));
        map.put("Eta", Character.valueOf('H'));
        map.put("Gamma", Character.valueOf('G'));
        map.put("Iota", Character.valueOf('I'));
        map.put("Kappa", Character.valueOf('K'));
        map.put("Lambda", Character.valueOf('L'));
        map.put("Mu", Character.valueOf('M'));
        map.put("Nu", Character.valueOf('N'));
        map.put("Omega", Character.valueOf('W'));
        map.put("Omicron", Character.valueOf('O'));
        map.put("Phi", Character.valueOf('F'));
        map.put("Pi", Character.valueOf('P'));
        map.put("Prime", Character.valueOf('\u00b2'));
        map.put("Psi", Character.valueOf('Y'));
        map.put("Rho", Character.valueOf('R'));
        map.put("Sigma", Character.valueOf('S'));
        map.put("Tau", Character.valueOf('T'));
        map.put("Theta", Character.valueOf('Q'));
        map.put("Upsilon", Character.valueOf('U'));
        map.put("Xi", Character.valueOf('X'));
        map.put("Zeta", Character.valueOf('Z'));
        map.put("alefsym", Character.valueOf('\u00c0'));
        map.put("alpha", Character.valueOf('a'));
        map.put("and", Character.valueOf('\u00d9'));
        map.put("ang", Character.valueOf('\u00d0'));
        map.put("asymp", Character.valueOf('\u00bb'));
        map.put("beta", Character.valueOf('b'));
        map.put("cap", Character.valueOf('\u00c7'));
        map.put("chi", Character.valueOf('c'));
        map.put("clubs", Character.valueOf('\u00a7'));
        map.put("cong", Character.valueOf('@'));
        map.put("copy", Character.valueOf('\u00d3'));
        map.put("crarr", Character.valueOf('\u00bf'));
        map.put("cup", Character.valueOf('\u00c8'));
        map.put("dArr", Character.valueOf('\u00df'));
        map.put("darr", Character.valueOf('\u00af'));
        map.put("delta", Character.valueOf('d'));
        map.put("diams", Character.valueOf('\u00a8'));
        map.put("divide", Character.valueOf('\u00b8'));
        map.put("empty", Character.valueOf('\u00c6'));
        map.put("epsilon", Character.valueOf('e'));
        map.put("equiv", Character.valueOf('\u00ba'));
        map.put("eta", Character.valueOf('h'));
        map.put("euro", Character.valueOf('\u00f0'));
        map.put("exist", Character.valueOf('$'));
        map.put("forall", Character.valueOf('\"'));
        map.put("frasl", Character.valueOf('\u00a4'));
        map.put("gamma", Character.valueOf('g'));
        map.put("ge", Character.valueOf('\u00b3'));
        map.put("hArr", Character.valueOf('\u00db'));
        map.put("harr", Character.valueOf('\u00ab'));
        map.put("hearts", Character.valueOf('\u00a9'));
        map.put("hellip", Character.valueOf('\u00bc'));
        map.put("horizontal arrow extender", Character.valueOf('\u00be'));
        map.put("image", Character.valueOf('\u00c1'));
        map.put("infin", Character.valueOf('\u00a5'));
        map.put("int", Character.valueOf('\u00f2'));
        map.put("iota", Character.valueOf('i'));
        map.put("isin", Character.valueOf('\u00ce'));
        map.put("kappa", Character.valueOf('k'));
        map.put("lArr", Character.valueOf('\u00dc'));
        map.put("lambda", Character.valueOf('l'));
        map.put("lang", Character.valueOf('\u00e1'));
        map.put("large brace extender", Character.valueOf('\u00ef'));
        map.put("large integral extender", Character.valueOf('\u00f4'));
        map.put("large left brace (bottom)", Character.valueOf('\u00ee'));
        map.put("large left brace (middle)", Character.valueOf('\u00ed'));
        map.put("large left brace (top)", Character.valueOf('\u00ec'));
        map.put("large left bracket (bottom)", Character.valueOf('\u00eb'));
        map.put("large left bracket (extender)", Character.valueOf('\u00ea'));
        map.put("large left bracket (top)", Character.valueOf('\u00e9'));
        map.put("large left parenthesis (bottom)", Character.valueOf('\u00e8'));
        map.put("large left parenthesis (extender)", Character.valueOf('\u00e7'));
        map.put("large left parenthesis (top)", Character.valueOf('\u00e6'));
        map.put("large right brace (bottom)", Character.valueOf('\u00fe'));
        map.put("large right brace (middle)", Character.valueOf('\u00fd'));
        map.put("large right brace (top)", Character.valueOf('\u00fc'));
        map.put("large right bracket (bottom)", Character.valueOf('\u00fb'));
        map.put("large right bracket (extender)", Character.valueOf('\u00fa'));
        map.put("large right bracket (top)", Character.valueOf('\u00f9'));
        map.put("large right parenthesis (bottom)", Character.valueOf('\u00f8'));
        map.put("large right parenthesis (extender)", Character.valueOf('\u00f7'));
        map.put("large right parenthesis (top)", Character.valueOf('\u00f6'));
        map.put("larr", Character.valueOf('\u00ac'));
        map.put("le", Character.valueOf('\u00a3'));
        map.put("lowast", Character.valueOf('*'));
        map.put("loz", Character.valueOf('\u00e0'));
        map.put("minus", Character.valueOf('-'));
        map.put("mu", Character.valueOf('m'));
        map.put("nabla", Character.valueOf('\u00d1'));
        map.put("ne", Character.valueOf('\u00b9'));
        map.put("not", Character.valueOf('\u00d8'));
        map.put("notin", Character.valueOf('\u00cf'));
        map.put("nsub", Character.valueOf('\u00cb'));
        map.put("nu", Character.valueOf('n'));
        map.put("omega", Character.valueOf('w'));
        map.put("omicron", Character.valueOf('o'));
        map.put("oplus", Character.valueOf('\u00c5'));
        map.put("or", Character.valueOf('\u00da'));
        map.put("otimes", Character.valueOf('\u00c4'));
        map.put("part", Character.valueOf('\u00b6'));
        map.put("perp", Character.valueOf('^'));
        map.put("phi", Character.valueOf('f'));
        map.put("pi", Character.valueOf('p'));
        map.put("piv", Character.valueOf('v'));
        map.put("plusmn", Character.valueOf('\u00b1'));
        map.put("prime", Character.valueOf('\u00a2'));
        map.put("prod", Character.valueOf('\u00d5'));
        map.put("prop", Character.valueOf('\u00b5'));
        map.put("psi", Character.valueOf('y'));
        map.put("rArr", Character.valueOf('\u00de'));
        map.put("radic", Character.valueOf('\u00d6'));
        map.put("radical extender", Character.valueOf('`'));
        map.put("rang", Character.valueOf('\u00f1'));
        map.put("rarr", Character.valueOf('\u00ae'));
        map.put("real", Character.valueOf('\u00c2'));
        map.put("reg", Character.valueOf('\u00d2'));
        map.put("rho", Character.valueOf('r'));
        map.put("sdot", Character.valueOf('\u00d7'));
        map.put("sigma", Character.valueOf('s'));
        map.put("sigmaf", Character.valueOf('V'));
        map.put("sim", Character.valueOf('~'));
        map.put("spades", Character.valueOf('\u00aa'));
        map.put("sub", Character.valueOf('\u00cc'));
        map.put("sube", Character.valueOf('\u00cd'));
        map.put("sum", Character.valueOf('\u00e5'));
        map.put("sup", Character.valueOf('\u00c9'));
        map.put("supe", Character.valueOf('\u00ca'));
        map.put("tau", Character.valueOf('t'));
        map.put("there4", Character.valueOf('\\'));
        map.put("theta", Character.valueOf('q'));
        map.put("thetasym", Character.valueOf('J'));
        map.put("times", Character.valueOf('\u00b4'));
        map.put("trade", Character.valueOf('\u00d4'));
        map.put("uArr", Character.valueOf('\u00dd'));
        map.put("uarr", Character.valueOf('\u00ad'));
        map.put("upsih", Character.valueOf('\u00a1'));
        map.put("upsilon", Character.valueOf('u'));
        map.put("vertical arrow extender", Character.valueOf('\u00bd'));
        map.put("weierp", Character.valueOf('\u00c3'));
        map.put("xi", Character.valueOf('x'));
        map.put("zeta", Character.valueOf('z'));
    }
}

