/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchableAttachment
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.index.api.ExactFilenameAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StoredFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.MappingDeconflictDarkFeature;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchableAttachment;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.PersonalInformation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

public class TitleExtractor
implements Extractor2 {
    private final MappingDeconflictDarkFeature deconflictDarkFeature;

    public TitleExtractor(MappingDeconflictDarkFeature deconflictDarkFeature) {
        this.deconflictDarkFeature = deconflictDarkFeature;
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof EditableLabelable)) {
            return Collections.emptyList();
        }
        boolean deconflict = this.deconflictDarkFeature.isEnabled();
        String titleText = searchable instanceof Comment || searchable instanceof CustomContentEntityObject || searchable instanceof PersonalInformation || searchable instanceof SpaceDescription ? ((Addressable)searchable).getDisplayTitle() : (searchable instanceof Attachment ? ((SearchableAttachment)searchable).getFileName() : ((EditableLabelable)searchable).getTitle());
        if (StringUtils.isBlank((CharSequence)titleText)) {
            return Collections.emptyList();
        }
        ArrayList<FieldDescriptor> descriptors = new ArrayList<FieldDescriptor>();
        if (searchable instanceof Comment) {
            if (deconflict) {
                descriptors.add(SearchFieldMappings.DISPLAY_TITLE.createField(titleText));
            } else {
                descriptors.add(new StoredFieldDescriptor(SearchFieldMappings.TITLE.getName(), titleText));
            }
        } else {
            descriptors.add(SearchFieldMappings.TITLE.createField(titleText));
            if (searchable instanceof Attachment) {
                if (deconflict) {
                    descriptors.add(SearchFieldMappings.EXACT_FILENAME.createField(titleText));
                } else {
                    descriptors.add(new TextFieldDescriptor(SearchFieldMappings.EXACT_TITLE.getName(), titleText, FieldDescriptor.Store.YES, new ExactFilenameAnalyzerDescriptor()));
                }
            } else {
                descriptors.add(SearchFieldMappings.EXACT_TITLE.createField(titleText));
            }
        }
        return descriptors;
    }
}

