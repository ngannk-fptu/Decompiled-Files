/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel.shape;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitorAcceptor;

public class ShapeDataAcceptor
implements ShapeVisitorAcceptor {
    @Override
    public boolean accept(XDGFShape shape) {
        if (shape.isDeleted()) {
            return false;
        }
        if (shape.hasText() && shape.getTextAsString().length() != 0) {
            return true;
        }
        if (shape.isShape1D()) {
            return true;
        }
        if (!shape.hasMaster() && !shape.hasMasterShape()) {
            return true;
        }
        if (shape.hasMaster() && !shape.hasMasterShape()) {
            return true;
        }
        return shape.hasMasterShape() && shape.getMasterShape().isTopmost();
    }
}

