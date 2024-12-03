/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.api.model.content.template;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

@ExperimentalApi
public final class ContentTemplateType
extends BaseApiEnum {
    public static final ContentTemplateType PAGE = new ContentTemplateType("page");
    public static final ContentTemplateType BLUEPRINT = new ContentTemplateType("blueprint");
    private static final ContentTemplateType[] BUILT_IN = new ContentTemplateType[]{PAGE, BLUEPRINT};

    @JsonIgnore
    private ContentTemplateType(String type) {
        super(type);
    }

    @JsonCreator
    public static ContentTemplateType valueOf(String type) {
        for (ContentTemplateType templateType : BUILT_IN) {
            if (!type.toLowerCase().equals(templateType.getType())) continue;
            return templateType;
        }
        return new ContentTemplateType(type);
    }

    public String getType() {
        return this.getValue();
    }

    @Override
    public String toString() {
        return "ContentTemplateType{value='" + this.value + '\'' + '}';
    }
}

