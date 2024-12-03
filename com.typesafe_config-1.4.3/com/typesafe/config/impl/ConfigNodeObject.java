/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.ConfigNodeComplexValue;
import com.typesafe.config.impl.ConfigNodeField;
import com.typesafe.config.impl.ConfigNodeInclude;
import com.typesafe.config.impl.ConfigNodePath;
import com.typesafe.config.impl.ConfigNodeSingleToken;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.PathParser;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.util.ArrayList;
import java.util.Collection;

final class ConfigNodeObject
extends ConfigNodeComplexValue {
    ConfigNodeObject(Collection<AbstractConfigNode> children) {
        super(children);
    }

    @Override
    protected ConfigNodeObject newNode(Collection<AbstractConfigNode> nodes) {
        return new ConfigNodeObject(nodes);
    }

    public boolean hasValue(Path desiredPath) {
        for (AbstractConfigNode node : this.children) {
            Path remainingPath;
            ConfigNodeObject obj;
            if (!(node instanceof ConfigNodeField)) continue;
            ConfigNodeField field = (ConfigNodeField)node;
            Path key = field.path().value();
            if (key.equals(desiredPath) || key.startsWith(desiredPath)) {
                return true;
            }
            if (!desiredPath.startsWith(key) || !(field.value() instanceof ConfigNodeObject) || !(obj = (ConfigNodeObject)field.value()).hasValue(remainingPath = desiredPath.subPath(key.length()))) continue;
            return true;
        }
        return false;
    }

    protected ConfigNodeObject changeValueOnPath(Path desiredPath, AbstractConfigNodeValue value, ConfigSyntax flavor) {
        ArrayList<AbstractConfigNode> childrenCopy = new ArrayList<AbstractConfigNode>(this.children);
        boolean seenNonMatching = false;
        AbstractConfigNodeValue valueCopy = value;
        for (int i = childrenCopy.size() - 1; i >= 0; --i) {
            if (childrenCopy.get(i) instanceof ConfigNodeSingleToken) {
                Token t = ((ConfigNodeSingleToken)childrenCopy.get(i)).token();
                if (flavor != ConfigSyntax.JSON || seenNonMatching || t != Tokens.COMMA) continue;
                childrenCopy.remove(i);
                continue;
            }
            if (!(childrenCopy.get(i) instanceof ConfigNodeField)) continue;
            ConfigNodeField node = (ConfigNodeField)childrenCopy.get(i);
            Path key = node.path().value();
            if (valueCopy == null && key.equals(desiredPath) || key.startsWith(desiredPath) && !key.equals(desiredPath)) {
                Token t;
                childrenCopy.remove(i);
                for (int j = i; j < childrenCopy.size() && childrenCopy.get(j) instanceof ConfigNodeSingleToken && (Tokens.isIgnoredWhitespace(t = ((ConfigNodeSingleToken)childrenCopy.get(j)).token()) || t == Tokens.COMMA); ++j) {
                    childrenCopy.remove(j);
                    --j;
                }
                continue;
            }
            if (key.equals(desiredPath)) {
                seenNonMatching = true;
                AbstractConfigNode before = i - 1 > 0 ? childrenCopy.get(i - 1) : null;
                AbstractConfigNodeValue indentedValue = value instanceof ConfigNodeComplexValue && before instanceof ConfigNodeSingleToken && Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)before).token()) ? ((ConfigNodeComplexValue)value).indentText(before) : value;
                childrenCopy.set(i, node.replaceValue(indentedValue));
                valueCopy = null;
                continue;
            }
            if (desiredPath.startsWith(key)) {
                seenNonMatching = true;
                if (!(node.value() instanceof ConfigNodeObject)) continue;
                Path remainingPath = desiredPath.subPath(key.length());
                childrenCopy.set(i, node.replaceValue(((ConfigNodeObject)node.value()).changeValueOnPath(remainingPath, valueCopy, flavor)));
                if (valueCopy == null || node.equals(this.children.get(i))) continue;
                valueCopy = null;
                continue;
            }
            seenNonMatching = true;
        }
        return new ConfigNodeObject(childrenCopy);
    }

    public ConfigNodeObject setValueOnPath(String desiredPath, AbstractConfigNodeValue value) {
        return this.setValueOnPath(desiredPath, value, ConfigSyntax.CONF);
    }

    public ConfigNodeObject setValueOnPath(String desiredPath, AbstractConfigNodeValue value, ConfigSyntax flavor) {
        ConfigNodePath path = PathParser.parsePathNode(desiredPath, flavor);
        return this.setValueOnPath(path, value, flavor);
    }

    private ConfigNodeObject setValueOnPath(ConfigNodePath desiredPath, AbstractConfigNodeValue value, ConfigSyntax flavor) {
        ConfigNodeObject node = this.changeValueOnPath(desiredPath.value(), value, flavor);
        if (!node.hasValue(desiredPath.value())) {
            return node.addValueOnPath(desiredPath, value, flavor);
        }
        return node;
    }

    private Collection<AbstractConfigNode> indentation() {
        boolean seenNewLine = false;
        ArrayList<AbstractConfigNode> indentation = new ArrayList<AbstractConfigNode>();
        if (this.children.isEmpty()) {
            return indentation;
        }
        for (int i = 0; i < this.children.size(); ++i) {
            if (!seenNewLine) {
                if (!(this.children.get(i) instanceof ConfigNodeSingleToken) || !Tokens.isNewline(((ConfigNodeSingleToken)this.children.get(i)).token())) continue;
                seenNewLine = true;
                indentation.add(new ConfigNodeSingleToken(Tokens.newLine(null)));
                continue;
            }
            if (!(this.children.get(i) instanceof ConfigNodeSingleToken) || !Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)this.children.get(i)).token()) || i + 1 >= this.children.size() || !(this.children.get(i + 1) instanceof ConfigNodeField) && !(this.children.get(i + 1) instanceof ConfigNodeInclude)) continue;
            indentation.add((AbstractConfigNode)this.children.get(i));
            return indentation;
        }
        if (indentation.isEmpty()) {
            indentation.add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, " ")));
        } else {
            AbstractConfigNode last = (AbstractConfigNode)this.children.get(this.children.size() - 1);
            if (last instanceof ConfigNodeSingleToken && ((ConfigNodeSingleToken)last).token() == Tokens.CLOSE_CURLY) {
                AbstractConfigNode beforeLast = (AbstractConfigNode)this.children.get(this.children.size() - 2);
                String indent = "";
                if (beforeLast instanceof ConfigNodeSingleToken && Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)beforeLast).token())) {
                    indent = ((ConfigNodeSingleToken)beforeLast).token().tokenText();
                }
                indent = indent + "  ";
                indentation.add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, indent)));
                return indentation;
            }
        }
        return indentation;
    }

    protected ConfigNodeObject addValueOnPath(ConfigNodePath desiredPath, AbstractConfigNodeValue value, ConfigSyntax flavor) {
        boolean sameLine;
        Path path = desiredPath.value();
        ArrayList<AbstractConfigNode> childrenCopy = new ArrayList<AbstractConfigNode>(this.children);
        ArrayList<AbstractConfigNode> indentation = new ArrayList<AbstractConfigNode>(this.indentation());
        AbstractConfigNodeValue indentedValue = value instanceof ConfigNodeComplexValue && !indentation.isEmpty() ? ((ConfigNodeComplexValue)value).indentText(indentation.get(indentation.size() - 1)) : value;
        boolean bl = sameLine = indentation.size() <= 0 || !(indentation.get(0) instanceof ConfigNodeSingleToken) || !Tokens.isNewline(((ConfigNodeSingleToken)indentation.get(0)).token());
        if (path.length() > 1) {
            for (int i = this.children.size() - 1; i >= 0; --i) {
                ConfigNodeField node;
                Path key;
                if (!(this.children.get(i) instanceof ConfigNodeField) || !path.startsWith(key = (node = (ConfigNodeField)this.children.get(i)).path().value()) || !(node.value() instanceof ConfigNodeObject)) continue;
                ConfigNodePath remainingPath = desiredPath.subPath(key.length());
                ConfigNodeObject newValue = (ConfigNodeObject)node.value();
                childrenCopy.set(i, node.replaceValue(newValue.addValueOnPath(remainingPath, value, flavor)));
                return new ConfigNodeObject(childrenCopy);
            }
        }
        boolean startsWithBrace = !this.children.isEmpty() && this.children.get(0) instanceof ConfigNodeSingleToken && ((ConfigNodeSingleToken)this.children.get(0)).token() == Tokens.OPEN_CURLY;
        ArrayList<AbstractConfigNode> newNodes = new ArrayList<AbstractConfigNode>();
        newNodes.addAll(indentation);
        newNodes.add(desiredPath.first());
        newNodes.add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, " ")));
        newNodes.add(new ConfigNodeSingleToken(Tokens.COLON));
        newNodes.add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, " ")));
        if (path.length() == 1) {
            newNodes.add(indentedValue);
        } else {
            ArrayList<AbstractConfigNode> newObjectNodes = new ArrayList<AbstractConfigNode>();
            newObjectNodes.add(new ConfigNodeSingleToken(Tokens.OPEN_CURLY));
            if (indentation.isEmpty()) {
                newObjectNodes.add(new ConfigNodeSingleToken(Tokens.newLine(null)));
            }
            newObjectNodes.addAll(indentation);
            newObjectNodes.add(new ConfigNodeSingleToken(Tokens.CLOSE_CURLY));
            ConfigNodeObject newObject = new ConfigNodeObject(newObjectNodes);
            newNodes.add(newObject.addValueOnPath(desiredPath.subPath(1), indentedValue, flavor));
        }
        if (flavor == ConfigSyntax.JSON || startsWithBrace || sameLine) {
            for (int i = childrenCopy.size() - 1; i >= 0; --i) {
                if ((flavor == ConfigSyntax.JSON || sameLine) && childrenCopy.get(i) instanceof ConfigNodeField) {
                    if (i + 1 < childrenCopy.size() && childrenCopy.get(i + 1) instanceof ConfigNodeSingleToken && ((ConfigNodeSingleToken)childrenCopy.get(i + 1)).token() == Tokens.COMMA) break;
                    childrenCopy.add(i + 1, new ConfigNodeSingleToken(Tokens.COMMA));
                    break;
                }
                if (!startsWithBrace || !(childrenCopy.get(i) instanceof ConfigNodeSingleToken) || ((ConfigNodeSingleToken)childrenCopy.get((int)i)).token != Tokens.CLOSE_CURLY) continue;
                AbstractConfigNode previous = childrenCopy.get(i - 1);
                if (previous instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)previous).token())) {
                    childrenCopy.add(i - 1, new ConfigNodeField(newNodes));
                    --i;
                    continue;
                }
                if (previous instanceof ConfigNodeSingleToken && Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)previous).token())) {
                    AbstractConfigNode beforePrevious = childrenCopy.get(i - 2);
                    if (sameLine) {
                        childrenCopy.add(i - 1, new ConfigNodeField(newNodes));
                        --i;
                        continue;
                    }
                    if (beforePrevious instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)beforePrevious).token())) {
                        childrenCopy.add(i - 2, new ConfigNodeField(newNodes));
                        i -= 2;
                        continue;
                    }
                    childrenCopy.add(i, new ConfigNodeField(newNodes));
                    continue;
                }
                childrenCopy.add(i, new ConfigNodeField(newNodes));
            }
        }
        if (!startsWithBrace) {
            if (!childrenCopy.isEmpty() && childrenCopy.get(childrenCopy.size() - 1) instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)childrenCopy.get(childrenCopy.size() - 1)).token())) {
                childrenCopy.add(childrenCopy.size() - 1, new ConfigNodeField(newNodes));
            } else {
                childrenCopy.add(new ConfigNodeField(newNodes));
            }
        }
        return new ConfigNodeObject(childrenCopy);
    }

    public ConfigNodeObject removeValueOnPath(String desiredPath, ConfigSyntax flavor) {
        Path path = PathParser.parsePathNode(desiredPath, flavor).value();
        return this.changeValueOnPath(path, null, flavor);
    }
}

