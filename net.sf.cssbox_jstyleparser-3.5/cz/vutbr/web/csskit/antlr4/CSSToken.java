/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.CommonToken
 *  org.antlr.v4.runtime.Lexer
 *  org.antlr.v4.runtime.TokenSource
 *  org.antlr.v4.runtime.misc.Pair
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSLexerState;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

public class CSSToken
extends CommonToken {
    private static final long serialVersionUID = 3L;
    protected CSSLexerState ls;
    protected URL base;
    protected boolean valid = true;
    public static final int FUNCTION = 1;
    public static final int URI = 2;
    public static final int STRING = 3;
    public static final int CLASSKEYWORD = 4;
    public static final int HASH = 5;
    public static final int UNCLOSED_STRING = 6;
    public static final int UNCLOSED_URI = 7;
    private final TypeMapper typeMapper;

    public CSSToken(Pair<TokenSource, CharStream> input, int type, int channel, int start, int stop, TypeMapper typeMapper) {
        super(input, type, channel, start, stop);
        this.typeMapper = typeMapper;
    }

    public CSSToken(int type, CSSLexerState state, TypeMapper typeMapper) {
        this(type, state, 0, 0, typeMapper);
    }

    public CSSToken(int type, CSSLexerState state, int start, int stop, TypeMapper typeMapper) {
        this((Pair<TokenSource, CharStream>)new Pair(null, null), type, 0, start, stop, typeMapper);
        this.ls = new CSSLexerState(state);
    }

    public CSSToken setLexerState(CSSLexerState state) {
        this.ls = state;
        return this;
    }

    public CSSLexerState getLexerState() {
        return this.ls;
    }

    public URL getBase() {
        return this.base;
    }

    public void setBase(URL base) {
        this.base = base;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public static String extractSTRING(String string) {
        return string.substring(1, string.length() - 1);
    }

    public static String extractUNCLOSEDSTRING(String string) {
        return string.substring(1, string.length());
    }

    public static String extractURI(String uri) {
        String ret = uri.substring(4, uri.length() - 1).trim();
        if (ret.length() > 0 && (ret.charAt(0) == '\'' || ret.charAt(0) == '\"')) {
            ret = ret.substring(1, ret.length() - 1);
        }
        return ret;
    }

    public static String extractUNCLOSEDURI(String uri) {
        char fc;
        String ret = uri.substring(4).trim();
        if (ret.length() > 0 && ((fc = ret.charAt(0)) == '\'' || fc == '\"')) {
            char lc = ret.length() > 1 ? ret.charAt(ret.length() - 1) : (char)' ';
            ret = fc == lc ? ret.substring(1, ret.length() - 1) : ret.substring(1, ret.length());
        }
        return ret;
    }

    public static String extractFUNCTION(String function) {
        return function.substring(0, function.length() - 1);
    }

    public static String extractHASH(String hash) {
        return hash.substring(1, hash.length());
    }

    public static String extractCLASSKEYWORD(String className) {
        return className.substring(1, className.length());
    }

    public String getText() {
        int t;
        this.text = super.getText();
        try {
            t = this.typeMapper.inverse().get(this.type);
        }
        catch (NullPointerException e) {
            return this.text;
        }
        switch (t) {
            case 1: {
                return this.text.substring(0, this.text.length() - 1);
            }
            case 2: {
                return CSSToken.extractURI(this.text);
            }
            case 7: {
                return CSSToken.extractUNCLOSEDURI(this.text);
            }
            case 3: {
                return CSSToken.extractSTRING(this.text);
            }
            case 6: {
                return CSSToken.extractUNCLOSEDSTRING(this.text);
            }
            case 4: {
                return CSSToken.extractCLASSKEYWORD(this.text);
            }
            case 5: {
                return CSSToken.extractHASH(this.text);
            }
        }
        return this.text;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(this.ls).append("/").append(super.toString());
        return sb.toString();
    }

    public static TypeMapper createDefaultTypeMapper(Class<? extends Lexer> lexerClass) {
        return new TypeMapper(CSSToken.class, lexerClass, "FUNCTION", "URI", "STRING", "CLASSKEYWORD", "HASH", "UNCLOSED_STRING", "UNCLOSED_URI");
    }

    public static String extractCHARSET(String charset) {
        String arg = charset.replace("@charset", "").replace(";", "").trim();
        if (arg.length() > 2) {
            return CSSToken.extractSTRING(arg);
        }
        return "";
    }

    public static class TypeMapper {
        private final Map<Integer, Integer> map;
        private final TypeMapper inverse;

        private TypeMapper(Map<Integer, Integer> map, TypeMapper inverse) {
            this.map = map;
            this.inverse = inverse;
        }

        public TypeMapper(Class<?> classA, Class<?> classB, String ... fieldNames) {
            this.map = new TreeMap<Integer, Integer>();
            TreeMap<Integer, Integer> inverseMap = new TreeMap<Integer, Integer>();
            for (String f : fieldNames) {
                try {
                    int a = classA.getField(f).getInt(null);
                    int b = classB.getField(f).getInt(null);
                    this.map.put(a, b);
                    inverseMap.put(b, a);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
            this.inverse = new TypeMapper(inverseMap, this);
        }

        public int get(int type) throws NullPointerException {
            return this.map.get(type);
        }

        public TypeMapper inverse() {
            return this.inverse;
        }
    }
}

