/*
 * Decompiled with CFR 0.152.
 */
package net.customware.confluence.plugin.toc;

import java.util.ArrayList;
import java.util.List;
import net.customware.confluence.plugin.toc.DefaultDocumentOutlineBuilder;
import net.customware.confluence.plugin.toc.DepthFirstDocumentOutlineBuilder;
import net.customware.confluence.plugin.toc.DocumentOutline;
import net.customware.confluence.plugin.toc.DocumentOutlineImpl;

public class EmptyLevelTrimmingDocumentOutlineBuilder
extends DefaultDocumentOutlineBuilder {
    @Override
    public DocumentOutline getDocumentOutline() {
        return EmptyLevelTrimmingDocumentOutlineBuilder.beginPlaceholderTrimming(this.topOfOutline).getDocumentOutline();
    }

    private static DepthFirstDocumentOutlineBuilder beginPlaceholderTrimming(List<DefaultDocumentOutlineBuilder.BuildableHeading> headings) {
        DefaultDocumentOutlineBuilder builder = new DefaultDocumentOutlineBuilder();
        DefaultDocumentOutlineBuilder.BuildableHeading rootHeading = new DefaultDocumentOutlineBuilder.BuildableHeading("__temporary_root_node__", null, 0);
        rootHeading.addChildren(headings);
        EmptyLevelTrimmingDocumentOutlineBuilder.applyDiscardToHeading(builder, rootHeading, true);
        return builder;
    }

    private static void applyDiscardToHeading(DepthFirstDocumentOutlineBuilder builder, DefaultDocumentOutlineBuilder.BuildableHeading heading, boolean rootNode) {
        DefaultDocumentOutlineBuilder.BuildableHeading processed = EmptyLevelTrimmingDocumentOutlineBuilder.discardPlaceholderChildren(heading);
        if (!rootNode) {
            builder.add(processed.getName(), processed.getAnchor(), processed.getType());
        }
        if (processed.hasChildren()) {
            if (!rootNode) {
                builder.nextLevel();
            }
            for (DefaultDocumentOutlineBuilder.BuildableHeading child : processed.getChildren()) {
                EmptyLevelTrimmingDocumentOutlineBuilder.applyDiscardToHeading(builder, child, false);
            }
            if (!rootNode) {
                builder.previousLevel();
            }
        }
    }

    private static DefaultDocumentOutlineBuilder.BuildableHeading discardPlaceholderChildren(DefaultDocumentOutlineBuilder.BuildableHeading heading) {
        boolean allPlaceholderChildren = true;
        List<DefaultDocumentOutlineBuilder.BuildableHeading> children = heading.getChildren();
        while (allPlaceholderChildren && !children.isEmpty()) {
            ArrayList<DefaultDocumentOutlineBuilder.BuildableHeading> candidateChildren = new ArrayList<DefaultDocumentOutlineBuilder.BuildableHeading>();
            for (DefaultDocumentOutlineBuilder.BuildableHeading child : children) {
                if (DocumentOutlineImpl.NOT_PLACEHOLDER_MATCHER.matches(child)) {
                    allPlaceholderChildren = false;
                    break;
                }
                for (DefaultDocumentOutlineBuilder.BuildableHeading grandChild : child.getChildren()) {
                    candidateChildren.add(grandChild);
                }
            }
            if (!allPlaceholderChildren) continue;
            children = candidateChildren;
            heading.clearChildren();
            heading.addChildren(children);
        }
        return heading;
    }
}

