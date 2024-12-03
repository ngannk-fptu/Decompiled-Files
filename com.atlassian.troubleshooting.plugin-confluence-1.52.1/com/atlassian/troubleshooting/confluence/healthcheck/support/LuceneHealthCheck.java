/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.ILuceneConnection
 *  com.atlassian.confluence.search.v2.SearchIndexAccessor
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.support;

import com.atlassian.bonnie.ILuceneConnection;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;

public final class LuceneHealthCheck
implements SupportHealthCheck {
    private final SupportHealthStatusBuilder builder;

    @Autowired
    public LuceneHealthCheck(SupportHealthStatusBuilder builder) {
        this.builder = builder;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        ContainerContext containerContext = ContainerManager.getInstance().getContainerContext();
        try {
            ILuceneConnection luceneConnection = (ILuceneConnection)containerContext.getComponent((Object)"luceneConnection");
            luceneConnection.getNumDocs();
            return this.builder.ok(this, "confluence.healthcheck.lucene.ok", new Serializable[0]);
        }
        catch (ComponentNotFoundException | NoClassDefFoundError luceneConnectionNotFoundException) {
            try {
                SearchIndexAccessor searchIndexAccessor = (SearchIndexAccessor)containerContext.getComponent((Object)"contentSearchIndexAccessor");
                searchIndexAccessor.numDocs();
                return this.builder.ok(this, "confluence.healthcheck.lucene.ok", new Serializable[0]);
            }
            catch (Exception exception) {
                return this.builder.warning(this, "confluence.healthcheck.lucene.warning", new Serializable[]{exception.getLocalizedMessage()});
            }
        }
        catch (Exception e) {
            return this.builder.warning(this, "confluence.healthcheck.lucene.warning", new Serializable[]{e.getLocalizedMessage()});
        }
    }
}

