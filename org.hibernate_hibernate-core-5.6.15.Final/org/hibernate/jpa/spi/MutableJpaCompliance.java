/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.spi;

import org.hibernate.jpa.spi.JpaCompliance;

public interface MutableJpaCompliance
extends JpaCompliance {
    public void setQueryCompliance(boolean var1);

    public void setTransactionCompliance(boolean var1);

    public void setListCompliance(boolean var1);

    public void setClosedCompliance(boolean var1);

    public void setProxyCompliance(boolean var1);

    public void setCachingCompliance(boolean var1);

    public JpaCompliance immutableCopy();
}

