/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

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
import com.microsoft.sqlserver.jdbc.VSMAttestationParameters;
import com.microsoft.sqlserver.jdbc.VSMAttestationResponse;
import com.microsoft.sqlserver.jdbc.X509CertificateEntry;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SQLServerVSMEnclaveProvider
implements ISQLServerEnclaveProvider {
    private static EnclaveSessionCache enclaveCache = new EnclaveSessionCache();
    private VSMAttestationParameters vsmParams = null;
    private VSMAttestationResponse hgsResponse = null;
    private String attestationUrl = null;
    private EnclaveSession enclaveSession = null;
    private static ConcurrentHashMap<String, X509CertificateEntry> certificateCache = new ConcurrentHashMap();

    @Override
    public void getAttestationParameters(String url) throws SQLServerException {
        if (null == this.vsmParams) {
            this.attestationUrl = url;
            this.vsmParams = new VSMAttestationParameters();
        }
    }

    @Override
    public ArrayList<byte[]> createEnclaveSession(SQLServerConnection connection, SQLServerStatement statement, String userSql, String preparedTypeDefinitions, Parameter[] params, ArrayList<String> parameterNames) throws SQLServerException {
        StringBuilder keyLookup = new StringBuilder(connection.getServerName()).append(connection.getCatalog()).append(this.attestationUrl);
        EnclaveCacheEntry entry = enclaveCache.getSession(keyLookup.toString());
        if (null != entry) {
            this.enclaveSession = entry.getEnclaveSession();
            this.vsmParams = (VSMAttestationParameters)entry.getBaseAttestationRequest();
        }
        ArrayList<byte[]> b = this.describeParameterEncryption(connection, statement, userSql, preparedTypeDefinitions, params, parameterNames);
        if (connection.enclaveEstablished()) {
            return b;
        }
        if (null != this.hgsResponse && !connection.enclaveEstablished()) {
            try {
                this.enclaveSession = new EnclaveSession(this.hgsResponse.getSessionID(), this.vsmParams.createSessionSecret(this.hgsResponse.getDHpublicKey()));
                enclaveCache.addEntry(connection.getServerName(), connection.getCatalog(), connection.enclaveAttestationUrl, this.vsmParams, this.enclaveSession);
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
        this.vsmParams = null;
        this.attestationUrl = null;
    }

    @Override
    public EnclaveSession getEnclaveSession() {
        return this.enclaveSession;
    }

    private void validateAttestationResponse() throws SQLServerException {
        if (null != this.hgsResponse) {
            try {
                byte[] attestationCerts = this.getAttestationCertificates();
                this.hgsResponse.validateCert(attestationCerts);
                this.hgsResponse.validateStatementSignature();
                this.hgsResponse.validateDHPublicKey();
            }
            catch (IOException | GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
    }

    private byte[] getAttestationCertificates() throws IOException {
        byte[] certData = null;
        X509CertificateEntry cacheEntry = certificateCache.get(this.attestationUrl);
        if (null != cacheEntry && !cacheEntry.expired()) {
            certData = cacheEntry.getCertificates();
        } else if (null != cacheEntry && cacheEntry.expired()) {
            certificateCache.remove(this.attestationUrl);
        }
        if (null == certData) {
            URL url = new URL(this.attestationUrl + "/attestationservice.svc/v2.0/signingCertificates/");
            URLConnection con = url.openConnection();
            byte[] buff = new byte[con.getInputStream().available()];
            con.getInputStream().read(buff, 0, buff.length);
            String s = new String(buff);
            String[] bytesString = s.substring(1, s.length() - 1).split(",");
            certData = new byte[bytesString.length];
            for (int i = 0; i < certData.length; ++i) {
                certData[i] = (byte)Integer.parseInt(bytesString[i]);
            }
            certificateCache.put(this.attestationUrl, new X509CertificateEntry(certData));
        }
        return certData;
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
            try (ResultSet rs = connection.enclaveEstablished() ? this.executeSDPEv1(stmt, userSql, preparedTypeDefinitions) : this.executeSDPEv2(stmt, userSql, preparedTypeDefinitions, this.vsmParams);){
                if (null == rs) {
                    ArrayList<byte[]> arrayList = enclaveRequestedCEKs;
                    return arrayList;
                }
                this.processSDPEv1(userSql, preparedTypeDefinitions, params, parameterNames, connection, statement, stmt, rs, enclaveRequestedCEKs);
                if (!connection.isAEv2()) return enclaveRequestedCEKs;
                if (!stmt.getMoreResults()) return enclaveRequestedCEKs;
                try (SQLServerResultSet hgsRs = (SQLServerResultSet)stmt.getResultSet();){
                    if (hgsRs.next()) {
                        this.hgsResponse = new VSMAttestationResponse(hgsRs.getBytes(1));
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

