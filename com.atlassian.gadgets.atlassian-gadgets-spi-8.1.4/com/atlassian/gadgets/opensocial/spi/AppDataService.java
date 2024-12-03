/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.OpenSocialRequestContext
 *  com.atlassian.gadgets.opensocial.model.AppId
 *  com.atlassian.gadgets.opensocial.model.PersonId
 */
package com.atlassian.gadgets.opensocial.spi;

import com.atlassian.gadgets.opensocial.OpenSocialRequestContext;
import com.atlassian.gadgets.opensocial.model.AppId;
import com.atlassian.gadgets.opensocial.model.PersonId;
import com.atlassian.gadgets.opensocial.spi.AppDataServiceException;
import java.util.Map;
import java.util.Set;

public interface AppDataService {
    public Map<PersonId, Map<String, String>> getPeopleData(Set<PersonId> var1, AppId var2, Set<String> var3, OpenSocialRequestContext var4) throws AppDataServiceException;

    public Map<PersonId, Map<String, String>> getPeopleData(Set<PersonId> var1, AppId var2, OpenSocialRequestContext var3) throws AppDataServiceException;

    public void deletePersonData(PersonId var1, AppId var2, Set<String> var3, OpenSocialRequestContext var4) throws AppDataServiceException;

    public void deletePersonData(PersonId var1, AppId var2, OpenSocialRequestContext var3) throws AppDataServiceException;

    public void updatePersonData(PersonId var1, AppId var2, Map<String, String> var3, OpenSocialRequestContext var4) throws AppDataServiceException;
}

