/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.thirdparty.api;

import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.google.common.base.Preconditions;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;

public class Application {
    private final String displayName;
    private final URI id;

    public static Either<ValidationErrors, Application> application(Option<String> displayName, Option<String> idString) {
        ValidationErrors.Builder errors = new ValidationErrors.Builder();
        if (!displayName.isDefined()) {
            errors.addError("displayName cannot be omitted");
        } else {
            errors.checkString(displayName, "displayName");
            if (StringUtils.isBlank((CharSequence)((CharSequence)displayName.get()))) {
                errors.addError("displayName cannot be blank");
            }
        }
        if (!idString.isDefined()) {
            errors.addError("id cannot be omitted");
        } else {
            Option<URI> id = errors.checkAbsoluteUriString(idString, "id");
            if (errors.isEmpty() && id.isDefined()) {
                return Either.right((Object)new Application((String)displayName.get(), (URI)id.get()));
            }
        }
        return Either.left((Object)errors.build());
    }

    public static Application application(String displayName, URI id) {
        Preconditions.checkNotNull((Object)displayName, (Object)"displayName");
        Preconditions.checkNotNull((Object)id, (Object)"id");
        Either<ValidationErrors, Application> ret = Application.application((Option<String>)Option.some((Object)displayName), (Option<String>)Option.some((Object)id.toASCIIString()));
        if (ret.isLeft()) {
            throw new IllegalArgumentException(((ValidationErrors)ret.left().get()).toString());
        }
        return (Application)ret.right().get();
    }

    private Application(String displayName, URI id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public URI getId() {
        return this.id;
    }

    public boolean equals(Object other) {
        if (other instanceof Application) {
            Application a = (Application)other;
            return this.displayName.equals(a.displayName) && this.id.equals(a.id);
        }
        return false;
    }

    public int hashCode() {
        return this.displayName.hashCode() * 37 + this.id.hashCode();
    }
}

