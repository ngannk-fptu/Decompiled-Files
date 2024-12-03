/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.CompositeType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.hibernate.type.spi.TypeConfiguration;

public abstract class IdsClauseBuilder {
    private final Dialect dialect;
    private final Type identifierType;
    private final TypeResolver typeResolver;
    private final String[] columns;
    private final List<Object[]> ids;

    protected IdsClauseBuilder(Dialect dialect, Type identifierType, TypeConfiguration typeConfiguration, String[] columns, List<Object[]> ids) {
        this.dialect = dialect;
        this.identifierType = identifierType;
        this.typeResolver = typeConfiguration.getTypeResolver();
        this.columns = columns;
        this.ids = ids;
    }

    @Deprecated
    protected IdsClauseBuilder(Dialect dialect, Type identifierType, TypeResolver typeResolver, String[] columns, List<Object[]> ids) {
        this.dialect = dialect;
        this.identifierType = identifierType;
        this.typeResolver = typeResolver;
        this.columns = columns;
        this.ids = ids;
    }

    public Type getIdentifierType() {
        return this.identifierType;
    }

    @Deprecated
    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    protected String[] getColumns() {
        return this.columns;
    }

    public List<Object[]> getIds() {
        return this.ids;
    }

    public abstract String toStatement();

    protected String quoteIdentifier(Object ... value) {
        if (value.length == 1) {
            return this.quoteIdentifier(value[0], this.identifierType);
        }
        if (this.identifierType instanceof CompositeType) {
            CompositeType compositeType = (CompositeType)this.identifierType;
            ArrayList<String> quotedIdentifiers = new ArrayList<String>();
            for (int i = 0; i < value.length; ++i) {
                quotedIdentifiers.add(this.quoteIdentifier(value[i], compositeType.getSubtypes()[i]));
            }
            return String.join((CharSequence)",", quotedIdentifiers);
        }
        throw new IllegalArgumentException("Composite identifier does not implement CompositeType");
    }

    private String quoteIdentifier(Object value, Type type) {
        Type resolvedType;
        Type type2 = resolvedType = !type.getReturnedClass().equals(value.getClass()) ? this.typeResolver.heuristicType(value.getClass().getName()) : type;
        if (resolvedType instanceof LiteralType) {
            LiteralType literalType = (LiteralType)((Object)resolvedType);
            try {
                return literalType.objectToSQLString(value, this.dialect);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return String.valueOf(value);
    }
}

