/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.Spaced;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class SpaceTypeExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof Spaced)) {
            return Collections.emptyList();
        }
        ArrayList<FieldDescriptor> descriptors = new ArrayList<FieldDescriptor>();
        Optional.ofNullable(((Spaced)searchable).getSpace()).map(Space::getSpaceType).map(SpaceType::toString).ifPresent(spaceType -> descriptors.add(SearchFieldMappings.SPACE_TYPE.createField((String)spaceType)));
        return descriptors;
    }
}

