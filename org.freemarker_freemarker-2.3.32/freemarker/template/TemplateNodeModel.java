/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

public interface TemplateNodeModel
extends TemplateModel {
    public TemplateNodeModel getParentNode() throws TemplateModelException;

    public TemplateSequenceModel getChildNodes() throws TemplateModelException;

    public String getNodeName() throws TemplateModelException;

    public String getNodeType() throws TemplateModelException;

    public String getNodeNamespace() throws TemplateModelException;
}

