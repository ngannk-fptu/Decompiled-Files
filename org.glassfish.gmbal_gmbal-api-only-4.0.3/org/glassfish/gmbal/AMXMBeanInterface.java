/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.util.Map;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;

@ManagedObject
@Description(value="Base interface for any MBean that works in the AMX framework")
public interface AMXMBeanInterface {
    public Map<String, ?> getMeta();

    @ManagedAttribute(id="Name")
    @Description(value="Return the name of this MBean.")
    public String getName();

    @ManagedAttribute(id="Parent")
    @Description(value="The container that contains this MBean")
    public AMXMBeanInterface getParent();

    @ManagedAttribute(id="Children")
    @Description(value="All children of this AMX MBean")
    public AMXMBeanInterface[] getChildren();
}

