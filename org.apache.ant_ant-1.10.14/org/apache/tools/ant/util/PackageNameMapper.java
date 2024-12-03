/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.util.GlobPatternMapper;

public class PackageNameMapper
extends GlobPatternMapper {
    @Override
    protected String extractVariablePart(String name) {
        String var = name.substring(this.prefixLength, name.length() - this.postfixLength);
        if (this.getHandleDirSep()) {
            var = var.replace('/', '.').replace('\\', '.');
        }
        return var.replace(File.separatorChar, '.');
    }
}

