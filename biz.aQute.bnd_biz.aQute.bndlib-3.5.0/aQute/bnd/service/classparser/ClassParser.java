/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.classparser;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.ClassDataCollector;

public interface ClassParser {
    public ClassDataCollector getClassDataCollector(Analyzer var1);
}

