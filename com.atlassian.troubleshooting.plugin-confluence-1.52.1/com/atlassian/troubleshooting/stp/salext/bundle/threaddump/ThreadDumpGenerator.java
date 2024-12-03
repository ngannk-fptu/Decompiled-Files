/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.salext.bundle.threaddump;

import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.io.IOException;
import java.io.OutputStream;

public interface ThreadDumpGenerator {
    public void generateThreadDump(OutputStream var1, SupportApplicationInfo var2) throws IOException;
}

