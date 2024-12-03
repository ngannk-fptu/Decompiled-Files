/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ColumnEncryptionVersion;
import com.microsoft.sqlserver.jdbc.EnclaveCacheEntry;
import com.microsoft.sqlserver.jdbc.EnclaveSession;
import com.microsoft.sqlserver.jdbc.EnclaveSessionCache;
import com.microsoft.sqlserver.jdbc.ISQLServerEnclaveProvider;
import com.microsoft.sqlserver.jdbc.NoneAttestationParameters;
import com.microsoft.sqlserver.jdbc.NoneAttestationResponse;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.ParameterMetaDataCache;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLServerNoneEnclaveProvider
implements ISQLServerEnclaveProvider {
    private static final EnclaveSessionCache enclaveCache = new EnclaveSessionCache();
    private NoneAttestationParameters noneParams = null;
    private NoneAttestationResponse noneResponse = null;
    private String attestationUrl = null;
    private EnclaveSession enclaveSession = null;

    @Override
    public void getAttestationParameters(String url) throws SQLServerException {
        if (null == this.noneParams) {
            this.attestationUrl = url;
            this.noneParams = new NoneAttestationParameters();
        }
    }

    @Override
    public ArrayList<byte[]> createEnclaveSession(SQLServerConnection connection, SQLServerStatement statement, String userSql, String preparedTypeDefinitions, Parameter[] params, ArrayList<String> parameterNames) throws SQLServerException {
        StringBuilder keyLookup = new StringBuilder(connection.getServerName()).append(connection.getCatalog()).append(this.attestationUrl);
        EnclaveCacheEntry entry = enclaveCache.getSession(keyLookup.toString());
        if (null != entry) {
            this.enclaveSession = entry.getEnclaveSession();
            this.noneParams = (NoneAttestationParameters)entry.getBaseAttestationRequest();
        }
        ArrayList<byte[]> b = this.describeParameterEncryption(connection, statement, userSql, preparedTypeDefinitions, params, parameterNames);
        if (connection.enclaveEstablished()) {
            return b;
        }
        if (null != this.noneResponse && !connection.enclaveEstablished()) {
            try {
                this.enclaveSession = new EnclaveSession(this.noneResponse.getSessionID(), this.noneParams.createSessionSecret(this.noneResponse.getDHpublicKey()));
                enclaveCache.addEntry(connection.getServerName(), connection.getCatalog(), connection.enclaveAttestationUrl, this.noneParams, this.enclaveSession);
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
        this.noneParams = null;
        this.attestationUrl = null;
    }

    @Override
    public EnclaveSession getEnclaveSession() {
        return this.enclaveSession;
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
            try (ResultSet rs = connection.enclaveEstablished() ? this.executeSDPEv1(stmt, userSql, preparedTypeDefinitions) : this.executeSDPEv2(stmt, userSql, preparedTypeDefinitions, this.noneParams);){
                if (null == rs) {
                    ArrayList<byte[]> arrayList = enclaveRequestedCEKs;
                    return arrayList;
                }
                this.processSDPEv1(userSql, preparedTypeDefinitions, params, parameterNames, connection, statement, stmt, rs, enclaveRequestedCEKs);
                if (!connection.isAEv2()) return enclaveRequestedCEKs;
                if (!stmt.getMoreResults()) return enclaveRequestedCEKs;
                try (ResultSet noneRs = stmt.getResultSet();){
                    if (noneRs.next()) {
                        this.noneResponse = new NoneAttestationResponse(noneRs.getBytes(1));
                        return enclaveRequestedCEKs;
                    }
                    SQLServerException.makeFromDriverError(null, this, SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), "0", false);
                    return enclaveRequestedCEKs;
                }
            }
        }
        catch (SQLServerException e) {
            throw e;
        }
        catch (IOException | SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), null, 0, (Throwable)e);
        }
    }
}

