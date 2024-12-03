/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;

public class NewlineEscapedStringFormatter
implements Converter<String, String> {
    private static final String CONTINUE_STRING = "\\\n\t";

    @Override
    public String convert(String input) throws IllegalArgumentException {
        if (input == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        int pos = 0;
        block9: for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            switch (c) {
                case '\r': {
                    continue block9;
                }
                case '\n': {
                    result.append("\\n").append(CONTINUE_STRING);
                    pos = 0;
                    continue block9;
                }
                case '\\': {
                    char next = '\u0000';
                    if (i < input.length() - 1) {
                        next = input.charAt(++i);
                    }
                    switch (next) {
                        case '\n': 
                        case '\\': 
                        case 'n': 
                        case 'r': 
                        case 't': 
                        case 'u': {
                            result.append('\\');
                            result.append(next);
                            break;
                        }
                        default: {
                            result.append('\\');
                            result.append('\\');
                            if (next <= '\u0000') break;
                            result.append(next);
                        }
                    }
                    ++pos;
                    continue block9;
                }
                case '\t': 
                case ' ': {
                    result.append(' ');
                    if (pos > 70) {
                        result.append(CONTINUE_STRING);
                        pos = 0;
                        continue block9;
                    }
                    ++pos;
                    continue block9;
                }
                default: {
                    ++pos;
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

