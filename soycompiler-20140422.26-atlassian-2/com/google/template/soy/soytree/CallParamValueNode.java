/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;

public class CallParamValueNode
extends CallParamNode
implements SoyNode.ExprHolderNode {
    private final String key;
    private final ExprUnion valueExprUnion;

    public CallParamValueNode(int id, String commandText) throws SoySyntaxException {
        super(id, commandText);
        CallParamNode.CommandTextParseResult parseResult = this.parseCommandTextHelper(commandText);
        this.key = parseResult.key;
        this.valueExprUnion = parseResult.valueExprUnion;
        if (this.valueExprUnion == null) {
            throw SoySyntaxException.createWithoutMetaInfo("A 'param' tag should be self-ending (with a trailing '/') if and only if it also contains a value (invalid tag is {param " + commandText + " /}).");
        }
        if (parseResult.contentKind != null) {
            throw SoySyntaxException.createWithoutMetaInfo("The 'kind' attribute is not allowed on self-ending 'param' tags that  contain a value (invalid tag is {param " + commandText + " /}).");
        }
    }

    protected CallParamValueNode(CallParamValueNode orig) {
        super(orig);
        this.key = orig.key;
        this.valueExprUnion = orig.valueExprUnion != null ? orig.valueExprUnion.clone() : null;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.CALL_PARAM_VALUE_NODE;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public String getValueExprText() {
        return this.valueExprUnion.getExprText();
    }

    public ExprUnion getValueExprUnion() {
        return this.valueExprUnion;
    }

    @Override
    public String getTagString() {
        return this.buildTagStringHelper(true);
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)this.valueExprUnion);
    }

    @Override
    public CallParamValueNode clone() {
        return new CallParamValueNode(this);
    }
}

