/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.ConfigIncludeKind;
import com.typesafe.config.impl.ConfigNodeArray;
import com.typesafe.config.impl.ConfigNodeComment;
import com.typesafe.config.impl.ConfigNodeComplexValue;
import com.typesafe.config.impl.ConfigNodeConcatenation;
import com.typesafe.config.impl.ConfigNodeField;
import com.typesafe.config.impl.ConfigNodeInclude;
import com.typesafe.config.impl.ConfigNodeObject;
import com.typesafe.config.impl.ConfigNodePath;
import com.typesafe.config.impl.ConfigNodeRoot;
import com.typesafe.config.impl.ConfigNodeSimpleValue;
import com.typesafe.config.impl.ConfigNodeSingleToken;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.PathParser;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

final class ConfigDocumentParser {
    ConfigDocumentParser() {
    }

    static ConfigNodeRoot parse(Iterator<Token> tokens, ConfigOrigin origin, ConfigParseOptions options) {
        ConfigSyntax syntax = options.getSyntax() == null ? ConfigSyntax.CONF : options.getSyntax();
        ParseContext context = new ParseContext(syntax, origin, tokens);
        return context.parse();
    }

    static AbstractConfigNodeValue parseValue(Iterator<Token> tokens, ConfigOrigin origin, ConfigParseOptions options) {
        ConfigSyntax syntax = options.getSyntax() == null ? ConfigSyntax.CONF : options.getSyntax();
        ParseContext context = new ParseContext(syntax, origin, tokens);
        return context.parseSingleValue();
    }

    private static final class ParseContext {
        private int lineNumber = 1;
        private final Stack<Token> buffer = new Stack();
        private final Iterator<Token> tokens;
        private final ConfigSyntax flavor;
        private final ConfigOrigin baseOrigin;
        int equalsCount;
        private final String ExpectingClosingParenthesisError = "expecting a close parentheses ')' here, not: ";

        ParseContext(ConfigSyntax flavor, ConfigOrigin origin, Iterator<Token> tokens) {
            this.tokens = tokens;
            this.flavor = flavor;
            this.equalsCount = 0;
            this.baseOrigin = origin;
        }

        private Token popToken() {
            if (this.buffer.isEmpty()) {
                return this.tokens.next();
            }
            return this.buffer.pop();
        }

        private Token nextToken() {
            Token t = this.popToken();
            if (this.flavor == ConfigSyntax.JSON) {
                if (Tokens.isUnquotedText(t) && !ParseContext.isUnquotedWhitespace(t)) {
                    throw this.parseError("Token not allowed in valid JSON: '" + Tokens.getUnquotedText(t) + "'");
                }
                if (Tokens.isSubstitution(t)) {
                    throw this.parseError("Substitutions (${} syntax) not allowed in JSON");
                }
            }
            return t;
        }

        private Token nextTokenCollectingWhitespace(Collection<AbstractConfigNode> nodes) {
            Token t;
            while (true) {
                if (Tokens.isIgnoredWhitespace(t = this.nextToken()) || Tokens.isNewline(t) || ParseContext.isUnquotedWhitespace(t)) {
                    nodes.add(new ConfigNodeSingleToken(t));
                    if (!Tokens.isNewline(t)) continue;
                    this.lineNumber = t.lineNumber() + 1;
                    continue;
                }
                if (!Tokens.isComment(t)) break;
                nodes.add(new ConfigNodeComment(t));
            }
            int newNumber = t.lineNumber();
            if (newNumber >= 0) {
                this.lineNumber = newNumber;
            }
            return t;
        }

        private void putBack(Token token) {
            this.buffer.push(token);
        }

        private boolean checkElementSeparator(Collection<AbstractConfigNode> nodes) {
            if (this.flavor == ConfigSyntax.JSON) {
                Token t = this.nextTokenCollectingWhitespace(nodes);
                if (t == Tokens.COMMA) {
                    nodes.add(new ConfigNodeSingleToken(t));
                    return true;
                }
                this.putBack(t);
                return false;
            }
            boolean sawSeparatorOrNewline = false;
            Token t = this.nextToken();
            while (true) {
                if (Tokens.isIgnoredWhitespace(t) || ParseContext.isUnquotedWhitespace(t)) {
                    nodes.add(new ConfigNodeSingleToken(t));
                } else if (Tokens.isComment(t)) {
                    nodes.add(new ConfigNodeComment(t));
                } else if (Tokens.isNewline(t)) {
                    sawSeparatorOrNewline = true;
                    ++this.lineNumber;
                    nodes.add(new ConfigNodeSingleToken(t));
                } else {
                    if (t == Tokens.COMMA) {
                        nodes.add(new ConfigNodeSingleToken(t));
                        return true;
                    }
                    this.putBack(t);
                    return sawSeparatorOrNewline;
                }
                t = this.nextToken();
            }
        }

        private AbstractConfigNodeValue consolidateValues(Collection<AbstractConfigNode> nodes) {
            if (this.flavor == ConfigSyntax.JSON) {
                return null;
            }
            ArrayList<AbstractConfigNode> values = new ArrayList<AbstractConfigNode>();
            int valueCount = 0;
            Token t = this.nextTokenCollectingWhitespace(nodes);
            while (true) {
                AbstractConfigNodeValue v = null;
                if (Tokens.isIgnoredWhitespace(t)) {
                    values.add(new ConfigNodeSingleToken(t));
                    t = this.nextToken();
                    continue;
                }
                if (!Tokens.isValue(t) && !Tokens.isUnquotedText(t) && !Tokens.isSubstitution(t) && t != Tokens.OPEN_CURLY && t != Tokens.OPEN_SQUARE) break;
                v = this.parseValue(t);
                ++valueCount;
                if (v == null) {
                    throw new ConfigException.BugOrBroken("no value");
                }
                values.add(v);
                t = this.nextToken();
            }
            this.putBack(t);
            if (valueCount < 2) {
                AbstractConfigNodeValue value = null;
                for (AbstractConfigNode node : values) {
                    if (node instanceof AbstractConfigNodeValue) {
                        value = (AbstractConfigNodeValue)node;
                        continue;
                    }
                    if (value == null) {
                        nodes.add(node);
                        continue;
                    }
                    this.putBack(new ArrayList<Token>(node.tokens()).get(0));
                }
                return value;
            }
            for (int i = values.size() - 1; i >= 0 && values.get(i) instanceof ConfigNodeSingleToken; --i) {
                this.putBack(((ConfigNodeSingleToken)values.get(i)).token());
                values.remove(i);
            }
            return new ConfigNodeConcatenation(values);
        }

        private ConfigException parseError(String message) {
            return this.parseError(message, null);
        }

        private ConfigException parseError(String message, Throwable cause) {
            return new ConfigException.Parse(this.baseOrigin.withLineNumber(this.lineNumber), message, cause);
        }

        private String addQuoteSuggestion(String badToken, String message) {
            return this.addQuoteSuggestion(null, this.equalsCount > 0, badToken, message);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private String addQuoteSuggestion(Path lastPath, boolean insideEquals, String badToken, String message) {
            String part;
            String previousFieldName;
            String string = previousFieldName = lastPath != null ? lastPath.render() : null;
            if (badToken.equals(Tokens.END.toString())) {
                if (previousFieldName == null) return message;
                part = message + " (if you intended '" + previousFieldName + "' to be part of a value, instead of a key, try adding double quotes around the whole value";
            } else {
                part = previousFieldName != null ? message + " (if you intended " + badToken + " to be part of the value for '" + previousFieldName + "', try enclosing the value in double quotes" : message + " (if you intended " + badToken + " to be part of a key or string value, try enclosing the key or value in double quotes";
            }
            if (!insideEquals) return part + ")";
            return part + ", or you may be able to rename the file .properties rather than .conf)";
        }

        private AbstractConfigNodeValue parseValue(Token t) {
            AbstractConfigNodeValue v = null;
            int startingEqualsCount = this.equalsCount;
            if (Tokens.isValue(t) || Tokens.isUnquotedText(t) || Tokens.isSubstitution(t)) {
                v = new ConfigNodeSimpleValue(t);
            } else if (t == Tokens.OPEN_CURLY) {
                v = this.parseObject(true);
            } else if (t == Tokens.OPEN_SQUARE) {
                v = this.parseArray();
            } else {
                throw this.parseError(this.addQuoteSuggestion(t.toString(), "Expecting a value but got wrong token: " + t));
            }
            if (this.equalsCount != startingEqualsCount) {
                throw new ConfigException.BugOrBroken("Bug in config parser: unbalanced equals count");
            }
            return v;
        }

        private ConfigNodePath parseKey(Token token) {
            if (this.flavor == ConfigSyntax.JSON) {
                if (Tokens.isValueWithType(token, ConfigValueType.STRING)) {
                    return PathParser.parsePathNodeExpression(Collections.singletonList(token).iterator(), this.baseOrigin.withLineNumber(this.lineNumber));
                }
                throw this.parseError("Expecting close brace } or a field name here, got " + token);
            }
            ArrayList<Token> expression = new ArrayList<Token>();
            Token t = token;
            while (Tokens.isValue(t) || Tokens.isUnquotedText(t)) {
                expression.add(t);
                t = this.nextToken();
            }
            if (expression.isEmpty()) {
                throw this.parseError("expecting a close parentheses ')' here, not: " + t);
            }
            this.putBack(t);
            return PathParser.parsePathNodeExpression(expression.iterator(), this.baseOrigin.withLineNumber(this.lineNumber));
        }

        private static boolean isIncludeKeyword(Token t) {
            return Tokens.isUnquotedText(t) && Tokens.getUnquotedText(t).equals("include");
        }

        private static boolean isUnquotedWhitespace(Token t) {
            if (!Tokens.isUnquotedText(t)) {
                return false;
            }
            String s = Tokens.getUnquotedText(t);
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (ConfigImplUtil.isWhitespace(c)) continue;
                return false;
            }
            return true;
        }

        private boolean isKeyValueSeparatorToken(Token t) {
            if (this.flavor == ConfigSyntax.JSON) {
                return t == Tokens.COLON;
            }
            return t == Tokens.COLON || t == Tokens.EQUALS || t == Tokens.PLUS_EQUALS;
        }

        private ConfigNodeInclude parseInclude(ArrayList<AbstractConfigNode> children) {
            Token t = this.nextTokenCollectingWhitespace(children);
            if (Tokens.isUnquotedText(t)) {
                String kindText = Tokens.getUnquotedText(t);
                if (kindText.startsWith("required(")) {
                    String r = kindText.replaceFirst("required\\(", "");
                    if (r.length() > 0) {
                        this.putBack(Tokens.newUnquotedText(t.origin(), r));
                    }
                    children.add(new ConfigNodeSingleToken(t));
                    ConfigNodeInclude res = this.parseIncludeResource(children, true);
                    t = this.nextTokenCollectingWhitespace(children);
                    if (!Tokens.isUnquotedText(t) || !Tokens.getUnquotedText(t).equals(")")) {
                        throw this.parseError("expecting a close parentheses ')' here, not: " + t);
                    }
                    return res;
                }
                this.putBack(t);
                return this.parseIncludeResource(children, false);
            }
            this.putBack(t);
            return this.parseIncludeResource(children, false);
        }

        private ConfigNodeInclude parseIncludeResource(ArrayList<AbstractConfigNode> children, boolean isRequired) {
            Token t = this.nextTokenCollectingWhitespace(children);
            if (Tokens.isUnquotedText(t)) {
                String prefix;
                ConfigIncludeKind kind;
                String kindText = Tokens.getUnquotedText(t);
                if (kindText.startsWith("url(")) {
                    kind = ConfigIncludeKind.URL;
                    prefix = "url(";
                } else if (kindText.startsWith("file(")) {
                    kind = ConfigIncludeKind.FILE;
                    prefix = "file(";
                } else if (kindText.startsWith("classpath(")) {
                    kind = ConfigIncludeKind.CLASSPATH;
                    prefix = "classpath(";
                } else {
                    throw this.parseError("expecting include parameter to be quoted filename, file(), classpath(), or url(). No spaces are allowed before the open paren. Not expecting: " + t);
                }
                String r = kindText.replaceFirst("[^(]*\\(", "");
                if (r.length() > 0) {
                    this.putBack(Tokens.newUnquotedText(t.origin(), r));
                }
                children.add(new ConfigNodeSingleToken(t));
                t = this.nextTokenCollectingWhitespace(children);
                if (!Tokens.isValueWithType(t, ConfigValueType.STRING)) {
                    throw this.parseError("expecting include " + prefix + ") parameter to be a quoted string, rather than: " + t);
                }
                children.add(new ConfigNodeSimpleValue(t));
                t = this.nextTokenCollectingWhitespace(children);
                if (Tokens.isUnquotedText(t) && Tokens.getUnquotedText(t).startsWith(")")) {
                    String rest = Tokens.getUnquotedText(t).substring(1);
                    if (rest.length() > 0) {
                        this.putBack(Tokens.newUnquotedText(t.origin(), rest));
                    }
                } else {
                    throw this.parseError("expecting a close parentheses ')' here, not: " + t);
                }
                return new ConfigNodeInclude(children, kind, isRequired);
            }
            if (Tokens.isValueWithType(t, ConfigValueType.STRING)) {
                children.add(new ConfigNodeSimpleValue(t));
                return new ConfigNodeInclude(children, ConfigIncludeKind.HEURISTIC, isRequired);
            }
            throw this.parseError("include keyword is not followed by a quoted string, but by: " + t);
        }

        private ConfigNodeComplexValue parseObject(boolean hadOpenCurly) {
            ArrayList<AbstractConfigNode> objectNodes;
            block24: {
                Token t;
                boolean afterComma = false;
                Path lastPath = null;
                boolean lastInsideEquals = false;
                objectNodes = new ArrayList<AbstractConfigNode>();
                HashMap<String, Boolean> keys = new HashMap<String, Boolean>();
                if (hadOpenCurly) {
                    objectNodes.add(new ConfigNodeSingleToken(Tokens.OPEN_CURLY));
                }
                while (true) {
                    if ((t = this.nextTokenCollectingWhitespace(objectNodes)) == Tokens.CLOSE_CURLY) {
                        if (this.flavor == ConfigSyntax.JSON && afterComma) {
                            throw this.parseError(this.addQuoteSuggestion(t.toString(), "expecting a field name after a comma, got a close brace } instead"));
                        }
                        if (!hadOpenCurly) {
                            throw this.parseError(this.addQuoteSuggestion(t.toString(), "unbalanced close brace '}' with no open brace"));
                        }
                        objectNodes.add(new ConfigNodeSingleToken(Tokens.CLOSE_CURLY));
                        break block24;
                    }
                    if (t == Tokens.END && !hadOpenCurly) {
                        this.putBack(t);
                        break block24;
                    }
                    if (this.flavor != ConfigSyntax.JSON && ParseContext.isIncludeKeyword(t)) {
                        ArrayList<AbstractConfigNode> includeNodes = new ArrayList<AbstractConfigNode>();
                        includeNodes.add(new ConfigNodeSingleToken(t));
                        objectNodes.add(this.parseInclude(includeNodes));
                        afterComma = false;
                    } else {
                        AbstractConfigNodeValue nextValue;
                        ArrayList<AbstractConfigNode> keyValueNodes = new ArrayList<AbstractConfigNode>();
                        Token keyToken = t;
                        ConfigNodePath path = this.parseKey(keyToken);
                        keyValueNodes.add(path);
                        Token afterKey = this.nextTokenCollectingWhitespace(keyValueNodes);
                        boolean insideEquals = false;
                        if (this.flavor == ConfigSyntax.CONF && afterKey == Tokens.OPEN_CURLY) {
                            nextValue = this.parseValue(afterKey);
                        } else {
                            if (!this.isKeyValueSeparatorToken(afterKey)) {
                                throw this.parseError(this.addQuoteSuggestion(afterKey.toString(), "Key '" + path.render() + "' may not be followed by token: " + afterKey));
                            }
                            keyValueNodes.add(new ConfigNodeSingleToken(afterKey));
                            if (afterKey == Tokens.EQUALS) {
                                insideEquals = true;
                                ++this.equalsCount;
                            }
                            if ((nextValue = this.consolidateValues(keyValueNodes)) == null) {
                                nextValue = this.parseValue(this.nextTokenCollectingWhitespace(keyValueNodes));
                            }
                        }
                        keyValueNodes.add(nextValue);
                        if (insideEquals) {
                            --this.equalsCount;
                        }
                        lastInsideEquals = insideEquals;
                        String key = path.value().first();
                        Path remaining = path.value().remainder();
                        if (remaining == null) {
                            Boolean existing = (Boolean)keys.get(key);
                            if (existing != null && this.flavor == ConfigSyntax.JSON) {
                                throw this.parseError("JSON does not allow duplicate fields: '" + key + "' was already seen");
                            }
                            keys.put(key, true);
                        } else {
                            if (this.flavor == ConfigSyntax.JSON) {
                                throw new ConfigException.BugOrBroken("somehow got multi-element path in JSON mode");
                            }
                            keys.put(key, true);
                        }
                        afterComma = false;
                        objectNodes.add(new ConfigNodeField(keyValueNodes));
                    }
                    if (!this.checkElementSeparator(objectNodes)) break;
                    afterComma = true;
                }
                t = this.nextTokenCollectingWhitespace(objectNodes);
                if (t == Tokens.CLOSE_CURLY) {
                    if (!hadOpenCurly) {
                        throw this.parseError(this.addQuoteSuggestion(lastPath, lastInsideEquals, t.toString(), "unbalanced close brace '}' with no open brace"));
                    }
                    objectNodes.add(new ConfigNodeSingleToken(t));
                } else {
                    if (hadOpenCurly) {
                        throw this.parseError(this.addQuoteSuggestion(lastPath, lastInsideEquals, t.toString(), "Expecting close brace } or a comma, got " + t));
                    }
                    if (t == Tokens.END) {
                        this.putBack(t);
                    } else {
                        throw this.parseError(this.addQuoteSuggestion(lastPath, lastInsideEquals, t.toString(), "Expecting end of input or a comma, got " + t));
                    }
                }
            }
            return new ConfigNodeObject(objectNodes);
        }

        private ConfigNodeComplexValue parseArray() {
            Token t;
            ArrayList<AbstractConfigNode> children = new ArrayList<AbstractConfigNode>();
            children.add(new ConfigNodeSingleToken(Tokens.OPEN_SQUARE));
            AbstractConfigNodeValue nextValue = this.consolidateValues(children);
            if (nextValue != null) {
                children.add(nextValue);
            } else {
                t = this.nextTokenCollectingWhitespace(children);
                if (t == Tokens.CLOSE_SQUARE) {
                    children.add(new ConfigNodeSingleToken(t));
                    return new ConfigNodeArray(children);
                }
                if (Tokens.isValue(t) || t == Tokens.OPEN_CURLY || t == Tokens.OPEN_SQUARE || Tokens.isUnquotedText(t) || Tokens.isSubstitution(t)) {
                    nextValue = this.parseValue(t);
                    children.add(nextValue);
                } else {
                    throw this.parseError("List should have ] or a first element after the open [, instead had token: " + t + " (if you want " + t + " to be part of a string value, then double-quote it)");
                }
            }
            while (true) {
                if (!this.checkElementSeparator(children)) {
                    t = this.nextTokenCollectingWhitespace(children);
                    if (t == Tokens.CLOSE_SQUARE) {
                        children.add(new ConfigNodeSingleToken(t));
                        return new ConfigNodeArray(children);
                    }
                    throw this.parseError("List should have ended with ] or had a comma, instead had token: " + t + " (if you want " + t + " to be part of a string value, then double-quote it)");
                }
                nextValue = this.consolidateValues(children);
                if (nextValue != null) {
                    children.add(nextValue);
                    continue;
                }
                t = this.nextTokenCollectingWhitespace(children);
                if (Tokens.isValue(t) || t == Tokens.OPEN_CURLY || t == Tokens.OPEN_SQUARE || Tokens.isUnquotedText(t) || Tokens.isSubstitution(t)) {
                    nextValue = this.parseValue(t);
                    children.add(nextValue);
                    continue;
                }
                if (this.flavor == ConfigSyntax.JSON || t != Tokens.CLOSE_SQUARE) break;
                this.putBack(t);
            }
            throw this.parseError("List should have had new element after a comma, instead had token: " + t + " (if you want the comma or " + t + " to be part of a string value, then double-quote it)");
        }

        ConfigNodeRoot parse() {
            ArrayList<AbstractConfigNode> children = new ArrayList<AbstractConfigNode>();
            Token t = this.nextToken();
            if (t != Tokens.START) {
                throw new ConfigException.BugOrBroken("token stream did not begin with START, had " + t);
            }
            t = this.nextTokenCollectingWhitespace(children);
            AbstractConfigNodeValue result = null;
            boolean missingCurly = false;
            if (t == Tokens.OPEN_CURLY || t == Tokens.OPEN_SQUARE) {
                result = this.parseValue(t);
            } else {
                if (this.flavor == ConfigSyntax.JSON) {
                    if (t == Tokens.END) {
                        throw this.parseError("Empty document");
                    }
                    throw this.parseError("Document must have an object or array at root, unexpected token: " + t);
                }
                this.putBack(t);
                missingCurly = true;
                result = this.parseObject(false);
            }
            if (result instanceof ConfigNodeObject && missingCurly) {
                children.addAll(((ConfigNodeComplexValue)result).children());
            } else {
                children.add(result);
            }
            t = this.nextTokenCollectingWhitespace(children);
            if (t == Tokens.END) {
                if (missingCurly) {
                    return new ConfigNodeRoot(Collections.singletonList(new ConfigNodeObject(children)), this.baseOrigin);
                }
                return new ConfigNodeRoot(children, this.baseOrigin);
            }
            throw this.parseError("Document has trailing tokens after first object or array: " + t);
        }

        AbstractConfigNodeValue parseSingleValue() {
            Token t = this.nextToken();
            if (t != Tokens.START) {
                throw new ConfigException.BugOrBroken("token stream did not begin with START, had " + t);
            }
            t = this.nextToken();
            if (Tokens.isIgnoredWhitespace(t) || Tokens.isNewline(t) || ParseContext.isUnquotedWhitespace(t) || Tokens.isComment(t)) {
                throw this.parseError("The value from withValueText cannot have leading or trailing newlines, whitespace, or comments");
            }
            if (t == Tokens.END) {
                throw this.parseError("Empty value");
            }
            if (this.flavor == ConfigSyntax.JSON) {
                AbstractConfigNodeValue node = this.parseValue(t);
                t = this.nextToken();
                if (t == Tokens.END) {
                    return node;
                }
                throw this.parseError("Parsing JSON and the value set in withValueText was either a concatenation or had trailing whitespace, newlines, or comments");
            }
            this.putBack(t);
            ArrayList<AbstractConfigNode> nodes = new ArrayList<AbstractConfigNode>();
            AbstractConfigNodeValue node = this.consolidateValues(nodes);
            t = this.nextToken();
            if (t == Tokens.END) {
                return node;
            }
            throw this.parseError("The value from withValueText cannot have leading or trailing newlines, whitespace, or comments");
        }
    }
}

