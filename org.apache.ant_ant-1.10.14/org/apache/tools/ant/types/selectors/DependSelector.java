/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.selectors.MappingSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;

public class DependSelector
extends MappingSelector {
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("{dependselector targetdir: ");
        if (this.targetdir == null) {
            buf.append("NOT YET SET");
        } else {
            buf.append(this.targetdir.getName());
        }
        buf.append(" granularity: ").append(this.granularity);
        if (this.map != null) {
            buf.append(" mapper: ");
            buf.append(this.map.toString());
        } else if (this.mapperElement != null) {
            buf.append(" mapper: ");
            buf.append(this.mapperElement.toString());
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public boolean selectionTest(File srcfile, File destfile) {
        return SelectorUtils.isOutOfDate(srcfile, destfile, this.granularity);
    }
}

