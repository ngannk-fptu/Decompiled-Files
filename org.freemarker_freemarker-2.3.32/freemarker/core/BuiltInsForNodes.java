/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInForNode;
import freemarker.core.BuiltInForNodeEx;
import freemarker.core.Environment;
import freemarker.ext.dom._ExtDomApi;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template._ObjectWrappers;
import java.util.List;

class BuiltInsForNodes {
    private BuiltInsForNodes() {
    }

    static class AncestorSequence
    extends SimpleSequence
    implements TemplateMethodModel {
        private Environment env;

        AncestorSequence(Environment env) {
            super(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
            this.env = env;
        }

        @Override
        public Object exec(List names) throws TemplateModelException {
            if (names == null || names.isEmpty()) {
                return this;
            }
            AncestorSequence result = new AncestorSequence(this.env);
            block0: for (int i = 0; i < this.size(); ++i) {
                TemplateNodeModel tnm = (TemplateNodeModel)this.get(i);
                String nodeName = tnm.getNodeName();
                String nsURI = tnm.getNodeNamespace();
                if (nsURI == null) {
                    if (!names.contains(nodeName)) continue;
                    result.add(tnm);
                    continue;
                }
                for (int j = 0; j < names.size(); ++j) {
                    if (!_ExtDomApi.matchesName((String)names.get(j), nodeName, nsURI, this.env)) continue;
                    result.add(tnm);
                    continue block0;
                }
            }
            return result;
        }
    }

    static class nextSiblingBI
    extends BuiltInForNodeEx {
        nextSiblingBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getNextSibling();
        }
    }

    static class previousSiblingBI
    extends BuiltInForNodeEx {
        previousSiblingBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getPreviousSibling();
        }
    }

    static class rootBI
    extends BuiltInForNode {
        rootBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            TemplateNodeModel result = nodeModel;
            TemplateNodeModel parent = nodeModel.getParentNode();
            while (parent != null) {
                result = parent;
                parent = result.getParentNode();
            }
            return result;
        }
    }

    static class parentBI
    extends BuiltInForNode {
        parentBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getParentNode();
        }
    }

    static class node_typeBI
    extends BuiltInForNode {
        node_typeBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return new SimpleScalar(nodeModel.getNodeType());
        }
    }

    static class node_namespaceBI
    extends BuiltInForNode {
        node_namespaceBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            String nsURI = nodeModel.getNodeNamespace();
            return nsURI == null ? null : new SimpleScalar(nsURI);
        }
    }

    static class node_nameBI
    extends BuiltInForNode {
        node_nameBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return new SimpleScalar(nodeModel.getNodeName());
        }
    }

    static class childrenBI
    extends BuiltInForNode {
        childrenBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            return nodeModel.getChildNodes();
        }
    }

    static class ancestorsBI
    extends BuiltInForNode {
        ancestorsBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
            AncestorSequence result = new AncestorSequence(env);
            for (TemplateNodeModel parent = nodeModel.getParentNode(); parent != null; parent = parent.getParentNode()) {
                result.add(parent);
            }
            return result;
        }
    }
}

