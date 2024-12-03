/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.io.Serializable;
import javax.media.jai.OperationGraph;
import javax.media.jai.PartialOrderNode;

final class ProductOperationGraph
extends OperationGraph
implements Serializable {
    ProductOperationGraph() {
        super(true);
    }

    void addProduct(String productName) {
        this.addOp(new PartialOrderNode(new OperationGraph(), productName));
    }
}

