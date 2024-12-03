/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class NamespaceAlias
extends TopLevelElement {
    private String sPrefix;
    private String rPrefix;

    NamespaceAlias() {
    }

    @Override
    public void parseContents(Parser parser) {
        this.sPrefix = this.getAttribute("stylesheet-prefix");
        this.rPrefix = this.getAttribute("result-prefix");
        parser.getSymbolTable().addPrefixAlias(this.sPrefix, this.rPrefix);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
    }
}

