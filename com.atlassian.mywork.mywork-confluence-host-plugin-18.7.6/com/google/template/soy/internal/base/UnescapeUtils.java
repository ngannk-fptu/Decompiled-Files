/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.google.template.soy.internal.base;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class UnescapeUtils {
    private static final Map<String, Integer> HTML_ENTITY_TO_CODEPOINT = ImmutableMap.builder().put((Object)"&quot;", (Object)34).put((Object)"&apos;", (Object)39).put((Object)"&amp;", (Object)38).put((Object)"&lt;", (Object)60).put((Object)"&gt;", (Object)62).put((Object)"&nbsp;", (Object)160).put((Object)"&iexcl;", (Object)161).put((Object)"&cent;", (Object)162).put((Object)"&pound;", (Object)163).put((Object)"&curren;", (Object)164).put((Object)"&yen;", (Object)165).put((Object)"&brvbar;", (Object)166).put((Object)"&sect;", (Object)167).put((Object)"&uml;", (Object)168).put((Object)"&copy;", (Object)169).put((Object)"&ordf;", (Object)170).put((Object)"&laquo;", (Object)171).put((Object)"&not;", (Object)172).put((Object)"&shy;", (Object)173).put((Object)"&reg;", (Object)174).put((Object)"&macr;", (Object)175).put((Object)"&deg;", (Object)176).put((Object)"&plusmn;", (Object)177).put((Object)"&sup2;", (Object)178).put((Object)"&sup3;", (Object)179).put((Object)"&acute;", (Object)180).put((Object)"&micro;", (Object)181).put((Object)"&para;", (Object)182).put((Object)"&middot;", (Object)183).put((Object)"&cedil;", (Object)184).put((Object)"&sup1;", (Object)185).put((Object)"&ordm;", (Object)186).put((Object)"&raquo;", (Object)187).put((Object)"&frac14;", (Object)188).put((Object)"&frac12;", (Object)189).put((Object)"&frac34;", (Object)190).put((Object)"&iquest;", (Object)191).put((Object)"&Agrave;", (Object)192).put((Object)"&Aacute;", (Object)193).put((Object)"&Acirc;", (Object)194).put((Object)"&Atilde;", (Object)195).put((Object)"&Auml;", (Object)196).put((Object)"&Aring;", (Object)197).put((Object)"&AElig;", (Object)198).put((Object)"&Ccedil;", (Object)199).put((Object)"&Egrave;", (Object)200).put((Object)"&Eacute;", (Object)201).put((Object)"&Ecirc;", (Object)202).put((Object)"&Euml;", (Object)203).put((Object)"&Igrave;", (Object)204).put((Object)"&Iacute;", (Object)205).put((Object)"&Icirc;", (Object)206).put((Object)"&Iuml;", (Object)207).put((Object)"&ETH;", (Object)208).put((Object)"&Ntilde;", (Object)209).put((Object)"&Ograve;", (Object)210).put((Object)"&Oacute;", (Object)211).put((Object)"&Ocirc;", (Object)212).put((Object)"&Otilde;", (Object)213).put((Object)"&Ouml;", (Object)214).put((Object)"&times;", (Object)215).put((Object)"&Oslash;", (Object)216).put((Object)"&Ugrave;", (Object)217).put((Object)"&Uacute;", (Object)218).put((Object)"&Ucirc;", (Object)219).put((Object)"&Uuml;", (Object)220).put((Object)"&Yacute;", (Object)221).put((Object)"&THORN;", (Object)222).put((Object)"&szlig;", (Object)223).put((Object)"&agrave;", (Object)224).put((Object)"&aacute;", (Object)225).put((Object)"&acirc;", (Object)226).put((Object)"&atilde;", (Object)227).put((Object)"&auml;", (Object)228).put((Object)"&aring;", (Object)229).put((Object)"&aelig;", (Object)230).put((Object)"&ccedil;", (Object)231).put((Object)"&egrave;", (Object)232).put((Object)"&eacute;", (Object)233).put((Object)"&ecirc;", (Object)234).put((Object)"&euml;", (Object)235).put((Object)"&igrave;", (Object)236).put((Object)"&iacute;", (Object)237).put((Object)"&icirc;", (Object)238).put((Object)"&iuml;", (Object)239).put((Object)"&eth;", (Object)240).put((Object)"&ntilde;", (Object)241).put((Object)"&ograve;", (Object)242).put((Object)"&oacute;", (Object)243).put((Object)"&ocirc;", (Object)244).put((Object)"&otilde;", (Object)245).put((Object)"&ouml;", (Object)246).put((Object)"&divide;", (Object)247).put((Object)"&oslash;", (Object)248).put((Object)"&ugrave;", (Object)249).put((Object)"&uacute;", (Object)250).put((Object)"&ucirc;", (Object)251).put((Object)"&uuml;", (Object)252).put((Object)"&yacute;", (Object)253).put((Object)"&thorn;", (Object)254).put((Object)"&yuml;", (Object)255).put((Object)"&OElig;", (Object)338).put((Object)"&oelig;", (Object)339).put((Object)"&Scaron;", (Object)352).put((Object)"&scaron;", (Object)353).put((Object)"&Yuml;", (Object)376).put((Object)"&fnof;", (Object)402).put((Object)"&circ;", (Object)710).put((Object)"&tilde;", (Object)732).put((Object)"&Alpha;", (Object)913).put((Object)"&Beta;", (Object)914).put((Object)"&Gamma;", (Object)915).put((Object)"&Delta;", (Object)916).put((Object)"&Epsilon;", (Object)917).put((Object)"&Zeta;", (Object)918).put((Object)"&Eta;", (Object)919).put((Object)"&Theta;", (Object)920).put((Object)"&Iota;", (Object)921).put((Object)"&Kappa;", (Object)922).put((Object)"&Lambda;", (Object)923).put((Object)"&Mu;", (Object)924).put((Object)"&Nu;", (Object)925).put((Object)"&Xi;", (Object)926).put((Object)"&Omicron;", (Object)927).put((Object)"&Pi;", (Object)928).put((Object)"&Rho;", (Object)929).put((Object)"&Sigma;", (Object)931).put((Object)"&Tau;", (Object)932).put((Object)"&Upsilon;", (Object)933).put((Object)"&Phi;", (Object)934).put((Object)"&Chi;", (Object)935).put((Object)"&Psi;", (Object)936).put((Object)"&Omega;", (Object)937).put((Object)"&alpha;", (Object)945).put((Object)"&beta;", (Object)946).put((Object)"&gamma;", (Object)947).put((Object)"&delta;", (Object)948).put((Object)"&epsilon;", (Object)949).put((Object)"&zeta;", (Object)950).put((Object)"&eta;", (Object)951).put((Object)"&theta;", (Object)952).put((Object)"&iota;", (Object)953).put((Object)"&kappa;", (Object)954).put((Object)"&lambda;", (Object)955).put((Object)"&mu;", (Object)956).put((Object)"&nu;", (Object)957).put((Object)"&xi;", (Object)958).put((Object)"&omicron;", (Object)959).put((Object)"&pi;", (Object)960).put((Object)"&rho;", (Object)961).put((Object)"&sigmaf;", (Object)962).put((Object)"&sigma;", (Object)963).put((Object)"&tau;", (Object)964).put((Object)"&upsilon;", (Object)965).put((Object)"&phi;", (Object)966).put((Object)"&chi;", (Object)967).put((Object)"&psi;", (Object)968).put((Object)"&omega;", (Object)969).put((Object)"&thetasym;", (Object)977).put((Object)"&upsih;", (Object)978).put((Object)"&piv;", (Object)982).put((Object)"&ensp;", (Object)8194).put((Object)"&emsp;", (Object)8195).put((Object)"&thinsp;", (Object)8201).put((Object)"&zwnj;", (Object)8204).put((Object)"&zwj;", (Object)8205).put((Object)"&lrm;", (Object)8206).put((Object)"&rlm;", (Object)8207).put((Object)"&ndash;", (Object)8211).put((Object)"&mdash;", (Object)8212).put((Object)"&lsquo;", (Object)8216).put((Object)"&rsquo;", (Object)8217).put((Object)"&sbquo;", (Object)8218).put((Object)"&ldquo;", (Object)8220).put((Object)"&rdquo;", (Object)8221).put((Object)"&bdquo;", (Object)8222).put((Object)"&dagger;", (Object)8224).put((Object)"&Dagger;", (Object)8225).put((Object)"&bull;", (Object)8226).put((Object)"&hellip;", (Object)8230).put((Object)"&permil;", (Object)8240).put((Object)"&prime;", (Object)8242).put((Object)"&Prime;", (Object)8243).put((Object)"&lsaquo;", (Object)8249).put((Object)"&rsaquo;", (Object)8250).put((Object)"&oline;", (Object)8254).put((Object)"&frasl;", (Object)8260).put((Object)"&euro;", (Object)8364).put((Object)"&image;", (Object)8465).put((Object)"&weierp;", (Object)8472).put((Object)"&real;", (Object)8476).put((Object)"&trade;", (Object)8482).put((Object)"&alefsym;", (Object)8501).put((Object)"&larr;", (Object)8592).put((Object)"&uarr;", (Object)8593).put((Object)"&rarr;", (Object)8594).put((Object)"&darr;", (Object)8595).put((Object)"&harr;", (Object)8596).put((Object)"&crarr;", (Object)8629).put((Object)"&lArr;", (Object)8656).put((Object)"&uArr;", (Object)8657).put((Object)"&rArr;", (Object)8658).put((Object)"&dArr;", (Object)8659).put((Object)"&hArr;", (Object)8660).put((Object)"&forall;", (Object)8704).put((Object)"&part;", (Object)8706).put((Object)"&exist;", (Object)8707).put((Object)"&empty;", (Object)8709).put((Object)"&nabla;", (Object)8711).put((Object)"&isin;", (Object)8712).put((Object)"&notin;", (Object)8713).put((Object)"&ni;", (Object)8715).put((Object)"&prod;", (Object)8719).put((Object)"&sum;", (Object)8721).put((Object)"&minus;", (Object)8722).put((Object)"&lowast;", (Object)8727).put((Object)"&radic;", (Object)8730).put((Object)"&prop;", (Object)8733).put((Object)"&infin;", (Object)8734).put((Object)"&ang;", (Object)8736).put((Object)"&and;", (Object)8743).put((Object)"&or;", (Object)8744).put((Object)"&cap;", (Object)8745).put((Object)"&cup;", (Object)8746).put((Object)"&int;", (Object)8747).put((Object)"&there4;", (Object)8756).put((Object)"&sim;", (Object)8764).put((Object)"&cong;", (Object)8773).put((Object)"&asymp;", (Object)8776).put((Object)"&ne;", (Object)8800).put((Object)"&equiv;", (Object)8801).put((Object)"&le;", (Object)8804).put((Object)"&ge;", (Object)8805).put((Object)"&sub;", (Object)8834).put((Object)"&sup;", (Object)8835).put((Object)"&nsub;", (Object)8836).put((Object)"&sube;", (Object)8838).put((Object)"&supe;", (Object)8839).put((Object)"&oplus;", (Object)8853).put((Object)"&otimes;", (Object)8855).put((Object)"&perp;", (Object)8869).put((Object)"&sdot;", (Object)8901).put((Object)"&lceil;", (Object)8968).put((Object)"&rceil;", (Object)8969).put((Object)"&lfloor;", (Object)8970).put((Object)"&rfloor;", (Object)8971).put((Object)"&lang;", (Object)9001).put((Object)"&rang;", (Object)9002).put((Object)"&loz;", (Object)9674).put((Object)"&spades;", (Object)9824).put((Object)"&clubs;", (Object)9827).put((Object)"&hearts;", (Object)9829).put((Object)"&diams;", (Object)9830).build();

    private UnescapeUtils() {
    }

    public static String unescapeJs(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\') {
                i = UnescapeUtils.unescapeJsHelper(s, i + 1, sb);
                continue;
            }
            sb.append(c);
            ++i;
        }
        return sb.toString();
    }

    private static int unescapeJsHelper(String s, int i, StringBuilder sb) {
        if (i >= s.length()) {
            throw new IllegalArgumentException("End-of-string after escape character in [" + s + "]");
        }
        char c = s.charAt(i++);
        switch (c) {
            case 'n': {
                sb.append('\n');
                break;
            }
            case 'r': {
                sb.append('\r');
                break;
            }
            case 't': {
                sb.append('\t');
                break;
            }
            case 'b': {
                sb.append('\b');
                break;
            }
            case 'f': {
                sb.append('\f');
                break;
            }
            case '\"': 
            case '\'': 
            case '>': 
            case '\\': {
                sb.append(c);
                break;
            }
            case '0': 
            case '1': 
            case '2': 
            case '3': 
            case '4': 
            case '5': 
            case '6': 
            case '7': {
                int nOctalDigits;
                int digitLimit;
                --i;
                int n = digitLimit = c < '4' ? 3 : 2;
                for (nOctalDigits = 1; nOctalDigits < digitLimit && i + nOctalDigits < s.length() && UnescapeUtils.isOctal(s.charAt(i + nOctalDigits)); ++nOctalDigits) {
                }
                sb.append((char)Integer.parseInt(s.substring(i, i + nOctalDigits), 8));
                i += nOctalDigits;
                break;
            }
            case 'u': 
            case 'x': {
                int unicodeValue;
                String hexCode;
                int nHexDigits = c == 'u' ? 4 : 2;
                try {
                    hexCode = s.substring(i, i + nHexDigits);
                }
                catch (IndexOutOfBoundsException ioobe) {
                    throw new IllegalArgumentException("Invalid unicode sequence [" + s.substring(i) + "] at index " + i + " in [" + s + "]");
                }
                try {
                    unicodeValue = Integer.parseInt(hexCode, 16);
                }
                catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Invalid unicode sequence [" + hexCode + "] at index " + i + " in [" + s + "]");
                }
                sb.append((char)unicodeValue);
                i += nHexDigits;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown escape code [" + c + "] at index " + i + " in [" + s + "]");
            }
        }
        return i;
    }

    private static boolean isOctal(char c) {
        return c >= '0' && c <= '7';
    }

    public static String unescapeHtml(String s) {
        int end;
        int amp = s.indexOf(38);
        if (amp < 0) {
            return s;
        }
        int n = s.length();
        StringBuilder sb = new StringBuilder(n);
        int pos = 0;
        do {
            int cp;
            block11: {
                end = -1;
                int entityLimit = Math.min(n, amp + 12);
                for (int i = amp + 1; i < entityLimit; ++i) {
                    if (s.charAt(i) != ';') continue;
                    end = i + 1;
                    break;
                }
                cp = -1;
                if (end == -1) {
                    cp = -1;
                } else if (s.charAt(amp + 1) == '#') {
                    char ch = s.charAt(amp + 2);
                    try {
                        if (ch == 'x' || ch == 'X') {
                            cp = Integer.parseInt(s.substring(amp + 3, end - 1), 16);
                            break block11;
                        }
                        cp = Integer.parseInt(s.substring(amp + 2, end - 1), 10);
                    }
                    catch (NumberFormatException ex) {
                        cp = -1;
                    }
                } else {
                    Integer cpI = HTML_ENTITY_TO_CODEPOINT.get(s.substring(amp, end));
                    int n2 = cp = cpI != null ? cpI : -1;
                }
            }
            if (cp == -1) {
                end = amp + 1;
                continue;
            }
            sb.append(s, pos, amp);
            sb.appendCodePoint(cp);
            pos = end;
        } while ((amp = s.indexOf(38, end)) >= 0);
        return sb.append(s, pos, n).toString();
    }
}

