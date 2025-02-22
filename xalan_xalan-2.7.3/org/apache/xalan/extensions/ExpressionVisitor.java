/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.functions.FuncExtFunctionAvailable;
import org.apache.xpath.functions.Function;

public class ExpressionVisitor
extends XPathVisitor {
    private StylesheetRoot m_sroot;

    public ExpressionVisitor(StylesheetRoot sroot) {
        this.m_sroot = sroot;
    }

    @Override
    public boolean visitFunction(ExpressionOwner owner, Function func) {
        String arg;
        if (func instanceof FuncExtFunction) {
            String namespace = ((FuncExtFunction)func).getNamespace();
            this.m_sroot.getExtensionNamespacesManager().registerExtension(namespace);
        } else if (func instanceof FuncExtFunctionAvailable && (arg = ((FuncExtFunctionAvailable)func).getArg0().toString()).indexOf(":") > 0) {
            String prefix = arg.substring(0, arg.indexOf(":"));
            String namespace = this.m_sroot.getNamespaceForPrefix(prefix);
            this.m_sroot.getExtensionNamespacesManager().registerExtension(namespace);
        }
        return true;
    }
}

