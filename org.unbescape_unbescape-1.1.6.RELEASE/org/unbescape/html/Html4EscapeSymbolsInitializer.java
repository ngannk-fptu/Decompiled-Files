/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.html;

import java.util.Arrays;
import org.unbescape.html.HtmlEscapeSymbols;

final class Html4EscapeSymbolsInitializer {
    static HtmlEscapeSymbols initializeHtml4() {
        int c;
        HtmlEscapeSymbols.References html4References = new HtmlEscapeSymbols.References();
        html4References.addReference(34, "&quot;");
        html4References.addReference(38, "&amp;");
        html4References.addReference(60, "&lt;");
        html4References.addReference(62, "&gt;");
        html4References.addReference(160, "&nbsp;");
        html4References.addReference(161, "&iexcl;");
        html4References.addReference(162, "&cent;");
        html4References.addReference(163, "&pound;");
        html4References.addReference(164, "&curren;");
        html4References.addReference(165, "&yen;");
        html4References.addReference(166, "&brvbar;");
        html4References.addReference(167, "&sect;");
        html4References.addReference(168, "&uml;");
        html4References.addReference(169, "&copy;");
        html4References.addReference(170, "&ordf;");
        html4References.addReference(171, "&laquo;");
        html4References.addReference(172, "&not;");
        html4References.addReference(173, "&shy;");
        html4References.addReference(174, "&reg;");
        html4References.addReference(175, "&macr;");
        html4References.addReference(176, "&deg;");
        html4References.addReference(177, "&plusmn;");
        html4References.addReference(178, "&sup2;");
        html4References.addReference(179, "&sup3;");
        html4References.addReference(180, "&acute;");
        html4References.addReference(181, "&micro;");
        html4References.addReference(182, "&para;");
        html4References.addReference(183, "&middot;");
        html4References.addReference(184, "&cedil;");
        html4References.addReference(185, "&sup1;");
        html4References.addReference(186, "&ordm;");
        html4References.addReference(187, "&raquo;");
        html4References.addReference(188, "&frac14;");
        html4References.addReference(189, "&frac12;");
        html4References.addReference(190, "&frac34;");
        html4References.addReference(191, "&iquest;");
        html4References.addReference(192, "&Agrave;");
        html4References.addReference(193, "&Aacute;");
        html4References.addReference(194, "&Acirc;");
        html4References.addReference(195, "&Atilde;");
        html4References.addReference(196, "&Auml;");
        html4References.addReference(197, "&Aring;");
        html4References.addReference(198, "&AElig;");
        html4References.addReference(199, "&Ccedil;");
        html4References.addReference(200, "&Egrave;");
        html4References.addReference(201, "&Eacute;");
        html4References.addReference(202, "&Ecirc;");
        html4References.addReference(203, "&Euml;");
        html4References.addReference(204, "&Igrave;");
        html4References.addReference(205, "&Iacute;");
        html4References.addReference(206, "&Icirc;");
        html4References.addReference(207, "&Iuml;");
        html4References.addReference(208, "&ETH;");
        html4References.addReference(209, "&Ntilde;");
        html4References.addReference(210, "&Ograve;");
        html4References.addReference(211, "&Oacute;");
        html4References.addReference(212, "&Ocirc;");
        html4References.addReference(213, "&Otilde;");
        html4References.addReference(214, "&Ouml;");
        html4References.addReference(215, "&times;");
        html4References.addReference(216, "&Oslash;");
        html4References.addReference(217, "&Ugrave;");
        html4References.addReference(218, "&Uacute;");
        html4References.addReference(219, "&Ucirc;");
        html4References.addReference(220, "&Uuml;");
        html4References.addReference(221, "&Yacute;");
        html4References.addReference(222, "&THORN;");
        html4References.addReference(223, "&szlig;");
        html4References.addReference(224, "&agrave;");
        html4References.addReference(225, "&aacute;");
        html4References.addReference(226, "&acirc;");
        html4References.addReference(227, "&atilde;");
        html4References.addReference(228, "&auml;");
        html4References.addReference(229, "&aring;");
        html4References.addReference(230, "&aelig;");
        html4References.addReference(231, "&ccedil;");
        html4References.addReference(232, "&egrave;");
        html4References.addReference(233, "&eacute;");
        html4References.addReference(234, "&ecirc;");
        html4References.addReference(235, "&euml;");
        html4References.addReference(236, "&igrave;");
        html4References.addReference(237, "&iacute;");
        html4References.addReference(238, "&icirc;");
        html4References.addReference(239, "&iuml;");
        html4References.addReference(240, "&eth;");
        html4References.addReference(241, "&ntilde;");
        html4References.addReference(242, "&ograve;");
        html4References.addReference(243, "&oacute;");
        html4References.addReference(244, "&ocirc;");
        html4References.addReference(245, "&otilde;");
        html4References.addReference(246, "&ouml;");
        html4References.addReference(247, "&divide;");
        html4References.addReference(248, "&oslash;");
        html4References.addReference(249, "&ugrave;");
        html4References.addReference(250, "&uacute;");
        html4References.addReference(251, "&ucirc;");
        html4References.addReference(252, "&uuml;");
        html4References.addReference(253, "&yacute;");
        html4References.addReference(254, "&thorn;");
        html4References.addReference(255, "&yuml;");
        html4References.addReference(402, "&fnof;");
        html4References.addReference(913, "&Alpha;");
        html4References.addReference(914, "&Beta;");
        html4References.addReference(915, "&Gamma;");
        html4References.addReference(916, "&Delta;");
        html4References.addReference(917, "&Epsilon;");
        html4References.addReference(918, "&Zeta;");
        html4References.addReference(919, "&Eta;");
        html4References.addReference(920, "&Theta;");
        html4References.addReference(921, "&Iota;");
        html4References.addReference(922, "&Kappa;");
        html4References.addReference(923, "&Lambda;");
        html4References.addReference(924, "&Mu;");
        html4References.addReference(925, "&Nu;");
        html4References.addReference(926, "&Xi;");
        html4References.addReference(927, "&Omicron;");
        html4References.addReference(928, "&Pi;");
        html4References.addReference(929, "&Rho;");
        html4References.addReference(931, "&Sigma;");
        html4References.addReference(932, "&Tau;");
        html4References.addReference(933, "&Upsilon;");
        html4References.addReference(934, "&Phi;");
        html4References.addReference(935, "&Chi;");
        html4References.addReference(936, "&Psi;");
        html4References.addReference(937, "&Omega;");
        html4References.addReference(945, "&alpha;");
        html4References.addReference(946, "&beta;");
        html4References.addReference(947, "&gamma;");
        html4References.addReference(948, "&delta;");
        html4References.addReference(949, "&epsilon;");
        html4References.addReference(950, "&zeta;");
        html4References.addReference(951, "&eta;");
        html4References.addReference(952, "&theta;");
        html4References.addReference(953, "&iota;");
        html4References.addReference(954, "&kappa;");
        html4References.addReference(955, "&lambda;");
        html4References.addReference(956, "&mu;");
        html4References.addReference(957, "&nu;");
        html4References.addReference(958, "&xi;");
        html4References.addReference(959, "&omicron;");
        html4References.addReference(960, "&pi;");
        html4References.addReference(961, "&rho;");
        html4References.addReference(962, "&sigmaf;");
        html4References.addReference(963, "&sigma;");
        html4References.addReference(964, "&tau;");
        html4References.addReference(965, "&upsilon;");
        html4References.addReference(966, "&phi;");
        html4References.addReference(967, "&chi;");
        html4References.addReference(968, "&psi;");
        html4References.addReference(969, "&omega;");
        html4References.addReference(977, "&thetasym;");
        html4References.addReference(978, "&upsih;");
        html4References.addReference(982, "&piv;");
        html4References.addReference(8226, "&bull;");
        html4References.addReference(8230, "&hellip;");
        html4References.addReference(8242, "&prime;");
        html4References.addReference(8243, "&Prime;");
        html4References.addReference(8254, "&oline;");
        html4References.addReference(8260, "&frasl;");
        html4References.addReference(8472, "&weierp;");
        html4References.addReference(8465, "&image;");
        html4References.addReference(8476, "&real;");
        html4References.addReference(8482, "&trade;");
        html4References.addReference(8501, "&alefsym;");
        html4References.addReference(8592, "&larr;");
        html4References.addReference(8593, "&uarr;");
        html4References.addReference(8594, "&rarr;");
        html4References.addReference(8595, "&darr;");
        html4References.addReference(8596, "&harr;");
        html4References.addReference(8629, "&crarr;");
        html4References.addReference(8656, "&lArr;");
        html4References.addReference(8657, "&uArr;");
        html4References.addReference(8658, "&rArr;");
        html4References.addReference(8659, "&dArr;");
        html4References.addReference(8660, "&hArr;");
        html4References.addReference(8704, "&forall;");
        html4References.addReference(8706, "&part;");
        html4References.addReference(8707, "&exist;");
        html4References.addReference(8709, "&empty;");
        html4References.addReference(8711, "&nabla;");
        html4References.addReference(8712, "&isin;");
        html4References.addReference(8713, "&notin;");
        html4References.addReference(8715, "&ni;");
        html4References.addReference(8719, "&prod;");
        html4References.addReference(8721, "&sum;");
        html4References.addReference(8722, "&minus;");
        html4References.addReference(8727, "&lowast;");
        html4References.addReference(8730, "&radic;");
        html4References.addReference(8733, "&prop;");
        html4References.addReference(8734, "&infin;");
        html4References.addReference(8736, "&ang;");
        html4References.addReference(8743, "&and;");
        html4References.addReference(8744, "&or;");
        html4References.addReference(8745, "&cap;");
        html4References.addReference(8746, "&cup;");
        html4References.addReference(8747, "&int;");
        html4References.addReference(8756, "&there4;");
        html4References.addReference(8764, "&sim;");
        html4References.addReference(8773, "&cong;");
        html4References.addReference(8776, "&asymp;");
        html4References.addReference(8800, "&ne;");
        html4References.addReference(8801, "&equiv;");
        html4References.addReference(8804, "&le;");
        html4References.addReference(8805, "&ge;");
        html4References.addReference(8834, "&sub;");
        html4References.addReference(8835, "&sup;");
        html4References.addReference(8836, "&nsub;");
        html4References.addReference(8838, "&sube;");
        html4References.addReference(8839, "&supe;");
        html4References.addReference(8853, "&oplus;");
        html4References.addReference(8855, "&otimes;");
        html4References.addReference(8869, "&perp;");
        html4References.addReference(8901, "&sdot;");
        html4References.addReference(8968, "&lceil;");
        html4References.addReference(8969, "&rceil;");
        html4References.addReference(8970, "&lfloor;");
        html4References.addReference(8971, "&rfloor;");
        html4References.addReference(9001, "&lang;");
        html4References.addReference(9002, "&rang;");
        html4References.addReference(9674, "&loz;");
        html4References.addReference(9824, "&spades;");
        html4References.addReference(9827, "&clubs;");
        html4References.addReference(9829, "&hearts;");
        html4References.addReference(9830, "&diams;");
        html4References.addReference(338, "&OElig;");
        html4References.addReference(339, "&oelig;");
        html4References.addReference(352, "&Scaron;");
        html4References.addReference(353, "&scaron;");
        html4References.addReference(376, "&Yuml;");
        html4References.addReference(710, "&circ;");
        html4References.addReference(732, "&tilde;");
        html4References.addReference(8194, "&ensp;");
        html4References.addReference(8195, "&emsp;");
        html4References.addReference(8201, "&thinsp;");
        html4References.addReference(8204, "&zwnj;");
        html4References.addReference(8205, "&zwj;");
        html4References.addReference(8206, "&lrm;");
        html4References.addReference(8207, "&rlm;");
        html4References.addReference(8211, "&ndash;");
        html4References.addReference(8212, "&mdash;");
        html4References.addReference(8216, "&lsquo;");
        html4References.addReference(8217, "&rsquo;");
        html4References.addReference(8218, "&sbquo;");
        html4References.addReference(8220, "&ldquo;");
        html4References.addReference(8221, "&rdquo;");
        html4References.addReference(8222, "&bdquo;");
        html4References.addReference(8224, "&dagger;");
        html4References.addReference(8225, "&Dagger;");
        html4References.addReference(8240, "&permil;");
        html4References.addReference(8249, "&lsaquo;");
        html4References.addReference(8250, "&rsaquo;");
        html4References.addReference(8364, "&euro;");
        byte[] escapeLevels = new byte[129];
        Arrays.fill(escapeLevels, (byte)3);
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 4;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 4;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 4;
        }
        escapeLevels[39] = 1;
        escapeLevels[34] = 0;
        escapeLevels[60] = 0;
        escapeLevels[62] = 0;
        escapeLevels[38] = 0;
        escapeLevels[128] = 2;
        return new HtmlEscapeSymbols(html4References, escapeLevels);
    }

    private Html4EscapeSymbolsInitializer() {
    }
}

