/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class IntoClause
extends HqlSqlWalkerNode
implements DisplayableNode {
    private Queryable persister;
    private String columnSpec = "";
    private Type[] types;
    private boolean discriminated;
    private boolean explicitIdInsertion;
    private boolean explicitVersionInsertion;
    private Set componentIds;
    private List explicitComponentIds;

    public void initialize(Queryable persister) {
        if (persister.isAbstract()) {
            throw new QueryException("cannot insert into abstract class (no table)");
        }
        this.persister = persister;
        this.initializeColumns();
        if (this.getWalker().getSessionFactoryHelper().hasPhysicalDiscriminatorColumn(persister)) {
            this.discriminated = true;
            this.columnSpec = this.columnSpec + ", " + persister.getDiscriminatorColumnName();
        }
        this.resetText();
    }

    private void resetText() {
        this.setText("into " + this.getTableName() + " ( " + this.columnSpec + " )");
    }

    public String getTableName() {
        return this.persister.getSubclassTableName(0);
    }

    public Queryable getQueryable() {
        return this.persister;
    }

    public String getEntityName() {
        return this.persister.getEntityName();
    }

    public Type[] getInsertionTypes() {
        return this.types;
    }

    public boolean isDiscriminated() {
        return this.discriminated;
    }

    public boolean isExplicitIdInsertion() {
        return this.explicitIdInsertion;
    }

    public boolean isExplicitVersionInsertion() {
        return this.explicitVersionInsertion;
    }

    public void prependIdColumnSpec() {
        this.columnSpec = this.persister.getIdentifierColumnNames()[0] + ", " + this.columnSpec;
        this.resetText();
    }

    public void prependVersionColumnSpec() {
        this.columnSpec = this.persister.getPropertyColumnNames(this.persister.getVersionProperty())[0] + ", " + this.columnSpec;
        this.resetText();
    }

    public void validateTypes(SelectClause selectClause) throws QueryException {
        Type[] selectTypes = selectClause.getQueryReturnTypes();
        if (selectTypes.length + selectClause.getTotalParameterCount() != this.types.length) {
            throw new QueryException("number of select types did not match those for insert");
        }
        int parameterCount = 0;
        for (int i = 0; i < this.types.length; ++i) {
            if (selectClause.getParameterPositions().contains(i)) {
                ++parameterCount;
                continue;
            }
            if (this.areCompatible(this.types[i], selectTypes[i - parameterCount])) continue;
            throw new QueryException("insertion type [" + this.types[i] + "] and selection type [" + selectTypes[i - parameterCount] + "] at position " + i + " are not compatible");
        }
    }

    @Override
    public String getDisplayText() {
        return "IntoClause{entityName=" + this.getEntityName() + ",tableName=" + this.getTableName() + ",columns={" + this.columnSpec + "}}";
    }

    private void initializeColumns() {
        AST propertySpec = this.getFirstChild();
        ArrayList types = new ArrayList();
        this.visitPropertySpecNodes(propertySpec.getFirstChild(), types);
        this.types = ArrayHelper.toTypeArray(types);
        this.columnSpec = this.columnSpec.substring(0, this.columnSpec.length() - 2);
    }

    private void visitPropertySpecNodes(AST propertyNode, List types) {
        if (propertyNode == null) {
            return;
        }
        String name = propertyNode.getText();
        if (this.isSuperclassProperty(name)) {
            throw new QueryException("INSERT statements cannot refer to superclass/joined properties [" + name + "]");
        }
        if (!this.explicitIdInsertion) {
            if (this.persister.getIdentifierType() instanceof CompositeType) {
                if (this.componentIds == null) {
                    String[] propertyNames = ((CompositeType)this.persister.getIdentifierType()).getPropertyNames();
                    this.componentIds = new HashSet();
                    for (String propertyName : propertyNames) {
                        this.componentIds.add(propertyName);
                    }
                }
                if (this.componentIds.contains(name)) {
                    if (this.explicitComponentIds == null) {
                        this.explicitComponentIds = new ArrayList(this.componentIds.size());
                    }
                    this.explicitComponentIds.add(name);
                    this.explicitIdInsertion = this.explicitComponentIds.size() == this.componentIds.size();
                }
            } else if (name.equals(this.persister.getIdentifierPropertyName())) {
                this.explicitIdInsertion = true;
            }
        }
        if (this.persister.isVersioned() && name.equals(this.persister.getPropertyNames()[this.persister.getVersionProperty()])) {
            this.explicitVersionInsertion = true;
        }
        String[] columnNames = this.persister.toColumns(name);
        this.renderColumns(columnNames);
        types.add(this.persister.toType(name));
        this.visitPropertySpecNodes(propertyNode.getNextSibling(), types);
        this.visitPropertySpecNodes(propertyNode.getFirstChild(), types);
    }

    private void renderColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            this.columnSpec = this.columnSpec + columnName + ", ";
        }
    }

    private boolean isSuperclassProperty(String propertyName) {
        return this.persister.getSubclassPropertyTableNumber(propertyName) != 0;
    }

    private boolean areCompatible(Type target, Type source) {
        int[] sourceDatatypes;
        if (target.equals(source)) {
            return true;
        }
        if (!target.getReturnedClass().isAssignableFrom(source.getReturnedClass())) {
            return false;
        }
        int[] targetDatatypes = target.sqlTypes(this.getSessionFactoryHelper().getFactory());
        if (targetDatatypes.length != (sourceDatatypes = source.sqlTypes(this.getSessionFactoryHelper().getFactory())).length) {
            return false;
        }
        for (int i = 0; i < targetDatatypes.length; ++i) {
            if (this.areSqlTypesCompatible(targetDatatypes[i], sourceDatatypes[i])) continue;
            return false;
        }
        return true;
    }

    private boolean areSqlTypesCompatible(int target, int source) {
        switch (target) {
            case 93: {
                return source == 91 || source == 92 || source == 93;
            }
            case 91: {
                return source == 91 || source == 93;
            }
            case 92: {
                return source == 92 || source == 93;
            }
        }
        return target == source;
    }
}

