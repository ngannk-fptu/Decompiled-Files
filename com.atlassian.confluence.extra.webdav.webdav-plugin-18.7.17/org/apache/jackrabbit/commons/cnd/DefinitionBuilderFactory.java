/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.cnd;

import javax.jcr.RepositoryException;

public abstract class DefinitionBuilderFactory<T, N> {
    public abstract AbstractNodeTypeDefinitionBuilder<T> newNodeTypeDefinitionBuilder() throws RepositoryException;

    public abstract void setNamespaceMapping(N var1);

    public abstract N getNamespaceMapping();

    public abstract void setNamespace(String var1, String var2) throws RepositoryException;

    public static abstract class AbstractNodeDefinitionBuilder<T>
    extends AbstractItemDefinitionBuilder<T> {
        protected boolean allowSns;

        public abstract void setDefaultPrimaryType(String var1) throws RepositoryException;

        public abstract void addRequiredPrimaryType(String var1) throws RepositoryException;

        public void setAllowsSameNameSiblings(boolean allowSns) throws RepositoryException {
            this.allowSns = allowSns;
        }
    }

    public static abstract class AbstractPropertyDefinitionBuilder<T>
    extends AbstractItemDefinitionBuilder<T> {
        private static final String[] ALL_OPERATORS = new String[]{"jcr.operator.equal.to", "jcr.operator.greater.than", "jcr.operator.greater.than.or.equal.to", "jcr.operator.less.than", "jcr.operator.less.than.or.equal.to", "jcr.operator.like", "jcr.operator.not.equal.to"};
        protected int requiredType = 0;
        protected boolean isMultiple = false;
        protected boolean fullTextSearchable = true;
        protected boolean queryOrderable = true;
        protected String[] queryOperators = ALL_OPERATORS;

        public void setRequiredType(int type) throws RepositoryException {
            this.requiredType = type;
        }

        public int getRequiredType() {
            return this.requiredType;
        }

        public abstract void addValueConstraint(String var1) throws RepositoryException;

        public abstract void addDefaultValues(String var1) throws RepositoryException;

        public void setMultiple(boolean isMultiple) throws RepositoryException {
            this.isMultiple = isMultiple;
        }

        public void setFullTextSearchable(boolean fullTextSearchable) throws RepositoryException {
            this.fullTextSearchable = fullTextSearchable;
        }

        public void setQueryOrderable(boolean queryOrderable) throws RepositoryException {
            this.queryOrderable = queryOrderable;
        }

        public void setAvailableQueryOperators(String[] queryOperators) throws RepositoryException {
            if (queryOperators == null) {
                throw new NullPointerException("queryOperators");
            }
            this.queryOperators = queryOperators;
        }
    }

    public static abstract class AbstractItemDefinitionBuilder<T> {
        protected String name;
        protected boolean autocreate;
        protected int onParent;
        protected boolean isProtected;
        protected boolean isMandatory;

        public void setName(String name) throws RepositoryException {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public abstract void setDeclaringNodeType(String var1) throws RepositoryException;

        public void setAutoCreated(boolean autocreate) throws RepositoryException {
            this.autocreate = autocreate;
        }

        public void setOnParentVersion(int onParent) throws RepositoryException {
            this.onParent = onParent;
        }

        public void setProtected(boolean isProtected) throws RepositoryException {
            this.isProtected = isProtected;
        }

        public void setMandatory(boolean isMandatory) throws RepositoryException {
            this.isMandatory = isMandatory;
        }

        public abstract void build() throws RepositoryException;
    }

    public static abstract class AbstractNodeTypeDefinitionBuilder<T> {
        protected String name;
        protected boolean isMixin;
        protected boolean isOrderable;
        protected boolean isAbstract;
        protected boolean queryable;

        public void setName(String name) throws RepositoryException {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public abstract void addSupertype(String var1) throws RepositoryException;

        public void setMixin(boolean isMixin) throws RepositoryException {
            this.isMixin = isMixin;
        }

        public void setOrderableChildNodes(boolean isOrderable) throws RepositoryException {
            this.isOrderable = isOrderable;
        }

        public abstract void setPrimaryItemName(String var1) throws RepositoryException;

        public void setAbstract(boolean isAbstract) throws RepositoryException {
            this.isAbstract = isAbstract;
        }

        public void setQueryable(boolean queryable) throws RepositoryException {
            this.queryable = queryable;
        }

        public abstract AbstractPropertyDefinitionBuilder<T> newPropertyDefinitionBuilder() throws RepositoryException;

        public abstract AbstractNodeDefinitionBuilder<T> newNodeDefinitionBuilder() throws RepositoryException;

        public abstract T build() throws RepositoryException;
    }
}

