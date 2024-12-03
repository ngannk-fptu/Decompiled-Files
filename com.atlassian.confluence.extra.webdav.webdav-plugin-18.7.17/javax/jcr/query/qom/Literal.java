/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.Value;
import javax.jcr.query.qom.StaticOperand;

public interface Literal
extends StaticOperand {
    public Value getLiteralValue();
}

