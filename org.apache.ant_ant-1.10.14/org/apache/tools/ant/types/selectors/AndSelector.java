/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.stream.Stream;
import org.apache.tools.ant.types.selectors.BaseSelectorContainer;

public class AndSelector
extends BaseSelectorContainer {
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.hasSelectors()) {
            buf.append("{andselect: ");
            buf.append(super.toString());
            buf.append("}");
        }
        return buf.toString();
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        return Stream.of(this.getSelectors(this.getProject())).allMatch(s -> s.isSelected(basedir, filename, file));
    }
}

