/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.soytree;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.SoyNode;

public abstract class AbstractCommandNode
extends AbstractSoyNode
implements SoyNode.CommandNode {
    private final String commandName;
    private final String commandText;

    public AbstractCommandNode(int id, String commandName, String commandText) {
        super(id);
        this.commandName = commandName;
        this.commandText = commandText.trim();
    }

    protected AbstractCommandNode(AbstractCommandNode orig) {
        super(orig);
        this.commandName = orig.commandName;
        this.commandText = orig.commandText;
    }

    @Override
    public String getCommandName() {
        return this.commandName;
    }

    @Override
    public String getCommandText() {
        return this.commandText;
    }

    @Override
    public String getTagString() {
        return this.buildTagStringHelper(false);
    }

    protected String buildTagStringHelper(boolean isSelfEnding) {
        return this.buildTagStringHelper(isSelfEnding, false);
    }

    protected String buildTagStringHelper(boolean isSelfEnding, boolean isImplicitCommandName) {
        String commandNameStr;
        String maybeSelfEndingStr;
        String commandName = this.getCommandName();
        String commandText = this.getCommandText();
        String string = maybeSelfEndingStr = isSelfEnding ? " /" : "";
        if (commandText.length() == 0) {
            Preconditions.checkArgument((!isImplicitCommandName ? 1 : 0) != 0);
            return "{" + commandName + maybeSelfEndingStr + "}";
        }
        String string2 = commandNameStr = isImplicitCommandName ? "" : commandName + " ";
        if (CharMatcher.anyOf((CharSequence)"{}").matchesNoneOf((CharSequence)commandText)) {
            return "{" + commandNameStr + commandText + maybeSelfEndingStr + "}";
        }
        char lastChar = commandText.charAt(commandText.length() - 1);
        if (lastChar == '{' || lastChar == '}') {
            if (isSelfEnding) {
                return "{{" + commandNameStr + commandText + " /}}";
            }
            return "{{" + commandNameStr + commandText + " }}";
        }
        return "{{" + commandNameStr + commandText + maybeSelfEndingStr + "}}";
    }

    @Override
    public String toSourceString() {
        return this.getTagString();
    }
}

