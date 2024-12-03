/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.scopes.api;

import com.atlassian.oauth2.scopes.api.Closeable;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.Scope;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ScopesRequestCache {
    public Closeable withScopes(Scope var1, Supplier<Optional<String>> var2);

    public boolean containsOnlyThisScope(Scope var1);

    public boolean hasPermission(Permission var1);

    @Nonnull
    public Optional<String> getApplicationNameForRequest();

    @Nonnull
    public Set<Permission> getPermissionsForRequest();

    public RequestCache getRequestCache();

    public void setRequestCache(@Nullable RequestCache var1);

    public void clearRequestCache();

    public static class RequestCache {
        private final Set<Permission> permissions;
        private final Supplier<Optional<String>> applicationNameSupplier;

        public RequestCache() {
            this.permissions = Collections.emptySet();
            this.applicationNameSupplier = Optional::empty;
        }

        public RequestCache(Collection<Permission> permissions, Supplier<Optional<String>> applicationNameSupplier) {
            this.permissions = new HashSet<Permission>(permissions){

                @Override
                public boolean contains(Object permission) {
                    return this.stream().anyMatch(setPermission -> setPermission.equals(permission));
                }
            };
            this.applicationNameSupplier = applicationNameSupplier != null ? applicationNameSupplier : Optional::empty;
        }

        public Set<Permission> getPermissions() {
            return this.permissions;
        }

        public Supplier<Optional<String>> getApplicationNameSupplier() {
            return this.applicationNameSupplier;
        }

        public RequestCache copy() {
            return new RequestCache(this.permissions, this.applicationNameSupplier);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            RequestCache that = (RequestCache)o;
            return Objects.equals(this.permissions, that.permissions) && Objects.equals(this.applicationNameSupplier, that.applicationNameSupplier);
        }

        public int hashCode() {
            return Objects.hash(this.permissions, this.applicationNameSupplier);
        }
    }
}

