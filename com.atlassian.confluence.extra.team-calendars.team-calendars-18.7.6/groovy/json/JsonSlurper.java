/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.json.JsonException;
import groovy.json.JsonParser;
import groovy.json.JsonParserType;
import groovy.json.internal.JsonFastParser;
import groovy.json.internal.JsonParserCharArray;
import groovy.json.internal.JsonParserLax;
import groovy.json.internal.JsonParserUsingCharacterSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class JsonSlurper {
    private int maxSizeForInMemory = 2000000;
    private boolean chop = false;
    private boolean lazyChop = true;
    private boolean checkDates = true;
    private JsonParserType type = JsonParserType.CHAR_BUFFER;

    public int getMaxSizeForInMemory() {
        return this.maxSizeForInMemory;
    }

    public JsonSlurper setMaxSizeForInMemory(int maxSizeForInMemory) {
        this.maxSizeForInMemory = maxSizeForInMemory;
        return this;
    }

    public JsonParserType getType() {
        return this.type;
    }

    public JsonSlurper setType(JsonParserType type) {
        this.type = type;
        return this;
    }

    public boolean isChop() {
        return this.chop;
    }

    public JsonSlurper setChop(boolean chop) {
        this.chop = chop;
        return this;
    }

    public boolean isLazyChop() {
        return this.lazyChop;
    }

    public JsonSlurper setLazyChop(boolean lazyChop) {
        this.lazyChop = lazyChop;
        return this;
    }

    public boolean isCheckDates() {
        return this.checkDates;
    }

    public JsonSlurper setCheckDates(boolean checkDates) {
        this.checkDates = checkDates;
        return this;
    }

    public Object parseText(String text) {
        if (text == null || "".equals(text)) {
            throw new IllegalArgumentException("Text must not be null or empty");
        }
        return this.createParser().parse(text);
    }

    public Object parse(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        JsonParser parser = this.createParser();
        Object content = parser.parse(reader);
        return content;
    }

    public Object parse(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream must not be null");
        }
        JsonParser parser = this.createParser();
        Object content = parser.parse(inputStream);
        return content;
    }

    public Object parse(InputStream inputStream, String charset) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream must not be null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("charset must not be null");
        }
        Object content = this.createParser().parse(inputStream, charset);
        return content;
    }

    public Object parse(byte[] bytes, String charset) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes must not be null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("charset must not be null");
        }
        Object content = this.createParser().parse(bytes, charset);
        return content;
    }

    public Object parse(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes must not be null");
        }
        Object content = this.createParser().parse(bytes);
        return content;
    }

    public Object parse(char[] chars) {
        if (chars == null) {
            throw new IllegalArgumentException("chars must not be null");
        }
        Object content = this.createParser().parse(chars);
        return content;
    }

    private JsonParser createParser() {
        switch (this.type) {
            case LAX: {
                return new JsonParserLax(false, this.chop, this.lazyChop, this.checkDates);
            }
            case CHAR_BUFFER: {
                return new JsonParserCharArray();
            }
            case CHARACTER_SOURCE: {
                return new JsonParserUsingCharacterSource();
            }
            case INDEX_OVERLAY: {
                return new JsonFastParser(false, this.chop, this.lazyChop, this.checkDates);
            }
        }
        return new JsonParserCharArray();
    }

    public Object parse(File file) {
        return this.parseFile(file, null);
    }

    public Object parse(File file, String charset) {
        return this.parseFile(file, charset);
    }

    private Object parseFile(File file, String charset) {
        if (file.length() < (long)this.maxSizeForInMemory) {
            return this.createParser().parse(file, charset);
        }
        return new JsonParserUsingCharacterSource().parse(file, charset);
    }

    public Object parse(URL url) {
        return this.parseURL(url, null);
    }

    public Object parse(URL url, Map params) {
        return this.parseURL(url, params);
    }

    public Object parse(Map params, URL url) {
        return this.parseURL(url, params);
    }

    private Object parseURL(URL url, Map params) {
        BufferedReader reader = null;
        try {
            reader = params == null || params.isEmpty() ? ResourceGroovyMethods.newReader(url) : ResourceGroovyMethods.newReader(url, params);
            Object object = this.createParser().parse(reader);
            return object;
        }
        catch (IOException ioe) {
            throw new JsonException("Unable to process url: " + url.toString(), ioe);
        }
        finally {
            if (reader != null) {
                DefaultGroovyMethodsSupport.closeWithWarning(reader);
            }
        }
    }

    public Object parse(URL url, String charset) {
        return this.parseURL(url, null, charset);
    }

    public Object parse(URL url, Map params, String charset) {
        return this.parseURL(url, params, charset);
    }

    public Object parse(Map params, URL url, String charset) {
        return this.parseURL(url, params, charset);
    }

    private Object parseURL(URL url, Map params, String charset) {
        BufferedReader reader = null;
        try {
            reader = params == null || params.isEmpty() ? ResourceGroovyMethods.newReader(url, charset) : ResourceGroovyMethods.newReader(url, params, charset);
            Object object = this.parse(reader);
            return object;
        }
        catch (IOException ioe) {
            throw new JsonException("Unable to process url: " + url.toString(), ioe);
        }
        finally {
            if (reader != null) {
                DefaultGroovyMethodsSupport.closeWithWarning(reader);
            }
        }
    }
}

