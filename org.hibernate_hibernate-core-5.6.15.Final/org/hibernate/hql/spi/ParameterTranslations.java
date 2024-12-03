/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi;

import java.util.Map;
import org.hibernate.hql.spi.NamedParameterInformation;
import org.hibernate.hql.spi.PositionalParameterInformation;

public interface ParameterTranslations {
    public Map<String, NamedParameterInformation> getNamedParameterInformationMap();

    public Map<Integer, PositionalParameterInformation> getPositionalParameterInformationMap();

    public PositionalParameterInformation getPositionalParameterInformation(int var1);

    public NamedParameterInformation getNamedParameterInformation(String var1);
}

