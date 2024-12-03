/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;

public interface Context {
    public void writeNameClass(NameClass var1);

    public String getTargetNamespace();

    public XMLWriter getWriter();
}

