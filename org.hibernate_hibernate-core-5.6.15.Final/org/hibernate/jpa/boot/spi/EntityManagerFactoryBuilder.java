/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 */
package org.hibernate.jpa.boot.spi;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public interface EntityManagerFactoryBuilder {
    public EntityManagerFactoryBuilder withValidatorFactory(Object var1);

    public EntityManagerFactoryBuilder withDataSource(DataSource var1);

    public EntityManagerFactory build();

    public void cancel();

    public void generateSchema();
}

