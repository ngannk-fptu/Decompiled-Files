/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.spi;

import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

public interface PersistenceProvider {
    public EntityManagerFactory createEntityManagerFactory(String var1, Map var2);

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo var1, Map var2);

    public void generateSchema(PersistenceUnitInfo var1, Map var2);

    public boolean generateSchema(String var1, Map var2);

    public ProviderUtil getProviderUtil();
}

