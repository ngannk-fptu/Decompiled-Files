/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.util.GlobPatternMapper;

public class UnPackageNameMapper
extends GlobPatternMapper {
    @Override
    protected String extractVariablePart(String name) {
        String var = name.substring(this.prefixLength, name.length() - this.postfixLength);
        return var.replace('.', File.separatorChar);
    }
}

