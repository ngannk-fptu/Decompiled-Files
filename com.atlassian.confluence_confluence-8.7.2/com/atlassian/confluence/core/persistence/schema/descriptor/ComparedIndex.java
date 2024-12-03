/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.google.common.base.Preconditions
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import com.atlassian.confluence.core.persistence.schema.api.SchemaElementComparison;
import com.atlassian.confluence.core.persistence.schema.descriptor.AbstractDescriptorComparison;
import com.atlassian.confluence.core.persistence.schema.descriptor.IndexDescriptor;
import com.atlassian.fugue.Maybe;
import com.google.common.base.Preconditions;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ComparedIndex
extends AbstractDescriptorComparison<IndexDescriptor>
implements SchemaElementComparison.IndexComparison {
    private String indexName;

    public ComparedIndex(String indexName, Maybe<IndexDescriptor> expected, Maybe<IndexDescriptor> actual) {
        super(expected, actual);
        this.indexName = ((String)Preconditions.checkNotNull((Object)indexName)).toLowerCase();
    }

    @Override
    public String getIndexName() {
        return this.indexName;
    }
}

