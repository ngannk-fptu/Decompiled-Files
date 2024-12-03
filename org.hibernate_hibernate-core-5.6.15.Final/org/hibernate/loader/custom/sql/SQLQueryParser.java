/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.hibernate.QueryException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.query.spi.ParameterParser;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.custom.sql.NamedParamBinder;
import org.hibernate.loader.custom.sql.PositionalParamBinder;
import org.hibernate.param.ParameterBinder;
import org.hibernate.persister.collection.SQLLoadableCollection;
import org.hibernate.persister.entity.SQLLoadable;

public class SQLQueryParser {
    private static final Pattern PREPARED_STATEMENT_PATTERN = Pattern.compile("^\\{.*?\\}$");
    private static final String HIBERNATE_PLACEHOLDER_PREFIX = "h-";
    private static final String DOMAIN_PLACEHOLDER = "h-domain";
    private static final String CATALOG_PLACEHOLDER = "h-catalog";
    private static final String SCHEMA_PLACEHOLDER = "h-schema";
    private final SessionFactoryImplementor factory;
    private final String originalQueryString;
    private final ParserContext context;
    private long aliasesFound;
    private List<ParameterBinder> paramValueBinders;

    public SQLQueryParser(String queryString, ParserContext context, SessionFactoryImplementor factory) {
        this.originalQueryString = queryString;
        this.context = context;
        this.factory = factory;
    }

    public List<ParameterBinder> getParameterValueBinders() {
        return this.paramValueBinders == null ? Collections.emptyList() : this.paramValueBinders;
    }

    public boolean queryHasAliases() {
        return this.aliasesFound > 0L;
    }

    protected String getOriginalQueryString() {
        return this.originalQueryString;
    }

    public String process() {
        String processedSql = this.substituteBrackets(this.originalQueryString);
        processedSql = this.substituteParams(processedSql);
        return processedSql;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected String substituteBrackets(String sqlQuery) throws QueryException {
        if (PREPARED_STATEMENT_PATTERN.matcher(sqlQuery.trim()).matches()) {
            return sqlQuery;
        }
        StringBuilder result = new StringBuilder(sqlQuery.length() + 20);
        SqlStringGenerationContext sqlStringGenerationContext = this.factory.getSqlStringGenerationContext();
        int curr = 0;
        while (curr < sqlQuery.length()) {
            int left = sqlQuery.indexOf(123, curr);
            if (left < 0) {
                result.append(sqlQuery.substring(curr));
                return result.toString();
            }
            result.append(sqlQuery.substring(curr, left));
            int right = sqlQuery.indexOf(125, left + 1);
            if (right < 0) {
                throw new QueryException("Unmatched braces for alias path", sqlQuery);
            }
            String aliasPath = sqlQuery.substring(left + 1, right);
            boolean isPlaceholder = aliasPath.startsWith(HIBERNATE_PLACEHOLDER_PREFIX);
            if (isPlaceholder) {
                if (DOMAIN_PLACEHOLDER.equals(aliasPath)) {
                    Identifier schemaName;
                    Identifier catalogName = sqlStringGenerationContext.getDefaultCatalog();
                    if (catalogName != null) {
                        result.append(catalogName.render(sqlStringGenerationContext.getDialect()));
                        result.append(".");
                    }
                    if ((schemaName = sqlStringGenerationContext.getDefaultSchema()) != null) {
                        result.append(schemaName.render(sqlStringGenerationContext.getDialect()));
                        result.append(".");
                    }
                } else if (SCHEMA_PLACEHOLDER.equals(aliasPath)) {
                    Identifier schemaName = sqlStringGenerationContext.getDefaultSchema();
                    if (schemaName != null) {
                        result.append(schemaName.render(sqlStringGenerationContext.getDialect()));
                        result.append(".");
                    }
                } else {
                    if (!CATALOG_PLACEHOLDER.equals(aliasPath)) throw new QueryException("Unknown placeholder ", aliasPath);
                    Identifier catalogName = sqlStringGenerationContext.getDefaultCatalog();
                    if (catalogName != null) {
                        result.append(catalogName.render(sqlStringGenerationContext.getDialect()));
                        result.append(".");
                    }
                }
            } else if (this.context != null) {
                int firstDot = aliasPath.indexOf(46);
                if (firstDot == -1) {
                    if (this.context.isEntityAlias(aliasPath)) {
                        result.append(aliasPath);
                        ++this.aliasesFound;
                    } else {
                        result.append('{').append(aliasPath).append('}');
                    }
                } else {
                    String propertyName;
                    String aliasName = aliasPath.substring(0, firstDot);
                    if (this.context.isCollectionAlias(aliasName)) {
                        propertyName = aliasPath.substring(firstDot + 1);
                        result.append(this.resolveCollectionProperties(aliasName, propertyName));
                        ++this.aliasesFound;
                    } else if (this.context.isEntityAlias(aliasName)) {
                        propertyName = aliasPath.substring(firstDot + 1);
                        result.append(this.resolveProperties(aliasName, propertyName));
                        ++this.aliasesFound;
                    } else {
                        result.append('{').append(aliasPath).append('}');
                    }
                }
            } else {
                result.append('{').append(aliasPath).append('}');
            }
            curr = right + 1;
        }
        return result.toString();
    }

    private String resolveCollectionProperties(String aliasName, String propertyName) {
        Map fieldResults = this.context.getPropertyResultsMapByAlias(aliasName);
        SQLLoadableCollection collectionPersister = this.context.getCollectionPersisterByAlias(aliasName);
        String collectionSuffix = this.context.getCollectionSuffixByAlias(aliasName);
        if ("*".equals(propertyName)) {
            if (!fieldResults.isEmpty()) {
                throw new QueryException("Using return-propertys together with * syntax is not supported.");
            }
            String selectFragment = collectionPersister.selectFragment(aliasName, collectionSuffix);
            ++this.aliasesFound;
            return selectFragment + ", " + this.resolveProperties(aliasName, propertyName);
        }
        if ("element.*".equals(propertyName)) {
            return this.resolveProperties(aliasName, "*");
        }
        String[] columnAliases = (String[])fieldResults.get(propertyName);
        if (columnAliases == null) {
            columnAliases = collectionPersister.getCollectionPropertyColumnAliases(propertyName, collectionSuffix);
        }
        if (columnAliases == null || columnAliases.length == 0) {
            throw new QueryException("No column name found for property [" + propertyName + "] for alias [" + aliasName + "]", this.originalQueryString);
        }
        if (columnAliases.length != 1) {
            throw new QueryException("SQL queries only support properties mapped to a single column - property [" + propertyName + "] is mapped to " + columnAliases.length + " columns.", this.originalQueryString);
        }
        ++this.aliasesFound;
        return columnAliases[0];
    }

    private String resolveProperties(String aliasName, String propertyName) {
        Map fieldResults = this.context.getPropertyResultsMapByAlias(aliasName);
        SQLLoadable persister = this.context.getEntityPersisterByAlias(aliasName);
        String suffix = this.context.getEntitySuffixByAlias(aliasName);
        if ("*".equals(propertyName)) {
            if (!fieldResults.isEmpty()) {
                throw new QueryException("Using return-propertys together with * syntax is not supported.");
            }
            ++this.aliasesFound;
            return persister.selectFragment(aliasName, suffix);
        }
        String[] columnAliases = (String[])fieldResults.get(propertyName);
        if (columnAliases == null) {
            columnAliases = persister.getSubclassPropertyColumnAliases(propertyName, suffix);
        }
        if (columnAliases == null || columnAliases.length == 0) {
            throw new QueryException("No column name found for property [" + propertyName + "] for alias [" + aliasName + "]", this.originalQueryString);
        }
        if (columnAliases.length != 1) {
            throw new QueryException("SQL queries only support properties mapped to a single column - property [" + propertyName + "] is mapped to " + columnAliases.length + " columns.", this.originalQueryString);
        }
        ++this.aliasesFound;
        return columnAliases[0];
    }

    private String substituteParams(String sqlString) {
        ParameterSubstitutionRecognizer recognizer = new ParameterSubstitutionRecognizer(this.factory);
        ParameterParser.parse(sqlString, recognizer);
        this.paramValueBinders = recognizer.getParameterValueBinders();
        return recognizer.result.toString();
    }

    public static class ParameterSubstitutionRecognizer
    implements ParameterParser.Recognizer {
        StringBuilder result = new StringBuilder();
        int jdbcPositionalParamCount;
        private List<ParameterBinder> paramValueBinders;

        public ParameterSubstitutionRecognizer(SessionFactoryImplementor factory) {
            this.jdbcPositionalParamCount = factory.getSessionFactoryOptions().jdbcStyleParamsZeroBased() ? 0 : 1;
        }

        @Override
        public void outParameter(int position) {
            this.result.append('?');
        }

        @Override
        public void ordinalParameter(int position) {
            this.result.append('?');
            this.registerPositionParamBinder(this.jdbcPositionalParamCount++);
        }

        private void registerPositionParamBinder(int label) {
            if (this.paramValueBinders == null) {
                this.paramValueBinders = new ArrayList<ParameterBinder>();
            }
            this.paramValueBinders.add(new PositionalParamBinder(label));
        }

        @Override
        public void jpaPositionalParameter(int name, int position) {
            this.result.append('?');
            this.registerPositionParamBinder(name);
        }

        @Override
        public void namedParameter(String name, int position) {
            this.result.append('?');
            this.registerNamedParamBinder(name);
        }

        private void registerNamedParamBinder(String name) {
            if (this.paramValueBinders == null) {
                this.paramValueBinders = new ArrayList<ParameterBinder>();
            }
            this.paramValueBinders.add(new NamedParamBinder(name));
        }

        @Override
        public void other(char character) {
            this.result.append(character);
        }

        public List<ParameterBinder> getParameterValueBinders() {
            return this.paramValueBinders;
        }

        @Override
        public void complete() {
        }
    }

    static interface ParserContext {
        public boolean isEntityAlias(String var1);

        public SQLLoadable getEntityPersisterByAlias(String var1);

        public String getEntitySuffixByAlias(String var1);

        public boolean isCollectionAlias(String var1);

        public SQLLoadableCollection getCollectionPersisterByAlias(String var1);

        public String getCollectionSuffixByAlias(String var1);

        public Map getPropertyResultsMapByAlias(String var1);
    }
}

