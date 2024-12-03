/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.dto;

import com.atlassian.confluence.plugins.rest.entities.UserPreferencesDto;
import com.google.errorprone.annotations.Immutable;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public final class UserDto {
    @XmlElement
    private final String userName;
    @XmlElement
    private final String fullName;
    @XmlElement
    private final String avatarUrl;
    @XmlElement
    private final String url;
    @XmlElement
    private final String phone;
    @XmlElement
    private final String email;
    @XmlElement
    private final String position;
    @XmlElement
    private final String department;
    @XmlElement
    private final UserPreferencesDto userPreferences;
    @XmlElement(name="anonymous")
    private final boolean anonymousUser;
    @XmlElement
    private final boolean unknownUser;
    @XmlElement
    private final String location;
    @XmlElement
    private final String about;

    public UserDto(String userName, String fullName, String avatarUrl, String url, String phone, String email, String position, String department, String location, String about, UserPreferencesDto userPreferencesDto, boolean anonymousUser, boolean unknownUser) {
        this.userName = userName;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.url = url;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.department = department;
        this.location = location;
        this.about = about;
        this.userPreferences = userPreferencesDto;
        this.anonymousUser = anonymousUser;
        this.unknownUser = unknownUser;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getPosition() {
        return this.position;
    }

    public String getDepartment() {
        return this.department;
    }

    public String getLocation() {
        return this.location;
    }

    public String getAbout() {
        return this.about;
    }

    public UserPreferencesDto getUserPreferences() {
        return this.userPreferences;
    }

    public boolean isAnonymous() {
        return this.anonymousUser;
    }

    public boolean isUnknownUser() {
        return this.unknownUser;
    }

    public String toString() {
        return new StringJoiner(", ", UserDto.class.getSimpleName() + "[", "]").add("userName='" + this.userName + "'").add("fullName='" + this.fullName + "'").add("avatarUrl='" + this.avatarUrl + "'").add("url='" + this.url + "'").add("phone='" + this.phone + "'").add("email='" + this.email + "'").add("position='" + this.position + "'").add("department='" + this.department + "'").add("userPreferences=" + this.userPreferences).add("anonymousUser=" + this.anonymousUser).add("unknownUser=" + this.unknownUser).add("location='" + this.location + "'").add("about='" + this.about + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDto)) {
            return false;
        }
        UserDto userDto = (UserDto)o;
        return this.anonymousUser == userDto.anonymousUser && this.unknownUser == userDto.unknownUser && Objects.equals(this.userName, userDto.userName) && Objects.equals(this.fullName, userDto.fullName) && Objects.equals(this.avatarUrl, userDto.avatarUrl) && Objects.equals(this.url, userDto.url) && Objects.equals(this.phone, userDto.phone) && Objects.equals(this.email, userDto.email) && Objects.equals(this.position, userDto.position) && Objects.equals(this.department, userDto.department) && Objects.equals(this.userPreferences, userDto.userPreferences) && Objects.equals(this.location, userDto.location) && Objects.equals(this.about, userDto.about);
    }

    public int hashCode() {
        return Objects.hash(this.userName);
    }
}

