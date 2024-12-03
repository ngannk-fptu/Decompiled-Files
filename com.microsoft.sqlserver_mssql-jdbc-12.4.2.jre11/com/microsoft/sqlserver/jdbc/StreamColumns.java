/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CekTable;
import com.microsoft.sqlserver.jdbc.CekTableEntry;
import com.microsoft.sqlserver.jdbc.Column;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.SQLIdentifier;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.StreamColInfo;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.StreamTabName;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.dataclassification.ColumnSensitivity;
import com.microsoft.sqlserver.jdbc.dataclassification.InformationType;
import com.microsoft.sqlserver.jdbc.dataclassification.Label;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityProperty;
import java.util.ArrayList;

final class StreamColumns
extends StreamPacket {
    private Column[] columns;
    private CekTable cekTable = null;
    private boolean shouldHonorAEForRead = false;

    CekTable getCekTable() {
        return this.cekTable;
    }

    StreamColumns() {
        super(129);
    }

    StreamColumns(boolean honorAE) {
        super(129);
        this.shouldHonorAEForRead = honorAE;
    }

    CekTableEntry readCEKTableEntry(TDSReader tdsReader) throws SQLServerException {
        int databaseId = tdsReader.readInt();
        int cekId = tdsReader.readInt();
        int cekVersion = tdsReader.readInt();
        byte[] cekMdVersion = new byte[8];
        tdsReader.readBytes(cekMdVersion, 0, 8);
        int cekValueCount = tdsReader.readUnsignedByte();
        CekTableEntry cekTableEntry = new CekTableEntry(cekValueCount);
        for (int i = 0; i < cekValueCount; ++i) {
            short encryptedCEKlength = tdsReader.readShort();
            byte[] encryptedCek = new byte[encryptedCEKlength];
            tdsReader.readBytes(encryptedCek, 0, encryptedCEKlength);
            int keyStoreLength = tdsReader.readUnsignedByte();
            String keyStoreName = tdsReader.readUnicodeString(keyStoreLength);
            short keyPathLength = tdsReader.readShort();
            String keyPath = tdsReader.readUnicodeString(keyPathLength);
            int algorithmLength = tdsReader.readUnsignedByte();
            String algorithmName = tdsReader.readUnicodeString(algorithmLength);
            cekTableEntry.add(encryptedCek, databaseId, cekId, cekVersion, cekMdVersion, keyPath, keyStoreName, algorithmName);
        }
        return cekTableEntry;
    }

    void readCEKTable(TDSReader tdsReader) throws SQLServerException {
        int tableSize = tdsReader.readShort();
        if (0 != tableSize) {
            this.cekTable = new CekTable(tableSize);
            for (int i = 0; i < tableSize; ++i) {
                this.cekTable.setCekTableEntry(i, this.readCEKTableEntry(tdsReader));
            }
        }
    }

    CryptoMetadata readCryptoMetadata(TDSReader tdsReader) throws SQLServerException {
        short ordinal = 0;
        if (null != this.cekTable) {
            ordinal = tdsReader.readShort();
        }
        TypeInfo typeInfo = TypeInfo.getInstance(tdsReader, false);
        byte algorithmId = (byte)tdsReader.readUnsignedByte();
        String algorithmName = null;
        if (0 == algorithmId) {
            int nameSize = tdsReader.readUnsignedByte();
            algorithmName = tdsReader.readUnicodeString(nameSize);
        }
        byte encryptionType = (byte)tdsReader.readUnsignedByte();
        byte normalizationRuleVersion = (byte)tdsReader.readUnsignedByte();
        CryptoMetadata cryptoMeta = new CryptoMetadata(this.cekTable == null ? null : this.cekTable.getCekTableEntry(ordinal), ordinal, algorithmId, algorithmName, encryptionType, normalizationRuleVersion);
        cryptoMeta.setBaseTypeInfo(typeInfo);
        return cryptoMeta;
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (129 != tdsReader.readUnsignedByte()) assert (false);
        int nTotColumns = tdsReader.readUnsignedShort();
        if (65535 == nTotColumns) {
            return;
        }
        if (tdsReader.getServerSupportsColumnEncryption()) {
            this.readCEKTable(tdsReader);
        }
        this.columns = new Column[nTotColumns];
        for (int numColumns = 0; numColumns < nTotColumns; ++numColumns) {
            TypeInfo typeInfo = TypeInfo.getInstance(tdsReader, true);
            SQLIdentifier tableName = new SQLIdentifier();
            if (SSType.TEXT == typeInfo.getSSType() || SSType.NTEXT == typeInfo.getSSType() || SSType.IMAGE == typeInfo.getSSType()) {
                tableName = tdsReader.readSQLIdentifier();
            }
            CryptoMetadata cryptoMeta = null;
            if (tdsReader.getServerSupportsColumnEncryption() && typeInfo.isEncrypted()) {
                cryptoMeta = this.readCryptoMetadata(tdsReader);
                cryptoMeta.baseTypeInfo.setFlags(typeInfo.getFlagsAsShort());
                typeInfo.setSQLCollation(cryptoMeta.baseTypeInfo.getSQLCollation());
            }
            String columnName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
            this.columns[numColumns] = this.shouldHonorAEForRead ? new Column(typeInfo, columnName, tableName, cryptoMeta) : new Column(typeInfo, columnName, tableName, null);
        }
        if (tdsReader.getServerSupportsDataClassification() && tdsReader.peekTokenType() == 163) {
            tdsReader.trySetSensitivityClassification(this.processDataClassification(tdsReader));
        }
    }

    SensitivityClassification processDataClassification(TDSReader tdsReader) throws SQLServerException {
        if (!tdsReader.getServerSupportsDataClassification()) {
            tdsReader.throwInvalidTDS();
        }
        int dataClassificationToken = tdsReader.readUnsignedByte();
        assert (dataClassificationToken == 163);
        SensitivityClassification sensitivityClassification = null;
        int sensitivityLabelCount = tdsReader.readUnsignedShort();
        ArrayList<Label> sensitivityLabels = new ArrayList<Label>(sensitivityLabelCount);
        for (int i = 0; i < sensitivityLabelCount; ++i) {
            sensitivityLabels.add(this.readSensitivityLabel(tdsReader));
        }
        int informationTypeCount = tdsReader.readUnsignedShort();
        ArrayList<InformationType> informationTypes = new ArrayList<InformationType>(informationTypeCount);
        for (int i = 0; i < informationTypeCount; ++i) {
            informationTypes.add(this.readSensitivityInformationType(tdsReader));
        }
        boolean sensitivityRankSupported = tdsReader.getServerSupportedDataClassificationVersion() >= 2;
        int sensitivityRank = SensitivityClassification.SensitivityRank.NOT_DEFINED.getValue();
        if (sensitivityRankSupported && !SensitivityClassification.SensitivityRank.isValid(sensitivityRank = tdsReader.readInt())) {
            tdsReader.throwInvalidTDS();
        }
        int numResultSetColumns = tdsReader.readUnsignedShort();
        ArrayList<ColumnSensitivity> columnSensitivities = new ArrayList<ColumnSensitivity>(numResultSetColumns);
        for (int columnNum = 0; columnNum < numResultSetColumns; ++columnNum) {
            int numSensitivityProperties = tdsReader.readUnsignedShort();
            ArrayList<SensitivityProperty> sensitivityProperties = new ArrayList<SensitivityProperty>(numSensitivityProperties);
            for (int sourceNum = 0; sourceNum < numSensitivityProperties; ++sourceNum) {
                int sensitivityLabelIndex = tdsReader.readUnsignedShort();
                Label label = null;
                if (sensitivityLabelIndex != 65535) {
                    if (sensitivityLabelIndex >= sensitivityLabels.size()) {
                        tdsReader.throwInvalidTDS();
                    }
                    label = (Label)sensitivityLabels.get(sensitivityLabelIndex);
                }
                int informationTypeIndex = tdsReader.readUnsignedShort();
                InformationType informationType = null;
                if (informationTypeIndex != 65535) {
                    if (informationTypeIndex >= informationTypes.size()) {
                        // empty if block
                    }
                    informationType = (InformationType)informationTypes.get(informationTypeIndex);
                }
                int sensitivityRankProperty = SensitivityClassification.SensitivityRank.NOT_DEFINED.getValue();
                if (sensitivityRankSupported) {
                    sensitivityRankProperty = tdsReader.readInt();
                    if (!SensitivityClassification.SensitivityRank.isValid(sensitivityRankProperty)) {
                        tdsReader.throwInvalidTDS();
                    }
                    sensitivityProperties.add(new SensitivityProperty(label, informationType, sensitivityRankProperty));
                    continue;
                }
                sensitivityProperties.add(new SensitivityProperty(label, informationType));
            }
            columnSensitivities.add(new ColumnSensitivity(sensitivityProperties));
        }
        sensitivityClassification = sensitivityRankSupported ? new SensitivityClassification(sensitivityLabels, informationTypes, columnSensitivities, sensitivityRank) : new SensitivityClassification(sensitivityLabels, informationTypes, columnSensitivities);
        return sensitivityClassification;
    }

    private String readByteString(TDSReader tdsReader) throws SQLServerException {
        String value = "";
        int byteLen = tdsReader.readUnsignedByte();
        value = tdsReader.readUnicodeString(byteLen);
        return value;
    }

    private Label readSensitivityLabel(TDSReader tdsReader) throws SQLServerException {
        String name = this.readByteString(tdsReader);
        String id = this.readByteString(tdsReader);
        return new Label(name, id);
    }

    private InformationType readSensitivityInformationType(TDSReader tdsReader) throws SQLServerException {
        String name = this.readByteString(tdsReader);
        String id = this.readByteString(tdsReader);
        return new InformationType(name, id);
    }

    Column[] buildColumns(StreamColInfo colInfoToken, StreamTabName tabNameToken) throws SQLServerException {
        if (null != colInfoToken && null != tabNameToken) {
            tabNameToken.applyTo(this.columns, colInfoToken.applyTo(this.columns));
        }
        return this.columns;
    }
}

