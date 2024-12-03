/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateScalarModel;
import org.w3c.dom.Attr;

class AttributeNodeModel
extends NodeModel
implements TemplateScalarModel {
    public AttributeNodeModel(Attr att) {
        super(att);
    }

    @Override
    public String getAsString() {
        return ((Attr)this.node).getValue();
    }

    @Override
    public String getNodeName() {
        String result = this.node.getLocalName();
        if (result == null || result.equals("")) {
            result = this.node.getNodeName();
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    String getQualifiedName() {
        String nsURI = this.node.getNamespaceURI();
        if (nsURI == null || nsURI.equals("")) {
            return this.node.getNodeName();
        }
        Environment env = Environment.getCurrentEnvironment();
        String defaultNS = env.getDefaultNS();
        String prefix = null;
        prefix = nsURI.equals(defaultNS) ? "D" : env.getPrefixForNamespace(nsURI);
        if (prefix == null) {
            return null;
        }
        return prefix + ":" + this.node.getLocalName();
    }
}

