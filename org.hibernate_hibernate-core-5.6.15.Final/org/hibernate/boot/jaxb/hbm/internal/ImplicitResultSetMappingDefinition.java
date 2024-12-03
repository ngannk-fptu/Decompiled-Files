/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryCollectionLoadReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryJoinReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryScalarReturnType;
import org.hibernate.boot.jaxb.hbm.spi.ResultSetMappingBindingDefinition;
import org.hibernate.internal.util.collections.CollectionHelper;

public class ImplicitResultSetMappingDefinition
implements ResultSetMappingBindingDefinition {
    private final String name;
    private final List valueMappingSources;

    public ImplicitResultSetMappingDefinition(String resultSetMappingName, List valueMappingSources) {
        this.name = resultSetMappingName;
        this.valueMappingSources = valueMappingSources;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List getValueMappingSources() {
        return this.valueMappingSources;
    }

    public static class Builder {
        private final String queryName;
        private List valueMappingSources;

        public Builder(String queryName) {
            this.queryName = queryName;
        }

        public void addReturn(JaxbHbmNativeQueryScalarReturnType scalarReturn) {
            if (this.valueMappingSources == null) {
                this.valueMappingSources = new ArrayList();
            }
            this.valueMappingSources.add(scalarReturn);
        }

        public void addReturn(JaxbHbmNativeQueryReturnType rootReturn) {
            if (this.valueMappingSources == null) {
                this.valueMappingSources = new ArrayList();
            }
            this.valueMappingSources.add(rootReturn);
        }

        public void addReturn(JaxbHbmNativeQueryJoinReturnType joinReturn) {
            if (this.valueMappingSources == null) {
                this.valueMappingSources = new ArrayList();
            }
            this.valueMappingSources.add(joinReturn);
        }

        public void addReturn(JaxbHbmNativeQueryCollectionLoadReturnType collectionLoadReturn) {
            if (this.valueMappingSources == null) {
                this.valueMappingSources = new ArrayList();
            }
            this.valueMappingSources.add(collectionLoadReturn);
        }

        public boolean hasAnyReturns() {
            return CollectionHelper.isNotEmpty(this.valueMappingSources);
        }

        public ImplicitResultSetMappingDefinition build() {
            return new ImplicitResultSetMappingDefinition(this.queryName + "-inline-result-set-mapping-def", this.copy(this.valueMappingSources));
        }

        private <T> List<T> copy(List<T> returnBindings) {
            if (returnBindings == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(returnBindings);
        }
    }
}

