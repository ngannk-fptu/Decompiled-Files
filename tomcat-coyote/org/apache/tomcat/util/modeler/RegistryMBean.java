/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import java.util.List;
import javax.management.ObjectName;

public interface RegistryMBean {
    public void invoke(List<ObjectName> var1, String var2, boolean var3) throws Exception;

    public void registerComponent(Object var1, String var2, String var3) throws Exception;

    public void unregisterComponent(String var1);

    public int getId(String var1, String var2);

    public void stop();
}

