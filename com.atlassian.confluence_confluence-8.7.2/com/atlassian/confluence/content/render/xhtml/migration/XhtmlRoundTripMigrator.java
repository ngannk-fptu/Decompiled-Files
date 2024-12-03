/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.migration.AbstractExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.MigrationAware;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.google.common.base.Predicate;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;

public class XhtmlRoundTripMigrator
extends AbstractExceptionTolerantMigrator {
    private final FragmentTransformer storageRoundTripFragmentTransformer;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final Predicate<ConversionContext> migrationPerformedPredicate;

    public static XhtmlRoundTripMigrator createMigratorWithMigrationAwareTransformer(FragmentTransformer storageRoundTripFragmentTransformer, XmlEventReaderFactory xmlEventReaderFactory) {
        MigrationAware.MigrationPerformedPredicate migrationPredicate = new MigrationAware.MigrationPerformedPredicate((MigrationAware)((Object)storageRoundTripFragmentTransformer));
        return new XhtmlRoundTripMigrator(storageRoundTripFragmentTransformer, xmlEventReaderFactory, migrationPredicate);
    }

    public XhtmlRoundTripMigrator(FragmentTransformer storageRoundTripFragmentTransformer, XmlEventReaderFactory xmlEventReaderFactory, Predicate<ConversionContext> migrationPerformedPredicate) {
        this.storageRoundTripFragmentTransformer = storageRoundTripFragmentTransformer;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.migrationPerformedPredicate = migrationPerformedPredicate;
    }

    @Override
    public ExceptionTolerantMigrator.MigrationResult migrate(String inputStorageFormat, ConversionContext conversionContext) {
        if (StringUtils.isBlank((CharSequence)inputStorageFormat)) {
            return new ExceptionTolerantMigrator.MigrationResult(inputStorageFormat, false);
        }
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        try {
            XMLEventReader xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(inputStorageFormat));
            Streamable transformedStreamable = this.storageRoundTripFragmentTransformer.transform(xmlEventReader, this.storageRoundTripFragmentTransformer, conversionContext);
            String transformedContentString = Streamables.writeToString(transformedStreamable);
            boolean migrationPerformed = this.migrationPerformedPredicate.apply((Object)conversionContext);
            return new ExceptionTolerantMigrator.MigrationResult(transformedContentString, migrationPerformed);
        }
        catch (XhtmlException | XMLStreamException e) {
            exceptions.add(new RuntimeException(e));
        }
        catch (RuntimeException e) {
            exceptions.add(e);
        }
        return new ExceptionTolerantMigrator.MigrationResult(inputStorageFormat, false, exceptions);
    }
}

