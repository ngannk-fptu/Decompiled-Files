/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.service.reporter.Reporter;
import java.util.Map;

public interface Plugin {
    public void setProperties(Map<String, String> var1) throws Exception;

    public void setReporter(Reporter var1);
}

