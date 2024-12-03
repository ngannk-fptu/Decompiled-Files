/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigConcatenation;
import com.typesafe.config.impl.ConfigNodeArray;
import com.typesafe.config.impl.ConfigNodeComment;
import com.typesafe.config.impl.ConfigNodeComplexValue;
import com.typesafe.config.impl.ConfigNodeConcatenation;
import com.typesafe.config.impl.ConfigNodeField;
import com.typesafe.config.impl.ConfigNodeInclude;
import com.typesafe.config.impl.ConfigNodeObject;
import com.typesafe.config.impl.ConfigNodeRoot;
import com.typesafe.config.impl.ConfigNodeSimpleValue;
import com.typesafe.config.impl.ConfigNodeSingleToken;
import com.typesafe.config.impl.ConfigReference;
import com.typesafe.config.impl.FullIncluder;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SimpleConfigList;
import com.typesafe.config.impl.SimpleConfigObject;
import com.typesafe.config.impl.SimpleConfigOrigin;
import com.typesafe.config.impl.SimpleIncluder;
import com.typesafe.config.impl.SubstitutionExpression;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

final class ConfigParser {
    ConfigParser() {
    }

    static AbstractConfigValue parse(ConfigNodeRoot document, ConfigOrigin origin, ConfigParseOptions options, ConfigIncludeContext includeContext) {
        ParseContext context = new ParseContext(options.getSyntax(), origin, document, SimpleIncluder.makeFull(options.getIncluder()), includeContext);
        return context.parse();
    }

    private static final class ParseContext {
        private int lineNumber = 1;
        private final ConfigNodeRoot document;
        private final FullIncluder includer;
        private final ConfigIncludeContext includeContext;
        private final ConfigSyntax flavor;
        private final ConfigOrigin baseOrigin;
        private final LinkedList<Path> pathStack;
        int arrayCount;

        ParseContext(ConfigSyntax flavor, ConfigOrigin origin, ConfigNodeRoot document, FullIncluder includer, ConfigIncludeContext includeContext) {
            this.document = document;
            this.flavor = flavor;
            this.baseOrigin = origin;
            this.includer = includer;
            this.includeContext = includeContext;
            this.pathStack = new LinkedList();
            this.arrayCount = 0;
        }

        private AbstractConfigValue parseConcatenation(ConfigNodeConcatenation n) {
            if (this.flavor == ConfigSyntax.JSON) {
                throw new ConfigException.BugOrBroken("Found a concatenation node in JSON");
            }
            ArrayList<AbstractConfigValue> values = new ArrayList<AbstractConfigValue>(n.children().size());
            for (AbstractConfigNode node : n.children()) {
                AbstractConfigValue v = null;
                if (!(node instanceof AbstractConfigNodeValue)) continue;
                v = this.parseValue((AbstractConfigNodeValue)node, null);
                values.add(v);
            }
            return ConfigConcatenation.concatenate(values);
        }

        private SimpleConfigOrigin lineOrigin() {
            return ((SimpleConfigOrigin)this.baseOrigin).withLineNumber(this.lineNumber);
        }

        private ConfigException parseError(String message) {
            return this.parseError(message, null);
        }

        private ConfigException parseError(String message, Throwable cause) {
            return new ConfigException.Parse(this.lineOrigin(), message, cause);
        }

        private Path fullCurrentPath() {
            if (this.pathStack.isEmpty()) {
                throw new ConfigException.BugOrBroken("Bug in parser; tried to get current path when at root");
            }
            return new Path(this.pathStack.descendingIterator());
        }

        private AbstractConfigValue parseValue(AbstractConfigNodeValue n, List<String> comments) {
            AbstractConfigValue v;
            int startingArrayCount = this.arrayCount;
            if (n instanceof ConfigNodeSimpleValue) {
                v = ((ConfigNodeSimpleValue)n).value();
            } else if (n instanceof ConfigNodeObject) {
                v = this.parseObject((ConfigNodeObject)n);
            } else if (n instanceof ConfigNodeArray) {
                v = this.parseArray((ConfigNodeArray)n);
            } else if (n instanceof ConfigNodeConcatenation) {
                v = this.parseConcatenation((ConfigNodeConcatenation)n);
            } else {
                throw this.parseError("Expecting a value but got wrong node type: " + n.getClass());
            }
            if (comments != null && !comments.isEmpty()) {
                v = v.withOrigin(v.origin().prependComments(new ArrayList<String>(comments)));
                comments.clear();
            }
            if (this.arrayCount != startingArrayCount) {
                throw new ConfigException.BugOrBroken("Bug in config parser: unbalanced array count");
            }
            return v;
        }

        private static AbstractConfigObject createValueUnderPath(Path path, AbstractConfigValue value) {
            ArrayList<String> keys = new ArrayList<String>();
            String key = path.first();
            Path remaining = path.remainder();
            while (key != null) {
                keys.add(key);
                if (remaining == null) break;
                key = remaining.first();
                remaining = remaining.remainder();
            }
            ListIterator i = keys.listIterator(keys.size());
            String deepest = (String)i.previous();
            SimpleConfigObject o = new SimpleConfigObject(value.origin().withComments((List)null), Collections.singletonMap(deepest, value));
            while (i.hasPrevious()) {
                Map<String, AbstractConfigValue> m = Collections.singletonMap(i.previous(), o);
                o = new SimpleConfigObject(value.origin().withComments((List)null), m);
            }
            return o;
        }

        private void parseInclude(Map<String, AbstractConfigValue> values, ConfigNodeInclude n) {
            AbstractConfigObject obj;
            boolean isRequired = n.isRequired();
            ConfigIncludeContext cic = this.includeContext.setParseOptions(this.includeContext.parseOptions().setAllowMissing(!isRequired));
            switch (n.kind()) {
                case URL: {
                    URL url;
                    try {
                        url = new URL(n.name());
                    }
                    catch (MalformedURLException e) {
                        throw this.parseError("include url() specifies an invalid URL: " + n.name(), e);
                    }
                    obj = (AbstractConfigObject)this.includer.includeURL(cic, url);
                    break;
                }
                case FILE: {
                    obj = (AbstractConfigObject)this.includer.includeFile(cic, new File(n.name()));
                    break;
                }
                case CLASSPATH: {
                    obj = (AbstractConfigObject)this.includer.includeResources(cic, n.name());
                    break;
                }
                case HEURISTIC: {
                    obj = (AbstractConfigObject)this.includer.include(cic, n.name());
                    break;
                }
                default: {
                    throw new ConfigException.BugOrBroken("should not be reached");
                }
            }
            if (this.arrayCount > 0 && obj.resolveStatus() != ResolveStatus.RESOLVED) {
                throw this.parseError("Due to current limitations of the config parser, when an include statement is nested inside a list value, ${} substitutions inside the included file cannot be resolved correctly. Either move the include outside of the list value or remove the ${} statements from the included file.");
            }
            if (!this.pathStack.isEmpty()) {
                Path prefix = this.fullCurrentPath();
                obj = obj.relativized(prefix);
            }
            for (String key : obj.keySet()) {
                AbstractConfigValue v = obj.get(key);
                AbstractConfigValue existing = values.get(key);
                if (existing != null) {
                    values.put(key, v.withFallback(existing));
                    continue;
                }
                values.put(key, v);
            }
        }

        private AbstractConfigObject parseObject(ConfigNodeObject n) {
            HashMap<String, AbstractConfigValue> values = new HashMap<String, AbstractConfigValue>();
            SimpleConfigOrigin objectOrigin = this.lineOrigin();
            boolean lastWasNewline = false;
            ArrayList<AbstractConfigNode> nodes = new ArrayList<AbstractConfigNode>(n.children());
            ArrayList<String> comments = new ArrayList<String>();
            for (int i = 0; i < nodes.size(); ++i) {
                AbstractConfigNode node = nodes.get(i);
                if (node instanceof ConfigNodeComment) {
                    lastWasNewline = false;
                    comments.add(((ConfigNodeComment)node).commentText());
                    continue;
                }
                if (node instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)node).token())) {
                    ++this.lineNumber;
                    if (lastWasNewline) {
                        comments.clear();
                    }
                    lastWasNewline = true;
                    continue;
                }
                if (this.flavor != ConfigSyntax.JSON && node instanceof ConfigNodeInclude) {
                    this.parseInclude(values, (ConfigNodeInclude)node);
                    lastWasNewline = false;
                    continue;
                }
                if (!(node instanceof ConfigNodeField)) continue;
                lastWasNewline = false;
                Path path = ((ConfigNodeField)node).path().value();
                comments.addAll(((ConfigNodeField)node).comments());
                this.pathStack.push(path);
                if (((ConfigNodeField)node).separator() == Tokens.PLUS_EQUALS) {
                    if (this.arrayCount > 0) {
                        throw this.parseError("Due to current limitations of the config parser, += does not work nested inside a list. += expands to a ${} substitution and the path in ${} cannot currently refer to list elements. You might be able to move the += outside of the list and then refer to it from inside the list with ${}.");
                    }
                    ++this.arrayCount;
                }
                AbstractConfigNodeValue valueNode = ((ConfigNodeField)node).value();
                AbstractConfigValue newValue = this.parseValue(valueNode, comments);
                if (((ConfigNodeField)node).separator() == Tokens.PLUS_EQUALS) {
                    --this.arrayCount;
                    ArrayList<AbstractConfigValue> concat = new ArrayList<AbstractConfigValue>(2);
                    ConfigReference previousRef = new ConfigReference(newValue.origin(), new SubstitutionExpression(this.fullCurrentPath(), true));
                    SimpleConfigList list = new SimpleConfigList(newValue.origin(), Collections.singletonList(newValue));
                    concat.add(previousRef);
                    concat.add(list);
                    newValue = ConfigConcatenation.concatenate(concat);
                }
                if (i < nodes.size() - 1) {
                    ++i;
                    while (i < nodes.size()) {
                        if (nodes.get(i) instanceof ConfigNodeComment) {
                            ConfigNodeComment comment = (ConfigNodeComment)nodes.get(i);
                            newValue = newValue.withOrigin(newValue.origin().appendComments(Collections.singletonList(comment.commentText())));
                            break;
                        }
                        if (nodes.get(i) instanceof ConfigNodeSingleToken) {
                            ConfigNodeSingleToken curr = (ConfigNodeSingleToken)nodes.get(i);
                            if (curr.token() != Tokens.COMMA && !Tokens.isIgnoredWhitespace(curr.token())) {
                                --i;
                                break;
                            }
                        } else {
                            --i;
                            break;
                        }
                        ++i;
                    }
                }
                this.pathStack.pop();
                String key = path.first();
                Path remaining = path.remainder();
                if (remaining == null) {
                    AbstractConfigValue existing = (AbstractConfigValue)values.get(key);
                    if (existing != null) {
                        if (this.flavor == ConfigSyntax.JSON) {
                            throw this.parseError("JSON does not allow duplicate fields: '" + key + "' was already seen at " + existing.origin().description());
                        }
                        newValue = newValue.withFallback(existing);
                    }
                    values.put(key, newValue);
                    continue;
                }
                if (this.flavor == ConfigSyntax.JSON) {
                    throw new ConfigException.BugOrBroken("somehow got multi-element path in JSON mode");
                }
                AbstractConfigObject obj = ParseContext.createValueUnderPath(remaining, newValue);
                AbstractConfigValue existing = (AbstractConfigValue)values.get(key);
                if (existing != null) {
                    obj = obj.withFallback(existing);
                }
                values.put(key, obj);
            }
            return new SimpleConfigObject(objectOrigin, values);
        }

        private SimpleConfigList parseArray(ConfigNodeArray n) {
            ++this.arrayCount;
            SimpleConfigOrigin arrayOrigin = this.lineOrigin();
            ArrayList<AbstractConfigValue> values = new ArrayList<AbstractConfigValue>();
            boolean lastWasNewLine = false;
            ArrayList<String> comments = new ArrayList<String>();
            AbstractConfigValue v = null;
            for (AbstractConfigNode node : n.children()) {
                if (node instanceof ConfigNodeComment) {
                    comments.add(((ConfigNodeComment)node).commentText());
                    lastWasNewLine = false;
                    continue;
                }
                if (node instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)node).token())) {
                    ++this.lineNumber;
                    if (lastWasNewLine && v == null) {
                        comments.clear();
                    } else if (v != null) {
                        values.add(v.withOrigin(v.origin().appendComments(new ArrayList<String>(comments))));
                        comments.clear();
                        v = null;
                    }
                    lastWasNewLine = true;
                    continue;
                }
                if (!(node instanceof AbstractConfigNodeValue)) continue;
                lastWasNewLine = false;
                if (v != null) {
                    values.add(v.withOrigin(v.origin().appendComments(new ArrayList<String>(comments))));
                    comments.clear();
                }
                v = this.parseValue((AbstractConfigNodeValue)node, comments);
            }
            if (v != null) {
                values.add(v.withOrigin(v.origin().appendComments(new ArrayList<String>(comments))));
            }
            --this.arrayCount;
            return new SimpleConfigList(arrayOrigin, values);
        }

        AbstractConfigValue parse() {
            AbstractConfigValue result = null;
            ArrayList<String> comments = new ArrayList<String>();
            boolean lastWasNewLine = false;
            for (AbstractConfigNode node : this.document.children()) {
                if (node instanceof ConfigNodeComment) {
                    comments.add(((ConfigNodeComment)node).commentText());
                    lastWasNewLine = false;
                    continue;
                }
                if (node instanceof ConfigNodeSingleToken) {
                    Token t = ((ConfigNodeSingleToken)node).token();
                    if (!Tokens.isNewline(t)) continue;
                    ++this.lineNumber;
                    if (lastWasNewLine && result == null) {
                        comments.clear();
                    } else if (result != null) {
                        result = result.withOrigin(result.origin().appendComments(new ArrayList<String>(comments)));
                        comments.clear();
                        break;
                    }
                    lastWasNewLine = true;
                    continue;
                }
                if (!(node instanceof ConfigNodeComplexValue)) continue;
                result = this.parseValue((ConfigNodeComplexValue)node, comments);
                lastWasNewLine = false;
            }
            return result;
        }
    }
}

