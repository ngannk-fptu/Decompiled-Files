/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import com.atlassian.gadgets.opensocial.model.Address;
import com.atlassian.gadgets.opensocial.model.EmailAddress;
import com.atlassian.gadgets.opensocial.model.Name;
import com.atlassian.gadgets.opensocial.model.PersonId;
import com.atlassian.gadgets.opensocial.model.PhoneNumber;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.jcip.annotations.Immutable;

@Immutable
public class Person {
    private PersonId personId;
    private Name name;
    private URI profileUrl;
    private URI thumbnailUrl;
    private List<Address> addresses;
    private List<PhoneNumber> phoneNumbers;
    private List<EmailAddress> emailAddresses;
    private String aboutMe;
    private String status;
    private Date dateOfBirth;
    private int timeZone;
    private Address currentLocation;
    private List<URI> urls;

    private Person(Builder builder) {
        this.personId = builder.personId;
        this.name = builder.name;
        this.profileUrl = builder.profileUrl;
        this.thumbnailUrl = builder.thumbnailUrl;
        this.addresses = builder.addresses;
        this.phoneNumbers = builder.phoneNumbers;
        this.emailAddresses = builder.emailAddresses;
        this.aboutMe = builder.aboutMe;
        this.status = builder.status;
        this.dateOfBirth = builder.dateOfBirth;
        this.timeZone = builder.timeZone;
        this.currentLocation = builder.currentLocation;
        this.urls = builder.urls;
    }

    public PersonId getPersonId() {
        return this.personId;
    }

    public Name getName() {
        return this.name;
    }

    public String getAboutMe() {
        return this.aboutMe;
    }

    public List<Address> getAddresses() {
        return this.addresses;
    }

    public Address getCurrentLocation() {
        return this.currentLocation;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public List<EmailAddress> getEmailAddresses() {
        return this.emailAddresses;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public URI getProfileUrl() {
        return this.profileUrl;
    }

    public String getStatus() {
        return this.status;
    }

    public URI getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public int getTimeZone() {
        return this.timeZone;
    }

    public List<URI> getUrls() {
        return this.urls;
    }

    public boolean equals(Object obj) {
        return obj instanceof Person && this.personId.equals(((Person)obj).getPersonId());
    }

    public int hashCode() {
        return this.personId.hashCode();
    }

    public String toString() {
        return this.personId.toString();
    }

    public static final class Builder {
        private PersonId personId;
        private Name name;
        private URI profileUrl;
        private URI thumbnailUrl;
        private List<Address> addresses;
        private List<PhoneNumber> phoneNumbers;
        private List<EmailAddress> emailAddresses;
        private String aboutMe;
        private String status;
        private Date dateOfBirth;
        private int timeZone;
        private Address currentLocation;
        private List<URI> urls;

        public Builder(Person person) {
            this.personId = person.personId;
            this.name = person.name;
            this.profileUrl = person.profileUrl;
            this.thumbnailUrl = person.thumbnailUrl;
            this.addresses = Collections.unmodifiableList(new ArrayList(person.addresses));
            this.phoneNumbers = Collections.unmodifiableList(new ArrayList(person.phoneNumbers));
            this.emailAddresses = Collections.unmodifiableList(new ArrayList(person.emailAddresses));
            this.aboutMe = person.aboutMe;
            this.status = person.status;
            this.dateOfBirth = new Date(person.dateOfBirth.getTime());
            this.timeZone = person.timeZone;
            this.currentLocation = person.currentLocation;
            this.urls = Collections.unmodifiableList(person.urls);
        }

        public Builder(PersonId personId) {
            this.personId = personId;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder profileUri(URI uri) {
            this.profileUrl = uri;
            return this;
        }

        public Builder thumbnailUri(URI uri) {
            this.thumbnailUrl = uri;
            return this;
        }

        public Builder addresses(List<Address> addresses) {
            this.addresses = Collections.unmodifiableList(new ArrayList<Address>(addresses));
            return this;
        }

        public Builder phoneNumbers(List<PhoneNumber> phoneNumbers) {
            this.phoneNumbers = Collections.unmodifiableList(new ArrayList<PhoneNumber>(phoneNumbers));
            return this;
        }

        public Builder emailAddresses(List<EmailAddress> emailAddresses) {
            this.emailAddresses = Collections.unmodifiableList(new ArrayList<EmailAddress>(emailAddresses));
            return this;
        }

        public Builder aboutMe(String aboutMe) {
            this.aboutMe = aboutMe;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder dateOfBirth(Date date) {
            this.dateOfBirth = new Date(date.getTime());
            return this;
        }

        public Builder timeZone(int timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder currentLocation(Address currentLocation) {
            this.currentLocation = currentLocation;
            return this;
        }

        public Builder urls(List<URI> urls) {
            this.urls = Collections.unmodifiableList(new ArrayList<URI>(urls));
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}

