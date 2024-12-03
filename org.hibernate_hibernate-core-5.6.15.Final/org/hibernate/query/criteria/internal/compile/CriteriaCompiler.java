/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 *  javax.persistence.TypedQuery
 *  javax.persistence.criteria.ParameterExpression
 */
package org.hibernate.query.criteria.internal.compile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.Stack;
import org.hibernate.internal.util.collections.StandardStack;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.hibernate.query.criteria.internal.compile.ExplicitParameterInfo;
import org.hibernate.query.criteria.internal.compile.ImplicitParameterBinding;
import org.hibernate.query.criteria.internal.compile.InterpretedParameterMetadata;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ParameterExpressionImpl;
import org.hibernate.query.criteria.internal.expression.function.FunctionExpression;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.sql.ast.Clause;
import org.hibernate.type.Type;

public class CriteriaCompiler
implements Serializable {
    private final SharedSessionContractImplementor entityManager;

    public CriteriaCompiler(SharedSessionContractImplementor entityManager) {
        this.entityManager = entityManager;
    }

    public QueryImplementor compile(CompilableCriteria criteria) {
        try {
            criteria.validate();
        }
        catch (IllegalStateException ise) {
            throw new IllegalArgumentException("Error occurred validating the Criteria", ise);
        }
        final HashMap explicitParameterInfoMap = new HashMap();
        final ArrayList implicitParameterBindings = new ArrayList();
        SessionFactoryImplementor sessionFactory = this.entityManager.getFactory();
        final LiteralHandlingMode criteriaLiteralHandlingMode = sessionFactory.getSessionFactoryOptions().getCriteriaLiteralHandlingMode();
        final Dialect dialect = sessionFactory.getServiceRegistry().getService(JdbcServices.class).getDialect();
        RenderingContext renderingContext = new RenderingContext(){
            private int aliasCount;
            private int explicitParameterCount;
            private final Stack<Clause> clauseStack = new StandardStack<Clause>();
            private final Stack<FunctionExpression> functionContextStack = new StandardStack<FunctionExpression>();

            @Override
            public String generateAlias() {
                return "generatedAlias" + this.aliasCount++;
            }

            public String generateParameterName() {
                return "param" + this.explicitParameterCount++;
            }

            @Override
            public Stack<Clause> getClauseStack() {
                return this.clauseStack;
            }

            @Override
            public Stack<FunctionExpression> getFunctionStack() {
                return this.functionContextStack;
            }

            @Override
            public ExplicitParameterInfo registerExplicitParameter(ParameterExpression<?> criteriaQueryParameter) {
                ExplicitParameterInfo parameterInfo = (ExplicitParameterInfo)explicitParameterInfoMap.get(criteriaQueryParameter);
                if (parameterInfo == null) {
                    parameterInfo = StringHelper.isNotEmpty(criteriaQueryParameter.getName()) && !((ParameterExpressionImpl)criteriaQueryParameter).isNameGenerated() ? new ExplicitParameterInfo(criteriaQueryParameter.getName(), null, criteriaQueryParameter.getJavaType()) : (criteriaQueryParameter.getPosition() != null ? new ExplicitParameterInfo(null, criteriaQueryParameter.getPosition(), criteriaQueryParameter.getJavaType()) : new ExplicitParameterInfo(this.generateParameterName(), null, criteriaQueryParameter.getJavaType()));
                    explicitParameterInfoMap.put(criteriaQueryParameter, parameterInfo);
                }
                return parameterInfo;
            }

            @Override
            public String registerLiteralParameterBinding(final Object literal, final Class javaType) {
                final String parameterName = this.generateParameterName();
                ImplicitParameterBinding binding = new ImplicitParameterBinding(){

                    @Override
                    public String getParameterName() {
                        return parameterName;
                    }

                    @Override
                    public Class getJavaType() {
                        return javaType;
                    }

                    @Override
                    public void bind(TypedQuery typedQuery) {
                        if (literal instanceof Parameter) {
                            return;
                        }
                        typedQuery.setParameter(parameterName, literal);
                    }
                };
                implicitParameterBindings.add(binding);
                return parameterName;
            }

            @Override
            public String getCastType(Class javaType) {
                SessionFactoryImplementor factory = CriteriaCompiler.this.entityManager.getFactory();
                Type hibernateType = factory.getTypeResolver().heuristicType(javaType.getName());
                if (hibernateType == null) {
                    throw new IllegalArgumentException("Could not convert java type [" + javaType.getName() + "] to Hibernate type");
                }
                return hibernateType.getName();
            }

            @Override
            public Dialect getDialect() {
                return dialect;
            }

            @Override
            public LiteralHandlingMode getCriteriaLiteralHandlingMode() {
                return criteriaLiteralHandlingMode;
            }
        };
        return criteria.interpret(renderingContext).buildCompiledQuery(this.entityManager, new InterpretedParameterMetadata(){

            @Override
            public Map<ParameterExpression<?>, ExplicitParameterInfo<?>> explicitParameterInfoMap() {
                return explicitParameterInfoMap;
            }

            @Override
            public List<ImplicitParameterBinding> implicitParameterBindings() {
                return implicitParameterBindings;
            }
        });
    }
}

