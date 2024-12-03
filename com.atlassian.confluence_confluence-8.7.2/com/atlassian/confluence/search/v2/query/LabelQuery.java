/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.PrefixQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LabelQuery
implements SearchQuery {
    private static final String KEY = "label";
    private final ParsedLabelName label;
    private final String preParsedLabelName;

    public LabelQuery(String label) {
        this.preParsedLabelName = label;
        this.label = LabelParser.parse(label);
        this.makeGlobalIfNecessary();
    }

    public LabelQuery(@NonNull Label label) {
        this.preParsedLabelName = label.getName();
        this.label = LabelParser.create(label);
        this.makeGlobalIfNecessary();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.singletonList(this.label);
    }

    public String getLabelAsString() {
        if (this.label == null) {
            throw new BadRequestException("Invalid label: \"" + this.preParsedLabelName + "\"");
        }
        return LabelParser.render(this.label, true);
    }

    @Override
    public SearchQuery expand() {
        BooleanQueryBuilder builder = BooleanQuery.builder().addShould(new TermQuery(SearchFieldNames.LABEL, this.getLabelAsString()));
        if (this.getLabelAsString().startsWith("~")) {
            builder.addShould(new PrefixQuery(SearchFieldNames.LABEL, this.getLabelAsString() + ":"));
        }
        return (SearchQuery)builder.build();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        LabelQuery other = (LabelQuery)obj;
        return new EqualsBuilder().append((Object)this.label, (Object)other.label).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(117, 37).append((Object)this.label).toHashCode();
    }

    private void makeGlobalIfNecessary() {
        if (this.label == null) {
            return;
        }
        if (StringUtils.isBlank((CharSequence)this.label.getOwner()) && StringUtils.isBlank((CharSequence)this.label.getPrefix())) {
            this.label.setPrefix(Namespace.GLOBAL.getPrefix());
        }
    }
}

