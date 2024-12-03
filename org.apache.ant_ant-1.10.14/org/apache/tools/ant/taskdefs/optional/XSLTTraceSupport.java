/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import javax.xml.transform.Transformer;
import org.apache.tools.ant.taskdefs.XSLTProcess;

public interface XSLTTraceSupport {
    public void configureTrace(Transformer var1, XSLTProcess.TraceConfiguration var2);
}

