/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel.shape;

import java.awt.geom.AffineTransform;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitorAcceptor;

public abstract class ShapeVisitor {
    protected ShapeVisitorAcceptor _acceptor = this.getAcceptor();

    protected ShapeVisitorAcceptor getAcceptor() {
        return shape -> !shape.isDeleted();
    }

    public void setAcceptor(ShapeVisitorAcceptor acceptor) {
        this._acceptor = acceptor;
    }

    public boolean accept(XDGFShape shape) {
        return this._acceptor.accept(shape);
    }

    public abstract void visit(XDGFShape var1, AffineTransform var2, int var3);
}

