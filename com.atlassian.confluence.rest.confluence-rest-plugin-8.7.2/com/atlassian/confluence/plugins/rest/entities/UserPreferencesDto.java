/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public final class UserPreferencesDto {
    @XmlElement
    private final boolean watchOwnContent;

    public UserPreferencesDto() {
        this(false);
    }

    public UserPreferencesDto(boolean watchOwnContent) {
        this.watchOwnContent = watchOwnContent;
    }

    public boolean isWatchOwnContent() {
        return this.watchOwnContent;
    }

    public String toString() {
        return new StringJoiner(", ", UserPreferencesDto.class.getSimpleName() + "[", "]").add("watchOwnContent=" + this.watchOwnContent).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPreferencesDto)) {
            return false;
        }
        UserPreferencesDto that = (UserPreferencesDto)o;
        return this.watchOwnContent == that.watchOwnContent;
    }

    public int hashCode() {
        return Objects.hash(this.watchOwnContent);
    }
}

