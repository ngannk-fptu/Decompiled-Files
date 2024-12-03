/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.ApiEnum;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalApi
public class SubjectType
implements ApiEnum {
    public static final SubjectType USER = new SubjectType("user");
    public static final SubjectType GROUP = new SubjectType("group");
    public static final Set<SubjectType> VALUES = Stream.of(USER, GROUP).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
    private String name;

    private SubjectType() {
    }

    private SubjectType(String name) {
        this.name = name;
    }

    @JsonCreator
    public static SubjectType valueOf(String name) {
        if (SubjectType.USER.name.equals(name)) {
            return USER;
        }
        if (SubjectType.GROUP.name.equals(name)) {
            return GROUP;
        }
        return new SubjectType(name);
    }

    public static SubjectType[] values() {
        return VALUES.toArray(new SubjectType[0]);
    }

    @JsonValue
    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        return obj instanceof SubjectType && this.name.equals(((SubjectType)obj).name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String serialise() {
        return this.toString();
    }

    public static final class Expansions {
        public static final String USERS = USER.toString();
        public static final String GROUPS = GROUP.toString();
    }
}

