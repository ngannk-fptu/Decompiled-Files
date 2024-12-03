/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.plugins.rest.common.expand.parameter;

import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.parameter.ExpandParameter;
import com.atlassian.plugins.rest.common.expand.parameter.IndexException;
import com.atlassian.plugins.rest.common.expand.parameter.IndexParser;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

class ChainingExpandParameter
implements ExpandParameter {
    private final Collection<ExpandParameter> expandParameters;

    ChainingExpandParameter(ExpandParameter ... expandParameters) {
        this(Arrays.asList(expandParameters));
    }

    ChainingExpandParameter(Iterable<ExpandParameter> expandParameters) {
        this.expandParameters = ImmutableList.copyOf(Objects.requireNonNull(expandParameters));
    }

    @Override
    public boolean shouldExpand(Expandable expandable) {
        for (ExpandParameter expandParameter : this.expandParameters) {
            if (!expandParameter.shouldExpand(expandable)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Indexes getIndexes(Expandable expandable) {
        Indexes indexes = null;
        for (ExpandParameter expandParameter : this.expandParameters) {
            Indexes i = expandParameter.getIndexes(expandable);
            if (i.equals(IndexParser.ALL)) {
                return IndexParser.ALL;
            }
            if (i.equals(IndexParser.EMPTY)) continue;
            if (indexes == null) {
                indexes = i;
                continue;
            }
            throw new IndexException("Cannot merge multiple indexed expand parameters.");
        }
        return indexes != null ? indexes : IndexParser.EMPTY;
    }

    @Override
    public ExpandParameter getExpandParameter(Expandable expandable) {
        LinkedList<ExpandParameter> newExpandParameters = new LinkedList<ExpandParameter>();
        for (ExpandParameter expandParameter : this.expandParameters) {
            newExpandParameters.add(expandParameter.getExpandParameter(expandable));
        }
        return new ChainingExpandParameter(newExpandParameters);
    }

    @Override
    public boolean isEmpty() {
        for (ExpandParameter expandParameter : this.expandParameters) {
            if (expandParameter.isEmpty()) continue;
            return false;
        }
        return true;
    }
}

