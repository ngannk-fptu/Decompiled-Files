/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ApplicationIntent;
import com.microsoft.sqlserver.jdbc.AuthenticationScheme;
import com.microsoft.sqlserver.jdbc.ColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.DatetimeType;
import com.microsoft.sqlserver.jdbc.EncryptOption;
import com.microsoft.sqlserver.jdbc.IPAddressPreference;
import com.microsoft.sqlserver.jdbc.PrepareMethod;
import com.microsoft.sqlserver.jdbc.SSLProtocol;
import com.microsoft.sqlserver.jdbc.SqlAuthentication;

enum SQLServerDriverStringProperty {
    APPLICATION_INTENT("applicationIntent", ApplicationIntent.READ_WRITE.toString()),
    APPLICATION_NAME("applicationName", "Microsoft JDBC Driver for SQL Server"),
    PREPARE_METHOD("prepareMethod", PrepareMethod.PREPEXEC.toString()),
    DATABASE_NAME("databaseName", ""),
    FAILOVER_PARTNER("failoverPartner", ""),
    HOSTNAME_IN_CERTIFICATE("hostNameInCertificate", ""),
    INSTANCE_NAME("instanceName", ""),
    JAAS_CONFIG_NAME("jaasConfigurationName", "SQLJDBCDriver"),
    PASSWORD("password", ""),
    RESPONSE_BUFFERING("responseBuffering", "adaptive"),
    SELECT_METHOD("selectMethod", "direct"),
    DOMAIN("domain", ""),
    SERVER_NAME("serverName", ""),
    IPADDRESS_PREFERENCE("iPAddressPreference", IPAddressPreference.IPV4_FIRST.toString()),
    SERVER_SPN("serverSpn", ""),
    REALM("realm", ""),
    SOCKET_FACTORY_CLASS("socketFactoryClass", ""),
    SOCKET_FACTORY_CONSTRUCTOR_ARG("socketFactoryConstructorArg", ""),
    TRUST_STORE_TYPE("trustStoreType", "JKS"),
    TRUST_STORE("trustStore", ""),
    TRUST_STORE_PASSWORD("trustStorePassword", ""),
    TRUST_MANAGER_CLASS("trustManagerClass", ""),
    TRUST_MANAGER_CONSTRUCTOR_ARG("trustManagerConstructorArg", ""),
    USER("user", ""),
    WORKSTATION_ID("workstationID", ""),
    AUTHENTICATION_SCHEME("authenticationScheme", AuthenticationScheme.NATIVE_AUTHENTICATION.toString()),
    AUTHENTICATION("authentication", SqlAuthentication.NOT_SPECIFIED.toString()),
    ACCESS_TOKEN("accessToken", ""),
    COLUMN_ENCRYPTION("columnEncryptionSetting", ColumnEncryptionSetting.DISABLED.toString()),
    ENCLAVE_ATTESTATION_URL("enclaveAttestationUrl", ""),
    ENCLAVE_ATTESTATION_PROTOCOL("enclaveAttestationProtocol", ""),
    KEY_STORE_AUTHENTICATION("keyStoreAuthentication", ""),
    KEY_STORE_SECRET("keyStoreSecret", ""),
    KEY_STORE_LOCATION("keyStoreLocation", ""),
    SSL_PROTOCOL("sslProtocol", SSLProtocol.TLS.toString()),
    MSI_CLIENT_ID("msiClientId", ""),
    KEY_VAULT_PROVIDER_CLIENT_ID("keyVaultProviderClientId", ""),
    KEY_VAULT_PROVIDER_CLIENT_KEY("keyVaultProviderClientKey", ""),
    KEY_STORE_PRINCIPAL_ID("keyStorePrincipalId", ""),
    CLIENT_CERTIFICATE("clientCertificate", ""),
    CLIENT_KEY("clientKey", ""),
    CLIENT_KEY_PASSWORD("clientKeyPassword", ""),
    AAD_SECURE_PRINCIPAL_ID("AADSecurePrincipalId", ""),
    AAD_SECURE_PRINCIPAL_SECRET("AADSecurePrincipalSecret", ""),
    MAX_RESULT_BUFFER("maxResultBuffer", "-1"),
    ENCRYPT("encrypt", EncryptOption.TRUE.toString()),
    SERVER_CERTIFICATE("serverCertificate", ""),
    DATETIME_DATATYPE("datetimeParameterType", DatetimeType.DATETIME2.toString()),
    ACCESS_TOKEN_CALLBACK_CLASS("accessTokenCallbackClass", "");

    private final String name;
    private final String defaultValue;

    private SQLServerDriverStringProperty(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    String getDefaultValue() {
        return this.defaultValue;
    }

    public String toString() {
        return this.name;
    }
}

