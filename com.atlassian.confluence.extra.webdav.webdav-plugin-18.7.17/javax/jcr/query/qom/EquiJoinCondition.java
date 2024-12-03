/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.JoinCondition;

public interface EquiJoinCondition
extends JoinCondition {
    public String getSelector1Name();

    public String getProperty1Name();

    public String getSelector2Name();

    public String getProperty2Name();
}

