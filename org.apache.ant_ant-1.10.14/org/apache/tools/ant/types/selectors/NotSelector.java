/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.NoneSelector;

public class NotSelector
extends NoneSelector {
    public NotSelector() {
    }

    public NotSelector(FileSelector other) {
        this();
        this.appendSelector(other);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.hasSelectors()) {
            buf.append("{notselect: ");
            buf.append(super.toString());
            buf.append("}");
        }
        return buf.toString();
    }

    @Override
    public void verifySettings() {
        if (this.selectorCount() != 1) {
            this.setError("One and only one selector is allowed within the <not> tag");
        }
    }
}

