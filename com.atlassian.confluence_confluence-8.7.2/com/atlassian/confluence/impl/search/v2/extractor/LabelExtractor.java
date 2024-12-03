/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.AbstractLabelableEntityObject;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.TextFieldMapping;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class LabelExtractor
implements Extractor2 {
    @Deprecated
    public static final String LABEL_FIELD = SearchFieldMappings.LABEL.getName();
    @Deprecated
    public static final String LABEL_TEXT_FIELD = SearchFieldMappings.LABEL_TEXT.getName();
    @Deprecated
    public static final String INHERITED_LABEL_FIELD = SearchFieldMappings.INHERITED_LABEL.getName();
    @Deprecated
    public static final String INHERITED_LABEL_TEXT_FIELD = SearchFieldMappings.INHERITED_LABEL_TEXT.getName();

    @Override
    public StringBuilder extractText(Object searchable) {
        StringBuilder searchableText = new StringBuilder();
        if (searchable instanceof Page || searchable instanceof BlogPost || searchable instanceof Attachment) {
            String text = ((EditableLabelable)searchable).getLabellings().stream().map(Labelling::getLabel).filter(label -> StringUtils.isNotEmpty((CharSequence)label.getDisplayTitle())).map(Label::getName).collect(Collectors.joining(" "));
            searchableText.append(text);
        }
        return searchableText;
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        Object container;
        ArrayList<FieldDescriptor> descriptors = new ArrayList<FieldDescriptor>();
        if (searchable instanceof EditableLabelable) {
            Function<Labelling, String> creationDate = labelling -> ":" + LuceneUtils.dateToString(labelling.getCreationDate());
            this.extractLabels(descriptors, ((EditableLabelable)searchable).getLabellings(), SearchFieldMappings.LABEL, SearchFieldMappings.LABEL_TEXT, creationDate);
        }
        if (searchable instanceof Contained && (container = ((Contained)searchable).getContainer()) != null) {
            this.extractLabels(descriptors, ((AbstractLabelableEntityObject)container).getLabellings(), SearchFieldMappings.INHERITED_LABEL, SearchFieldMappings.INHERITED_LABEL_TEXT, labelling -> "");
        }
        return descriptors;
    }

    private void extractLabels(List<FieldDescriptor> descriptors, List<Labelling> labellings, StringFieldMapping labelField, TextFieldMapping labelTextField, Function<Labelling, String> getCreationDate) {
        HashSet<String> visibleLabels = new HashSet<String>();
        for (Labelling labelling : labellings) {
            Label label = labelling.getLabel();
            if (StringUtils.isBlank((CharSequence)label.getDisplayTitle())) continue;
            Namespace namespace = label.getNamespace();
            if ("public".equals(namespace.getVisibility())) {
                visibleLabels.add(label.getName());
            }
            String indexedValue = Namespace.PERSONAL.equals(namespace) ? label.toStringWithOwnerPrefix() + getCreationDate.apply(labelling) : label.toStringWithNamespace();
            descriptors.add(labelField.createField(indexedValue));
        }
        descriptors.add(labelTextField.createField(StringUtils.join(visibleLabels, (String)" ")));
    }
}

