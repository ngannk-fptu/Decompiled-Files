/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;
import org.apache.jackrabbit.util.XMLChar;

public class NameParser {
    private static final int STATE_PREFIX_START = 0;
    private static final int STATE_PREFIX = 1;
    private static final int STATE_NAME_START = 2;
    private static final int STATE_NAME = 3;
    private static final int STATE_URI_START = 4;
    private static final int STATE_URI = 5;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Name parse(String jcrName, NamespaceResolver resolver, NameFactory factory) throws IllegalNameException, NamespaceException {
        int len;
        int n = len = jcrName == null ? 0 : jcrName.length();
        if (len == 0) {
            throw new IllegalNameException("empty name");
        }
        if (".".equals(jcrName) || "..".equals(jcrName)) {
            throw new IllegalNameException(jcrName);
        }
        String prefix = "";
        String uri = null;
        int nameStart = 0;
        int state = 0;
        boolean trailingSpaces = false;
        boolean checkFormat = resolver == null;
        for (int i = 0; i < len; ++i) {
            char c = jcrName.charAt(i);
            if (c == ':') {
                if (state == 0) {
                    throw new IllegalNameException("Prefix must not be empty");
                }
                if (state == 1) {
                    if (trailingSpaces) {
                        throw new IllegalNameException("Trailing spaces not allowed");
                    }
                    prefix = jcrName.substring(0, i);
                    if (!XMLChar.isValidNCName(prefix)) {
                        throw new IllegalNameException("Invalid name prefix: " + prefix);
                    }
                    state = 2;
                } else if (state != 5) {
                    throw new IllegalNameException(NameParser.asDisplayableString(c) + " not allowed in name");
                }
                trailingSpaces = false;
                continue;
            }
            if (c == ' ') {
                if (state == 0 || state == 2) {
                    throw new IllegalNameException(NameParser.asDisplayableString(c) + " not valid name start");
                }
                trailingSpaces = true;
                continue;
            }
            if (c == '[' || c == ']' || c == '*' || c == '|') {
                throw new IllegalNameException(NameParser.asDisplayableString(c) + " not allowed in name");
            }
            if (Character.isWhitespace(c) && c < '\u0080') {
                throw new IllegalNameException("Whitespace character " + NameParser.asDisplayableString(c) + " not allowed in name");
            }
            if (c == '/') {
                if (state == 4) {
                    state = 5;
                } else if (state != 5) {
                    throw new IllegalNameException(NameParser.asDisplayableString(c) + " not allowed in name");
                }
                trailingSpaces = false;
                continue;
            }
            if (c == '{') {
                if (state == 0) {
                    state = 4;
                } else if (state == 4 || state == 5) {
                    state = 3;
                    nameStart = 0;
                } else if (state == 2) {
                    state = 3;
                    nameStart = i;
                }
                trailingSpaces = false;
                continue;
            }
            if (c == '}') {
                if (state == 4 || state == 5) {
                    String tmp = jcrName.substring(1, i);
                    if (tmp.length() == 0 || tmp.indexOf(58) != -1) {
                        uri = tmp;
                        state = 2;
                    } else if (tmp.equals("internal")) {
                        uri = tmp;
                        state = 2;
                    } else {
                        if (tmp.indexOf(47) != -1) throw new IllegalNameException("The URI prefix of the name " + jcrName + " is neither a valid URI nor a valid part of a local name.");
                        state = 3;
                        nameStart = 0;
                    }
                } else if (state == 0) {
                    state = 1;
                } else if (state == 2) {
                    state = 3;
                    nameStart = i;
                }
                trailingSpaces = false;
                continue;
            }
            if (state == 0) {
                state = 1;
            } else if (state == 2) {
                state = 3;
                nameStart = i;
            } else if (state == 4) {
                state = 5;
            }
            trailingSpaces = false;
        }
        if (state == 5 && (jcrName.indexOf(58) > -1 || jcrName.indexOf(47) > -1)) {
            throw new IllegalNameException("Local name may not contain ':' nor '/'");
        }
        if (nameStart == len || state == 2) {
            throw new IllegalNameException("Local name must not be empty");
        }
        if (trailingSpaces) {
            throw new IllegalNameException("Trailing spaces not allowed");
        }
        if (checkFormat) {
            return null;
        }
        if (uri == null) {
            uri = resolver.getURI(prefix);
        }
        String localName = nameStart == 0 ? jcrName : jcrName.substring(nameStart, len);
        return factory.create(uri, localName);
    }

    private static String asDisplayableString(char c) {
        if (c >= ' ' && c < '\u007f') {
            return Character.toString(c);
        }
        if (c == '\b') {
            return "\\b";
        }
        if (c == '\f') {
            return "\\f";
        }
        if (c == '\n') {
            return "\\n";
        }
        if (c == '\r') {
            return "\\r";
        }
        if (c == '\t') {
            return "\\t";
        }
        return String.format("\\u%04x", c);
    }

    public static Name[] parse(String[] jcrNames, NamespaceResolver resolver, NameFactory factory) throws NameException, NamespaceException {
        Name[] ret = new Name[jcrNames.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = NameParser.parse(jcrNames[i], resolver, factory);
        }
        return ret;
    }

    public static void checkFormat(String jcrName) throws IllegalNameException {
        try {
            NameParser.parse(jcrName, null, null);
        }
        catch (NamespaceException namespaceException) {
            // empty catch block
        }
    }
}

