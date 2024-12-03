/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.export;

import aQute.bnd.build.Project;
import aQute.bnd.osgi.Resource;
import java.util.Map;

public interface Exporter {
    public String[] getTypes();

    public Map.Entry<String, Resource> export(String var1, Project var2, Map<String, String> var3) throws Exception;
}

