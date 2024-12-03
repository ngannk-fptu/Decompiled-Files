/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.lang.Closure;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum JsonTokenType {
    OPEN_CURLY("an openning curly brace '{'", "{"),
    CLOSE_CURLY("a closing curly brace '}'", "}"),
    OPEN_BRACKET("an openning square bracket '['", "["),
    CLOSE_BRACKET("a closing square bracket ']'", "]"),
    COLON("a colon ':'", ":"),
    COMMA("a comma ','", ","),
    NULL("the constant 'null'", "null"),
    TRUE("the constant 'true'", "true"),
    FALSE("the constant 'false'", "false"),
    NUMBER("a number", Pattern.compile("-?\\d+(\\.\\d+)?((e|E)(\\+|-)?\\d+)?")),
    STRING("a string", new Closure(null){
        private Pattern replacePattern = Pattern.compile("(?:\\\\[\"\\\\bfnrt\\/]|\\\\u[0-9a-fA-F]{4})");
        private Pattern validatePattern = Pattern.compile("\"[^\"\\\\]*\"");

        boolean doCall(String it) {
            return this.validatePattern.matcher(this.replacePattern.matcher(it).replaceAll("@")).matches();
        }
    });

    private Object validator;
    private String label;

    private JsonTokenType(String label, Object validator) {
        this.validator = validator;
        this.label = label;
    }

    public boolean matching(String input) {
        if (this.validator instanceof Pattern) {
            Matcher matcher = ((Pattern)this.validator).matcher(input);
            return matcher.matches();
        }
        if (this.validator instanceof Closure) {
            return (Boolean)((Closure)this.validator).call((Object)input);
        }
        if (this.validator instanceof String) {
            return input.equals(this.validator);
        }
        return false;
    }

    public static JsonTokenType startingWith(char c) {
        switch (c) {
            case '{': {
                return OPEN_CURLY;
            }
            case '}': {
                return CLOSE_CURLY;
            }
            case '[': {
                return OPEN_BRACKET;
            }
            case ']': {
                return CLOSE_BRACKET;
            }
            case ',': {
                return COMMA;
            }
            case ':': {
                return COLON;
            }
            case 't': {
                return TRUE;
            }
            case 'f': {
                return FALSE;
            }
            case 'n': {
                return NULL;
            }
            case '\"': {
                return STRING;
            }
            case '-': 
            case '0': 
            case '1': 
            case '2': 
            case '3': 
            case '4': 
            case '5': 
            case '6': 
            case '7': 
            case '8': 
            case '9': {
                return NUMBER;
            }
        }
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    public Object getValidator() {
        return this.validator;
    }
}

