/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import org.w3c.dom.DocumentType;
import org.w3c.dom.ProcessingInstruction;

class DocumentTypeModel
extends NodeModel {
    public DocumentTypeModel(DocumentType docType) {
        super(docType);
    }

    public String getAsString() {
        return ((ProcessingInstruction)this.node).getData();
    }

    public TemplateSequenceModel getChildren() throws TemplateModelException {
        throw new TemplateModelException("entering the child nodes of a DTD node is not currently supported");
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        throw new TemplateModelException("accessing properties of a DTD is not currently supported");
    }

    @Override
    public String getNodeName() {
        return "@document_type$" + this.node.getNodeName();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}

