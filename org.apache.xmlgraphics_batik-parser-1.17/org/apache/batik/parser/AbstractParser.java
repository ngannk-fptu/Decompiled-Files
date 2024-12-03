/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.i18n.LocalizableSupport
 *  org.apache.batik.util.io.NormalizingReader
 *  org.apache.batik.util.io.StreamNormalizingReader
 *  org.apache.batik.util.io.StringNormalizingReader
 */
package org.apache.batik.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.parser.DefaultErrorHandler;
import org.apache.batik.parser.ErrorHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.Parser;
import org.apache.batik.util.io.NormalizingReader;
import org.apache.batik.util.io.StreamNormalizingReader;
import org.apache.batik.util.io.StringNormalizingReader;

public abstract class AbstractParser
implements Parser {
    public static final String BUNDLE_CLASSNAME = "org.apache.batik.parser.resources.Messages";
    protected ErrorHandler errorHandler = new DefaultErrorHandler();
    protected LocalizableSupport localizableSupport = new LocalizableSupport("org.apache.batik.parser.resources.Messages", AbstractParser.class.getClassLoader());
    protected NormalizingReader reader;
    protected int current;

    public int getCurrent() {
        return this.current;
    }

    public void setLocale(Locale l) {
        this.localizableSupport.setLocale(l);
    }

    public Locale getLocale() {
        return this.localizableSupport.getLocale();
    }

    public String formatMessage(String key, Object[] args) throws MissingResourceException {
        return this.localizableSupport.formatMessage(key, args);
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override
    public void parse(Reader r) throws ParseException {
        try {
            this.reader = new StreamNormalizingReader(r);
            this.doParse();
        }
        catch (IOException e) {
            this.errorHandler.error(new ParseException(this.createErrorMessage("io.exception", null), e));
        }
    }

    public void parse(InputStream is, String enc) throws ParseException {
        try {
            this.reader = new StreamNormalizingReader(is, enc);
            this.doParse();
        }
        catch (IOException e) {
            this.errorHandler.error(new ParseException(this.createErrorMessage("io.exception", null), e));
        }
    }

    @Override
    public void parse(String s) throws ParseException {
        try {
            this.reader = new StringNormalizingReader(s);
            this.doParse();
        }
        catch (IOException e) {
            this.errorHandler.error(new ParseException(this.createErrorMessage("io.exception", null), e));
        }
    }

    protected abstract void doParse() throws ParseException, IOException;

    protected void reportError(String key, Object[] args) throws ParseException {
        this.errorHandler.error(new ParseException(this.createErrorMessage(key, args), this.reader.getLine(), this.reader.getColumn()));
    }

    protected void reportCharacterExpectedError(char expectedChar, int currentChar) {
        this.reportError("character.expected", new Object[]{Character.valueOf(expectedChar), currentChar});
    }

    protected void reportUnexpectedCharacterError(int currentChar) {
        this.reportError("character.unexpected", new Object[]{currentChar});
    }

    protected String createErrorMessage(String key, Object[] args) {
        try {
            return this.formatMessage(key, args);
        }
        catch (MissingResourceException e) {
            return key;
        }
    }

    protected String getBundleClassName() {
        return BUNDLE_CLASSNAME;
    }

    protected void skipSpaces() throws IOException {
        while (true) {
            switch (this.current) {
                default: {
                    return;
                }
                case 9: 
                case 10: 
                case 13: 
                case 32: 
            }
            this.current = this.reader.read();
        }
    }

    protected void skipCommaSpaces() throws IOException {
        block6: while (true) {
            switch (this.current) {
                default: {
                    break block6;
                }
                case 9: 
                case 10: 
                case 13: 
                case 32: {
                    this.current = this.reader.read();
                    continue block6;
                }
            }
            break;
        }
        if (this.current == 44) {
            block7: while (true) {
                this.current = this.reader.read();
                switch (this.current) {
                    default: {
                        break block7;
                    }
                    case 9: 
                    case 10: 
                    case 13: 
                    case 32: {
                        continue block7;
                    }
                }
                break;
            }
        }
    }
}

