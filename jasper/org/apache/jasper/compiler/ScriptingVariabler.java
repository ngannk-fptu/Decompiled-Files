/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagVariableInfo
 *  javax.servlet.jsp.tagext.VariableInfo
 */
package org.apache.jasper.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.Node;

class ScriptingVariabler {
    private static final Integer MAX_SCOPE = Integer.MAX_VALUE;

    ScriptingVariabler() {
    }

    public static void set(Node.Nodes page, ErrorDispatcher err) throws JasperException {
        page.visit(new CustomTagCounter());
        page.visit(new ScriptingVariableVisitor(err));
    }

    private static class CustomTagCounter
    extends Node.Visitor {
        private int count;
        private Node.CustomTag parent;

        private CustomTagCounter() {
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            n.setCustomTagParent(this.parent);
            Node.CustomTag tmpParent = this.parent;
            this.parent = n;
            this.visitBody(n);
            this.parent = tmpParent;
            n.setNumCount(this.count++);
        }
    }

    private static class ScriptingVariableVisitor
    extends Node.Visitor {
        private final ErrorDispatcher err;
        private final Map<String, Integer> scriptVars;

        ScriptingVariableVisitor(ErrorDispatcher err) {
            this.err = err;
            this.scriptVars = new HashMap<String, Integer>();
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            this.setScriptingVars(n, 1);
            this.setScriptingVars(n, 0);
            this.visitBody(n);
            this.setScriptingVars(n, 2);
        }

        private void setScriptingVars(Node.CustomTag n, int scope) throws JasperException {
            TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
            VariableInfo[] varInfos = n.getVariableInfos();
            if (tagVarInfos.length == 0 && varInfos.length == 0) {
                return;
            }
            ArrayList<Object> vec = new ArrayList<Object>();
            Integer ownRange = null;
            Node.CustomTag parent = n.getCustomTagParent();
            ownRange = scope == 1 || scope == 2 ? (parent == null ? MAX_SCOPE : parent.getNumCount()) : n.getNumCount();
            if (varInfos.length > 0) {
                for (VariableInfo varInfo : varInfos) {
                    String varName;
                    Integer currentRange;
                    if (varInfo.getScope() != scope || !varInfo.getDeclare() || (currentRange = this.scriptVars.get(varName = varInfo.getVarName())) != null && ownRange.compareTo(currentRange) <= 0) continue;
                    this.scriptVars.put(varName, ownRange);
                    vec.add(varInfo);
                }
            } else {
                for (TagVariableInfo tagVarInfo : tagVarInfos) {
                    Integer currentRange;
                    if (tagVarInfo.getScope() != scope || !tagVarInfo.getDeclare()) continue;
                    String varName = tagVarInfo.getNameGiven();
                    if (varName == null && (varName = n.getTagData().getAttributeString(tagVarInfo.getNameFromAttribute())) == null) {
                        this.err.jspError((Node)n, "jsp.error.scripting.variable.missing_name", tagVarInfo.getNameFromAttribute());
                    }
                    if ((currentRange = this.scriptVars.get(varName)) != null && ownRange.compareTo(currentRange) <= 0) continue;
                    this.scriptVars.put(varName, ownRange);
                    vec.add(tagVarInfo);
                }
            }
            n.setScriptingVars(vec, scope);
        }
    }
}

