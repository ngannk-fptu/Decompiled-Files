/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import net.sf.ehcache.search.parser.CustomParseException;
import net.sf.ehcache.search.parser.ParseException;
import net.sf.ehcache.search.parser.Token;

public class ParserSupport {
    private static String[] formats = new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSS z", "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss z", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd z", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss.SSS z", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss z", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd z", "yyyy-MM-dd", "MM/dd/yyyy HH:mm:ss.SSS z", "MM/dd/yyyy HH:mm:ss.SSS", "MM/dd/yyyy HH:mm:ss z", "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy z", "MM/dd/yyyy"};

    public static String processQuotedString(Token tok, String s) throws CustomParseException {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 1; i < s.length() - 1; ++i) {
                char c = s.charAt(i);
                if (c == '\\') {
                    c = s.charAt(++i);
                    switch (c) {
                        case 'r': {
                            sb.append('\r');
                            break;
                        }
                        case 'n': {
                            sb.append('\n');
                            break;
                        }
                        case 'u': {
                            Object tmp = "";
                            for (int j = 0; j < 4; ++j) {
                                tmp = (String)tmp + s.charAt(++i);
                            }
                            sb.append((char)Integer.parseInt((String)tmp));
                            break;
                        }
                        case 't': {
                            sb.append('\t');
                            break;
                        }
                        default: {
                            sb.append(c);
                            break;
                        }
                    }
                    continue;
                }
                sb.append(c);
            }
            return sb.toString();
        }
        catch (Throwable t) {
            throw CustomParseException.factory(tok, CustomParseException.Message.SINGLE_QUOTE);
        }
    }

    public static <T extends Enum<T>> Enum<T> makeEnumFromString(ClassLoader loader, String enumName, String valueName) {
        Class<?> realType;
        try {
            realType = loader.loadClass(enumName);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Unable to load class specified as name of enum %s: %s", enumName, e.getMessage()));
        }
        Object obj = Enum.valueOf(realType, valueName);
        return obj;
    }

    public static Date variantDateParse(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setLenient(false);
        for (String attempt : formats) {
            try {
                sdf.applyPattern(attempt);
                return sdf.parse(s);
            }
            catch (java.text.ParseException parseException) {
            }
        }
        throw new ParseException("Date parsing error. Acceptable formats include: " + Arrays.asList(formats));
    }
}

