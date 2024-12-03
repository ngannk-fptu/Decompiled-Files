/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.JsonException;
import groovy.json.JsonParser;
import groovy.json.internal.CharBuf;
import groovy.json.internal.Exceptions;
import groovy.json.internal.FastStringUtils;
import groovy.json.internal.IO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public abstract class BaseJsonParser
implements JsonParser {
    protected static final int COLON = 58;
    protected static final int COMMA = 44;
    protected static final int CLOSED_CURLY = 125;
    protected static final int CLOSED_BRACKET = 93;
    protected static final int LETTER_E = 101;
    protected static final int LETTER_BIG_E = 69;
    protected static final int MINUS = 45;
    protected static final int PLUS = 43;
    protected static final int DECIMAL_POINT = 46;
    protected static final int ALPHA_0 = 48;
    protected static final int ALPHA_1 = 49;
    protected static final int ALPHA_2 = 50;
    protected static final int ALPHA_3 = 51;
    protected static final int ALPHA_4 = 52;
    protected static final int ALPHA_5 = 53;
    protected static final int ALPHA_6 = 54;
    protected static final int ALPHA_7 = 55;
    protected static final int ALPHA_8 = 56;
    protected static final int ALPHA_9 = 57;
    protected static final int DOUBLE_QUOTE = 34;
    protected static final int ESCAPE = 92;
    protected static final boolean internKeys = Boolean.parseBoolean(System.getProperty("groovy.json.internKeys", "false"));
    protected static ConcurrentHashMap<String, String> internedKeysCache;
    private static final Charset UTF_8;
    protected String charset = UTF_8.name();
    private CharBuf fileInputBuf;
    protected int bufSize = 256;
    int[] indexHolder = new int[1];

    protected String charDescription(int c) {
        String charString = c == 32 ? "[SPACE]" : (c == 9 ? "[TAB]" : (c == 10 ? "[NEWLINE]" : "'" + (char)c + "'"));
        charString = charString + " with an int value of " + c;
        return charString;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public Object parse(String jsonString) {
        return this.parse(FastStringUtils.toCharArray(jsonString));
    }

    @Override
    public Object parse(byte[] bytes) {
        return this.parse(bytes, this.charset);
    }

    @Override
    public Object parse(byte[] bytes, String charset) {
        try {
            return this.parse(new String(bytes, charset));
        }
        catch (UnsupportedEncodingException e) {
            return Exceptions.handle(Object.class, e);
        }
    }

    @Override
    public Object parse(CharSequence charSequence) {
        return this.parse(FastStringUtils.toCharArray(charSequence));
    }

    @Override
    public Object parse(Reader reader) {
        this.fileInputBuf = IO.read(reader, this.fileInputBuf, this.bufSize);
        return this.parse(this.fileInputBuf.readForRecycle());
    }

    @Override
    public Object parse(InputStream input) {
        return this.parse(input, this.charset);
    }

    @Override
    public Object parse(InputStream input, String charset) {
        try {
            return this.parse(new InputStreamReader(input, charset));
        }
        catch (UnsupportedEncodingException e) {
            return Exceptions.handle(Object.class, e);
        }
    }

    @Override
    public Object parse(File file, String charset) {
        BufferedReader reader = null;
        try {
            reader = charset == null || charset.length() == 0 ? ResourceGroovyMethods.newReader(file) : ResourceGroovyMethods.newReader(file, charset);
            Object object = this.parse(reader);
            return object;
        }
        catch (IOException ioe) {
            throw new JsonException("Unable to process file: " + file.getPath(), ioe);
        }
        finally {
            if (reader != null) {
                DefaultGroovyMethodsSupport.closeWithWarning(reader);
            }
        }
    }

    protected static boolean isDecimalChar(int currentChar) {
        switch (currentChar) {
            case 43: 
            case 45: 
            case 46: 
            case 69: 
            case 101: {
                return true;
            }
        }
        return false;
    }

    protected static boolean isDelimiter(int c) {
        return c == 44 || c == 125 || c == 93;
    }

    protected static final boolean isNumberDigit(int c) {
        return c >= 48 && c <= 57;
    }

    protected static final boolean isDoubleQuote(int c) {
        return c == 34;
    }

    protected static final boolean isEscape(int c) {
        return c == 92;
    }

    protected static boolean hasEscapeChar(char[] array, int index, int[] indexHolder) {
        while (index < array.length) {
            char currentChar = array[index];
            if (BaseJsonParser.isDoubleQuote(currentChar)) {
                indexHolder[0] = index;
                return false;
            }
            if (BaseJsonParser.isEscape(currentChar)) {
                indexHolder[0] = index;
                return true;
            }
            ++index;
        }
        indexHolder[0] = index;
        return false;
    }

    protected static int findEndQuote(char[] array, int index) {
        char currentChar;
        boolean escape = false;
        while (index < array.length && (!BaseJsonParser.isDoubleQuote(currentChar = array[index]) || escape)) {
            escape = BaseJsonParser.isEscape(currentChar) && !escape;
            ++index;
        }
        return index;
    }

    static {
        UTF_8 = Charset.forName("UTF-8");
        if (internKeys) {
            internedKeysCache = new ConcurrentHashMap();
        }
    }
}

