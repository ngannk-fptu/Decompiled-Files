/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.core.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.metadata.CallMetaDataProvider;
import org.springframework.jdbc.core.metadata.CallMetaDataProviderFactory;
import org.springframework.jdbc.core.metadata.CallParameterMetaData;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class CallMetaDataContext {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private String procedureName;
    @Nullable
    private String catalogName;
    @Nullable
    private String schemaName;
    private List<SqlParameter> callParameters = new ArrayList<SqlParameter>();
    @Nullable
    private String actualFunctionReturnName;
    private Set<String> limitedInParameterNames = new HashSet<String>();
    private List<String> outParameterNames = new ArrayList<String>();
    private boolean function = false;
    private boolean returnValueRequired = false;
    private boolean accessCallParameterMetaData = true;
    private boolean namedBinding;
    @Nullable
    private CallMetaDataProvider metaDataProvider;

    public void setFunctionReturnName(String functionReturnName) {
        this.actualFunctionReturnName = functionReturnName;
    }

    public String getFunctionReturnName() {
        return this.actualFunctionReturnName != null ? this.actualFunctionReturnName : "return";
    }

    public void setLimitedInParameterNames(Set<String> limitedInParameterNames) {
        this.limitedInParameterNames = limitedInParameterNames;
    }

    public Set<String> getLimitedInParameterNames() {
        return this.limitedInParameterNames;
    }

    public void setOutParameterNames(List<String> outParameterNames) {
        this.outParameterNames = outParameterNames;
    }

    public List<String> getOutParameterNames() {
        return this.outParameterNames;
    }

    public void setProcedureName(@Nullable String procedureName) {
        this.procedureName = procedureName;
    }

    @Nullable
    public String getProcedureName() {
        return this.procedureName;
    }

    public void setCatalogName(@Nullable String catalogName) {
        this.catalogName = catalogName;
    }

    @Nullable
    public String getCatalogName() {
        return this.catalogName;
    }

    public void setSchemaName(@Nullable String schemaName) {
        this.schemaName = schemaName;
    }

    @Nullable
    public String getSchemaName() {
        return this.schemaName;
    }

    public void setFunction(boolean function) {
        this.function = function;
    }

    public boolean isFunction() {
        return this.function;
    }

    public void setReturnValueRequired(boolean returnValueRequired) {
        this.returnValueRequired = returnValueRequired;
    }

    public boolean isReturnValueRequired() {
        return this.returnValueRequired;
    }

    public void setAccessCallParameterMetaData(boolean accessCallParameterMetaData) {
        this.accessCallParameterMetaData = accessCallParameterMetaData;
    }

    public boolean isAccessCallParameterMetaData() {
        return this.accessCallParameterMetaData;
    }

    public void setNamedBinding(boolean namedBinding) {
        this.namedBinding = namedBinding;
    }

    public boolean isNamedBinding() {
        return this.namedBinding;
    }

    public void initializeMetaData(DataSource dataSource) {
        this.metaDataProvider = CallMetaDataProviderFactory.createMetaDataProvider(dataSource, this);
    }

    private CallMetaDataProvider obtainMetaDataProvider() {
        Assert.state((this.metaDataProvider != null ? 1 : 0) != 0, (String)"No CallMetaDataProvider - call initializeMetaData first");
        return this.metaDataProvider;
    }

    public SqlParameter createReturnResultSetParameter(String parameterName, RowMapper<?> rowMapper) {
        CallMetaDataProvider provider = this.obtainMetaDataProvider();
        if (provider.isReturnResultSetSupported()) {
            return new SqlReturnResultSet(parameterName, rowMapper);
        }
        if (provider.isRefCursorSupported()) {
            return new SqlOutParameter(parameterName, provider.getRefCursorSqlType(), rowMapper);
        }
        throw new InvalidDataAccessApiUsageException("Return of a ResultSet from a stored procedure is not supported");
    }

    @Nullable
    public String getScalarOutParameterName() {
        if (this.isFunction()) {
            return this.getFunctionReturnName();
        }
        if (this.outParameterNames.size() > 1) {
            this.logger.info((Object)"Accessing single output value when procedure has more than one output parameter");
        }
        return !this.outParameterNames.isEmpty() ? this.outParameterNames.get(0) : null;
    }

    public List<SqlParameter> getCallParameters() {
        return this.callParameters;
    }

    public void processParameters(List<SqlParameter> parameters) {
        this.callParameters = this.reconcileParameters(parameters);
    }

    protected List<SqlParameter> reconcileParameters(List<SqlParameter> parameters) {
        CallMetaDataProvider provider = this.obtainMetaDataProvider();
        ArrayList<SqlParameter> declaredReturnParams = new ArrayList<SqlParameter>();
        LinkedHashMap<String, SqlParameter> declaredParams = new LinkedHashMap<String, SqlParameter>();
        boolean returnDeclared = false;
        ArrayList<String> outParamNames = new ArrayList<String>();
        ArrayList<String> metaDataParamNames = new ArrayList<String>();
        for (CallParameterMetaData meta : provider.getCallParameterMetaData()) {
            if (meta.isReturnParameter()) continue;
            metaDataParamNames.add(CallMetaDataContext.lowerCase(meta.getParameterName()));
        }
        for (SqlParameter param : parameters) {
            if (param.isResultsParameter()) {
                declaredReturnParams.add(param);
                continue;
            }
            String paramName = param.getName();
            if (paramName == null) {
                throw new IllegalArgumentException("Anonymous parameters not supported for calls - please specify a name for the parameter of SQL type " + param.getSqlType());
            }
            String paramNameToMatch = CallMetaDataContext.lowerCase(provider.parameterNameToUse(paramName));
            declaredParams.put(paramNameToMatch, param);
            if (!(param instanceof SqlOutParameter)) continue;
            outParamNames.add(paramName);
            if (!this.isFunction() || metaDataParamNames.contains(paramNameToMatch) || returnDeclared) continue;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using declared out parameter '" + paramName + "' for function return value"));
            }
            this.actualFunctionReturnName = paramName;
            returnDeclared = true;
        }
        this.setOutParameterNames(outParamNames);
        ArrayList<SqlParameter> workParams = new ArrayList<SqlParameter>(declaredReturnParams);
        if (!provider.isProcedureColumnMetaDataUsed()) {
            workParams.addAll(declaredParams.values());
            return workParams;
        }
        HashMap limitedInParamNamesMap = CollectionUtils.newHashMap((int)this.limitedInParameterNames.size());
        for (String limitedParamName : this.limitedInParameterNames) {
            limitedInParamNamesMap.put(CallMetaDataContext.lowerCase(provider.parameterNameToUse(limitedParamName)), limitedParamName);
        }
        for (CallParameterMetaData meta : provider.getCallParameterMetaData()) {
            String paramName = meta.getParameterName();
            String paramNameToCheck = null;
            if (paramName != null) {
                paramNameToCheck = CallMetaDataContext.lowerCase(provider.parameterNameToUse(paramName));
            }
            String paramNameToUse = provider.parameterNameToUse(paramName);
            if (declaredParams.containsKey(paramNameToCheck) || meta.isReturnParameter() && returnDeclared) {
                SqlParameter param;
                if (meta.isReturnParameter()) {
                    param = (SqlParameter)declaredParams.get(this.getFunctionReturnName());
                    if (param == null && !this.getOutParameterNames().isEmpty()) {
                        param = (SqlParameter)declaredParams.get(this.getOutParameterNames().get(0).toLowerCase());
                    }
                    if (param == null) {
                        throw new InvalidDataAccessApiUsageException("Unable to locate declared parameter for function return value -  add an SqlOutParameter with name '" + this.getFunctionReturnName() + "'");
                    }
                    this.actualFunctionReturnName = param.getName();
                } else {
                    param = (SqlParameter)declaredParams.get(paramNameToCheck);
                }
                if (param == null) continue;
                workParams.add(param);
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)("Using declared parameter for '" + (paramNameToUse != null ? paramNameToUse : this.getFunctionReturnName()) + "'"));
                continue;
            }
            if (meta.isReturnParameter()) {
                if (!this.isFunction() && !this.isReturnValueRequired() && paramName != null && provider.byPassReturnParameter(paramName)) {
                    if (!this.logger.isDebugEnabled()) continue;
                    this.logger.debug((Object)("Bypassing meta-data return parameter for '" + paramName + "'"));
                    continue;
                }
                String returnNameToUse = StringUtils.hasLength((String)paramNameToUse) ? paramNameToUse : this.getFunctionReturnName();
                workParams.add(provider.createDefaultOutParameter(returnNameToUse, meta));
                if (this.isFunction()) {
                    this.actualFunctionReturnName = returnNameToUse;
                    outParamNames.add(returnNameToUse);
                }
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)("Added meta-data return parameter for '" + returnNameToUse + "'"));
                continue;
            }
            if (paramNameToUse == null) {
                paramNameToUse = "";
            }
            if (meta.isOutParameter()) {
                workParams.add(provider.createDefaultOutParameter(paramNameToUse, meta));
                outParamNames.add(paramNameToUse);
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)("Added meta-data out parameter for '" + paramNameToUse + "'"));
                continue;
            }
            if (meta.isInOutParameter()) {
                workParams.add(provider.createDefaultInOutParameter(paramNameToUse, meta));
                outParamNames.add(paramNameToUse);
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)("Added meta-data in-out parameter for '" + paramNameToUse + "'"));
                continue;
            }
            if (this.limitedInParameterNames.isEmpty() || limitedInParamNamesMap.containsKey(CallMetaDataContext.lowerCase(paramNameToUse))) {
                workParams.add(provider.createDefaultInParameter(paramNameToUse, meta));
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)("Added meta-data in parameter for '" + paramNameToUse + "'"));
                continue;
            }
            if (!this.logger.isDebugEnabled()) continue;
            this.logger.debug((Object)("Limited set of parameters " + limitedInParamNamesMap.keySet() + " skipped parameter for '" + paramNameToUse + "'"));
        }
        return workParams;
    }

    public Map<String, Object> matchInParameterValuesWithCallParameters(SqlParameterSource parameterSource) {
        Map<String, String> caseInsensitiveParameterNames = SqlParameterSourceUtils.extractCaseInsensitiveParameterNames(parameterSource);
        HashMap callParameterNames = CollectionUtils.newHashMap((int)this.callParameters.size());
        HashMap matchedParameters = CollectionUtils.newHashMap((int)this.callParameters.size());
        for (SqlParameter parameter : this.callParameters) {
            if (!parameter.isInputValueProvided()) continue;
            String parameterName = parameter.getName();
            String parameterNameToMatch = this.obtainMetaDataProvider().parameterNameToUse(parameterName);
            if (parameterNameToMatch != null) {
                callParameterNames.put(parameterNameToMatch.toLowerCase(), parameterName);
            }
            if (parameterName == null) continue;
            if (parameterSource.hasValue(parameterName)) {
                matchedParameters.put(parameterName, SqlParameterSourceUtils.getTypedValue(parameterSource, parameterName));
                continue;
            }
            String lowerCaseName = parameterName.toLowerCase();
            if (parameterSource.hasValue(lowerCaseName)) {
                matchedParameters.put(parameterName, SqlParameterSourceUtils.getTypedValue(parameterSource, lowerCaseName));
                continue;
            }
            String englishLowerCaseName = parameterName.toLowerCase(Locale.ENGLISH);
            if (parameterSource.hasValue(englishLowerCaseName)) {
                matchedParameters.put(parameterName, SqlParameterSourceUtils.getTypedValue(parameterSource, englishLowerCaseName));
                continue;
            }
            String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(parameterName);
            if (parameterSource.hasValue(propertyName)) {
                matchedParameters.put(parameterName, SqlParameterSourceUtils.getTypedValue(parameterSource, propertyName));
                continue;
            }
            if (caseInsensitiveParameterNames.containsKey(lowerCaseName)) {
                String sourceName = caseInsensitiveParameterNames.get(lowerCaseName);
                matchedParameters.put(parameterName, SqlParameterSourceUtils.getTypedValue(parameterSource, sourceName));
                continue;
            }
            if (!this.logger.isInfoEnabled()) continue;
            this.logger.info((Object)("Unable to locate the corresponding parameter value for '" + parameterName + "' within the parameter values provided: " + caseInsensitiveParameterNames.values()));
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Matching " + caseInsensitiveParameterNames.values() + " with " + callParameterNames.values()));
            this.logger.debug((Object)("Found match for " + matchedParameters.keySet()));
        }
        return matchedParameters;
    }

    public Map<String, ?> matchInParameterValuesWithCallParameters(Map<String, ?> inParameters) {
        String parameterNameToMatch;
        CallMetaDataProvider provider = this.obtainMetaDataProvider();
        if (!provider.isProcedureColumnMetaDataUsed()) {
            return inParameters;
        }
        HashMap callParameterNames = CollectionUtils.newHashMap((int)this.callParameters.size());
        for (SqlParameter parameter : this.callParameters) {
            String parameterName2;
            if (!parameter.isInputValueProvided() || (parameterNameToMatch = provider.parameterNameToUse(parameterName2 = parameter.getName())) == null) continue;
            callParameterNames.put(parameterNameToMatch.toLowerCase(), parameterName2);
        }
        HashMap matchedParameters = CollectionUtils.newHashMap((int)inParameters.size());
        inParameters.forEach((parameterName, parameterValue) -> {
            String parameterNameToMatch = provider.parameterNameToUse((String)parameterName);
            String callParameterName = (String)callParameterNames.get(CallMetaDataContext.lowerCase(parameterNameToMatch));
            if (callParameterName == null) {
                if (this.logger.isDebugEnabled()) {
                    Object value = parameterValue;
                    if (value instanceof SqlParameterValue) {
                        value = ((SqlParameterValue)value).getValue();
                    }
                    if (value != null) {
                        this.logger.debug((Object)("Unable to locate the corresponding IN or IN-OUT parameter for \"" + parameterName + "\" in the parameters used: " + callParameterNames.keySet()));
                    }
                }
            } else {
                matchedParameters.put(callParameterName, parameterValue);
            }
        });
        if (matchedParameters.size() < callParameterNames.size()) {
            for (String parameterName2 : callParameterNames.keySet()) {
                parameterNameToMatch = provider.parameterNameToUse(parameterName2);
                String callParameterName = (String)callParameterNames.get(CallMetaDataContext.lowerCase(parameterNameToMatch));
                if (matchedParameters.containsKey(callParameterName) || !this.logger.isInfoEnabled()) continue;
                this.logger.info((Object)("Unable to locate the corresponding parameter value for '" + parameterName2 + "' within the parameter values provided: " + inParameters.keySet()));
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Matching " + inParameters.keySet() + " with " + callParameterNames.values()));
            this.logger.debug((Object)("Found match for " + matchedParameters.keySet()));
        }
        return matchedParameters;
    }

    public Map<String, ?> matchInParameterValuesWithCallParameters(Object[] parameterValues) {
        HashMap matchedParameters = CollectionUtils.newHashMap((int)parameterValues.length);
        int i = 0;
        for (SqlParameter parameter : this.callParameters) {
            if (!parameter.isInputValueProvided()) continue;
            String parameterName = parameter.getName();
            matchedParameters.put(parameterName, parameterValues[i++]);
        }
        return matchedParameters;
    }

    public String createCallString() {
        StringBuilder callString;
        String catalogNameToUse;
        String schemaNameToUse;
        Assert.state((this.metaDataProvider != null ? 1 : 0) != 0, (String)"No CallMetaDataProvider available");
        int parameterCount = 0;
        if (this.metaDataProvider.isSupportsSchemasInProcedureCalls() && !this.metaDataProvider.isSupportsCatalogsInProcedureCalls()) {
            schemaNameToUse = this.metaDataProvider.catalogNameToUse(this.getCatalogName());
            catalogNameToUse = this.metaDataProvider.schemaNameToUse(this.getSchemaName());
        } else {
            catalogNameToUse = this.metaDataProvider.catalogNameToUse(this.getCatalogName());
            schemaNameToUse = this.metaDataProvider.schemaNameToUse(this.getSchemaName());
        }
        if (this.isFunction() || this.isReturnValueRequired()) {
            callString = new StringBuilder("{? = call ");
            parameterCount = -1;
        } else {
            callString = new StringBuilder("{call ");
        }
        if (StringUtils.hasLength((String)catalogNameToUse)) {
            callString.append(catalogNameToUse).append('.');
        }
        if (StringUtils.hasLength((String)schemaNameToUse)) {
            callString.append(schemaNameToUse).append('.');
        }
        callString.append(this.metaDataProvider.procedureNameToUse(this.getProcedureName()));
        callString.append('(');
        for (SqlParameter parameter : this.callParameters) {
            if (parameter.isResultsParameter()) continue;
            if (parameterCount > 0) {
                callString.append(", ");
            }
            if (parameterCount >= 0) {
                callString.append(this.createParameterBinding(parameter));
            }
            ++parameterCount;
        }
        callString.append(")}");
        return callString.toString();
    }

    protected String createParameterBinding(SqlParameter parameter) {
        return this.isNamedBinding() ? parameter.getName() + " => ?" : "?";
    }

    private static String lowerCase(@Nullable String paramName) {
        return paramName != null ? paramName.toLowerCase() : "";
    }
}

