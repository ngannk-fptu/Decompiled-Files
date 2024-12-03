/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.Collections;
import org.apache.tools.ant.types.selectors.BaseSelectorContainer;
import org.apache.tools.ant.types.selectors.FileSelector;

public class MajoritySelector
extends BaseSelectorContainer {
    private boolean allowtie = true;

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.hasSelectors()) {
            buf.append("{majorityselect: ");
            buf.append(super.toString());
            buf.append("}");
        }
        return buf.toString();
    }

    public void setAllowtie(boolean tiebreaker) {
        this.allowtie = tiebreaker;
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        int yesvotes = 0;
        int novotes = 0;
        for (FileSelector fs : Collections.list(this.selectorElements())) {
            if (fs.isSelected(basedir, filename, file)) {
                ++yesvotes;
                continue;
            }
            ++novotes;
        }
        if (yesvotes > novotes) {
            return true;
        }
        if (novotes > yesvotes) {
            return false;
        }
        return this.allowtie;
    }
}

