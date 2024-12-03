/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;

public final class NodeSortRecordFactGenerator
extends ClassGenerator {
    public NodeSortRecordFactGenerator(String className, String superClassName, String fileName, int accessFlags, String[] interfaces, Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
    }

    @Override
    public boolean isExternal() {
        return true;
    }
}

