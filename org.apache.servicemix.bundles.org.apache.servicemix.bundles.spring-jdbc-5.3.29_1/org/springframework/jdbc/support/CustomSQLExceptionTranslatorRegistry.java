/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;

public final class CustomSQLExceptionTranslatorRegistry {
    private static final Log logger = LogFactory.getLog(CustomSQLExceptionTranslatorRegistry.class);
    private static final CustomSQLExceptionTranslatorRegistry instance = new CustomSQLExceptionTranslatorRegistry();
    private final Map<String, SQLExceptionTranslator> translatorMap = new HashMap<String, SQLExceptionTranslator>();

    public static CustomSQLExceptionTranslatorRegistry getInstance() {
        return instance;
    }

    private CustomSQLExceptionTranslatorRegistry() {
    }

    public void registerTranslator(String dbName, SQLExceptionTranslator translator) {
        SQLExceptionTranslator replaced = this.translatorMap.put(dbName, translator);
        if (logger.isDebugEnabled()) {
            if (replaced != null) {
                logger.debug((Object)("Replacing custom translator [" + replaced + "] for database '" + dbName + "' with [" + translator + "]"));
            } else {
                logger.debug((Object)("Adding custom translator of type [" + translator.getClass().getName() + "] for database '" + dbName + "'"));
            }
        }
    }

    @Nullable
    public SQLExceptionTranslator findTranslatorForDatabase(String dbName) {
        return this.translatorMap.get(dbName);
    }
}

