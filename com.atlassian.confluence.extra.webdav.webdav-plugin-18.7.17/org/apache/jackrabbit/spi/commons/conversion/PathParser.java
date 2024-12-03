/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.conversion.IdentifierResolver;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.conversion.NameParser;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.name.PathBuilder;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;

public class PathParser {
    private static final int STATE_PREFIX_START = 0;
    private static final int STATE_PREFIX = 1;
    private static final int STATE_NAME_START = 2;
    private static final int STATE_NAME = 3;
    private static final int STATE_INDEX = 4;
    private static final int STATE_INDEX_END = 5;
    private static final int STATE_DOT = 6;
    private static final int STATE_DOTDOT = 7;
    private static final int STATE_IDENTIFIER = 8;
    private static final int STATE_URI = 9;
    private static final int STATE_URI_END = 10;
    private static final char EOF = '\uffff';

    public static Path parse(String jcrPath, NameResolver resolver, PathFactory factory) throws MalformedPathException, IllegalNameException, NamespaceException {
        return PathParser.parse(null, jcrPath, resolver, factory);
    }

    public static Path parse(String jcrPath, NameResolver nameResolver, IdentifierResolver identifierResolver, PathFactory factory) throws MalformedPathException, IllegalNameException, NamespaceException {
        return PathParser.parse(null, jcrPath, nameResolver, identifierResolver, factory);
    }

    public static Path parse(String jcrPath, NameResolver nameResolver, IdentifierResolver identifierResolver, PathFactory factory, boolean normalizeIdentifier) throws MalformedPathException, IllegalNameException, NamespaceException {
        return PathParser.parse(null, jcrPath, nameResolver, identifierResolver, factory, normalizeIdentifier);
    }

    public static Path parse(Path parent, String jcrPath, NameResolver resolver, PathFactory factory) throws MalformedPathException, IllegalNameException, NamespaceException {
        return PathParser.parse(parent, jcrPath, resolver, null, factory);
    }

    public static Path parse(Path parent, String jcrPath, NameResolver nameResolver, IdentifierResolver identifierResolver, PathFactory factory) throws MalformedPathException, IllegalNameException, NamespaceException {
        return PathParser.parse(parent, jcrPath, nameResolver, identifierResolver, factory, true);
    }

    private static Path parse(Path parent, String jcrPath, NameResolver nameResolver, IdentifierResolver identifierResolver, PathFactory factory, boolean normalizeIdentifier) throws MalformedPathException, IllegalNameException, NamespaceException {
        boolean checkFormat;
        int state;
        int len;
        int n = len = jcrPath == null ? 0 : jcrPath.length();
        if (len == 1 && jcrPath.charAt(0) == '/') {
            return factory.getRootPath();
        }
        if (len == 0) {
            throw new MalformedPathException("empty path");
        }
        PathBuilder builder = new PathBuilder(factory);
        int pos = 0;
        if (jcrPath.charAt(0) == '/') {
            if (parent != null) {
                throw new MalformedPathException("'" + jcrPath + "' is not a relative path.");
            }
            builder.addRoot();
            ++pos;
        }
        if (parent != null) {
            builder.addAll(parent.getElements());
        }
        if (jcrPath.charAt(0) == '[') {
            if (parent != null) {
                throw new MalformedPathException("'" + jcrPath + "' is not a relative path.");
            }
            state = 8;
            ++pos;
        } else {
            state = 0;
        }
        int lastPos = pos;
        String name = null;
        int index = 0;
        boolean wasSlash = false;
        boolean bl = checkFormat = nameResolver == null;
        while (pos <= len) {
            char c;
            char rawCharacter = c = pos == len ? (char)'\uffff' : (char)jcrPath.charAt(pos);
            ++pos;
            if (c != ' ' && Character.isWhitespace(c)) {
                c = '\t';
            }
            switch (c) {
                case '/': 
                case '\uffff': {
                    if (state == 0 && c != '\uffff') {
                        throw new MalformedPathException("'" + jcrPath + "' is not a valid path. double slash '//' not allowed.");
                    }
                    if (state == 9 && c == '\uffff') {
                        state = 10;
                    }
                    if (state == 1 || state == 3 || state == 5 || state == 10) {
                        if (name == null) {
                            if (wasSlash) {
                                throw new MalformedPathException("'" + jcrPath + "' is not a valid path: Trailing slashes not allowed in prefixes and names.");
                            }
                            name = jcrPath.substring(lastPos, pos - 1);
                        }
                        if (checkFormat) {
                            NameParser.checkFormat(name);
                        } else {
                            Name qName = nameResolver.getQName(name);
                            builder.addLast(qName, index);
                        }
                        state = 0;
                        lastPos = pos;
                        name = null;
                        index = 0;
                        break;
                    }
                    if (state == 8) {
                        if (c != 65535) break;
                        if (jcrPath.charAt(pos - 2) != ']') {
                            throw new MalformedPathException("'" + jcrPath + "' is not a valid path: Unterminated identifier segment.");
                        }
                        String identifier = jcrPath.substring(lastPos, pos - 2);
                        if (checkFormat) {
                            if (identifierResolver != null) {
                                identifierResolver.checkFormat(identifier);
                            }
                        } else {
                            if (identifierResolver == null) {
                                throw new MalformedPathException("'" + jcrPath + "' is not a valid path: Identifier segments are not supported.");
                            }
                            if (normalizeIdentifier) {
                                builder.addAll(identifierResolver.getPath(identifier).getElements());
                            } else {
                                identifierResolver.checkFormat(identifier);
                                builder.addLast(factory.createElement(identifier));
                            }
                        }
                        state = 0;
                        lastPos = pos;
                        break;
                    }
                    if (state == 6) {
                        builder.addLast(factory.getCurrentElement());
                        lastPos = pos;
                        state = 0;
                        break;
                    }
                    if (state == 7) {
                        builder.addLast(factory.getParentElement());
                        lastPos = pos;
                        state = 0;
                        break;
                    }
                    if (state == 9 || state == 0 && c == 65535) break;
                    throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not a valid name character.");
                }
                case '.': {
                    if (state == 0) {
                        state = 6;
                        break;
                    }
                    if (state == 6) {
                        state = 7;
                        break;
                    }
                    if (state == 7) {
                        state = 1;
                        break;
                    }
                    if (state != 5) break;
                    throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not valid after index. '/' expected.");
                }
                case ':': {
                    if (state == 0) {
                        throw new MalformedPathException("'" + jcrPath + "' is not a valid path. Prefix must not be empty");
                    }
                    if (state == 1) {
                        if (wasSlash) {
                            throw new MalformedPathException("'" + jcrPath + "' is not a valid path: Trailing slashes not allowed in prefixes and names.");
                        }
                        state = 2;
                        break;
                    }
                    if (state == 8 || state == 9) break;
                    throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not valid name character");
                }
                case '[': {
                    if (state == 1 || state == 3) {
                        if (wasSlash) {
                            throw new MalformedPathException("'" + jcrPath + "' is not a valid path: Trailing slashes not allowed in prefixes and names.");
                        }
                        state = 4;
                        name = jcrPath.substring(lastPos, pos - 1);
                        lastPos = pos;
                        break;
                    }
                    if (state == 8) break;
                    throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not a valid name character.");
                }
                case ']': {
                    if (state == 4) {
                        try {
                            index = Integer.parseInt(jcrPath.substring(lastPos, pos - 1));
                        }
                        catch (NumberFormatException e) {
                            throw new MalformedPathException("'" + jcrPath + "' is not a valid path. NumberFormatException in index: " + jcrPath.substring(lastPos, pos - 1));
                        }
                        if (index < 1) {
                            throw new MalformedPathException("'" + jcrPath + "' is not a valid path. Index number invalid: " + index);
                        }
                        state = 5;
                        break;
                    }
                    if (state == 8) break;
                    throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not a valid name character.");
                }
                case ' ': {
                    if (state == 0 || state == 2) {
                        throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not valid name start");
                    }
                    if (state == 5) {
                        throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not valid after index. '/' expected.");
                    }
                    if (state != 6 && state != 7) break;
                    state = 1;
                    break;
                }
                case '\t': {
                    if (state != 8) {
                        String message = String.format("'%s' is not a valid path. Whitespace other than SP (U+0020) not a allowed in a name, but U+%04x was found at position %d.", jcrPath, (long)rawCharacter, pos - 1);
                        throw new MalformedPathException(message);
                    }
                }
                case '*': 
                case '|': {
                    if (state != 8) {
                        throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not a valid name character.");
                    }
                }
                case '{': {
                    if (state == 0 && lastPos == pos - 1) {
                        state = 9;
                        break;
                    }
                    if (state != 2 && state != 6 && state != 7) break;
                    state = 3;
                    break;
                }
                case '}': {
                    if (state != 9) break;
                    state = 10;
                    break;
                }
                default: {
                    if (state == 0 || state == 6 || state == 7) {
                        state = 1;
                        break;
                    }
                    if (state == 2) {
                        state = 3;
                        break;
                    }
                    if (state != 5) break;
                    throw new MalformedPathException("'" + jcrPath + "' is not a valid path. '" + c + "' not valid after index. '/' expected.");
                }
            }
            wasSlash = c == ' ';
        }
        if (checkFormat) {
            return null;
        }
        return builder.getPath();
    }

    public static void checkFormat(String jcrPath) throws MalformedPathException {
        try {
            PathParser.parse(jcrPath, null, null, PathFactoryImpl.getInstance());
        }
        catch (NamespaceException namespaceException) {
        }
        catch (IllegalNameException illegalNameException) {
            // empty catch block
        }
    }
}

