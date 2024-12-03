/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateElement;
import freemarker.template.Template;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

@Deprecated
public class FreeMarkerTree
extends JTree {
    public FreeMarkerTree(Template template) {
        super(template.getRootTreeNode());
    }

    public void setTemplate(Template template) {
        this.setModel(new DefaultTreeModel(template.getRootTreeNode()));
        this.invalidate();
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof TemplateElement) {
            return ((TemplateElement)value).getDescription();
        }
        return value.toString();
    }
}

