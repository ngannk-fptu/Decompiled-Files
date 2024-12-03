/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessor;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DefaultImportProcessor
implements ImportProcessor {
    private static final Logger log = LoggerFactory.getLogger(DefaultImportProcessor.class);
    private final ImportProcessorContext context;
    private final ObjectPersisterFactory persisterFactory;

    public DefaultImportProcessor(ObjectPersisterFactory persisterFactory, ImportProcessorContext context) {
        this.context = context;
        this.persisterFactory = persisterFactory;
    }

    @Override
    public void processObject(ImportedObject object) throws Exception {
        log.debug("Processing imported object: {}", (Object)object);
        ImportedObjectPreProcessor preProcessor = this.context.getPreProcessor();
        if (preProcessor != null) {
            object = preProcessor.process(object);
        }
        if (object != null) {
            this.persist(object);
        }
        while (this.context.hasPendingDeferredObject()) {
            this.persist(this.context.nextPendingDeferredObject());
        }
    }

    private void persist(ImportedObject object) throws Exception {
        try {
            List<TransientHibernateHandle> ids = this.persisterFactory.createPersisterFor(object).persist(this.context, object);
            for (TransientHibernateHandle id : ids) {
                this.context.objectImported(id);
            }
        }
        catch (Exception e) {
            log.error("Error while saving object: " + object);
            throw e;
        }
    }
}

