/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 */
package org.springframework.jdbc.support;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.support.CustomSQLExceptionTranslatorRegistry;
import org.springframework.jdbc.support.SQLExceptionTranslator;

public class CustomSQLExceptionTranslatorRegistrar
implements InitializingBean {
    private final Map<String, SQLExceptionTranslator> translators = new HashMap<String, SQLExceptionTranslator>();

    public void setTranslators(Map<String, SQLExceptionTranslator> translators) {
        this.translators.putAll(translators);
    }

    public void afterPropertiesSet() {
        this.translators.forEach((dbName, translator) -> CustomSQLExceptionTranslatorRegistry.getInstance().registerTranslator((String)dbName, (SQLExceptionTranslator)translator));
    }
}

