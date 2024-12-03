/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.soy.renderer.CustomSoyDataMapper
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.soy.renderer.CustomSoyDataMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

@CustomSoyDataMapper(value="jackson2soy")
@ExperimentalApi
public final class ContentType
extends BaseApiEnum {
    public static final ContentType PAGE = new ContentType("page");
    public static final ContentType BLOG_POST = new ContentType("blogpost");
    public static final ContentType COMMENT = new ContentType("comment");
    public static final ContentType ATTACHMENT = new ContentType("attachment");
    public static final List<ContentType> BUILT_IN = Collections.unmodifiableList(Arrays.asList(PAGE, BLOG_POST, COMMENT, ATTACHMENT));

    @JsonCreator
    public static ContentType valueOf(String type) {
        for (ContentType contentType : BUILT_IN) {
            if (!type.equals(contentType.getType())) continue;
            return contentType;
        }
        return new ContentType(type);
    }

    public static Set<ContentType> valuesOf(Iterable<String> types) {
        return StreamSupport.stream(types.spliterator(), false).map(ContentType::valueOf).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
    }

    @JsonIgnore
    private ContentType(String type) {
        super(type);
    }

    public String getType() {
        return this.serialise();
    }
}

