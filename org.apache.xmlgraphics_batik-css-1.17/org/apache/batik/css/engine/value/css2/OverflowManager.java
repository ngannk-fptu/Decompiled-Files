/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;

public class OverflowManager
extends IdentifierManager {
    protected static final StringMap values = new StringMap();

    @Override
    public boolean isInheritedProperty() {
        return false;
    }

    @Override
    public boolean isAnimatableProperty() {
        return true;
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
        return "overflow";
    }

    @Override
    public Value getDefaultValue() {
        return ValueConstants.VISIBLE_VALUE;
    }

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("auto", ValueConstants.AUTO_VALUE);
        values.put("hidden", ValueConstants.HIDDEN_VALUE);
        values.put("scroll", ValueConstants.SCROLL_VALUE);
        values.put("visible", ValueConstants.VISIBLE_VALUE);
    }
}

