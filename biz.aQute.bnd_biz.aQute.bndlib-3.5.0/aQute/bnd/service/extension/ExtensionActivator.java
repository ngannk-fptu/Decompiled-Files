/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.extension;

import aQute.bnd.build.Workspace;
import java.util.List;
import java.util.Map;

public interface ExtensionActivator {
    public List<?> activate(Workspace var1, Map<String, String> var2);

    public void deactivate();
}

