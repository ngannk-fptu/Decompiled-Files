/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.exception.ConstraintViolationException
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.DefaultImportProcessor;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessor;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.InputStreamFactory;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.XmlImporter;
import com.atlassian.confluence.importexport.xmlimport.parser.BackupParser;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.security.xml.SecureXmlParserFactory;
import java.io.IOException;
import java.util.function.Supplier;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

@Deprecated
public class DefaultXmlImporter
implements XmlImporter {
    private final ObjectPersisterFactory objectPersisterFactory;
    private final SessionFactory sessionFactory;

    public DefaultXmlImporter(ObjectPersisterFactory objectPersisterFactory, SessionFactory sessionFactory) {
        this.objectPersisterFactory = objectPersisterFactory;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ImportProcessorSummary doImport(Session session, InputStreamFactory xmlStreamFactory, ProgressMeter meter, boolean incrementalImport, ImportedObjectPreProcessor preProcessor) throws ImportExportException {
        Supplier<ImportProcessorContext> processorContextSupplier = this.getImportProcessorContextSupplier((SessionImplementor)session, meter, incrementalImport, preProcessor);
        return this.doImportInternal(session, xmlStreamFactory, meter, processorContextSupplier);
    }

    @Override
    public ImportProcessorSummary doImport(Session session, InputStreamFactory xmlImportStreamFactory, boolean incrementalImport, ImportContext context) throws ImportExportException {
        Supplier<ImportProcessorContext> processorContextSupplier = this.getImportProcessorContextSupplier((SessionImplementor)session, incrementalImport, context);
        return this.doImportInternal(session, xmlImportStreamFactory, context.getProgressMeter(), processorContextSupplier);
    }

    private ImportProcessorSummary doImportInternal(Session session, InputStreamFactory xmlStreamFactory, ProgressMeter meter, Supplier<ImportProcessorContext> processorContextSupplier) throws ImportExportException {
        try {
            this.initProgressMeter(xmlStreamFactory, meter);
            ImportProcessorContext processorContext = processorContextSupplier.get();
            DefaultImportProcessor importProcessor = new DefaultImportProcessor(this.objectPersisterFactory, processorContext);
            this.parseBackup(xmlStreamFactory, importProcessor);
            session.flush();
            session.clear();
            processorContext.reportIncompleteDefferredOperations();
            return processorContext;
        }
        catch (SAXException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConstraintViolationException) {
                String rootCause = ExceptionUtils.getRootCauseMessage((Throwable)e);
                throw new ImportExportException("Unable to complete import because the data does not match the constraints in the Confluence schema. Cause: " + rootCause, e);
            }
            throw new ImportExportException("Unable to complete import: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new ImportExportException("Unable to complete import: " + e.getMessage(), e);
        }
    }

    private void initProgressMeter(InputStreamFactory xmlStreamFactory, ProgressMeter meter) throws ImportExportException, IOException, SAXException {
        int[] counter = new int[]{0};
        meter.setStatus("Counting objects to import");
        this.parseBackup(xmlStreamFactory, object -> {
            counter[0] = counter[0] + 1;
        });
        meter.setTotalObjects(counter[0] * 2);
        meter.setStatus("Importing objects");
    }

    private void parseBackup(InputStreamFactory xmlStreamFactory, ImportProcessor importProcessor) throws SAXException, IOException, ImportExportException {
        XMLReader reader = SecureXmlParserFactory.newXmlReader();
        reader.setContentHandler(new BackupParser(importProcessor));
        reader.parse(new InputSource(xmlStreamFactory.newInputStream()));
    }

    private Supplier<ImportProcessorContext> getImportProcessorContextSupplier(SessionImplementor session, ProgressMeter meter, boolean incrementalImport, ImportedObjectPreProcessor preProcessor) {
        return () -> {
            try {
                return new ImportProcessorContext(session, (SessionFactoryImplementor)this.sessionFactory, meter, !incrementalImport, preProcessor);
            }
            catch (HibernateException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Supplier<ImportProcessorContext> getImportProcessorContextSupplier(SessionImplementor session, boolean incrementalImport, ImportContext context) {
        return () -> {
            try {
                return new ImportProcessorContext(session, (SessionFactoryImplementor)this.sessionFactory, !incrementalImport, context);
            }
            catch (HibernateException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

