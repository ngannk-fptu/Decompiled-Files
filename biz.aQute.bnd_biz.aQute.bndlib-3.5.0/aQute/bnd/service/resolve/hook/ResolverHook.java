/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.resolve.hook;

import java.util.List;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

public interface ResolverHook {
    public void filterMatches(Requirement var1, List<Capability> var2);
}

