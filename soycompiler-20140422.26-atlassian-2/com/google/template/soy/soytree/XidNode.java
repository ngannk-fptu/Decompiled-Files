/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.shared.SoyIdRenamingMap;
import com.google.template.soy.soytree.AbstractCommandNode;
import com.google.template.soy.soytree.SoyNode;

public class XidNode
extends AbstractCommandNode
implements SoyNode.StandaloneNode,
SoyNode.StatementNode {
    private final String text;
    private volatile Pair<SoyIdRenamingMap, String> renameCache;

    public XidNode(int id, String commandText) {
        super(id, "xid", commandText);
        this.text = commandText;
        if (!BaseUtils.isDottedOrDashedIdent(this.text)) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid xid value: '" + this.text + "'");
        }
    }

    protected XidNode(XidNode orig) {
        super(orig);
        this.text = orig.text;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.XID_NODE;
    }

    public String getText() {
        return this.text;
    }

    public String getRenamedText(SoyIdRenamingMap idRenamingMap) {
        String mappedText;
        Pair<SoyIdRenamingMap, String> cache = this.renameCache;
        if (cache != null && cache.first == idRenamingMap) {
            return (String)cache.second;
        }
        if (idRenamingMap != null && (mappedText = idRenamingMap.get(this.text)) != null) {
            this.renameCache = Pair.of(idRenamingMap, mappedText);
            return mappedText;
        }
        return this.text;
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public XidNode clone() {
        return new XidNode(this);
    }
}

