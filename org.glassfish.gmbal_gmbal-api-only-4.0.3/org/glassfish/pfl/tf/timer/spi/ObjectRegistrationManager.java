/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.pfl.tf.timer.spi;

import org.glassfish.pfl.tf.timer.spi.Named;
import org.glassfish.pfl.tf.timer.spi.ObjectRegistrationManagerNOPImpl;

public interface ObjectRegistrationManager {
    public static final ObjectRegistrationManager nullImpl = new ObjectRegistrationManagerNOPImpl();

    public void manage(Named var1);

    public void manage(Named var1, Named var2);

    public void unmanage(Named var1);
}

