/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.ja.util;

import java.io.IOException;
import java.util.HashMap;

public class ToStringUtil {
    private static final HashMap<String, String> posTranslations = new HashMap();
    private static final HashMap<String, String> inflTypeTranslations;
    private static final HashMap<String, String> inflFormTranslations;

    public static String getPOSTranslation(String s) {
        return posTranslations.get(s);
    }

    public static String getInflectionTypeTranslation(String s) {
        return inflTypeTranslations.get(s);
    }

    public static String getInflectedFormTranslation(String s) {
        return inflFormTranslations.get(s);
    }

    public static String getRomanization(String s) {
        StringBuilder out = new StringBuilder();
        try {
            ToStringUtil.getRomanization(out, s);
        }
        catch (IOException bogus) {
            throw new RuntimeException(bogus);
        }
        return out.toString();
    }

    public static void getRomanization(Appendable builder, CharSequence s) throws IOException {
        int len = s.length();
        block123: for (int i = 0; i < len; ++i) {
            char ch = s.charAt(i);
            char ch2 = i < len - 1 ? s.charAt(i + 1) : (char)'\u0000';
            char ch3 = i < len - 2 ? s.charAt(i + 2) : (char)'\u0000';
            switch (ch) {
                case '\u30c3': {
                    switch (ch2) {
                        case '\u30ab': 
                        case '\u30ad': 
                        case '\u30af': 
                        case '\u30b1': 
                        case '\u30b3': {
                            builder.append('k');
                            continue block123;
                        }
                        case '\u30b5': 
                        case '\u30b7': 
                        case '\u30b9': 
                        case '\u30bb': 
                        case '\u30bd': {
                            builder.append('s');
                            continue block123;
                        }
                        case '\u30bf': 
                        case '\u30c1': 
                        case '\u30c4': 
                        case '\u30c6': 
                        case '\u30c8': {
                            builder.append('t');
                            continue block123;
                        }
                        case '\u30d1': 
                        case '\u30d4': 
                        case '\u30d7': 
                        case '\u30da': 
                        case '\u30dd': {
                            builder.append('p');
                            continue block123;
                        }
                    }
                    continue block123;
                }
                case '\u30a2': {
                    builder.append('a');
                    continue block123;
                }
                case '\u30a4': {
                    if (ch2 == '\u30a3') {
                        builder.append("yi");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("ye");
                        ++i;
                        continue block123;
                    }
                    builder.append('i');
                    continue block123;
                }
                case '\u30a6': {
                    switch (ch2) {
                        case '\u30a1': {
                            builder.append("wa");
                            ++i;
                            continue block123;
                        }
                        case '\u30a3': {
                            builder.append("wi");
                            ++i;
                            continue block123;
                        }
                        case '\u30a5': {
                            builder.append("wu");
                            ++i;
                            continue block123;
                        }
                        case '\u30a7': {
                            builder.append("we");
                            ++i;
                            continue block123;
                        }
                        case '\u30a9': {
                            builder.append("wo");
                            ++i;
                            continue block123;
                        }
                        case '\u30e5': {
                            builder.append("wyu");
                            ++i;
                            continue block123;
                        }
                    }
                    builder.append('u');
                    continue block123;
                }
                case '\u30a8': {
                    builder.append('e');
                    continue block123;
                }
                case '\u30aa': {
                    if (ch2 == '\u30a6') {
                        builder.append('\u014d');
                        ++i;
                        continue block123;
                    }
                    builder.append('o');
                    continue block123;
                }
                case '\u30ab': {
                    builder.append("ka");
                    continue block123;
                }
                case '\u30ad': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("ky\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("ky\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("kya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("kyo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("kyu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("kye");
                        ++i;
                        continue block123;
                    }
                    builder.append("ki");
                    continue block123;
                }
                case '\u30af': {
                    switch (ch2) {
                        case '\u30a1': {
                            builder.append("kwa");
                            ++i;
                            continue block123;
                        }
                        case '\u30a3': {
                            builder.append("kwi");
                            ++i;
                            continue block123;
                        }
                        case '\u30a7': {
                            builder.append("kwe");
                            ++i;
                            continue block123;
                        }
                        case '\u30a9': {
                            builder.append("kwo");
                            ++i;
                            continue block123;
                        }
                        case '\u30ee': {
                            builder.append("kwa");
                            ++i;
                            continue block123;
                        }
                    }
                    builder.append("ku");
                    continue block123;
                }
                case '\u30b1': {
                    builder.append("ke");
                    continue block123;
                }
                case '\u30b3': {
                    if (ch2 == '\u30a6') {
                        builder.append("k\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("ko");
                    continue block123;
                }
                case '\u30b5': {
                    builder.append("sa");
                    continue block123;
                }
                case '\u30b7': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("sh\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("sh\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("sha");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("sho");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("shu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("she");
                        ++i;
                        continue block123;
                    }
                    builder.append("shi");
                    continue block123;
                }
                case '\u30b9': {
                    if (ch2 == '\u30a3') {
                        builder.append("si");
                        ++i;
                        continue block123;
                    }
                    builder.append("su");
                    continue block123;
                }
                case '\u30bb': {
                    builder.append("se");
                    continue block123;
                }
                case '\u30bd': {
                    if (ch2 == '\u30a6') {
                        builder.append("s\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("so");
                    continue block123;
                }
                case '\u30bf': {
                    builder.append("ta");
                    continue block123;
                }
                case '\u30c1': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("ch\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("ch\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("cha");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("cho");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("chu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("che");
                        ++i;
                        continue block123;
                    }
                    builder.append("chi");
                    continue block123;
                }
                case '\u30c4': {
                    if (ch2 == '\u30a1') {
                        builder.append("tsa");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a3') {
                        builder.append("tsi");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("tse");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a9') {
                        builder.append("tso");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("tsyu");
                        ++i;
                        continue block123;
                    }
                    builder.append("tsu");
                    continue block123;
                }
                case '\u30c6': {
                    if (ch2 == '\u30a3') {
                        builder.append("ti");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a5') {
                        builder.append("tu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("tyu");
                        ++i;
                        continue block123;
                    }
                    builder.append("te");
                    continue block123;
                }
                case '\u30c8': {
                    if (ch2 == '\u30a6') {
                        builder.append("t\u014d");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a5') {
                        builder.append("tu");
                        ++i;
                        continue block123;
                    }
                    builder.append("to");
                    continue block123;
                }
                case '\u30ca': {
                    builder.append("na");
                    continue block123;
                }
                case '\u30cb': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("ny\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("ny\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("nya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("nyo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("nyu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("nye");
                        ++i;
                        continue block123;
                    }
                    builder.append("ni");
                    continue block123;
                }
                case '\u30cc': {
                    builder.append("nu");
                    continue block123;
                }
                case '\u30cd': {
                    builder.append("ne");
                    continue block123;
                }
                case '\u30ce': {
                    if (ch2 == '\u30a6') {
                        builder.append("n\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("no");
                    continue block123;
                }
                case '\u30cf': {
                    builder.append("ha");
                    continue block123;
                }
                case '\u30d2': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("hy\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("hy\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("hya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("hyo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("hyu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("hye");
                        ++i;
                        continue block123;
                    }
                    builder.append("hi");
                    continue block123;
                }
                case '\u30d5': {
                    if (ch2 == '\u30e3') {
                        builder.append("fya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("fyu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a3' && ch3 == '\u30a7') {
                        builder.append("fye");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("fyo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a1') {
                        builder.append("fa");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a3') {
                        builder.append("fi");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("fe");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a9') {
                        builder.append("fo");
                        ++i;
                        continue block123;
                    }
                    builder.append("fu");
                    continue block123;
                }
                case '\u30d8': {
                    builder.append("he");
                    continue block123;
                }
                case '\u30db': {
                    if (ch2 == '\u30a6') {
                        builder.append("h\u014d");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a5') {
                        builder.append("hu");
                        ++i;
                        continue block123;
                    }
                    builder.append("ho");
                    continue block123;
                }
                case '\u30de': {
                    builder.append("ma");
                    continue block123;
                }
                case '\u30df': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("my\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("my\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("mya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("myo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("myu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("mye");
                        ++i;
                        continue block123;
                    }
                    builder.append("mi");
                    continue block123;
                }
                case '\u30e0': {
                    builder.append("mu");
                    continue block123;
                }
                case '\u30e1': {
                    builder.append("me");
                    continue block123;
                }
                case '\u30e2': {
                    if (ch2 == '\u30a6') {
                        builder.append("m\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("mo");
                    continue block123;
                }
                case '\u30e4': {
                    builder.append("ya");
                    continue block123;
                }
                case '\u30e6': {
                    builder.append("yu");
                    continue block123;
                }
                case '\u30e8': {
                    if (ch2 == '\u30a6') {
                        builder.append("y\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("yo");
                    continue block123;
                }
                case '\u30e9': {
                    if (ch2 == '\u309c') {
                        builder.append("la");
                        ++i;
                        continue block123;
                    }
                    builder.append("ra");
                    continue block123;
                }
                case '\u30ea': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("ry\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("ry\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("rya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("ryo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("ryu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("rye");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u309c') {
                        builder.append("li");
                        ++i;
                        continue block123;
                    }
                    builder.append("ri");
                    continue block123;
                }
                case '\u30eb': {
                    if (ch2 == '\u309c') {
                        builder.append("lu");
                        ++i;
                        continue block123;
                    }
                    builder.append("ru");
                    continue block123;
                }
                case '\u30ec': {
                    if (ch2 == '\u309c') {
                        builder.append("le");
                        ++i;
                        continue block123;
                    }
                    builder.append("re");
                    continue block123;
                }
                case '\u30ed': {
                    if (ch2 == '\u30a6') {
                        builder.append("r\u014d");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u309c') {
                        builder.append("lo");
                        ++i;
                        continue block123;
                    }
                    builder.append("ro");
                    continue block123;
                }
                case '\u30ef': {
                    builder.append("wa");
                    continue block123;
                }
                case '\u30f0': {
                    builder.append("i");
                    continue block123;
                }
                case '\u30f1': {
                    builder.append("e");
                    continue block123;
                }
                case '\u30f2': {
                    builder.append("o");
                    continue block123;
                }
                case '\u30f3': {
                    switch (ch2) {
                        case '\u30d0': 
                        case '\u30d1': 
                        case '\u30d3': 
                        case '\u30d4': 
                        case '\u30d6': 
                        case '\u30d7': 
                        case '\u30d9': 
                        case '\u30da': 
                        case '\u30dc': 
                        case '\u30dd': 
                        case '\u30de': 
                        case '\u30df': 
                        case '\u30e0': 
                        case '\u30e1': 
                        case '\u30e2': {
                            builder.append('m');
                            continue block123;
                        }
                        case '\u30a2': 
                        case '\u30a4': 
                        case '\u30a6': 
                        case '\u30a8': 
                        case '\u30aa': 
                        case '\u30e4': 
                        case '\u30e6': 
                        case '\u30e8': {
                            builder.append("n'");
                            continue block123;
                        }
                    }
                    builder.append("n");
                    continue block123;
                }
                case '\u30ac': {
                    builder.append("ga");
                    continue block123;
                }
                case '\u30ae': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("gy\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("gy\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("gya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("gyo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("gyu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("gye");
                        ++i;
                        continue block123;
                    }
                    builder.append("gi");
                    continue block123;
                }
                case '\u30b0': {
                    switch (ch2) {
                        case '\u30a1': {
                            builder.append("gwa");
                            ++i;
                            continue block123;
                        }
                        case '\u30a3': {
                            builder.append("gwi");
                            ++i;
                            continue block123;
                        }
                        case '\u30a7': {
                            builder.append("gwe");
                            ++i;
                            continue block123;
                        }
                        case '\u30a9': {
                            builder.append("gwo");
                            ++i;
                            continue block123;
                        }
                        case '\u30ee': {
                            builder.append("gwa");
                            ++i;
                            continue block123;
                        }
                    }
                    builder.append("gu");
                    continue block123;
                }
                case '\u30b2': {
                    builder.append("ge");
                    continue block123;
                }
                case '\u30b4': {
                    if (ch2 == '\u30a6') {
                        builder.append("g\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("go");
                    continue block123;
                }
                case '\u30b6': {
                    builder.append("za");
                    continue block123;
                }
                case '\u30b8': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("j\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("j\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("ja");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("jo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("ju");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("je");
                        ++i;
                        continue block123;
                    }
                    builder.append("ji");
                    continue block123;
                }
                case '\u30ba': {
                    if (ch2 == '\u30a3') {
                        builder.append("zi");
                        ++i;
                        continue block123;
                    }
                    builder.append("zu");
                    continue block123;
                }
                case '\u30bc': {
                    builder.append("ze");
                    continue block123;
                }
                case '\u30be': {
                    if (ch2 == '\u30a6') {
                        builder.append("z\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("zo");
                    continue block123;
                }
                case '\u30c0': {
                    builder.append("da");
                    continue block123;
                }
                case '\u30c2': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("j\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("j\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("ja");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("jo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("ju");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("je");
                        ++i;
                        continue block123;
                    }
                    builder.append("ji");
                    continue block123;
                }
                case '\u30c5': {
                    builder.append("zu");
                    continue block123;
                }
                case '\u30c7': {
                    if (ch2 == '\u30a3') {
                        builder.append("di");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("dyu");
                        ++i;
                        continue block123;
                    }
                    builder.append("de");
                    continue block123;
                }
                case '\u30c9': {
                    if (ch2 == '\u30a6') {
                        builder.append("d\u014d");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a5') {
                        builder.append("du");
                        ++i;
                        continue block123;
                    }
                    builder.append("do");
                    continue block123;
                }
                case '\u30d0': {
                    builder.append("ba");
                    continue block123;
                }
                case '\u30d3': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("by\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("by\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("bya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("byo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("byu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("bye");
                        ++i;
                        continue block123;
                    }
                    builder.append("bi");
                    continue block123;
                }
                case '\u30d6': {
                    builder.append("bu");
                    continue block123;
                }
                case '\u30d9': {
                    builder.append("be");
                    continue block123;
                }
                case '\u30dc': {
                    if (ch2 == '\u30a6') {
                        builder.append("b\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("bo");
                    continue block123;
                }
                case '\u30d1': {
                    builder.append("pa");
                    continue block123;
                }
                case '\u30d4': {
                    if (ch2 == '\u30e7' && ch3 == '\u30a6') {
                        builder.append("py\u014d");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e5' && ch3 == '\u30a6') {
                        builder.append("py\u016b");
                        i += 2;
                        continue block123;
                    }
                    if (ch2 == '\u30e3') {
                        builder.append("pya");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e7') {
                        builder.append("pyo");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30e5') {
                        builder.append("pyu");
                        ++i;
                        continue block123;
                    }
                    if (ch2 == '\u30a7') {
                        builder.append("pye");
                        ++i;
                        continue block123;
                    }
                    builder.append("pi");
                    continue block123;
                }
                case '\u30d7': {
                    builder.append("pu");
                    continue block123;
                }
                case '\u30da': {
                    builder.append("pe");
                    continue block123;
                }
                case '\u30dd': {
                    if (ch2 == '\u30a6') {
                        builder.append("p\u014d");
                        ++i;
                        continue block123;
                    }
                    builder.append("po");
                    continue block123;
                }
                case '\u30f7': {
                    builder.append("va");
                    continue block123;
                }
                case '\u30f8': {
                    builder.append("vi");
                    continue block123;
                }
                case '\u30f9': {
                    builder.append("ve");
                    continue block123;
                }
                case '\u30fa': {
                    builder.append("vo");
                    continue block123;
                }
                case '\u30f4': {
                    if (ch2 == '\u30a3' && ch3 == '\u30a7') {
                        builder.append("vye");
                        i += 2;
                        continue block123;
                    }
                    builder.append('v');
                    continue block123;
                }
                case '\u30a1': {
                    builder.append('a');
                    continue block123;
                }
                case '\u30a3': {
                    builder.append('i');
                    continue block123;
                }
                case '\u30a5': {
                    builder.append('u');
                    continue block123;
                }
                case '\u30a7': {
                    builder.append('e');
                    continue block123;
                }
                case '\u30a9': {
                    builder.append('o');
                    continue block123;
                }
                case '\u30ee': {
                    builder.append("wa");
                    continue block123;
                }
                case '\u30e3': {
                    builder.append("ya");
                    continue block123;
                }
                case '\u30e5': {
                    builder.append("yu");
                    continue block123;
                }
                case '\u30e7': {
                    builder.append("yo");
                    continue block123;
                }
                case '\u30fc': {
                    continue block123;
                }
                default: {
                    builder.append(ch);
                }
            }
        }
    }

    static {
        posTranslations.put("\u540d\u8a5e", "noun");
        posTranslations.put("\u540d\u8a5e-\u4e00\u822c", "noun-common");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e", "noun-proper");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u4e00\u822c", "noun-proper-misc");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u4eba\u540d", "noun-proper-person");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u4eba\u540d-\u4e00\u822c", "noun-proper-person-misc");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u4eba\u540d-\u59d3", "noun-proper-person-surname");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u4eba\u540d-\u540d", "noun-proper-person-given_name");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u7d44\u7e54", "noun-proper-organization");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u5730\u57df", "noun-proper-place");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u5730\u57df-\u4e00\u822c", "noun-proper-place-misc");
        posTranslations.put("\u540d\u8a5e-\u56fa\u6709\u540d\u8a5e-\u5730\u57df-\u56fd", "noun-proper-place-country");
        posTranslations.put("\u540d\u8a5e-\u4ee3\u540d\u8a5e", "noun-pronoun");
        posTranslations.put("\u540d\u8a5e-\u4ee3\u540d\u8a5e-\u4e00\u822c", "noun-pronoun-misc");
        posTranslations.put("\u540d\u8a5e-\u4ee3\u540d\u8a5e-\u7e2e\u7d04", "noun-pronoun-contraction");
        posTranslations.put("\u540d\u8a5e-\u526f\u8a5e\u53ef\u80fd", "noun-adverbial");
        posTranslations.put("\u540d\u8a5e-\u30b5\u5909\u63a5\u7d9a", "noun-verbal");
        posTranslations.put("\u540d\u8a5e-\u5f62\u5bb9\u52d5\u8a5e\u8a9e\u5e79", "noun-adjective-base");
        posTranslations.put("\u540d\u8a5e-\u6570", "noun-numeric");
        posTranslations.put("\u540d\u8a5e-\u975e\u81ea\u7acb", "noun-affix");
        posTranslations.put("\u540d\u8a5e-\u975e\u81ea\u7acb-\u4e00\u822c", "noun-affix-misc");
        posTranslations.put("\u540d\u8a5e-\u975e\u81ea\u7acb-\u526f\u8a5e\u53ef\u80fd", "noun-affix-adverbial");
        posTranslations.put("\u540d\u8a5e-\u975e\u81ea\u7acb-\u52a9\u52d5\u8a5e\u8a9e\u5e79", "noun-affix-aux");
        posTranslations.put("\u540d\u8a5e-\u975e\u81ea\u7acb-\u5f62\u5bb9\u52d5\u8a5e\u8a9e\u5e79", "noun-affix-adjective-base");
        posTranslations.put("\u540d\u8a5e-\u7279\u6b8a", "noun-special");
        posTranslations.put("\u540d\u8a5e-\u7279\u6b8a-\u52a9\u52d5\u8a5e\u8a9e\u5e79", "noun-special-aux");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e", "noun-suffix");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u4e00\u822c", "noun-suffix-misc");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u4eba\u540d", "noun-suffix-person");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u5730\u57df", "noun-suffix-place");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u30b5\u5909\u63a5\u7d9a", "noun-suffix-verbal");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u52a9\u52d5\u8a5e\u8a9e\u5e79", "noun-suffix-aux");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u5f62\u5bb9\u52d5\u8a5e\u8a9e\u5e79", "noun-suffix-adjective-base");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u526f\u8a5e\u53ef\u80fd", "noun-suffix-adverbial");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u52a9\u6570\u8a5e", "noun-suffix-classifier");
        posTranslations.put("\u540d\u8a5e-\u63a5\u5c3e-\u7279\u6b8a", "noun-suffix-special");
        posTranslations.put("\u540d\u8a5e-\u63a5\u7d9a\u8a5e\u7684", "noun-suffix-conjunctive");
        posTranslations.put("\u540d\u8a5e-\u52d5\u8a5e\u975e\u81ea\u7acb\u7684", "noun-verbal_aux");
        posTranslations.put("\u540d\u8a5e-\u5f15\u7528\u6587\u5b57\u5217", "noun-quotation");
        posTranslations.put("\u540d\u8a5e-\u30ca\u30a4\u5f62\u5bb9\u8a5e\u8a9e\u5e79", "noun-nai_adjective");
        posTranslations.put("\u63a5\u982d\u8a5e", "prefix");
        posTranslations.put("\u63a5\u982d\u8a5e-\u540d\u8a5e\u63a5\u7d9a", "prefix-nominal");
        posTranslations.put("\u63a5\u982d\u8a5e-\u52d5\u8a5e\u63a5\u7d9a", "prefix-verbal");
        posTranslations.put("\u63a5\u982d\u8a5e-\u5f62\u5bb9\u8a5e\u63a5\u7d9a", "prefix-adjectival");
        posTranslations.put("\u63a5\u982d\u8a5e-\u6570\u63a5\u7d9a", "prefix-numerical");
        posTranslations.put("\u52d5\u8a5e", "verb");
        posTranslations.put("\u52d5\u8a5e-\u81ea\u7acb", "verb-main");
        posTranslations.put("\u52d5\u8a5e-\u975e\u81ea\u7acb", "verb-auxiliary");
        posTranslations.put("\u52d5\u8a5e-\u63a5\u5c3e", "verb-suffix");
        posTranslations.put("\u5f62\u5bb9\u8a5e", "adjective");
        posTranslations.put("\u5f62\u5bb9\u8a5e-\u81ea\u7acb", "adjective-main");
        posTranslations.put("\u5f62\u5bb9\u8a5e-\u975e\u81ea\u7acb", "adjective-auxiliary");
        posTranslations.put("\u5f62\u5bb9\u8a5e-\u63a5\u5c3e", "adjective-suffix");
        posTranslations.put("\u526f\u8a5e", "adverb");
        posTranslations.put("\u526f\u8a5e-\u4e00\u822c", "adverb-misc");
        posTranslations.put("\u526f\u8a5e-\u52a9\u8a5e\u985e\u63a5\u7d9a", "adverb-particle_conjunction");
        posTranslations.put("\u9023\u4f53\u8a5e", "adnominal");
        posTranslations.put("\u63a5\u7d9a\u8a5e", "conjunction");
        posTranslations.put("\u52a9\u8a5e", "particle");
        posTranslations.put("\u52a9\u8a5e-\u683c\u52a9\u8a5e", "particle-case");
        posTranslations.put("\u52a9\u8a5e-\u683c\u52a9\u8a5e-\u4e00\u822c", "particle-case-misc");
        posTranslations.put("\u52a9\u8a5e-\u683c\u52a9\u8a5e-\u5f15\u7528", "particle-case-quote");
        posTranslations.put("\u52a9\u8a5e-\u683c\u52a9\u8a5e-\u9023\u8a9e", "particle-case-compound");
        posTranslations.put("\u52a9\u8a5e-\u63a5\u7d9a\u52a9\u8a5e", "particle-conjunctive");
        posTranslations.put("\u52a9\u8a5e-\u4fc2\u52a9\u8a5e", "particle-dependency");
        posTranslations.put("\u52a9\u8a5e-\u526f\u52a9\u8a5e", "particle-adverbial");
        posTranslations.put("\u52a9\u8a5e-\u9593\u6295\u52a9\u8a5e", "particle-interjective");
        posTranslations.put("\u52a9\u8a5e-\u4e26\u7acb\u52a9\u8a5e", "particle-coordinate");
        posTranslations.put("\u52a9\u8a5e-\u7d42\u52a9\u8a5e", "particle-final");
        posTranslations.put("\u52a9\u8a5e-\u526f\u52a9\u8a5e\uff0f\u4e26\u7acb\u52a9\u8a5e\uff0f\u7d42\u52a9\u8a5e", "particle-adverbial/conjunctive/final");
        posTranslations.put("\u52a9\u8a5e-\u9023\u4f53\u5316", "particle-adnominalizer");
        posTranslations.put("\u52a9\u8a5e-\u526f\u8a5e\u5316", "particle-adnominalizer");
        posTranslations.put("\u52a9\u8a5e-\u7279\u6b8a", "particle-special");
        posTranslations.put("\u52a9\u52d5\u8a5e", "auxiliary-verb");
        posTranslations.put("\u611f\u52d5\u8a5e", "interjection");
        posTranslations.put("\u8a18\u53f7", "symbol");
        posTranslations.put("\u8a18\u53f7-\u4e00\u822c", "symbol-misc");
        posTranslations.put("\u8a18\u53f7-\u53e5\u70b9", "symbol-period");
        posTranslations.put("\u8a18\u53f7-\u8aad\u70b9", "symbol-comma");
        posTranslations.put("\u8a18\u53f7-\u7a7a\u767d", "symbol-space");
        posTranslations.put("\u8a18\u53f7-\u62ec\u5f27\u958b", "symbol-open_bracket");
        posTranslations.put("\u8a18\u53f7-\u62ec\u5f27\u9589", "symbol-close_bracket");
        posTranslations.put("\u8a18\u53f7-\u30a2\u30eb\u30d5\u30a1\u30d9\u30c3\u30c8", "symbol-alphabetic");
        posTranslations.put("\u305d\u306e\u4ed6", "other");
        posTranslations.put("\u305d\u306e\u4ed6-\u9593\u6295", "other-interjection");
        posTranslations.put("\u30d5\u30a3\u30e9\u30fc", "filler");
        posTranslations.put("\u975e\u8a00\u8a9e\u97f3", "non-verbal");
        posTranslations.put("\u8a9e\u65ad\u7247", "fragment");
        posTranslations.put("\u672a\u77e5\u8a9e", "unknown");
        inflTypeTranslations = new HashMap();
        inflTypeTranslations.put("*", "*");
        inflTypeTranslations.put("\u5f62\u5bb9\u8a5e\u30fb\u30a2\u30a6\u30aa\u6bb5", "adj-group-a-o-u");
        inflTypeTranslations.put("\u5f62\u5bb9\u8a5e\u30fb\u30a4\u6bb5", "adj-group-i");
        inflTypeTranslations.put("\u5f62\u5bb9\u8a5e\u30fb\u30a4\u30a4", "adj-group-ii");
        inflTypeTranslations.put("\u4e0d\u5909\u5316\u578b", "non-inflectional");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30bf", "special-da");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30c0", "special-ta");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30b4\u30c8\u30b7", "classical-gotoshi");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30b8\u30e3", "special-ja");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30ca\u30a4", "special-nai");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30e9\u884c\u7279\u6b8a", "5-row-cons-r-special");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30cc", "special-nu");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30ad", "classical-ki");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30bf\u30a4", "special-tai");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30d9\u30b7", "classical-beshi");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30e4", "special-ya");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30de\u30b8", "classical-maji");
        inflTypeTranslations.put("\u4e0b\u4e8c\u30fb\u30bf\u884c", "2-row-lower-cons-t");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30c7\u30b9", "special-desu");
        inflTypeTranslations.put("\u7279\u6b8a\u30fb\u30de\u30b9", "special-masu");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30e9\u884c\u30a2\u30eb", "5-row-aru");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30ca\u30ea", "classical-nari");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30ea", "classical-ri");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30b1\u30ea", "classical-keri");
        inflTypeTranslations.put("\u6587\u8a9e\u30fb\u30eb", "classical-ru");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30ab\u884c\u30a4\u97f3\u4fbf", "5-row-cons-k-i-onbin");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30b5\u884c", "5-row-cons-s");
        inflTypeTranslations.put("\u4e00\u6bb5", "1-row");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30ef\u884c\u4fc3\u97f3\u4fbf", "5-row-cons-w-cons-onbin");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30de\u884c", "5-row-cons-m");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30bf\u884c", "5-row-cons-t");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30e9\u884c", "5-row-cons-r");
        inflTypeTranslations.put("\u30b5\u5909\u30fb\u2212\u30b9\u30eb", "irregular-suffix-suru");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30ac\u884c", "5-row-cons-g");
        inflTypeTranslations.put("\u30b5\u5909\u30fb\u2212\u30ba\u30eb", "irregular-suffix-zuru");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30d0\u884c", "5-row-cons-b");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30ef\u884c\u30a6\u97f3\u4fbf", "5-row-cons-w-u-onbin");
        inflTypeTranslations.put("\u4e0b\u4e8c\u30fb\u30c0\u884c", "2-row-lower-cons-d");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30ab\u884c\u4fc3\u97f3\u4fbf\u30e6\u30af", "5-row-cons-k-cons-onbin-yuku");
        inflTypeTranslations.put("\u4e0a\u4e8c\u30fb\u30c0\u884c", "2-row-upper-cons-d");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30ab\u884c\u4fc3\u97f3\u4fbf", "5-row-cons-k-cons-onbin");
        inflTypeTranslations.put("\u4e00\u6bb5\u30fb\u5f97\u30eb", "1-row-eru");
        inflTypeTranslations.put("\u56db\u6bb5\u30fb\u30bf\u884c", "4-row-cons-t");
        inflTypeTranslations.put("\u4e94\u6bb5\u30fb\u30ca\u884c", "5-row-cons-n");
        inflTypeTranslations.put("\u4e0b\u4e8c\u30fb\u30cf\u884c", "2-row-lower-cons-h");
        inflTypeTranslations.put("\u56db\u6bb5\u30fb\u30cf\u884c", "4-row-cons-h");
        inflTypeTranslations.put("\u56db\u6bb5\u30fb\u30d0\u884c", "4-row-cons-b");
        inflTypeTranslations.put("\u30b5\u5909\u30fb\u30b9\u30eb", "irregular-suru");
        inflTypeTranslations.put("\u4e0a\u4e8c\u30fb\u30cf\u884c", "2-row-upper-cons-h");
        inflTypeTranslations.put("\u4e0b\u4e8c\u30fb\u30de\u884c", "2-row-lower-cons-m");
        inflTypeTranslations.put("\u56db\u6bb5\u30fb\u30b5\u884c", "4-row-cons-s");
        inflTypeTranslations.put("\u4e0b\u4e8c\u30fb\u30ac\u884c", "2-row-lower-cons-g");
        inflTypeTranslations.put("\u30ab\u5909\u30fb\u6765\u30eb", "kuru-kanji");
        inflTypeTranslations.put("\u4e00\u6bb5\u30fb\u30af\u30ec\u30eb", "1-row-kureru");
        inflTypeTranslations.put("\u4e0b\u4e8c\u30fb\u5f97", "2-row-lower-u");
        inflTypeTranslations.put("\u30ab\u5909\u30fb\u30af\u30eb", "kuru-kana");
        inflTypeTranslations.put("\u30e9\u5909", "irregular-cons-r");
        inflTypeTranslations.put("\u4e0b\u4e8c\u30fb\u30ab\u884c", "2-row-lower-cons-k");
        inflFormTranslations = new HashMap();
        inflFormTranslations.put("*", "*");
        inflFormTranslations.put("\u57fa\u672c\u5f62", "base");
        inflFormTranslations.put("\u6587\u8a9e\u57fa\u672c\u5f62", "classical-base");
        inflFormTranslations.put("\u672a\u7136\u30cc\u63a5\u7d9a", "imperfective-nu-connection");
        inflFormTranslations.put("\u672a\u7136\u30a6\u63a5\u7d9a", "imperfective-u-connection");
        inflFormTranslations.put("\u9023\u7528\u30bf\u63a5\u7d9a", "conjunctive-ta-connection");
        inflFormTranslations.put("\u9023\u7528\u30c6\u63a5\u7d9a", "conjunctive-te-connection");
        inflFormTranslations.put("\u9023\u7528\u30b4\u30b6\u30a4\u63a5\u7d9a", "conjunctive-gozai-connection");
        inflFormTranslations.put("\u4f53\u8a00\u63a5\u7d9a", "uninflected-connection");
        inflFormTranslations.put("\u4eee\u5b9a\u5f62", "subjunctive");
        inflFormTranslations.put("\u547d\u4ee4\uff45", "imperative-e");
        inflFormTranslations.put("\u4eee\u5b9a\u7e2e\u7d04\uff11", "conditional-contracted-1");
        inflFormTranslations.put("\u4eee\u5b9a\u7e2e\u7d04\uff12", "conditional-contracted-2");
        inflFormTranslations.put("\u30ac\u30eb\u63a5\u7d9a", "garu-connection");
        inflFormTranslations.put("\u672a\u7136\u5f62", "imperfective");
        inflFormTranslations.put("\u9023\u7528\u5f62", "conjunctive");
        inflFormTranslations.put("\u97f3\u4fbf\u57fa\u672c\u5f62", "onbin-base");
        inflFormTranslations.put("\u9023\u7528\u30c7\u63a5\u7d9a", "conjunctive-de-connection");
        inflFormTranslations.put("\u672a\u7136\u7279\u6b8a", "imperfective-special");
        inflFormTranslations.put("\u547d\u4ee4\uff49", "imperative-i");
        inflFormTranslations.put("\u9023\u7528\u30cb\u63a5\u7d9a", "conjunctive-ni-connection");
        inflFormTranslations.put("\u547d\u4ee4\uff59\uff4f", "imperative-yo");
        inflFormTranslations.put("\u4f53\u8a00\u63a5\u7d9a\u7279\u6b8a", "adnominal-special");
        inflFormTranslations.put("\u547d\u4ee4\uff52\uff4f", "imperative-ro");
        inflFormTranslations.put("\u4f53\u8a00\u63a5\u7d9a\u7279\u6b8a\uff12", "uninflected-special-connection-2");
        inflFormTranslations.put("\u672a\u7136\u30ec\u30eb\u63a5\u7d9a", "imperfective-reru-connection");
        inflFormTranslations.put("\u73fe\u4ee3\u57fa\u672c\u5f62", "modern-base");
        inflFormTranslations.put("\u57fa\u672c\u5f62-\u4fc3\u97f3\u4fbf", "base-onbin");
    }
}

