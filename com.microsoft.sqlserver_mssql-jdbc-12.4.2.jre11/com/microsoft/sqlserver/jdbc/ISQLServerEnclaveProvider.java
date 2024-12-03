/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseAttestationRequest;
import com.microsoft.sqlserver.jdbc.CekTableEntry;
import com.microsoft.sqlserver.jdbc.ColumnEncryptionVersion;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DescribeParameterEncryptionResultSet1;
import com.microsoft.sqlserver.jdbc.DescribeParameterEncryptionResultSet2;
import com.microsoft.sqlserver.jdbc.EnclaveSession;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.ParameterMetaDataCache;
import com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256Algorithm;
import com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256EncryptionKey;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

interface ISQLServerEnclaveProvider {
    default public byte[] getEnclavePackage(String userSQL, ArrayList<byte[]> enclaveCEKs) throws SQLServerException {
        EnclaveSession enclaveSession = this.getEnclaveSession();
        if (null != enclaveSession) {
            try {
                ByteArrayOutputStream enclavePackage = new ByteArrayOutputStream();
                enclavePackage.write(enclaveSession.getSessionID());
                ByteArrayOutputStream keys = new ByteArrayOutputStream();
                byte[] randomGUID = new byte[16];
                new SecureRandom().nextBytes(randomGUID);
                keys.write(randomGUID);
                keys.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(enclaveSession.getCounter()).array());
                keys.write(MessageDigest.getInstance("SHA-256").digest(userSQL.getBytes(StandardCharsets.UTF_16LE)));
                for (byte[] b : enclaveCEKs) {
                    keys.write(b);
                }
                enclaveCEKs.clear();
                SQLServerAeadAes256CbcHmac256EncryptionKey encryptedKey = new SQLServerAeadAes256CbcHmac256EncryptionKey(enclaveSession.getSessionSecret(), "AEAD_AES_256_CBC_HMAC_SHA256");
                SQLServerAeadAes256CbcHmac256Algorithm algo = new SQLServerAeadAes256CbcHmac256Algorithm(encryptedKey, SQLServerEncryptionType.RANDOMIZED, 1);
                enclavePackage.write(algo.encryptData(keys.toByteArray()));
                return enclavePackage.toByteArray();
            }
            catch (SQLServerException | IOException | GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
        return null;
    }

    default public ResultSet executeSDPEv2(PreparedStatement stmt, String userSql, String preparedTypeDefinitions, BaseAttestationRequest req) throws SQLException, IOException {
        ((SQLServerPreparedStatement)stmt).isInternalEncryptionQuery = true;
        stmt.setNString(1, userSql);
        if (preparedTypeDefinitions != null && preparedTypeDefinitions.length() != 0) {
            stmt.setNString(2, preparedTypeDefinitions);
        } else {
            stmt.setNString(2, "");
        }
        stmt.setBytes(3, req.getBytes());
        return ((SQLServerPreparedStatement)stmt).executeQueryInternal();
    }

    default public ResultSet executeSDPEv1(PreparedStatement stmt, String userSql, String preparedTypeDefinitions) throws SQLException {
        ((SQLServerPreparedStatement)stmt).isInternalEncryptionQuery = true;
        stmt.setNString(1, userSql);
        if (preparedTypeDefinitions != null && preparedTypeDefinitions.length() != 0) {
            stmt.setNString(2, preparedTypeDefinitions);
        } else {
            stmt.setNString(2, "");
        }
        return ((SQLServerPreparedStatement)stmt).executeQueryInternal();
    }

    default public void processSDPEv1(String userSql, String preparedTypeDefinitions, Parameter[] params, ArrayList<String> parameterNames, SQLServerConnection connection, SQLServerStatement sqlServerStatement, PreparedStatement stmt, ResultSet rs, ArrayList<byte[]> enclaveRequestedCEKs) throws SQLException {
        HashMap<Integer, CekTableEntry> cekList = new HashMap<Integer, CekTableEntry>();
        CekTableEntry cekEntry = null;
        boolean isRequestedByEnclave = false;
        SQLServerPreparedStatement statement = (SQLServerPreparedStatement)stmt;
        if (null != sqlServerStatement && sqlServerStatement.hasColumnEncryptionKeyStoreProvidersRegistered()) {
            statement.registerColumnEncryptionKeyStoreProvidersOnStatement(sqlServerStatement.statementColumnEncryptionKeyStoreProviders);
        }
        while (rs.next()) {
            int currentOrdinal = rs.getInt(DescribeParameterEncryptionResultSet1.KEYORDINAL.value());
            if (!cekList.containsKey(currentOrdinal)) {
                cekEntry = new CekTableEntry(currentOrdinal);
                cekList.put(cekEntry.ordinal, cekEntry);
            } else {
                cekEntry = (CekTableEntry)cekList.get(currentOrdinal);
            }
            String keyStoreName = rs.getString(DescribeParameterEncryptionResultSet1.PROVIDERNAME.value());
            String algo = rs.getString(DescribeParameterEncryptionResultSet1.KEYENCRYPTIONALGORITHM.value());
            String keyPath = rs.getString(DescribeParameterEncryptionResultSet1.KEYPATH.value());
            int dbID = rs.getInt(DescribeParameterEncryptionResultSet1.DBID.value());
            byte[] mdVer = rs.getBytes(DescribeParameterEncryptionResultSet1.KEYMDVERSION.value());
            int keyID = rs.getInt(DescribeParameterEncryptionResultSet1.KEYID.value());
            byte[] encryptedKey = rs.getBytes(DescribeParameterEncryptionResultSet1.ENCRYPTEDKEY.value());
            cekEntry.add(encryptedKey, dbID, keyID, rs.getInt(DescribeParameterEncryptionResultSet1.KEYVERSION.value()), mdVer, keyPath, keyStoreName, algo);
            if (ColumnEncryptionVersion.AE_V2.value() <= connection.getServerColumnEncryptionVersion().value()) {
                isRequestedByEnclave = rs.getBoolean(DescribeParameterEncryptionResultSet1.ISREQUESTEDBYENCLAVE.value());
            }
            if (!isRequestedByEnclave) continue;
            byte[] keySignature = rs.getBytes(DescribeParameterEncryptionResultSet1.ENCLAVECMKSIGNATURE.value());
            String serverName = connection.getTrustedServerNameAE();
            SQLServerSecurityUtility.verifyColumnMasterKeyMetadata(connection, statement, keyStoreName, keyPath, serverName, isRequestedByEnclave, keySignature);
            ByteBuffer aev2CekEntry = ByteBuffer.allocate(46);
            aev2CekEntry.order(ByteOrder.LITTLE_ENDIAN).putInt(dbID);
            aev2CekEntry.put(mdVer);
            aev2CekEntry.putShort((short)keyID);
            SQLServerColumnEncryptionKeyStoreProvider provider = SQLServerSecurityUtility.getColumnEncryptionKeyStoreProvider(keyStoreName, connection, statement);
            aev2CekEntry.put(provider.decryptColumnEncryptionKey(keyPath, algo, encryptedKey));
            enclaveRequestedCEKs.add(aev2CekEntry.array());
        }
        if (!stmt.getMoreResults()) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_UnexpectedDescribeParamFormat"), null, 0, false);
        }
        try (ResultSet rs2 = stmt.getResultSet();){
            while (rs2.next() && null != params) {
                String paramName = rs2.getString(DescribeParameterEncryptionResultSet2.PARAMETERNAME.value());
                int paramIndex = parameterNames.indexOf(paramName);
                int cekOrdinal = rs2.getInt(DescribeParameterEncryptionResultSet2.COLUMNENCRYPTIONKEYORDINAL.value());
                cekEntry = (CekTableEntry)cekList.get(cekOrdinal);
                if (null != cekEntry && cekList.size() < cekOrdinal) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEncryptionKeyOrdinal"));
                    Object[] msgArgs = new Object[]{cekOrdinal, cekEntry.getSize()};
                    throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                }
                SQLServerEncryptionType encType = SQLServerEncryptionType.of((byte)rs2.getInt(DescribeParameterEncryptionResultSet2.COLUMNENCRYPTIONTYPE.value()));
                if (SQLServerEncryptionType.PLAINTEXT != encType) {
                    params[paramIndex].cryptoMeta = new CryptoMetadata(cekEntry, (short)cekOrdinal, (byte)rs2.getInt(DescribeParameterEncryptionResultSet2.COLUMNENCRYPTIONALGORITHM.value()), null, encType.value, (byte)rs2.getInt(DescribeParameterEncryptionResultSet2.NORMALIZATIONRULEVERSION.value()));
                    SQLServerSecurityUtility.decryptSymmetricKey(params[paramIndex].cryptoMeta, connection, statement);
                    continue;
                }
                if (!params[paramIndex].getForceEncryption()) continue;
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAETrue_UnencryptedColumn"));
                Object[] msgArgs = new Object[]{userSql, paramIndex + 1};
                SQLServerException.makeFromDriverError(null, connection, form.format(msgArgs), "0", true);
            }
        }
        if (connection.getServerColumnEncryptionVersion() != ColumnEncryptionVersion.AE_V2 && params != null && params.length > 0) {
            ParameterMetaDataCache.addQueryMetadata(params, parameterNames, connection, sqlServerStatement, userSql);
        }
    }

    public void getAttestationParameters(String var1) throws SQLServerException;

    public ArrayList<byte[]> createEnclaveSession(SQLServerConnection var1, SQLServerStatement var2, String var3, String var4, Parameter[] var5, ArrayList<String> var6) throws SQLServerException;

    public void invalidateEnclaveSession();

    public EnclaveSession getEnclaveSession();
}

