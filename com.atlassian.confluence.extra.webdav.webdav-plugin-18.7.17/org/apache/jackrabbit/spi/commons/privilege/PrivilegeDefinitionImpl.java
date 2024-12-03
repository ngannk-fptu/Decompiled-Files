/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.privilege;

import java.util.Collections;
import java.util.Set;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.PrivilegeDefinition;

public class PrivilegeDefinitionImpl
implements PrivilegeDefinition {
    private final Name name;
    private final boolean isAbstract;
    private final Set<Name> declaredAggregateNames;

    public PrivilegeDefinitionImpl(Name name, boolean isAbstract, Set<Name> declaredAggregateNames) {
        this.name = name;
        this.isAbstract = isAbstract;
        this.declaredAggregateNames = declaredAggregateNames == null ? Collections.emptySet() : Collections.unmodifiableSet(declaredAggregateNames);
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public boolean isAbstract() {
        return this.isAbstract;
    }

    @Override
    public Set<Name> getDeclaredAggregateNames() {
        return this.declaredAggregateNames;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PrivilegeDefinitionImpl that = (PrivilegeDefinitionImpl)o;
        if (this.isAbstract != that.isAbstract) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return this.declaredAggregateNames.equals(that.declaredAggregateNames);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.isAbstract ? 1 : 0);
        result = 31 * result + (this.declaredAggregateNames != null ? this.declaredAggregateNames.hashCode() : 0);
        return result;
    }
}

