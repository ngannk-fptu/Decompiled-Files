/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw;

import org.apache.poi.sl.draw.DrawSimpleShape;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.SimpleShape;

public class DrawConnectorShape
extends DrawSimpleShape {
    public DrawConnectorShape(ConnectorShape<?, ?> shape) {
        super((SimpleShape<?, ?>)shape);
    }
}

