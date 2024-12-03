/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses;

import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveHtmlCommentsVisitor
extends AbstractSoyNodeVisitor<Void> {
    private static final Pattern HTML_COMMENT = Pattern.compile("<!--.*?-->");
    private IdGenerator nodeIdGen;
    private final IdGenerator explicitNodeIdGen;

    public RemoveHtmlCommentsVisitor(IdGenerator nodeIdGen) {
        this.explicitNodeIdGen = nodeIdGen;
    }

    public RemoveHtmlCommentsVisitor() {
        this(null);
    }

    @Override
    public Void exec(SoyNode node) {
        this.nodeIdGen = this.explicitNodeIdGen != null ? this.explicitNodeIdGen : node.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator();
        this.visit(node);
        this.nodeIdGen = null;
        return null;
    }

    @Override
    protected void visitRawTextNode(RawTextNode node) {
        Matcher matcher = HTML_COMMENT.matcher(node.getRawText());
        if (!matcher.find()) {
            return;
        }
        matcher.reset();
        StringBuffer newRawText = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(newRawText, "");
        }
        matcher.appendTail(newRawText);
        if (newRawText.length() > 0) {
            RawTextNode newRawTextNode = new RawTextNode(this.nodeIdGen.genId(), newRawText.toString());
            node.getParent().replaceChild(node, newRawTextNode);
        } else {
            node.getParent().removeChild(node);
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
    }
}

