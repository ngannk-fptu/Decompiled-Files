/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.release;

import aQute.bnd.build.Project;

public interface ReleaseBracketingPlugin {
    public void begin(Project var1);

    public void end(Project var1);
}

