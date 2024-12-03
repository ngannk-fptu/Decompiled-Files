/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.hbm2ddl.SingleLineSqlCommandExtractor;

public class ImportSqlCommandExtractorInitiator
implements StandardServiceInitiator<ImportSqlCommandExtractor> {
    public static final ImportSqlCommandExtractorInitiator INSTANCE = new ImportSqlCommandExtractorInitiator();
    public static final ImportSqlCommandExtractor DEFAULT_EXTRACTOR = new SingleLineSqlCommandExtractor();

    @Override
    public ImportSqlCommandExtractor initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        String extractorClassName = (String)configurationValues.get("hibernate.hbm2ddl.import_files_sql_extractor");
        if (StringHelper.isEmpty(extractorClassName)) {
            return DEFAULT_EXTRACTOR;
        }
        ClassLoaderService classLoaderService = registry.getService(ClassLoaderService.class);
        return this.instantiateExplicitCommandExtractor(extractorClassName, classLoaderService);
    }

    private ImportSqlCommandExtractor instantiateExplicitCommandExtractor(String extractorClassName, ClassLoaderService classLoaderService) {
        try {
            return (ImportSqlCommandExtractor)classLoaderService.classForName(extractorClassName).newInstance();
        }
        catch (Exception e) {
            throw new HibernateException("Could not instantiate import sql command extractor [" + extractorClassName + "]", e);
        }
    }

    @Override
    public Class<ImportSqlCommandExtractor> getServiceInitiated() {
        return ImportSqlCommandExtractor.class;
    }
}

