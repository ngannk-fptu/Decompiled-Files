/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlFixerReader
extends Reader {
    protected Reader in;
    private boolean trimmed;
    private boolean cdata = false;
    private final StringBuffer buffer;
    private int bufferPos;
    private int state = 0;
    private static Map<String, String> CODED_ENTITIES = new HashMap<String, String>();
    private static Pattern ENTITIES_PATTERN;

    public XmlFixerReader(Reader in) {
        super(in);
        this.in = in;
        this.buffer = new StringBuffer();
        this.state = 0;
    }

    private boolean trimStream() throws IOException {
        boolean loop;
        boolean hasContent = true;
        int state = 0;
        do {
            switch (state) {
                case 0: {
                    int c = this.in.read();
                    if (c == -1) {
                        loop = false;
                        hasContent = false;
                        break;
                    }
                    if (c == 32 || c == 10 || c == 13 || c == 9) {
                        loop = true;
                        break;
                    }
                    if (c == 60) {
                        state = 1;
                        this.buffer.setLength(0);
                        this.bufferPos = 0;
                        this.buffer.append((char)c);
                        loop = true;
                        break;
                    }
                    this.buffer.setLength(0);
                    this.bufferPos = 0;
                    this.buffer.append((char)c);
                    loop = false;
                    hasContent = true;
                    this.state = 3;
                    break;
                }
                case 1: {
                    int c = this.in.read();
                    if (c == -1) {
                        loop = false;
                        hasContent = true;
                        this.state = 3;
                        break;
                    }
                    if (c != 33) {
                        this.buffer.append((char)c);
                        this.state = 3;
                        loop = false;
                        hasContent = true;
                        this.state = 3;
                        break;
                    }
                    this.buffer.append((char)c);
                    state = 2;
                    loop = true;
                    break;
                }
                case 2: {
                    int c = this.in.read();
                    if (c == -1) {
                        loop = false;
                        hasContent = true;
                        this.state = 3;
                        break;
                    }
                    if (c == 45) {
                        this.buffer.append((char)c);
                        state = 3;
                        loop = true;
                        break;
                    }
                    this.buffer.append((char)c);
                    loop = false;
                    hasContent = true;
                    this.state = 3;
                    break;
                }
                case 3: {
                    int c = this.in.read();
                    if (c == -1) {
                        loop = false;
                        hasContent = true;
                        this.state = 3;
                        break;
                    }
                    if (c == 45) {
                        this.buffer.append((char)c);
                        state = 4;
                        loop = true;
                        break;
                    }
                    this.buffer.append((char)c);
                    loop = false;
                    hasContent = true;
                    this.state = 3;
                    break;
                }
                case 4: {
                    int c = this.in.read();
                    if (c == -1) {
                        loop = false;
                        hasContent = true;
                        this.state = 3;
                        break;
                    }
                    if (c != 45) {
                        this.buffer.append((char)c);
                        loop = true;
                        break;
                    }
                    this.buffer.append((char)c);
                    state = 5;
                    loop = true;
                    break;
                }
                case 5: {
                    int c = this.in.read();
                    if (c == -1) {
                        loop = false;
                        hasContent = true;
                        this.state = 3;
                        break;
                    }
                    if (c != 45) {
                        this.buffer.append((char)c);
                        loop = true;
                        state = 4;
                        break;
                    }
                    this.buffer.append((char)c);
                    state = 6;
                    loop = true;
                    break;
                }
                case 6: {
                    int c = this.in.read();
                    if (c == -1) {
                        loop = false;
                        hasContent = true;
                        this.state = 3;
                        break;
                    }
                    if (c != 62) {
                        this.buffer.append((char)c);
                        loop = true;
                        state = 4;
                        break;
                    }
                    this.buffer.setLength(0);
                    state = 0;
                    loop = true;
                    break;
                }
                default: {
                    throw new IOException("It shouldn't happen");
                }
            }
        } while (loop);
        return hasContent;
    }

    @Override
    public int read() throws IOException {
        int c;
        boolean loop;
        if (!this.trimmed) {
            this.trimmed = true;
            if (!this.trimStream()) {
                return -1;
            }
        }
        block18: do {
            block0 : switch (this.state) {
                case 0: {
                    c = this.in.read();
                    if (c > -1) {
                        if (c == 38) {
                            this.state = 1;
                            this.buffer.setLength(0);
                            this.bufferPos = 0;
                            this.buffer.append((char)c);
                            loop = true;
                            break;
                        }
                        if (c == 60) {
                            this.state = 4;
                            this.buffer.setLength(0);
                            this.bufferPos = 0;
                            this.buffer.append((char)c);
                            loop = true;
                            break;
                        }
                        if (c == 93 && this.cdata) {
                            this.state = 5;
                            this.buffer.setLength(0);
                            this.bufferPos = 0;
                            this.buffer.append((char)c);
                            loop = true;
                            break;
                        }
                        loop = false;
                        break;
                    }
                    loop = false;
                    break;
                }
                case 1: {
                    c = this.in.read();
                    if (c > -1) {
                        if (c == 59) {
                            this.buffer.append((char)c);
                            this.state = 2;
                            loop = true;
                            break;
                        }
                        if (c >= 97 && c <= 122 || c >= 65 && c <= 90 || c == 35 || c >= 48 && c <= 57) {
                            this.buffer.append((char)c);
                            loop = true;
                            break;
                        }
                        if (!this.cdata) {
                            this.buffer.insert(1, "amp;");
                        }
                        this.buffer.append((char)c);
                        this.state = 3;
                        loop = true;
                        break;
                    }
                    if (!this.cdata) {
                        this.buffer.insert(1, "amp;");
                    }
                    this.state = 3;
                    loop = true;
                    break;
                }
                case 2: {
                    c = 0;
                    String literalEntity = this.buffer.toString();
                    String codedEntity = CODED_ENTITIES.get(literalEntity);
                    if (codedEntity != null) {
                        this.buffer.setLength(0);
                        this.buffer.append(codedEntity);
                    }
                    this.state = 3;
                    loop = true;
                    break;
                }
                case 3: {
                    if (this.bufferPos < this.buffer.length()) {
                        c = this.buffer.charAt(this.bufferPos++);
                        loop = false;
                        break;
                    }
                    c = 0;
                    this.state = 0;
                    loop = true;
                    break;
                }
                case 4: {
                    String actBufferContent;
                    c = this.in.read();
                    loop = true;
                    this.state = 3;
                    switch (c) {
                        case -1: {
                            break block0;
                        }
                        case 32: 
                        case 47: 
                        case 62: {
                            this.buffer.append((char)c);
                            break block0;
                        }
                        case 91: {
                            this.buffer.append((char)c);
                            actBufferContent = this.buffer.toString();
                            if ("<![CDATA[".equals(actBufferContent)) {
                                this.cdata = true;
                                break block0;
                            }
                            this.state = 4;
                            break block0;
                        }
                    }
                    this.state = 4;
                    this.buffer.append((char)c);
                    break;
                }
                case 5: {
                    String actBufferContent;
                    c = this.in.read();
                    loop = true;
                    this.state = 3;
                    switch (c) {
                        case -1: {
                            break;
                        }
                        case 93: {
                            this.buffer.append((char)c);
                            this.state = 5;
                            break;
                        }
                        case 62: {
                            this.buffer.append((char)c);
                            actBufferContent = this.buffer.toString();
                            if (!"]]>".equals(actBufferContent)) continue block18;
                            this.cdata = false;
                            break;
                        }
                        default: {
                            this.buffer.append((char)c);
                            break;
                        }
                    }
                    continue block18;
                }
                default: {
                    throw new IOException("It shouldn't happen");
                }
            }
        } while (loop);
        return c;
    }

    @Override
    public int read(char[] buffer, int offset, int len) throws IOException {
        int charsRead = 0;
        int c = this.read();
        if (c == -1) {
            return -1;
        }
        buffer[offset + charsRead++] = (char)c;
        while (charsRead < len && (c = this.read()) > -1) {
            buffer[offset + charsRead++] = (char)c;
        }
        return charsRead;
    }

    @Override
    public long skip(long n) throws IOException {
        long counter;
        if (n == 0L) {
            return 0L;
        }
        if (n < 0L) {
            throw new IllegalArgumentException("'n' cannot be negative");
        }
        int c = this.read();
        for (counter = 1L; c > -1 && counter < n; ++counter) {
            c = this.read();
        }
        return counter;
    }

    @Override
    public boolean ready() throws IOException {
        return this.state != 0 || this.in.ready();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw new IOException("Stream does not support mark");
    }

    @Override
    public void reset() throws IOException {
        throw new IOException("Stream does not support mark");
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    public String processHtmlEntities(String s) {
        if (s.indexOf(38) == -1) {
            return s;
        }
        StringBuffer sb = new StringBuffer(s.length());
        int pos = 0;
        while (pos < s.length()) {
            String chunck = s.substring(pos);
            Matcher m = ENTITIES_PATTERN.matcher(chunck);
            if (m.find()) {
                String codedEntity;
                int b = pos + m.start();
                int e = pos + m.end();
                if (b > pos) {
                    sb.append(s.substring(pos, b));
                    pos = b;
                }
                if ((codedEntity = CODED_ENTITIES.get(chunck = s.substring(pos, e))) == null) {
                    codedEntity = chunck;
                }
                sb.append(codedEntity);
                pos = e;
                continue;
            }
            sb.append(chunck);
            pos += chunck.length();
        }
        return sb.toString();
    }

    static {
        CODED_ENTITIES.put("&nbsp;", "&#160;");
        CODED_ENTITIES.put("&iexcl;", "&#161;");
        CODED_ENTITIES.put("&cent;", "&#162;");
        CODED_ENTITIES.put("&pound;", "&#163;");
        CODED_ENTITIES.put("&curren;", "&#164;");
        CODED_ENTITIES.put("&yen;", "&#165;");
        CODED_ENTITIES.put("&brvbar;", "&#166;");
        CODED_ENTITIES.put("&sect;", "&#167;");
        CODED_ENTITIES.put("&uml;", "&#168;");
        CODED_ENTITIES.put("&copy;", "&#169;");
        CODED_ENTITIES.put("&ordf;", "&#170;");
        CODED_ENTITIES.put("&laquo;", "&#171;");
        CODED_ENTITIES.put("&not;", "&#172;");
        CODED_ENTITIES.put("&shy;", "&#173;");
        CODED_ENTITIES.put("&reg;", "&#174;");
        CODED_ENTITIES.put("&macr;", "&#175;");
        CODED_ENTITIES.put("&deg;", "&#176;");
        CODED_ENTITIES.put("&plusmn;", "&#177;");
        CODED_ENTITIES.put("&sup2;", "&#178;");
        CODED_ENTITIES.put("&sup3;", "&#179;");
        CODED_ENTITIES.put("&acute;", "&#180;");
        CODED_ENTITIES.put("&micro;", "&#181;");
        CODED_ENTITIES.put("&para;", "&#182;");
        CODED_ENTITIES.put("&middot;", "&#183;");
        CODED_ENTITIES.put("&cedil;", "&#184;");
        CODED_ENTITIES.put("&sup1;", "&#185;");
        CODED_ENTITIES.put("&ordm;", "&#186;");
        CODED_ENTITIES.put("&raquo;", "&#187;");
        CODED_ENTITIES.put("&frac14;", "&#188;");
        CODED_ENTITIES.put("&frac12;", "&#189;");
        CODED_ENTITIES.put("&frac34;", "&#190;");
        CODED_ENTITIES.put("&iquest;", "&#191;");
        CODED_ENTITIES.put("&Agrave;", "&#192;");
        CODED_ENTITIES.put("&Aacute;", "&#193;");
        CODED_ENTITIES.put("&Acirc;", "&#194;");
        CODED_ENTITIES.put("&Atilde;", "&#195;");
        CODED_ENTITIES.put("&Auml;", "&#196;");
        CODED_ENTITIES.put("&Aring;", "&#197;");
        CODED_ENTITIES.put("&AElig;", "&#198;");
        CODED_ENTITIES.put("&Ccedil;", "&#199;");
        CODED_ENTITIES.put("&Egrave;", "&#200;");
        CODED_ENTITIES.put("&Eacute;", "&#201;");
        CODED_ENTITIES.put("&Ecirc;", "&#202;");
        CODED_ENTITIES.put("&Euml;", "&#203;");
        CODED_ENTITIES.put("&Igrave;", "&#204;");
        CODED_ENTITIES.put("&Iacute;", "&#205;");
        CODED_ENTITIES.put("&Icirc;", "&#206;");
        CODED_ENTITIES.put("&Iuml;", "&#207;");
        CODED_ENTITIES.put("&ETH;", "&#208;");
        CODED_ENTITIES.put("&Ntilde;", "&#209;");
        CODED_ENTITIES.put("&Ograve;", "&#210;");
        CODED_ENTITIES.put("&Oacute;", "&#211;");
        CODED_ENTITIES.put("&Ocirc;", "&#212;");
        CODED_ENTITIES.put("&Otilde;", "&#213;");
        CODED_ENTITIES.put("&Ouml;", "&#214;");
        CODED_ENTITIES.put("&times;", "&#215;");
        CODED_ENTITIES.put("&Oslash;", "&#216;");
        CODED_ENTITIES.put("&Ugrave;", "&#217;");
        CODED_ENTITIES.put("&Uacute;", "&#218;");
        CODED_ENTITIES.put("&Ucirc;", "&#219;");
        CODED_ENTITIES.put("&Uuml;", "&#220;");
        CODED_ENTITIES.put("&Yacute;", "&#221;");
        CODED_ENTITIES.put("&THORN;", "&#222;");
        CODED_ENTITIES.put("&szlig;", "&#223;");
        CODED_ENTITIES.put("&agrave;", "&#224;");
        CODED_ENTITIES.put("&aacute;", "&#225;");
        CODED_ENTITIES.put("&acirc;", "&#226;");
        CODED_ENTITIES.put("&atilde;", "&#227;");
        CODED_ENTITIES.put("&auml;", "&#228;");
        CODED_ENTITIES.put("&aring;", "&#229;");
        CODED_ENTITIES.put("&aelig;", "&#230;");
        CODED_ENTITIES.put("&ccedil;", "&#231;");
        CODED_ENTITIES.put("&egrave;", "&#232;");
        CODED_ENTITIES.put("&eacute;", "&#233;");
        CODED_ENTITIES.put("&ecirc;", "&#234;");
        CODED_ENTITIES.put("&euml;", "&#235;");
        CODED_ENTITIES.put("&igrave;", "&#236;");
        CODED_ENTITIES.put("&iacute;", "&#237;");
        CODED_ENTITIES.put("&icirc;", "&#238;");
        CODED_ENTITIES.put("&iuml;", "&#239;");
        CODED_ENTITIES.put("&eth;", "&#240;");
        CODED_ENTITIES.put("&ntilde;", "&#241;");
        CODED_ENTITIES.put("&ograve;", "&#242;");
        CODED_ENTITIES.put("&oacute;", "&#243;");
        CODED_ENTITIES.put("&ocirc;", "&#244;");
        CODED_ENTITIES.put("&otilde;", "&#245;");
        CODED_ENTITIES.put("&ouml;", "&#246;");
        CODED_ENTITIES.put("&divide;", "&#247;");
        CODED_ENTITIES.put("&oslash;", "&#248;");
        CODED_ENTITIES.put("&ugrave;", "&#249;");
        CODED_ENTITIES.put("&uacute;", "&#250;");
        CODED_ENTITIES.put("&ucirc;", "&#251;");
        CODED_ENTITIES.put("&uuml;", "&#252;");
        CODED_ENTITIES.put("&yacute;", "&#253;");
        CODED_ENTITIES.put("&thorn;", "&#254;");
        CODED_ENTITIES.put("&yuml;", "&#255;");
        CODED_ENTITIES.put("&fnof;", "&#402;");
        CODED_ENTITIES.put("&Alpha;", "&#913;");
        CODED_ENTITIES.put("&Beta;", "&#914;");
        CODED_ENTITIES.put("&Gamma;", "&#915;");
        CODED_ENTITIES.put("&Delta;", "&#916;");
        CODED_ENTITIES.put("&Epsilon;", "&#917;");
        CODED_ENTITIES.put("&Zeta;", "&#918;");
        CODED_ENTITIES.put("&Eta;", "&#919;");
        CODED_ENTITIES.put("&Theta;", "&#920;");
        CODED_ENTITIES.put("&Iota;", "&#921;");
        CODED_ENTITIES.put("&Kappa;", "&#922;");
        CODED_ENTITIES.put("&Lambda;", "&#923;");
        CODED_ENTITIES.put("&Mu;", "&#924;");
        CODED_ENTITIES.put("&Nu;", "&#925;");
        CODED_ENTITIES.put("&Xi;", "&#926;");
        CODED_ENTITIES.put("&Omicron;", "&#927;");
        CODED_ENTITIES.put("&Pi;", "&#928;");
        CODED_ENTITIES.put("&Rho;", "&#929;");
        CODED_ENTITIES.put("&Sigma;", "&#931;");
        CODED_ENTITIES.put("&Tau;", "&#932;");
        CODED_ENTITIES.put("&Upsilon;", "&#933;");
        CODED_ENTITIES.put("&Phi;", "&#934;");
        CODED_ENTITIES.put("&Chi;", "&#935;");
        CODED_ENTITIES.put("&Psi;", "&#936;");
        CODED_ENTITIES.put("&Omega;", "&#937;");
        CODED_ENTITIES.put("&alpha;", "&#945;");
        CODED_ENTITIES.put("&beta;", "&#946;");
        CODED_ENTITIES.put("&gamma;", "&#947;");
        CODED_ENTITIES.put("&delta;", "&#948;");
        CODED_ENTITIES.put("&epsilon;", "&#949;");
        CODED_ENTITIES.put("&zeta;", "&#950;");
        CODED_ENTITIES.put("&eta;", "&#951;");
        CODED_ENTITIES.put("&theta;", "&#952;");
        CODED_ENTITIES.put("&iota;", "&#953;");
        CODED_ENTITIES.put("&kappa;", "&#954;");
        CODED_ENTITIES.put("&lambda;", "&#955;");
        CODED_ENTITIES.put("&mu;", "&#956;");
        CODED_ENTITIES.put("&nu;", "&#957;");
        CODED_ENTITIES.put("&xi;", "&#958;");
        CODED_ENTITIES.put("&omicron;", "&#959;");
        CODED_ENTITIES.put("&pi;", "&#960;");
        CODED_ENTITIES.put("&rho;", "&#961;");
        CODED_ENTITIES.put("&sigmaf;", "&#962;");
        CODED_ENTITIES.put("&sigma;", "&#963;");
        CODED_ENTITIES.put("&tau;", "&#964;");
        CODED_ENTITIES.put("&upsilon;", "&#965;");
        CODED_ENTITIES.put("&phi;", "&#966;");
        CODED_ENTITIES.put("&chi;", "&#967;");
        CODED_ENTITIES.put("&psi;", "&#968;");
        CODED_ENTITIES.put("&omega;", "&#969;");
        CODED_ENTITIES.put("&thetasym;", "&#977;");
        CODED_ENTITIES.put("&upsih;", "&#978;");
        CODED_ENTITIES.put("&piv;", "&#982;");
        CODED_ENTITIES.put("&bull;", "&#8226;");
        CODED_ENTITIES.put("&hellip;", "&#8230;");
        CODED_ENTITIES.put("&prime;", "&#8242;");
        CODED_ENTITIES.put("&Prime;", "&#8243;");
        CODED_ENTITIES.put("&oline;", "&#8254;");
        CODED_ENTITIES.put("&frasl;", "&#8260;");
        CODED_ENTITIES.put("&weierp;", "&#8472;");
        CODED_ENTITIES.put("&image;", "&#8465;");
        CODED_ENTITIES.put("&real;", "&#8476;");
        CODED_ENTITIES.put("&trade;", "&#8482;");
        CODED_ENTITIES.put("&alefsym;", "&#8501;");
        CODED_ENTITIES.put("&larr;", "&#8592;");
        CODED_ENTITIES.put("&uarr;", "&#8593;");
        CODED_ENTITIES.put("&rarr;", "&#8594;");
        CODED_ENTITIES.put("&darr;", "&#8595;");
        CODED_ENTITIES.put("&harr;", "&#8596;");
        CODED_ENTITIES.put("&crarr;", "&#8629;");
        CODED_ENTITIES.put("&lArr;", "&#8656;");
        CODED_ENTITIES.put("&uArr;", "&#8657;");
        CODED_ENTITIES.put("&rArr;", "&#8658;");
        CODED_ENTITIES.put("&dArr;", "&#8659;");
        CODED_ENTITIES.put("&hArr;", "&#8660;");
        CODED_ENTITIES.put("&forall;", "&#8704;");
        CODED_ENTITIES.put("&part;", "&#8706;");
        CODED_ENTITIES.put("&exist;", "&#8707;");
        CODED_ENTITIES.put("&empty;", "&#8709;");
        CODED_ENTITIES.put("&nabla;", "&#8711;");
        CODED_ENTITIES.put("&isin;", "&#8712;");
        CODED_ENTITIES.put("&notin;", "&#8713;");
        CODED_ENTITIES.put("&ni;", "&#8715;");
        CODED_ENTITIES.put("&prod;", "&#8719;");
        CODED_ENTITIES.put("&sum;", "&#8721;");
        CODED_ENTITIES.put("&minus;", "&#8722;");
        CODED_ENTITIES.put("&lowast;", "&#8727;");
        CODED_ENTITIES.put("&radic;", "&#8730;");
        CODED_ENTITIES.put("&prop;", "&#8733;");
        CODED_ENTITIES.put("&infin;", "&#8734;");
        CODED_ENTITIES.put("&ang;", "&#8736;");
        CODED_ENTITIES.put("&and;", "&#8743;");
        CODED_ENTITIES.put("&or;", "&#8744;");
        CODED_ENTITIES.put("&cap;", "&#8745;");
        CODED_ENTITIES.put("&cup;", "&#8746;");
        CODED_ENTITIES.put("&int;", "&#8747;");
        CODED_ENTITIES.put("&there4;", "&#8756;");
        CODED_ENTITIES.put("&sim;", "&#8764;");
        CODED_ENTITIES.put("&cong;", "&#8773;");
        CODED_ENTITIES.put("&asymp;", "&#8776;");
        CODED_ENTITIES.put("&ne;", "&#8800;");
        CODED_ENTITIES.put("&equiv;", "&#8801;");
        CODED_ENTITIES.put("&le;", "&#8804;");
        CODED_ENTITIES.put("&ge;", "&#8805;");
        CODED_ENTITIES.put("&sub;", "&#8834;");
        CODED_ENTITIES.put("&sup;", "&#8835;");
        CODED_ENTITIES.put("&nsub;", "&#8836;");
        CODED_ENTITIES.put("&sube;", "&#8838;");
        CODED_ENTITIES.put("&supe;", "&#8839;");
        CODED_ENTITIES.put("&oplus;", "&#8853;");
        CODED_ENTITIES.put("&otimes;", "&#8855;");
        CODED_ENTITIES.put("&perp;", "&#8869;");
        CODED_ENTITIES.put("&sdot;", "&#8901;");
        CODED_ENTITIES.put("&lceil;", "&#8968;");
        CODED_ENTITIES.put("&rceil;", "&#8969;");
        CODED_ENTITIES.put("&lfloor;", "&#8970;");
        CODED_ENTITIES.put("&rfloor;", "&#8971;");
        CODED_ENTITIES.put("&lang;", "&#9001;");
        CODED_ENTITIES.put("&rang;", "&#9002;");
        CODED_ENTITIES.put("&loz;", "&#9674;");
        CODED_ENTITIES.put("&spades;", "&#9824;");
        CODED_ENTITIES.put("&clubs;", "&#9827;");
        CODED_ENTITIES.put("&hearts;", "&#9829;");
        CODED_ENTITIES.put("&diams;", "&#9830;");
        CODED_ENTITIES.put("&quot;", "&#34;");
        CODED_ENTITIES.put("&amp;", "&#38;");
        CODED_ENTITIES.put("&lt;", "&#60;");
        CODED_ENTITIES.put("&gt;", "&#62;");
        CODED_ENTITIES.put("&OElig;", "&#338;");
        CODED_ENTITIES.put("&oelig;", "&#339;");
        CODED_ENTITIES.put("&Scaron;", "&#352;");
        CODED_ENTITIES.put("&scaron;", "&#353;");
        CODED_ENTITIES.put("&Yuml;", "&#376;");
        CODED_ENTITIES.put("&circ;", "&#710;");
        CODED_ENTITIES.put("&tilde;", "&#732;");
        CODED_ENTITIES.put("&ensp;", "&#8194;");
        CODED_ENTITIES.put("&emsp;", "&#8195;");
        CODED_ENTITIES.put("&thinsp;", "&#8201;");
        CODED_ENTITIES.put("&zwnj;", "&#8204;");
        CODED_ENTITIES.put("&zwj;", "&#8205;");
        CODED_ENTITIES.put("&lrm;", "&#8206;");
        CODED_ENTITIES.put("&rlm;", "&#8207;");
        CODED_ENTITIES.put("&ndash;", "&#8211;");
        CODED_ENTITIES.put("&mdash;", "&#8212;");
        CODED_ENTITIES.put("&lsquo;", "&#8216;");
        CODED_ENTITIES.put("&rsquo;", "&#8217;");
        CODED_ENTITIES.put("&sbquo;", "&#8218;");
        CODED_ENTITIES.put("&ldquo;", "&#8220;");
        CODED_ENTITIES.put("&rdquo;", "&#8221;");
        CODED_ENTITIES.put("&bdquo;", "&#8222;");
        CODED_ENTITIES.put("&dagger;", "&#8224;");
        CODED_ENTITIES.put("&Dagger;", "&#8225;");
        CODED_ENTITIES.put("&permil;", "&#8240;");
        CODED_ENTITIES.put("&lsaquo;", "&#8249;");
        CODED_ENTITIES.put("&rsaquo;", "&#8250;");
        CODED_ENTITIES.put("&euro;", "&#8364;");
        ENTITIES_PATTERN = Pattern.compile("&[A-Za-z^#]+;");
    }
}

