/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.MixinParentNode;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;
import javax.annotation.Nullable;

public class CallParamContentNode
extends CallParamNode
implements SoyNode.RenderUnitNode {
    private final MixinParentNode<SoyNode.StandaloneNode> parentMixin;
    private final String key;
    @Nullable
    private final SanitizedContent.ContentKind contentKind;

    public CallParamContentNode(int id, String commandText) throws SoySyntaxException {
        super(id, commandText);
        this.parentMixin = new MixinParentNode<SoyNode.StandaloneNode>(this);
        CallParamNode.CommandTextParseResult parseResult = this.parseCommandTextHelper(commandText);
        this.key = parseResult.key;
        this.contentKind = parseResult.contentKind;
        if (parseResult.valueExprUnion != null) {
            throw SoySyntaxException.createWithoutMetaInfo("A 'param' tag should contain a value if and only if it is also self-ending (with a trailing '/') (invalid tag is {param " + commandText + "}).");
        }
    }

    protected CallParamContentNode(CallParamContentNode orig) {
        super(orig);
        this.parentMixin = new MixinParentNode<SoyNode.StandaloneNode>(orig.parentMixin, this);
        this.key = orig.key;
        this.contentKind = orig.contentKind;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.CALL_PARAM_CONTENT_NODE;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    @Nullable
    public SanitizedContent.ContentKind getContentKind() {
        return this.contentKind;
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTagString());
        this.appendSourceStringForChildren(sb);
        sb.append("{/").append(this.getCommandName()).append('}');
        return sb.toString();
    }

    @Override
    public void setNeedsEnvFrameDuringInterp(Boolean needsEnvFrameDuringInterp) {
        this.parentMixin.setNeedsEnvFrameDuringInterp(needsEnvFrameDuringInterp);
    }

    @Override
    public Boolean needsEnvFrameDuringInterp() {
        return this.parentMixin.needsEnvFrameDuringInterp();
    }

    @Override
    public int numChildren() {
        return this.parentMixin.numChildren();
    }

    @Override
    public SoyNode.StandaloneNode getChild(int index) {
        return this.parentMixin.getChild(index);
    }

    @Override
    public int getChildIndex(SoyNode.StandaloneNode child) {
        return this.parentMixin.getChildIndex(child);
    }

    @Override
    public List<SoyNode.StandaloneNode> getChildren() {
        return this.parentMixin.getChildren();
    }

    @Override
    public void addChild(SoyNode.StandaloneNode child) {
        this.parentMixin.addChild(child);
    }

    @Override
    public void addChild(int index, SoyNode.StandaloneNode child) {
        this.parentMixin.addChild(index, child);
    }

    @Override
    public void removeChild(int index) {
        this.parentMixin.removeChild(index);
    }

    @Override
    public void removeChild(SoyNode.StandaloneNode child) {
        this.parentMixin.removeChild(child);
    }

    @Override
    public void replaceChild(int index, SoyNode.StandaloneNode newChild) {
        this.parentMixin.replaceChild((SoyNode.StandaloneNode)index, newChild);
    }

    @Override
    public void replaceChild(SoyNode.StandaloneNode currChild, SoyNode.StandaloneNode newChild) {
        this.parentMixin.replaceChild(currChild, newChild);
    }

    @Override
    public void clearChildren() {
        this.parentMixin.clearChildren();
    }

    @Override
    public void addChildren(List<? extends SoyNode.StandaloneNode> children) {
        this.parentMixin.addChildren(children);
    }

    @Override
    public void addChildren(int index, List<? extends SoyNode.StandaloneNode> children) {
        this.parentMixin.addChildren(index, children);
    }

    @Override
    public void appendSourceStringForChildren(StringBuilder sb) {
        this.parentMixin.appendSourceStringForChildren(sb);
    }

    @Override
    public void appendTreeStringForChildren(StringBuilder sb, int indent) {
        this.parentMixin.appendTreeStringForChildren(sb, indent);
    }

    @Override
    public String toTreeString(int indent) {
        return this.parentMixin.toTreeString(indent);
    }

    @Override
    public CallParamContentNode clone() {
        return new CallParamContentNode(this);
    }
}

