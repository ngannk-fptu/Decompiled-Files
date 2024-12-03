/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.internal.XmlWriter;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CSVInput;
import com.amazonaws.services.s3.model.CSVOutput;
import com.amazonaws.services.s3.model.Encryption;
import com.amazonaws.services.s3.model.GlacierJobParameters;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.InputSerialization;
import com.amazonaws.services.s3.model.JSONInput;
import com.amazonaws.services.s3.model.JSONOutput;
import com.amazonaws.services.s3.model.MetadataEntry;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.OutputLocation;
import com.amazonaws.services.s3.model.OutputSerialization;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.RequestProgress;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.S3Location;
import com.amazonaws.services.s3.model.ScanRange;
import com.amazonaws.services.s3.model.SelectObjectContentRequest;
import com.amazonaws.services.s3.model.SelectParameters;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.transform.AclXmlFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RequestXmlFactory {
    public static byte[] convertToXmlByteArray(List<PartETag> partETags) {
        XmlWriter xml = new XmlWriter();
        xml.start("CompleteMultipartUpload");
        if (partETags != null) {
            ArrayList<PartETag> sortedPartETags = new ArrayList<PartETag>(partETags);
            Collections.sort(sortedPartETags, new Comparator<PartETag>(){

                @Override
                public int compare(PartETag tag1, PartETag tag2) {
                    if (tag1.getPartNumber() < tag2.getPartNumber()) {
                        return -1;
                    }
                    if (tag1.getPartNumber() > tag2.getPartNumber()) {
                        return 1;
                    }
                    return 0;
                }
            });
            for (PartETag partEtag : sortedPartETags) {
                xml.start("Part");
                xml.start("PartNumber").value(Integer.toString(partEtag.getPartNumber())).end();
                xml.start("ETag").value(partEtag.getETag()).end();
                xml.end();
            }
        }
        xml.end();
        return xml.getBytes();
    }

    public static byte[] convertToXmlByteArray(RestoreObjectRequest restoreObjectRequest) throws SdkClientException {
        GlacierJobParameters glacierJobParameters;
        XmlWriter xml = new XmlWriter();
        xml.start("RestoreRequest");
        if (restoreObjectRequest.getExpirationInDays() != -1) {
            xml.start("Days").value(Integer.toString(restoreObjectRequest.getExpirationInDays())).end();
        }
        if ((glacierJobParameters = restoreObjectRequest.getGlacierJobParameters()) != null) {
            xml.start("GlacierJobParameters");
            RequestXmlFactory.addIfNotNull(xml, "Tier", glacierJobParameters.getTier());
            xml.end();
        }
        RequestXmlFactory.addIfNotNull(xml, "Type", restoreObjectRequest.getType());
        RequestXmlFactory.addIfNotNull(xml, "Tier", restoreObjectRequest.getTier());
        RequestXmlFactory.addIfNotNull(xml, "Description", restoreObjectRequest.getDescription());
        RequestXmlFactory.addSelectParametersIfNotNull(xml, restoreObjectRequest.getSelectParameters());
        RequestXmlFactory.addOutputLocationIfNotNull(xml, restoreObjectRequest.getOutputLocation());
        xml.end();
        return xml.getBytes();
    }

    public static byte[] convertToXmlByteArray(SelectObjectContentRequest selectRequest) {
        XmlWriter xml = new XmlWriter();
        xml.start("SelectObjectContentRequest");
        RequestXmlFactory.addIfNotNull(xml, "Expression", selectRequest.getExpression());
        RequestXmlFactory.addIfNotNull(xml, "ExpressionType", selectRequest.getExpressionType());
        RequestXmlFactory.addScanRangeIfNotNull(xml, selectRequest.getScanRange());
        RequestXmlFactory.addRequestProgressIfNotNull(xml, selectRequest.getRequestProgress());
        RequestXmlFactory.addInputSerializationIfNotNull(xml, selectRequest.getInputSerialization());
        RequestXmlFactory.addOutputSerializationIfNotNull(xml, selectRequest.getOutputSerialization());
        xml.end();
        return xml.getBytes();
    }

    private static void addRequestProgressIfNotNull(XmlWriter xml, RequestProgress requestProgress) {
        if (requestProgress == null) {
            return;
        }
        xml.start("RequestProgress");
        RequestXmlFactory.addIfNotNull(xml, "Enabled", requestProgress.getEnabled());
        xml.end();
    }

    private static void addSelectParametersIfNotNull(XmlWriter xml, SelectParameters selectParameters) {
        if (selectParameters == null) {
            return;
        }
        xml.start("SelectParameters");
        RequestXmlFactory.addInputSerializationIfNotNull(xml, selectParameters.getInputSerialization());
        RequestXmlFactory.addIfNotNull(xml, "ExpressionType", selectParameters.getExpressionType());
        RequestXmlFactory.addIfNotNull(xml, "Expression", selectParameters.getExpression());
        RequestXmlFactory.addOutputSerializationIfNotNull(xml, selectParameters.getOutputSerialization());
        xml.end();
    }

    private static void addScanRangeIfNotNull(XmlWriter xml, ScanRange scanRange) {
        if (scanRange != null) {
            xml.start("ScanRange");
            RequestXmlFactory.addIfNotNull(xml, "Start", scanRange.getStart());
            RequestXmlFactory.addIfNotNull(xml, "End", scanRange.getEnd());
            xml.end();
        }
    }

    private static void addInputSerializationIfNotNull(XmlWriter xml, InputSerialization inputSerialization) {
        if (inputSerialization != null) {
            xml.start("InputSerialization");
            if (inputSerialization.getCsv() != null) {
                xml.start("CSV");
                CSVInput csvInput = inputSerialization.getCsv();
                RequestXmlFactory.addIfNotNull(xml, "FileHeaderInfo", csvInput.getFileHeaderInfo());
                RequestXmlFactory.addIfNotNull(xml, "Comments", csvInput.getCommentsAsString());
                RequestXmlFactory.addIfNotNull(xml, "QuoteEscapeCharacter", csvInput.getQuoteEscapeCharacterAsString());
                RequestXmlFactory.addIfNotNull(xml, "RecordDelimiter", csvInput.getRecordDelimiterAsString());
                RequestXmlFactory.addIfNotNull(xml, "FieldDelimiter", csvInput.getFieldDelimiterAsString());
                RequestXmlFactory.addIfNotNull(xml, "QuoteCharacter", csvInput.getQuoteCharacterAsString());
                RequestXmlFactory.addIfNotNull(xml, "AllowQuotedRecordDelimiter", csvInput.getAllowQuotedRecordDelimiter());
                xml.end();
            }
            if (inputSerialization.getJson() != null) {
                xml.start("JSON");
                JSONInput jsonInput = inputSerialization.getJson();
                RequestXmlFactory.addIfNotNull(xml, "Type", jsonInput.getType());
                xml.end();
            }
            if (inputSerialization.getParquet() != null) {
                xml.start("Parquet");
                xml.end();
            }
            RequestXmlFactory.addIfNotNull(xml, "CompressionType", inputSerialization.getCompressionType());
            xml.end();
        }
    }

    private static void addOutputSerializationIfNotNull(XmlWriter xml, OutputSerialization outputSerialization) {
        if (outputSerialization != null) {
            xml.start("OutputSerialization");
            if (outputSerialization.getCsv() != null) {
                xml.start("CSV");
                CSVOutput csvOutput = outputSerialization.getCsv();
                RequestXmlFactory.addIfNotNull(xml, "QuoteFields", csvOutput.getQuoteFields());
                RequestXmlFactory.addIfNotNull(xml, "QuoteEscapeCharacter", csvOutput.getQuoteEscapeCharacterAsString());
                RequestXmlFactory.addIfNotNull(xml, "RecordDelimiter", csvOutput.getRecordDelimiterAsString());
                RequestXmlFactory.addIfNotNull(xml, "FieldDelimiter", csvOutput.getFieldDelimiterAsString());
                RequestXmlFactory.addIfNotNull(xml, "QuoteCharacter", csvOutput.getQuoteCharacterAsString());
                xml.end();
            }
            if (outputSerialization.getJson() != null) {
                xml.start("JSON");
                JSONOutput jsonOutput = outputSerialization.getJson();
                RequestXmlFactory.addIfNotNull(xml, "RecordDelimiter", jsonOutput.getRecordDelimiterAsString());
                xml.end();
            }
            xml.end();
        }
    }

    private static void addTaggingIfNotNull(XmlWriter xml, ObjectTagging tagSet) {
        if (tagSet == null) {
            return;
        }
        xml.start("Tagging");
        xml.start("TagSet");
        for (Tag tag : tagSet.getTagSet()) {
            xml.start("Tag");
            xml.start("Key").value(tag.getKey()).end();
            xml.start("Value").value(tag.getValue()).end();
            xml.end();
        }
        xml.end();
        xml.end();
    }

    private static void addOutputLocationIfNotNull(XmlWriter xml, OutputLocation outputLocation) {
        if (outputLocation == null) {
            return;
        }
        xml.start("OutputLocation");
        if (outputLocation.getS3() != null) {
            S3Location s3 = outputLocation.getS3();
            xml.start("S3");
            RequestXmlFactory.addIfNotNull(xml, "BucketName", s3.getBucketName());
            RequestXmlFactory.addIfNotNull(xml, "Prefix", s3.getPrefix());
            if (s3.getEncryption() != null) {
                Encryption encryption = s3.getEncryption();
                xml.start("Encryption");
                RequestXmlFactory.addIfNotNull(xml, "EncryptionType", encryption.getEncryptionType());
                RequestXmlFactory.addIfNotNull(xml, "KMSKeyId", encryption.getKmsKeyId());
                RequestXmlFactory.addIfNotNull(xml, "KMSContext", encryption.getKmsContext());
                xml.end();
            }
            RequestXmlFactory.addIfNotNull(xml, "CannedACL", s3.getCannedACL());
            RequestXmlFactory.addGrantsIfNotNull(xml, s3.getAccessControlList());
            RequestXmlFactory.addTaggingIfNotNull(xml, s3.getTagging());
            RequestXmlFactory.addIfNotNull(xml, "StorageClass", s3.getStorageClass());
            RequestXmlFactory.addUserMetaDataIfNotNull(xml, s3.getUserMetadata());
            xml.end();
        }
        xml.end();
    }

    private static void addGrantsIfNotNull(XmlWriter xml, AccessControlList accessControlList) {
        if (accessControlList == null) {
            return;
        }
        AclXmlFactory aclXmlFactory = new AclXmlFactory();
        xml.start("AccessControlList");
        for (Grant grant : accessControlList.getGrantsAsList()) {
            xml.start("Grant");
            if (grant.getGrantee() != null) {
                aclXmlFactory.convertToXml(grant.getGrantee(), xml);
            }
            RequestXmlFactory.addIfNotNull(xml, "Permission", (Object)grant.getPermission());
            xml.end();
        }
        xml.end();
    }

    private static void addUserMetaDataIfNotNull(XmlWriter xml, List<MetadataEntry> userMetadata) {
        if (userMetadata == null || userMetadata.isEmpty()) {
            return;
        }
        xml.start("UserMetadata");
        for (MetadataEntry metadataEntry : userMetadata) {
            xml.start("MetadataEntry");
            RequestXmlFactory.addIfNotNull(xml, "Name", metadataEntry.getName());
            RequestXmlFactory.addIfNotNull(xml, "Value", metadataEntry.getValue());
            xml.end();
        }
        xml.end();
    }

    private static void addIfNotNull(XmlWriter xml, String xmlTag, String value) {
        if (value != null) {
            xml.start(xmlTag).value(value).end();
        }
    }

    private static void addIfNotNull(XmlWriter xml, String xmlTag, Object value) {
        if (value != null && value.toString() != null) {
            xml.start(xmlTag).value(value.toString()).end();
        }
    }
}

