/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.streams.internal.rest.resources;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.streams.internal.ProjectKeyValidator;
import com.atlassian.streams.internal.rest.representations.ValidationErrorCollectionRepresentation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@Path(value="/validate")
@AnonymousAllowed
public class StreamsValidationResource {
    private static final CacheControl NO_CACHE = new CacheControl();
    private static final String PREF_TITLE = "title";
    private static final String PREF_KEYS = "keys";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_NUMOFENTRIES = "numofentries";
    private final ProjectKeyValidator validator;

    public StreamsValidationResource(ProjectKeyValidator validator) {
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    @GET
    @Produces(value={"application/vnd.atl.streams+json"})
    public Response validate(@QueryParam(value="title") String title, @QueryParam(value="keys") String keys, @QueryParam(value="username") String usernames, @QueryParam(value="numofentries") String numOfEntriesString, @QueryParam(value="local") boolean local) {
        ImmutableList.Builder errorBuilder = ImmutableList.builder();
        if (StringUtils.isBlank((CharSequence)title)) {
            errorBuilder.add((Object)new ValidationErrorCollectionRepresentation.ValidationErrorEntry(PREF_TITLE, "gadget.activity.stream.error.pref.title"));
        }
        if (StringUtils.isNotBlank((CharSequence)keys) && !keys.contains("__all_projects__") && this.hasInvalidKey(keys, local)) {
            errorBuilder.add((Object)new ValidationErrorCollectionRepresentation.ValidationErrorEntry(PREF_KEYS, "gadget.activity.stream.error.pref.keys"));
        }
        if (StringUtils.isBlank((CharSequence)numOfEntriesString)) {
            errorBuilder.add((Object)new ValidationErrorCollectionRepresentation.ValidationErrorEntry(PREF_NUMOFENTRIES, "gadget.activity.stream.error.pref.numofentries.required"));
        } else if (!this.isValidNumber(numOfEntriesString)) {
            errorBuilder.add((Object)new ValidationErrorCollectionRepresentation.ValidationErrorEntry(PREF_NUMOFENTRIES, "gadget.activity.stream.error.pref.numofentries.number"));
        }
        ImmutableList errorCollection = errorBuilder.build();
        if (Iterables.isEmpty((Iterable)errorCollection)) {
            return Response.ok().cacheControl(NO_CACHE).build();
        }
        return Response.status((int)400).entity((Object)new ValidationErrorCollectionRepresentation((Collection<ValidationErrorCollectionRepresentation.ValidationErrorEntry>)errorCollection)).cacheControl(NO_CACHE).build();
    }

    private boolean hasInvalidKey(String keys, boolean local) {
        return !this.validator.allKeysAreValid(Arrays.asList(keys.split(",")), local);
    }

    private boolean isValidNumber(String number) {
        if (NumberUtils.isNumber((String)number)) {
            Long numberOfEntries = Long.valueOf(number);
            return numberOfEntries > 0L && numberOfEntries <= 100L;
        }
        return false;
    }

    static {
        NO_CACHE.setNoStore(true);
        NO_CACHE.setNoCache(true);
    }
}

