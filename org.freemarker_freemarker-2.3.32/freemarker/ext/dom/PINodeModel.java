/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateScalarModel;
import org.w3c.dom.ProcessingInstruction;

class PINodeModel
extends NodeModel
implements TemplateScalarModel {
    public PINodeModel(ProcessingInstruction pi) {
        super(pi);
    }

    @Override
    public String getAsString() {
        return ((ProcessingInstruction)this.node).getData();
    }

    @Override
    public String getNodeName() {
        return "@pi$" + ((ProcessingInstruction)this.node).getTarget();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}

