/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;

public class FontStyleManager
extends IdentifierManager {
    protected static final StringMap values = new StringMap();

    @Override
    public boolean isInheritedProperty() {
        return true;
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
        return "font-style";
    }

    @Override
    public Value getDefaultValue() {
        return ValueConstants.NORMAL_VALUE;
    }

    @Override
    public StringMap getIdentifiers() {
        return values;
    }

    static {
        values.put("all", ValueConstants.ALL_VALUE);
        values.put("italic", ValueConstants.ITALIC_VALUE);
        values.put("normal", ValueConstants.NORMAL_VALUE);
        values.put("oblique", ValueConstants.OBLIQUE_VALUE);
    }
}

