/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.AASAttestationParameters;
import com.microsoft.sqlserver.jdbc.AASAttestationResponse;
import com.microsoft.sqlserver.jdbc.ColumnEncryptionVersion;
import com.microsoft.sqlserver.jdbc.EnclaveCacheEntry;
import com.microsoft.sqlserver.jdbc.EnclaveSession;
import com.microsoft.sqlserver.jdbc.EnclaveSessionCache;
import com.microsoft.sqlserver.jdbc.ISQLServerEnclaveProvider;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.ParameterMetaDataCache;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLServerAASEnclaveProvider
implements ISQLServerEnclaveProvider {
    private static EnclaveSessionCache enclaveCache = new EnclaveSessionCache();
    private AASAttestationParameters aasParams = null;
    private AASAttestationResponse hgsResponse = null;
    private String attestationUrl = null;
    private EnclaveSession enclaveSession = null;

    @Override
    public void getAttestationParameters(String url) throws SQLServerException {
        if (null == this.aasParams) {
            this.attestationUrl = url;
            try {
                this.aasParams = new AASAttestationParameters(this.attestationUrl);
            }
            catch (IOException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
    }

    @Override
    public ArrayList<byte[]> createEnclaveSession(SQLServerConnection connection, SQLServerStatement statement, String userSql, String preparedTypeDefinitions, Parameter[] params, ArrayList<String> parameterNames) throws SQLServerException {
        StringBuilder keyLookup = new StringBuilder(connection.getServerName()).append(connection.getCatalog()).append(this.attestationUrl);
        EnclaveCacheEntry entry = enclaveCache.getSession(keyLookup.toString());
        if (null != entry) {
            this.enclaveSession = entry.getEnclaveSession();
            this.aasParams = (AASAttestationParameters)entry.getBaseAttestationRequest();
        }
        ArrayList<byte[]> b = this.describeParameterEncryption(connection, statement, userSql, preparedTypeDefinitions, params, parameterNames);
        if (connection.enclaveEstablished()) {
            return b;
        }
        if (null != this.hgsResponse && !connection.enclaveEstablished()) {
            try {
                this.enclaveSession = new EnclaveSession(this.hgsResponse.getSessionID(), this.aasParams.createSessionSecret(this.hgsResponse.getDHpublicKey()));
                enclaveCache.addEntry(connection.getServerName(), connection.getCatalog(), connection.enclaveAttestationUrl, this.aasParams, this.enclaveSession);
            }
            catch (GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(connection, this, e.getLocalizedMessage(), "0", false);
            }
        }
        return b;
    }

    @Override
    public void invalidateEnclaveSession() {
        if (null != this.enclaveSession) {
            enclaveCache.removeEntry(this.enclaveSession);
        }
        this.enclaveSession = null;
        this.aasParams = null;
        this.attestationUrl = null;
    }

    @Override
    public EnclaveSession getEnclaveSession() {
        return this.enclaveSession;
    }

    private void validateAttestationResponse() throws SQLServerException {
        if (null != this.hgsResponse) {
            try {
                this.hgsResponse.validateToken(this.attestationUrl, this.aasParams.getNonce());
                this.hgsResponse.validateDHPublicKey(this.aasParams.getNonce());
            }
            catch (GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private ArrayList<byte[]> describeParameterEncryption(SQLServerConnection connection, SQLServerStatement statement, String userSql, String preparedTypeDefinitions, Parameter[] params, ArrayList<String> parameterNames) throws SQLServerException {
        String SDPE1 = "EXEC sp_describe_parameter_encryption ?,?";
        String SDPE2 = "EXEC sp_describe_parameter_encryption ?,?,?";
        ArrayList<byte[]> enclaveRequestedCEKs = new ArrayList<byte[]>();
        try (PreparedStatement stmt = connection.prepareStatement(connection.enclaveEstablished() ? "EXEC sp_describe_parameter_encryption ?,?" : "EXEC sp_describe_parameter_encryption ?,?,?");){
            if (connection.getServerColumnEncryptionVersion() != ColumnEncryptionVersion.AE_V2 && params != null && params.length != 0) {
                if (ParameterMetaDataCache.getQueryMetadata(params, parameterNames, connection, statement, userSql)) return enclaveRequestedCEKs;
            }
            try (ResultSet rs = connection.enclaveEstablished() ? this.executeSDPEv1(stmt, userSql, preparedTypeDefinitions) : this.executeSDPEv2(stmt, userSql, preparedTypeDefinitions, this.aasParams);){
                if (null == rs) {
                    ArrayList<byte[]> arrayList = enclaveRequestedCEKs;
                    return arrayList;
                }
                this.processSDPEv1(userSql, preparedTypeDefinitions, params, parameterNames, connection, statement, stmt, rs, enclaveRequestedCEKs);
                if (!connection.isAEv2()) return enclaveRequestedCEKs;
                if (!stmt.getMoreResults()) return enclaveRequestedCEKs;
                try (SQLServerResultSet hgsRs = (SQLServerResultSet)stmt.getResultSet();){
                    if (hgsRs.next()) {
                        this.hgsResponse = new AASAttestationResponse(hgsRs.getBytes(1));
                        this.validateAttestationResponse();
                        return enclaveRequestedCEKs;
                    }
                    SQLServerException.makeFromDriverError(null, this, SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), "0", false);
                    return enclaveRequestedCEKs;
                }
            }
        }
        catch (IOException | SQLException e) {
            if (!(e instanceof SQLServerException)) throw new SQLServerException(SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), null, 0, (Throwable)e);
            throw (SQLServerException)e;
        }
    }
}

