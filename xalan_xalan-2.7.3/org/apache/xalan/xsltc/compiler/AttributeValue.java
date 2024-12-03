/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.AttributeValueTemplate;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SimpleAttributeValue;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;

abstract class AttributeValue
extends Expression {
    AttributeValue() {
    }

    public static final AttributeValue create(SyntaxTreeNode parent, String text, Parser parser) {
        AttributeValue result;
        if (text.indexOf(123) != -1) {
            result = new AttributeValueTemplate(text, parser, parent);
        } else if (text.indexOf(125) != -1) {
            result = new AttributeValueTemplate(text, parser, parent);
        } else {
            result = new SimpleAttributeValue(text);
            result.setParser(parser);
            result.setParent(parent);
        }
        return result;
    }
}

