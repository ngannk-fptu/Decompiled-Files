/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.io.LineColumnReader;
import groovy.json.JsonException;
import groovy.json.JsonToken;
import groovy.json.JsonTokenType;
import groovy.json.StringEscapeUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class JsonLexer
implements Iterator<JsonToken> {
    private static final char SPACE = ' ';
    private static final char DOT = '.';
    private static final char MINUS = '-';
    private static final char PLUS = '+';
    private static final char LOWER_E = 'e';
    private static final char UPPER_E = 'E';
    private static final char ZERO = '0';
    private static final char NINE = '9';
    private LineColumnReader reader;
    private JsonToken currentToken = null;

    public LineColumnReader getReader() {
        return this.reader;
    }

    public JsonLexer(Reader reader) {
        this.reader = reader instanceof LineColumnReader ? (LineColumnReader)reader : new LineColumnReader(reader);
    }

    public JsonToken nextToken() {
        try {
            int firstIntRead = this.skipWhitespace();
            if (firstIntRead == -1) {
                return null;
            }
            char firstChar = (char)firstIntRead;
            JsonTokenType possibleTokenType = JsonTokenType.startingWith((char)firstIntRead);
            if (possibleTokenType == null) {
                throw new JsonException("Lexing failed on line: " + this.reader.getLine() + ", column: " + this.reader.getColumn() + ", while reading '" + firstChar + "', no possible valid JSON value or punctuation could be recognized.");
            }
            this.reader.reset();
            long startLine = this.reader.getLine();
            long startColumn = this.reader.getColumn();
            JsonToken token = new JsonToken();
            token.setStartLine(startLine);
            token.setStartColumn(startColumn);
            token.setEndLine(startLine);
            token.setEndColumn(startColumn + 1L);
            token.setType(possibleTokenType);
            token.setText("" + firstChar);
            if (possibleTokenType.ordinal() >= JsonTokenType.OPEN_CURLY.ordinal() && possibleTokenType.ordinal() <= JsonTokenType.FALSE.ordinal()) {
                return this.readingConstant(possibleTokenType, token);
            }
            if (possibleTokenType == JsonTokenType.STRING) {
                char charRead;
                StringBuilder currentContent = new StringBuilder("\"");
                this.reader.read();
                boolean isEscaped = false;
                do {
                    int read;
                    if ((read = this.reader.read()) == -1) {
                        return null;
                    }
                    isEscaped = !isEscaped && currentContent.charAt(currentContent.length() - 1) == '\\';
                    charRead = (char)read;
                    currentContent.append(charRead);
                } while (charRead != '\"' || isEscaped || !possibleTokenType.matching(currentContent.toString()));
                token.setEndLine(this.reader.getLine());
                token.setEndColumn(this.reader.getColumn());
                token.setText(JsonLexer.unescape(currentContent.toString()));
                return token;
            }
            if (possibleTokenType == JsonTokenType.NUMBER) {
                StringBuilder currentContent = new StringBuilder();
                while (true) {
                    this.reader.mark(1);
                    int read = this.reader.read();
                    if (read == -1) {
                        return null;
                    }
                    char lastCharRead = (char)read;
                    if ((lastCharRead < '0' || lastCharRead > '9') && lastCharRead != '.' && lastCharRead != '-' && lastCharRead != '+' && lastCharRead != 'e' && lastCharRead != 'E') break;
                    currentContent.append(lastCharRead);
                }
                this.reader.reset();
                String content = currentContent.toString();
                if (possibleTokenType.matching(content)) {
                    token.setEndLine(this.reader.getLine());
                    token.setEndColumn(this.reader.getColumn());
                    token.setText(currentContent.toString());
                    return token;
                }
                this.throwJsonException(currentContent.toString(), possibleTokenType);
            }
            return null;
        }
        catch (IOException ioe) {
            throw new JsonException("An IO exception occurred while reading the JSON payload", ioe);
        }
    }

    private void throwJsonException(String content, JsonTokenType type) {
        throw new JsonException("Lexing failed on line: " + this.reader.getLine() + ", column: " + this.reader.getColumn() + ", while reading '" + content + "', was trying to match " + type.getLabel());
    }

    public static String unescape(String input) {
        return StringEscapeUtils.unescapeJavaScript(input);
    }

    private JsonToken readingConstant(JsonTokenType type, JsonToken token) {
        try {
            int numCharsToRead = ((String)type.getValidator()).length();
            char[] chars = new char[numCharsToRead];
            this.reader.read(chars);
            String stringRead = new String(chars);
            if (stringRead.equals(type.getValidator())) {
                token.setEndColumn(token.getStartColumn() + (long)numCharsToRead);
                token.setText(stringRead);
                return token;
            }
            this.throwJsonException(stringRead, type);
        }
        catch (IOException ioe) {
            throw new JsonException("An IO exception occurred while reading the JSON payload", ioe);
        }
        return null;
    }

    public int skipWhitespace() {
        try {
            int readChar = 20;
            char c = ' ';
            while (Character.isWhitespace(c)) {
                this.reader.mark(1);
                readChar = this.reader.read();
                c = (char)readChar;
            }
            this.reader.reset();
            return readChar;
        }
        catch (IOException ioe) {
            throw new JsonException("An IO exception occurred while reading the JSON payload", ioe);
        }
    }

    @Override
    public boolean hasNext() {
        this.currentToken = this.nextToken();
        return this.currentToken != null;
    }

    @Override
    public JsonToken next() {
        return this.currentToken;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("The method remove() is not supported on this lexer.");
    }
}

