/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.io.LineColumnReader;
import groovy.json.JsonException;
import groovy.json.JsonLexer;
import groovy.json.JsonToken;
import groovy.json.JsonTokenType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class JsonSlurperClassic {
    public Object parseText(String text) {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("The JSON input text should neither be null nor empty.");
        }
        return this.parse(new LineColumnReader(new StringReader(text)));
    }

    public Object parse(Reader reader) {
        Object content;
        JsonLexer lexer = new JsonLexer(reader);
        JsonToken token = lexer.nextToken();
        if (token.getType() == JsonTokenType.OPEN_CURLY) {
            content = this.parseObject(lexer);
        } else if (token.getType() == JsonTokenType.OPEN_BRACKET) {
            content = this.parseArray(lexer);
        } else {
            throw new JsonException("A JSON payload should start with " + JsonTokenType.OPEN_CURLY.getLabel() + " or " + JsonTokenType.OPEN_BRACKET.getLabel() + ".\nInstead, '" + token.getText() + "' was found on line: " + token.getStartLine() + ", column: " + token.getStartColumn());
        }
        return content;
    }

    public Object parse(File file) {
        return this.parseFile(file, null);
    }

    public Object parse(File file, String charset) {
        return this.parseFile(file, charset);
    }

    private Object parseFile(File file, String charset) {
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

    private List parseArray(JsonLexer lexer) {
        ArrayList<Object> content;
        block10: {
            JsonToken currentToken;
            content = new ArrayList<Object>();
            do {
                if ((currentToken = lexer.nextToken()) == null) {
                    throw new JsonException("Expected a value on line: " + lexer.getReader().getLine() + ", column: " + lexer.getReader().getColumn() + ".\nBut got an unterminated array.");
                }
                if (currentToken.getType() == JsonTokenType.OPEN_CURLY) {
                    content.add(this.parseObject(lexer));
                } else if (currentToken.getType() == JsonTokenType.OPEN_BRACKET) {
                    content.add(this.parseArray(lexer));
                } else if (currentToken.getType().ordinal() >= JsonTokenType.NULL.ordinal()) {
                    content.add(currentToken.getValue());
                } else {
                    if (currentToken.getType() == JsonTokenType.CLOSE_BRACKET) {
                        return content;
                    }
                    throw new JsonException("Expected a value, an array, or an object on line: " + currentToken.getStartLine() + ", column: " + currentToken.getStartColumn() + ".\nBut got '" + currentToken.getText() + "' instead.");
                }
                currentToken = lexer.nextToken();
                if (currentToken == null) {
                    throw new JsonException("Expected " + JsonTokenType.CLOSE_BRACKET.getLabel() + " or " + JsonTokenType.COMMA.getLabel() + " on line: " + lexer.getReader().getLine() + ", column: " + lexer.getReader().getColumn() + ".\nBut got an unterminated array.");
                }
                if (currentToken.getType() == JsonTokenType.CLOSE_BRACKET) break block10;
            } while (currentToken.getType() == JsonTokenType.COMMA);
            throw new JsonException("Expected a value or " + JsonTokenType.CLOSE_BRACKET.getLabel() + " on line: " + currentToken.getStartLine() + " column: " + currentToken.getStartColumn() + ".\nBut got '" + currentToken.getText() + "' instead.");
        }
        return content;
    }

    private Map parseObject(JsonLexer lexer) {
        HashMap<String, Object> content;
        block14: {
            content = new HashMap<String, Object>();
            JsonToken previousToken = null;
            JsonToken currentToken = null;
            do {
                if ((currentToken = lexer.nextToken()) == null) {
                    throw new JsonException("Expected a String key on line: " + lexer.getReader().getLine() + ", column: " + lexer.getReader().getColumn() + ".\nBut got an unterminated object.");
                }
                if (currentToken.getType() == JsonTokenType.CLOSE_CURLY) {
                    return content;
                }
                if (currentToken.getType() != JsonTokenType.STRING) {
                    throw new JsonException("Expected " + JsonTokenType.STRING.getLabel() + " key on line: " + currentToken.getStartLine() + ", column: " + currentToken.getStartColumn() + ".\nBut got '" + currentToken.getText() + "' instead.");
                }
                String mapKey = (String)currentToken.getValue();
                currentToken = lexer.nextToken();
                if (currentToken == null) {
                    throw new JsonException("Expected a " + JsonTokenType.COLON.getLabel() + " on line: " + lexer.getReader().getLine() + ", column: " + lexer.getReader().getColumn() + ".\nBut got an unterminated object.");
                }
                if (currentToken.getType() != JsonTokenType.COLON) {
                    throw new JsonException("Expected " + JsonTokenType.COLON.getLabel() + " on line: " + currentToken.getStartLine() + ", column: " + currentToken.getStartColumn() + ".\nBut got '" + currentToken.getText() + "' instead.");
                }
                currentToken = lexer.nextToken();
                if (currentToken == null) {
                    throw new JsonException("Expected a value on line: " + lexer.getReader().getLine() + ", column: " + lexer.getReader().getColumn() + ".\nBut got an unterminated object.");
                }
                if (currentToken.getType() == JsonTokenType.OPEN_CURLY) {
                    content.put(mapKey, this.parseObject(lexer));
                } else if (currentToken.getType() == JsonTokenType.OPEN_BRACKET) {
                    content.put(mapKey, this.parseArray(lexer));
                } else if (currentToken.getType().ordinal() >= JsonTokenType.NULL.ordinal()) {
                    content.put(mapKey, currentToken.getValue());
                } else {
                    throw new JsonException("Expected a value, an array, or an object on line: " + currentToken.getStartLine() + ", column: " + currentToken.getStartColumn() + ".\nBut got '" + currentToken.getText() + "' instead.");
                }
                previousToken = currentToken;
                currentToken = lexer.nextToken();
                if (currentToken == null) {
                    throw new JsonException("Expected " + JsonTokenType.CLOSE_CURLY.getLabel() + " or " + JsonTokenType.COMMA.getLabel() + " on line: " + previousToken.getEndLine() + ", column: " + previousToken.getEndColumn() + ".\nBut got an unterminated object.");
                }
                if (currentToken.getType() == JsonTokenType.CLOSE_CURLY) break block14;
            } while (currentToken.getType() == JsonTokenType.COMMA);
            throw new JsonException("Expected a value or " + JsonTokenType.CLOSE_CURLY.getLabel() + " on line: " + currentToken.getStartLine() + ", column: " + currentToken.getStartColumn() + ".\nBut got '" + currentToken.getText() + "' instead.");
        }
        return content;
    }
}

