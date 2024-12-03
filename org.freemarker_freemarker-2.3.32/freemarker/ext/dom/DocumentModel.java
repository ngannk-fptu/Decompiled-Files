/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.ext.dom.DomStringUtil;
import freemarker.ext.dom.ElementModel;
import freemarker.ext.dom.NodeListModel;
import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

class DocumentModel
extends NodeModel
implements TemplateHashModel {
    private ElementModel rootElement;

    DocumentModel(Document doc) {
        super(doc);
    }

    @Override
    public String getNodeName() {
        return "@document";
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        if (key.equals("*")) {
            return this.getRootElement();
        }
        if (key.equals("**")) {
            NodeList nl = ((Document)this.node).getElementsByTagName("*");
            return new NodeListModel(nl, (NodeModel)this);
        }
        if (DomStringUtil.isXMLNameLike(key)) {
            ElementModel em = (ElementModel)NodeModel.wrap(((Document)this.node).getDocumentElement());
            if (em.matchesName(key, Environment.getCurrentEnvironment())) {
                return em;
            }
            return new NodeListModel(this);
        }
        return super.get(key);
    }

    ElementModel getRootElement() {
        if (this.rootElement == null) {
            this.rootElement = (ElementModel)DocumentModel.wrap(((Document)this.node).getDocumentElement());
        }
        return this.rootElement;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

