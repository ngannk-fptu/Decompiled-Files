/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagVariableInfo
 *  javax.servlet.jsp.tagext.VariableInfo
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;

class ScriptingVariabler {
    private static final Integer MAX_SCOPE = new Integer(Integer.MAX_VALUE);

    ScriptingVariabler() {
    }

    public static void set(Node.Nodes page, ErrorDispatcher err) throws JasperException {
        page.visit(new CustomTagCounter());
        page.visit(new ScriptingVariableVisitor(err));
    }

    static class ScriptingVariableVisitor
    extends Node.Visitor {
        private ErrorDispatcher err;
        private Hashtable scriptVars;

        public ScriptingVariableVisitor(ErrorDispatcher err) {
            this.err = err;
            this.scriptVars = new Hashtable();
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            this.setScriptingVars(n, 1);
            this.setScriptingVars(n, 0);
            this.visitBody(n);
            this.setScriptingVars(n, 2);
        }

        private void setScriptingVars(Node.CustomTag n, int scope) throws JasperException {
            Node.CustomTag parent;
            TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
            VariableInfo[] varInfos = n.getVariableInfos();
            if (tagVarInfos.length == 0 && varInfos.length == 0) {
                return;
            }
            Vector<Object> vec = new Vector<Object>();
            Integer ownRange = null;
            ownRange = scope == 1 || scope == 2 ? ((parent = n.getCustomTagParent()) == null ? MAX_SCOPE : parent.getNumCount()) : n.getNumCount();
            if (varInfos.length > 0) {
                for (int i = 0; i < varInfos.length; ++i) {
                    String varName;
                    Integer currentRange;
                    if (varInfos[i].getScope() != scope || !varInfos[i].getDeclare() || (currentRange = (Integer)this.scriptVars.get(varName = varInfos[i].getVarName())) != null && ownRange.compareTo(currentRange) <= 0) continue;
                    this.scriptVars.put(varName, ownRange);
                    vec.add(varInfos[i]);
                }
            } else {
                for (int i = 0; i < tagVarInfos.length; ++i) {
                    Integer currentRange;
                    if (tagVarInfos[i].getScope() != scope || !tagVarInfos[i].getDeclare()) continue;
                    String varName = tagVarInfos[i].getNameGiven();
                    if (varName == null && (varName = n.getTagData().getAttributeString(tagVarInfos[i].getNameFromAttribute())) == null) {
                        this.err.jspError(n, "jsp.error.scripting.variable.missing_name", tagVarInfos[i].getNameFromAttribute());
                    }
                    if ((currentRange = (Integer)this.scriptVars.get(varName)) != null && ownRange.compareTo(currentRange) <= 0) continue;
                    this.scriptVars.put(varName, ownRange);
                    vec.add(tagVarInfos[i]);
                }
            }
            n.setScriptingVars(vec, scope);
        }
    }

    static class CustomTagCounter
    extends Node.Visitor {
        private int count;
        private Node.CustomTag parent;

        CustomTagCounter() {
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            n.setCustomTagParent(this.parent);
            Node.CustomTag tmpParent = this.parent;
            this.parent = n;
            this.visitBody(n);
            this.parent = tmpParent;
            n.setNumCount(new Integer(this.count++));
        }
    }
}

