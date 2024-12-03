/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.CommonAST
 */
package org.hibernate.sql.ordering.antlr;

import antlr.CommonAST;
import org.hibernate.sql.ordering.antlr.Node;

public class NodeSupport
extends CommonAST
implements Node {
    @Override
    public String getDebugText() {
        return this.getText();
    }

    @Override
    public String getRenderableText() {
        return this.getText();
    }
}

