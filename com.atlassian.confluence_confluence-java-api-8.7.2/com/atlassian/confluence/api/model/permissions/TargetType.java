/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.api.model.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.content.ContentType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

@ExperimentalApi
public final class TargetType
extends BaseApiEnum {
    public static final TargetType PAGE = new TargetType(ContentType.PAGE);
    public static final TargetType BLOG_POST = new TargetType(ContentType.BLOG_POST);
    public static final TargetType COMMENT = new TargetType(ContentType.COMMENT);
    public static final TargetType ATTACHMENT = new TargetType(ContentType.ATTACHMENT);
    public static final TargetType SPACE = new TargetType("space");
    public static final List<TargetType> BUILT_IN = Collections.unmodifiableList(Arrays.asList(PAGE, BLOG_POST, COMMENT, ATTACHMENT, SPACE));

    @JsonCreator
    public static TargetType valueOf(@NonNull String type) {
        return new TargetType(type);
    }

    @JsonIgnore
    public static TargetType valueOf(@NonNull ContentType type) {
        return new TargetType(type);
    }

    public static Set<TargetType> valuesOf(@NonNull Iterable<String> types) {
        return StreamSupport.stream(types.spliterator(), false).map(TargetType::valueOf).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
    }

    @JsonIgnore
    private TargetType(String type) {
        super(Objects.requireNonNull(type));
    }

    @JsonIgnore
    private TargetType(ContentType contentType) {
        super(Objects.requireNonNull(contentType.getValue()));
    }

    public @NonNull String getType() {
        return this.serialise();
    }
}

