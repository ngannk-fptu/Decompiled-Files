/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.pfl.tf.timer.spi;

import org.glassfish.pfl.tf.timer.spi.Named;
import org.glassfish.pfl.tf.timer.spi.ObjectRegistrationManager;

public class ObjectRegistrationManagerNOPImpl
implements ObjectRegistrationManager {
    @Override
    public void manage(Named obj) {
    }

    @Override
    public void manage(Named parent, Named obj) {
    }

    @Override
    public void unmanage(Named obj) {
    }
}

