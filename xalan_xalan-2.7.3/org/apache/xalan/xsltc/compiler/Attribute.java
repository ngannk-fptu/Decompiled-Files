/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Attribute
extends Instruction {
    private QName _name;

    Attribute() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("Attribute " + this._name);
        this.displayContents(indent + 4);
    }

    @Override
    public void parseContents(Parser parser) {
        this._name = parser.getQName(this.getAttribute("name"));
        this.parseChildren(parser);
    }
}

