/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.ThreadSafe
 *  javax.inject.Named
 */
package com.atlassian.ccev;

import com.atlassian.ccev.AidEmailAddress;
import com.atlassian.ccev.ValidatorJs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

@ParametersAreNonnullByDefault
@Named
@ThreadSafe
public class EmailValidator {
    public <T> List<Result<T>> validate(Iterable<T> emailProviders, Function<T, String> getEmail) {
        HashMap processedEmails = new HashMap();
        emailProviders.forEach(ep -> this.validate(ep, getEmail, processedEmails));
        return processedEmails.values().stream().flatMap(Collection::stream).filter(this::isInvalidOrDuplicated).collect(Collectors.toList());
    }

    public boolean validate(@Nullable String email) {
        return email != null && AidEmailAddress.validEmail(email) && ValidatorJs.isEmail(email);
    }

    private <T> boolean isInvalidOrDuplicated(Result<T> result) {
        return result.isDuplicated() || !result.isValid() || result.isEmpty();
    }

    private <T> void validate(T user, Function<T, String> getEmail, Map<String, List<Result<T>>> processedEmails) {
        String email = this.getNonNullEmail(user, getEmail);
        processedEmails.compute(email.toLowerCase(), (key, results) -> {
            if (results == null) {
                results = new ArrayList<Result<Object>>();
            } else {
                ((Result)results.get(0)).duplicated = true;
            }
            results.add(new Result<Object>(user, this.validate(email), email.isEmpty(), !results.isEmpty()));
            return results;
        });
    }

    private <T> String getNonNullEmail(T user, Function<T, String> getEmail) {
        String email = getEmail.apply(user);
        return email != null ? email : "";
    }

    public static class Result<T> {
        private final T user;
        private final boolean valid;
        private final boolean empty;
        private boolean duplicated;

        public Result(T user, boolean valid, boolean empty, boolean duplicated) {
            this.user = user;
            this.valid = valid;
            this.empty = empty;
            this.duplicated = duplicated;
        }

        public T getUser() {
            return this.user;
        }

        public boolean isValid() {
            return this.valid;
        }

        public boolean isEmpty() {
            return this.empty;
        }

        public boolean isDuplicated() {
            return this.duplicated;
        }
    }
}

