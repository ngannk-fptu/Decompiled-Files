/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.internal.content;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.persistence.hibernate.PluginContentHibernateInterceptor;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.storage.XhtmlCleaningInterceptor;
import com.atlassian.confluence.core.persistence.hibernate.CompositeInterceptor;
import com.atlassian.confluence.impl.content.duplicatetags.NestedDuplicateTagsRemoverInterceptor;
import com.atlassian.confluence.impl.content.duplicatetags.internal.DuplicateNestedTagsRemoverImpl;
import com.atlassian.confluence.impl.content.encoding.MySQLUTF8SupportInterceptor;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
class HibernateContentInterceptorConfig {
    @Resource
    private SingleConnectionProvider databaseHelper;
    @Resource
    private HibernateConfig hibernateConfig;
    @Resource
    private StorageFormatCleaner storageFormatCleaner;
    @Lazy
    @Resource
    private FullReindexManager fullReindexManager;
    @Resource
    private XmlOutputFactory xmlOutputFactory;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Lazy
    @Resource
    private ContentTypeManager contentTypeManager;

    HibernateContentInterceptorConfig() {
    }

    @Bean
    CompositeInterceptor hibernateContentInterceptors() {
        return new CompositeInterceptor(List.of(new MySQLUTF8SupportInterceptor(this.databaseHelper, this.hibernateConfig), new XhtmlCleaningInterceptor(this.storageFormatCleaner, () -> this.fullReindexManager.isReIndexing()), new NestedDuplicateTagsRemoverInterceptor(new DuplicateNestedTagsRemoverImpl(this.xmlOutputFactory, this.xmlEventReaderFactory)), new PluginContentHibernateInterceptor(this.contentTypeManager)));
    }
}

