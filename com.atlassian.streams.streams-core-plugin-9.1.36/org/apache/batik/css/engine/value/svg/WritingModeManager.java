/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;

public class WritingModeManager
extends IdentifierManager {
    protected static final StringMap values = new StringMap();

    @Override
    public boolean isInheritedProperty() {
        return true;
    }

    @Override
    public boolean isAnimatableProperty() {
        return false;
    }

    @Override
    public boolean isAdditiveProperty() {
        return false;
    }

    @Override
    public int getPropertyType() {
        return 15;
    }

    @Override
    public String getPropertyName() {
        return "writing-mode";
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.LR_TB_VALUE;
    }

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("lr", SVGValueConstants.LR_VALUE);
        values.put("lr-tb", SVGValueConstants.LR_TB_VALUE);
        values.put("rl", SVGValueConstants.RL_VALUE);
        values.put("rl-tb", SVGValueConstants.RL_TB_VALUE);
        values.put("tb", SVGValueConstants.TB_VALUE);
        values.put("tb-rl", SVGValueConstants.TB_RL_VALUE);
    }
}

