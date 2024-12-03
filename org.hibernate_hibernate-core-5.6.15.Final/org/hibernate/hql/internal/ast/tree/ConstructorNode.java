/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.QueryException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.hql.internal.ast.DetailedSemanticException;
import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.PathNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.tree.SelectExpressionList;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;

public class ConstructorNode
extends SelectExpressionList
implements AggregatedSelectExpression {
    private Class resultType;
    private Constructor constructor;
    private Type[] constructorArgumentTypes;
    private boolean isMap;
    private boolean isList;
    private String[] aggregatedAliases;

    @Override
    public ResultTransformer getResultTransformer() {
        if (this.constructor != null) {
            return new AliasToBeanConstructorResultTransformer(this.constructor);
        }
        if (this.isMap) {
            return Transformers.ALIAS_TO_ENTITY_MAP;
        }
        if (this.isList) {
            return Transformers.TO_LIST;
        }
        throw new QueryException("Unable to determine proper dynamic-instantiation tranformer to use.");
    }

    @Override
    public String[] getAggregatedAliases() {
        if (this.aggregatedAliases == null) {
            this.aggregatedAliases = this.buildAggregatedAliases();
        }
        return this.aggregatedAliases;
    }

    private String[] buildAggregatedAliases() {
        SelectExpression[] selectExpressions = this.collectSelectExpressions();
        String[] aliases = new String[selectExpressions.length];
        for (int i = 0; i < selectExpressions.length; ++i) {
            String alias = selectExpressions[i].getAlias();
            aliases[i] = alias == null ? Integer.toString(i) : alias;
        }
        return aliases;
    }

    @Override
    public void setScalarColumn(int i) throws SemanticException {
        SelectExpression[] selectExpressions = this.collectSelectExpressions();
        for (int j = 0; j < selectExpressions.length; ++j) {
            SelectExpression selectExpression = selectExpressions[j];
            selectExpression.setScalarColumn(j);
        }
    }

    @Override
    public int getScalarColumnIndex() {
        return -1;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        SelectExpression[] selectExpressions = this.collectSelectExpressions();
        for (int j = 0; j < selectExpressions.length; ++j) {
            SelectExpression selectExpression = selectExpressions[j];
            selectExpression.setScalarColumnText(j);
        }
    }

    @Override
    protected AST getFirstSelectExpression() {
        return this.getFirstChild().getNextSibling();
    }

    @Override
    public Class getAggregationResultType() {
        return this.resultType;
    }

    @Override
    @Deprecated
    public Type getDataType() {
        throw new UnsupportedOperationException("getDataType() is not supported by ConstructorNode!");
    }

    public void prepare() throws SemanticException {
        this.constructorArgumentTypes = this.resolveConstructorArgumentTypes();
        String path = ((PathNode)this.getFirstChild()).getPath();
        if ("map".equals(path.toLowerCase(Locale.ROOT))) {
            this.isMap = true;
            this.resultType = Map.class;
        } else if ("list".equals(path.toLowerCase(Locale.ROOT))) {
            this.isList = true;
            this.resultType = List.class;
        } else {
            this.constructor = this.resolveConstructor(path);
            this.resultType = this.constructor.getDeclaringClass();
        }
    }

    private Type[] resolveConstructorArgumentTypes() throws SemanticException {
        SelectExpression[] argumentExpressions = this.collectSelectExpressions();
        if (argumentExpressions == null) {
            return new Type[0];
        }
        Type[] types = new Type[argumentExpressions.length];
        for (int x = 0; x < argumentExpressions.length; ++x) {
            types[x] = argumentExpressions[x].getDataType();
        }
        return types;
    }

    private Constructor resolveConstructor(String path) throws SemanticException {
        String className;
        String importedClassName = this.getSessionFactoryHelper().getImportedClassName(path);
        String string = className = StringHelper.isEmpty(importedClassName) ? path : importedClassName;
        if (className == null) {
            throw new SemanticException("Unable to locate class [" + path + "]");
        }
        try {
            Class holderClass = this.getSessionFactoryHelper().getFactory().getServiceRegistry().getService(ClassLoaderService.class).classForName(className);
            return ReflectHelper.getConstructor(holderClass, this.constructorArgumentTypes);
        }
        catch (ClassLoadingException e) {
            throw new DetailedSemanticException("Unable to locate class [" + className + "]", (Throwable)((Object)e));
        }
        catch (PropertyNotFoundException e) {
            throw new DetailedSemanticException(this.formatMissingContructorExceptionMessage(className), (Throwable)((Object)e));
        }
    }

    private String formatMissingContructorExceptionMessage(String className) {
        CharSequence[] params = new String[this.constructorArgumentTypes.length];
        for (int j = 0; j < this.constructorArgumentTypes.length; ++j) {
            params[j] = this.constructorArgumentTypes[j] instanceof PrimitiveType ? ((PrimitiveType)((Object)this.constructorArgumentTypes[j])).getPrimitiveClass().getName() : this.constructorArgumentTypes[j].getReturnedClass().getName();
        }
        String formattedList = params.length == 0 ? "no arguments constructor" : String.join((CharSequence)", ", params);
        return String.format("Unable to locate appropriate constructor on class [%s]. Expected arguments are: %s", className, formattedList);
    }

    public Constructor getConstructor() {
        return this.constructor;
    }

    public List getConstructorArgumentTypeList() {
        return Arrays.asList(this.constructorArgumentTypes);
    }

    @Override
    public List getAggregatedSelectionTypeList() {
        return this.getConstructorArgumentTypeList();
    }

    @Override
    public FromElement getFromElement() {
        return null;
    }

    @Override
    public boolean isConstructor() {
        return true;
    }

    @Override
    public boolean isReturnableEntity() throws SemanticException {
        return false;
    }

    @Override
    public boolean isScalar() {
        return true;
    }

    @Override
    public void setAlias(String alias) {
        throw new UnsupportedOperationException("constructor may not be aliased");
    }

    @Override
    public String getAlias() {
        throw new UnsupportedOperationException("constructor may not be aliased");
    }
}

