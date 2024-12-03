/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

@Deprecated
public interface Manageable {
    default public String getManagementDomain() {
        return null;
    }

    default public String getManagementServiceType() {
        return null;
    }

    default public Object getManagementBean() {
        return this;
    }
}

