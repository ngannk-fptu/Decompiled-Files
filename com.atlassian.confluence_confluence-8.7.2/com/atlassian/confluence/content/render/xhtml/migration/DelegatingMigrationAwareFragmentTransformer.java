/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.MigrationAware;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class DelegatingMigrationAwareFragmentTransformer
implements MigrationAware,
FragmentTransformer {
    private final MigrationAware migrationAware;
    private final FragmentTransformer fragmentTransformer;

    public DelegatingMigrationAwareFragmentTransformer(FragmentTransformer fragmentTransformer, MigrationAware migrationAware) {
        this.fragmentTransformer = fragmentTransformer;
        this.migrationAware = migrationAware;
    }

    @Override
    public boolean wasMigrationPerformed(ConversionContext conversionContext) {
        return this.migrationAware.wasMigrationPerformed(conversionContext);
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.fragmentTransformer.handles(startElementEvent, conversionContext);
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        return this.fragmentTransformer.transform(reader, mainFragmentTransformer, conversionContext);
    }
}

