/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.modeler.modules;

import java.util.List;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public abstract class ModelerSource {
    protected static final StringManager sm = StringManager.getManager(Registry.class);
    protected Object source;

    public abstract List<ObjectName> loadDescriptors(Registry var1, String var2, Object var3) throws Exception;
}

