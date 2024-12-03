/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public interface FileSelector
extends ResourceSelector {
    public boolean isSelected(File var1, String var2, File var3) throws BuildException;

    @Override
    default public boolean isSelected(Resource r) {
        return r.asOptional(FileProvider.class).map(FileProvider::getFile).map(f -> this.isSelected(null, null, (File)f)).orElse(false);
    }
}

