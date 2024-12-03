/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import java.math.BigInteger;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class BigIntLiteral
extends AstNode {
    private String value;
    private BigInteger bigInt;

    public BigIntLiteral() {
        this.type = 83;
    }

    public BigIntLiteral(int pos) {
        super(pos);
        this.type = 83;
    }

    public BigIntLiteral(int pos, int len) {
        super(pos, len);
        this.type = 83;
    }

    public BigIntLiteral(int pos, String value) {
        super(pos);
        this.type = 83;
        this.setValue(value);
        this.setLength(value.length());
    }

    public BigIntLiteral(int pos, String value, BigInteger bigInt) {
        this(pos, value);
        this.setBigInt(bigInt);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.assertNotNull(value);
        this.value = value;
    }

    @Override
    public BigInteger getBigInt() {
        return this.bigInt;
    }

    @Override
    public void setBigInt(BigInteger value) {
        this.bigInt = value;
    }

    @Override
    public String toSource(int depth) {
        return this.makeIndent(depth) + (this.bigInt == null ? "<null>" : this.bigInt.toString());
    }

    @Override
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}

