/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.OpenSocialRequestContext
 *  com.atlassian.gadgets.opensocial.model.Group
 *  com.atlassian.gadgets.opensocial.model.Person
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.opensocial.spi;

import com.atlassian.gadgets.opensocial.OpenSocialRequestContext;
import com.atlassian.gadgets.opensocial.model.Group;
import com.atlassian.gadgets.opensocial.model.Person;
import com.atlassian.gadgets.opensocial.spi.PersonServiceException;
import java.util.Set;
import javax.annotation.Nullable;

public interface PersonService {
    @Nullable
    public Person getPerson(String var1, OpenSocialRequestContext var2) throws PersonServiceException;

    public Set<Person> getPeople(Set<String> var1, Group var2, OpenSocialRequestContext var3) throws PersonServiceException;
}

