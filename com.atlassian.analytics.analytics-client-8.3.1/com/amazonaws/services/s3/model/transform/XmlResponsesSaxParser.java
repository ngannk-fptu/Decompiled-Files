/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.internal.DeleteObjectsResponse;
import com.amazonaws.services.s3.internal.ObjectExpirationResult;
import com.amazonaws.services.s3.internal.S3RequesterChargedResult;
import com.amazonaws.services.s3.internal.S3VersionResult;
import com.amazonaws.services.s3.internal.ServerSideEncryptionResult;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.AbortIncompleteMultipartUpload;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AccessControlTranslation;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketAccelerateConfiguration;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DefaultRetention;
import com.amazonaws.services.s3.model.DeleteMarkerReplication;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.EncryptionConfiguration;
import com.amazonaws.services.s3.model.ExistingObjectReplication;
import com.amazonaws.services.s3.model.GetBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketIntelligentTieringConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketMetricsConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketOwnershipControlsResult;
import com.amazonaws.services.s3.model.GetObjectLegalHoldResult;
import com.amazonaws.services.s3.model.GetObjectLockConfigurationResult;
import com.amazonaws.services.s3.model.GetObjectRetentionResult;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListBucketAnalyticsConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketIntelligentTieringConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketInventoryConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketMetricsConfigurationsResult;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.Metrics;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectLockConfiguration;
import com.amazonaws.services.s3.model.ObjectLockLegalHold;
import com.amazonaws.services.s3.model.ObjectLockRetention;
import com.amazonaws.services.s3.model.ObjectLockRule;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PartSummary;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.RedirectRule;
import com.amazonaws.services.s3.model.ReplicaModifications;
import com.amazonaws.services.s3.model.ReplicationDestinationConfig;
import com.amazonaws.services.s3.model.ReplicationRule;
import com.amazonaws.services.s3.model.ReplicationTime;
import com.amazonaws.services.s3.model.ReplicationTimeValue;
import com.amazonaws.services.s3.model.RequestPaymentConfiguration;
import com.amazonaws.services.s3.model.RoutingRule;
import com.amazonaws.services.s3.model.RoutingRuleCondition;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.SourceSelectionCriteria;
import com.amazonaws.services.s3.model.SseKmsEncryptedObjects;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.TagSet;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.s3.model.analytics.AnalyticsAndOperator;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import com.amazonaws.services.s3.model.analytics.AnalyticsExportDestination;
import com.amazonaws.services.s3.model.analytics.AnalyticsFilter;
import com.amazonaws.services.s3.model.analytics.AnalyticsFilterPredicate;
import com.amazonaws.services.s3.model.analytics.AnalyticsPrefixPredicate;
import com.amazonaws.services.s3.model.analytics.AnalyticsS3BucketDestination;
import com.amazonaws.services.s3.model.analytics.AnalyticsTagPredicate;
import com.amazonaws.services.s3.model.analytics.StorageClassAnalysis;
import com.amazonaws.services.s3.model.analytics.StorageClassAnalysisDataExport;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringAccessTier;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringAndOperator;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringConfiguration;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringFilter;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringFilterPredicate;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringPrefixPredicate;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringStatus;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringTagPredicate;
import com.amazonaws.services.s3.model.intelligenttiering.Tiering;
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration;
import com.amazonaws.services.s3.model.inventory.InventoryDestination;
import com.amazonaws.services.s3.model.inventory.InventoryFilter;
import com.amazonaws.services.s3.model.inventory.InventoryPrefixPredicate;
import com.amazonaws.services.s3.model.inventory.InventoryS3BucketDestination;
import com.amazonaws.services.s3.model.inventory.InventorySchedule;
import com.amazonaws.services.s3.model.inventory.ServerSideEncryptionKMS;
import com.amazonaws.services.s3.model.inventory.ServerSideEncryptionS3;
import com.amazonaws.services.s3.model.lifecycle.LifecycleAndOperator;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilterPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleObjectSizeGreaterThanPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleObjectSizeLessThanPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePrefixPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsAccessPointArnPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsAndOperator;
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration;
import com.amazonaws.services.s3.model.metrics.MetricsFilter;
import com.amazonaws.services.s3.model.metrics.MetricsFilterPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsPrefixPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsTagPredicate;
import com.amazonaws.services.s3.model.ownership.OwnershipControls;
import com.amazonaws.services.s3.model.ownership.OwnershipControlsRule;
import com.amazonaws.services.s3.model.replication.ReplicationAndOperator;
import com.amazonaws.services.s3.model.replication.ReplicationFilter;
import com.amazonaws.services.s3.model.replication.ReplicationFilterPredicate;
import com.amazonaws.services.s3.model.replication.ReplicationPrefixPredicate;
import com.amazonaws.services.s3.model.replication.ReplicationTagPredicate;
import com.amazonaws.services.s3.model.transform.AbstractHandler;
import com.amazonaws.services.s3.model.transform.AbstractSSEHandler;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.SdkHttpUtils;
import com.amazonaws.util.StringUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlResponsesSaxParser {
    private static final Log log = LogFactory.getLog(XmlResponsesSaxParser.class);
    private XMLReader xr = null;
    private boolean sanitizeXmlDocument = true;

    public XmlResponsesSaxParser() throws SdkClientException {
        try {
            this.xr = XMLReaderFactory.createXMLReader();
            this.disableExternalResourceFetching(this.xr);
        }
        catch (SAXException e) {
            throw new SdkClientException("Couldn't initialize a SAX driver to create an XMLReader", e);
        }
    }

    protected void parseXmlInputStream(DefaultHandler handler, InputStream inputStream) throws IOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Parsing XML response document with handler: " + handler.getClass()));
            }
            BufferedReader breader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            this.xr.setContentHandler(handler);
            this.xr.setErrorHandler(handler);
            this.xr.parse(new InputSource(breader));
        }
        catch (IOException e) {
            throw e;
        }
        catch (Throwable t) {
            block6: {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    if (!log.isErrorEnabled()) break block6;
                    log.error((Object)"Unable to close response InputStream up after XML parse failure", (Throwable)e);
                }
            }
            throw new SdkClientException("Failed to parse XML document with handler " + handler.getClass(), t);
        }
    }

    protected InputStream sanitizeXmlDocument(DefaultHandler handler, InputStream inputStream) throws IOException {
        if (!this.sanitizeXmlDocument) {
            return inputStream;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Sanitizing XML document destined for handler " + handler.getClass()));
        }
        ByteArrayInputStream sanitizedInputStream = null;
        try {
            StringBuilder listingDocBuffer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            char[] buf = new char[8192];
            int read = -1;
            while ((read = br.read(buf)) != -1) {
                listingDocBuffer.append(buf, 0, read);
            }
            br.close();
            String listingDoc = listingDocBuffer.toString().replaceAll("\r", "&#013;");
            sanitizedInputStream = new ByteArrayInputStream(listingDoc.getBytes(StringUtils.UTF8));
        }
        catch (IOException e) {
            throw e;
        }
        catch (Throwable t) {
            block8: {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    if (!log.isErrorEnabled()) break block8;
                    log.error((Object)"Unable to close response InputStream after failure sanitizing XML document", (Throwable)e);
                }
            }
            throw new SdkClientException("Failed to sanitize XML document destined for handler " + handler.getClass(), t);
        }
        return sanitizedInputStream;
    }

    private void disableExternalResourceFetching(XMLReader reader) throws SAXNotRecognizedException, SAXNotSupportedException {
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    }

    private static String checkForEmptyString(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return null;
        }
        return s;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException nfe) {
            log.error((Object)("Unable to parse integer value '" + s + "'"), (Throwable)nfe);
            return -1;
        }
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        }
        catch (NumberFormatException nfe) {
            log.error((Object)("Unable to parse long value '" + s + "'"), (Throwable)nfe);
            return -1L;
        }
    }

    private static String decodeIfSpecified(String value, boolean decode) {
        return decode ? SdkHttpUtils.urlDecode(value) : value;
    }

    public ListBucketHandler parseListBucketObjectsResponse(InputStream inputStream, boolean shouldSDKDecodeResponse) throws IOException {
        ListBucketHandler handler = new ListBucketHandler(shouldSDKDecodeResponse);
        this.parseXmlInputStream(handler, this.sanitizeXmlDocument(handler, inputStream));
        return handler;
    }

    public ListObjectsV2Handler parseListObjectsV2Response(InputStream inputStream, boolean shouldSDKDecodeResponse) throws IOException {
        ListObjectsV2Handler handler = new ListObjectsV2Handler(shouldSDKDecodeResponse);
        this.parseXmlInputStream(handler, this.sanitizeXmlDocument(handler, inputStream));
        return handler;
    }

    public ListVersionsHandler parseListVersionsResponse(InputStream inputStream, boolean shouldSDKDecodeResponse) throws IOException {
        ListVersionsHandler handler = new ListVersionsHandler(shouldSDKDecodeResponse);
        this.parseXmlInputStream(handler, this.sanitizeXmlDocument(handler, inputStream));
        return handler;
    }

    public ListAllMyBucketsHandler parseListMyBucketsResponse(InputStream inputStream) throws IOException {
        ListAllMyBucketsHandler handler = new ListAllMyBucketsHandler();
        this.parseXmlInputStream(handler, this.sanitizeXmlDocument(handler, inputStream));
        return handler;
    }

    public AccessControlListHandler parseAccessControlListResponse(InputStream inputStream) throws IOException {
        AccessControlListHandler handler = new AccessControlListHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketLoggingConfigurationHandler parseLoggingStatusResponse(InputStream inputStream) throws IOException {
        BucketLoggingConfigurationHandler handler = new BucketLoggingConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketLifecycleConfigurationHandler parseBucketLifecycleConfigurationResponse(InputStream inputStream) throws IOException {
        BucketLifecycleConfigurationHandler handler = new BucketLifecycleConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketCrossOriginConfigurationHandler parseBucketCrossOriginConfigurationResponse(InputStream inputStream) throws IOException {
        BucketCrossOriginConfigurationHandler handler = new BucketCrossOriginConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public String parseBucketLocationResponse(InputStream inputStream) throws IOException {
        BucketLocationHandler handler = new BucketLocationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler.getLocation();
    }

    public BucketVersioningConfigurationHandler parseVersioningConfigurationResponse(InputStream inputStream) throws IOException {
        BucketVersioningConfigurationHandler handler = new BucketVersioningConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketWebsiteConfigurationHandler parseWebsiteConfigurationResponse(InputStream inputStream) throws IOException {
        BucketWebsiteConfigurationHandler handler = new BucketWebsiteConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketReplicationConfigurationHandler parseReplicationConfigurationResponse(InputStream inputStream) throws IOException {
        BucketReplicationConfigurationHandler handler = new BucketReplicationConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketTaggingConfigurationHandler parseTaggingConfigurationResponse(InputStream inputStream) throws IOException {
        BucketTaggingConfigurationHandler handler = new BucketTaggingConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketAccelerateConfigurationHandler parseAccelerateConfigurationResponse(InputStream inputStream) throws IOException {
        BucketAccelerateConfigurationHandler handler = new BucketAccelerateConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public DeleteObjectsHandler parseDeletedObjectsResult(InputStream inputStream) throws IOException {
        DeleteObjectsHandler handler = new DeleteObjectsHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public CopyObjectResultHandler parseCopyObjectResponse(InputStream inputStream) throws IOException {
        CopyObjectResultHandler handler = new CopyObjectResultHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public CompleteMultipartUploadHandler parseCompleteMultipartUploadResponse(InputStream inputStream) throws IOException {
        CompleteMultipartUploadHandler handler = new CompleteMultipartUploadHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public InitiateMultipartUploadHandler parseInitiateMultipartUploadResponse(InputStream inputStream) throws IOException {
        InitiateMultipartUploadHandler handler = new InitiateMultipartUploadHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListMultipartUploadsHandler parseListMultipartUploadsResponse(InputStream inputStream) throws IOException {
        ListMultipartUploadsHandler handler = new ListMultipartUploadsHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListPartsHandler parseListPartsResponse(InputStream inputStream) throws IOException {
        ListPartsHandler handler = new ListPartsHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetObjectTaggingHandler parseObjectTaggingResponse(InputStream inputStream) throws IOException {
        GetObjectTaggingHandler handler = new GetObjectTaggingHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetBucketMetricsConfigurationHandler parseGetBucketMetricsConfigurationResponse(InputStream inputStream) throws IOException {
        GetBucketMetricsConfigurationHandler handler = new GetBucketMetricsConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListBucketMetricsConfigurationsHandler parseListBucketMetricsConfigurationsResponse(InputStream inputStream) throws IOException {
        ListBucketMetricsConfigurationsHandler handler = new ListBucketMetricsConfigurationsHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetBucketOwnershipControlsHandler parseGetBucketOwnershipControlsResponse(InputStream inputStream) throws IOException {
        GetBucketOwnershipControlsHandler handler = new GetBucketOwnershipControlsHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetBucketAnalyticsConfigurationHandler parseGetBucketAnalyticsConfigurationResponse(InputStream inputStream) throws IOException {
        GetBucketAnalyticsConfigurationHandler handler = new GetBucketAnalyticsConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListBucketAnalyticsConfigurationHandler parseListBucketAnalyticsConfigurationResponse(InputStream inputStream) throws IOException {
        ListBucketAnalyticsConfigurationHandler handler = new ListBucketAnalyticsConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetBucketIntelligentTieringConfigurationHandler parseGetBucketIntelligentTieringConfigurationResponse(InputStream inputStream) throws IOException {
        GetBucketIntelligentTieringConfigurationHandler handler = new GetBucketIntelligentTieringConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListBucketIntelligentTieringConfigurationHandler parseListBucketIntelligentTieringConfigurationResponse(InputStream inputStream) throws IOException {
        ListBucketIntelligentTieringConfigurationHandler handler = new ListBucketIntelligentTieringConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetBucketInventoryConfigurationHandler parseGetBucketInventoryConfigurationResponse(InputStream inputStream) throws IOException {
        GetBucketInventoryConfigurationHandler handler = new GetBucketInventoryConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListBucketInventoryConfigurationsHandler parseBucketListInventoryConfigurationsResponse(InputStream inputStream) throws IOException {
        ListBucketInventoryConfigurationsHandler handler = new ListBucketInventoryConfigurationsHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public RequestPaymentConfigurationHandler parseRequestPaymentConfigurationResponse(InputStream inputStream) throws IOException {
        RequestPaymentConfigurationHandler handler = new RequestPaymentConfigurationHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetObjectLegalHoldResponseHandler parseGetObjectLegalHoldResponse(InputStream inputStream) throws IOException {
        GetObjectLegalHoldResponseHandler handler = new GetObjectLegalHoldResponseHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetObjectLockConfigurationResponseHandler parseGetObjectLockConfigurationResponse(InputStream inputStream) throws IOException {
        GetObjectLockConfigurationResponseHandler handler = new GetObjectLockConfigurationResponseHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public GetObjectRetentionResponseHandler parseGetObjectRetentionResponse(InputStream inputStream) throws IOException {
        GetObjectRetentionResponseHandler handler = new GetObjectRetentionResponseHandler();
        this.parseXmlInputStream(handler, inputStream);
        return handler;
    }

    private static String findAttributeValue(String qnameToFind, Attributes attrs) {
        for (int i = 0; i < attrs.getLength(); ++i) {
            String qname = attrs.getQName(i);
            if (!qname.trim().equalsIgnoreCase(qnameToFind.trim())) continue;
            return attrs.getValue(i);
        }
        return null;
    }

    public static class GetObjectRetentionResponseHandler
    extends AbstractHandler {
        private GetObjectRetentionResult result = new GetObjectRetentionResult();
        private ObjectLockRetention retention = new ObjectLockRetention();

        public GetObjectRetentionResult getResult() {
            return this.result.withRetention(this.retention);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("Retention")) {
                if ("Mode".equals(name)) {
                    this.retention.setMode(this.getText());
                } else if ("RetainUntilDate".equals(name)) {
                    this.retention.setRetainUntilDate(ServiceUtils.parseIso8601Date(this.getText()));
                }
            }
        }
    }

    public static class GetObjectLockConfigurationResponseHandler
    extends AbstractHandler {
        private GetObjectLockConfigurationResult result = new GetObjectLockConfigurationResult();
        private ObjectLockConfiguration objectLockConfiguration = new ObjectLockConfiguration();
        private ObjectLockRule rule;
        private DefaultRetention defaultRetention;

        public GetObjectLockConfigurationResult getResult() {
            return this.result.withObjectLockConfiguration(this.objectLockConfiguration);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ObjectLockConfiguration")) {
                if ("Rule".equals(name)) {
                    this.rule = new ObjectLockRule();
                }
            } else if (this.in("ObjectLockConfiguration", "Rule") && "DefaultRetention".equals(name)) {
                this.defaultRetention = new DefaultRetention();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ObjectLockConfiguration")) {
                if ("ObjectLockEnabled".equals(name)) {
                    this.objectLockConfiguration.setObjectLockEnabled(this.getText());
                } else if ("Rule".equals(name)) {
                    this.objectLockConfiguration.setRule(this.rule);
                }
            } else if (this.in("ObjectLockConfiguration", "Rule")) {
                if ("DefaultRetention".equals(name)) {
                    this.rule.setDefaultRetention(this.defaultRetention);
                }
            } else if (this.in("ObjectLockConfiguration", "Rule", "DefaultRetention")) {
                if ("Mode".equals(name)) {
                    this.defaultRetention.setMode(this.getText());
                } else if ("Days".equals(name)) {
                    this.defaultRetention.setDays(Integer.parseInt(this.getText()));
                } else if ("Years".equals(name)) {
                    this.defaultRetention.setYears(Integer.parseInt(this.getText()));
                }
            }
        }
    }

    public static class GetObjectLegalHoldResponseHandler
    extends AbstractHandler {
        private GetObjectLegalHoldResult getObjectLegalHoldResult = new GetObjectLegalHoldResult();
        private ObjectLockLegalHold legalHold = new ObjectLockLegalHold();

        public GetObjectLegalHoldResult getResult() {
            return this.getObjectLegalHoldResult.withLegalHold(this.legalHold);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("LegalHold") && "Status".equals(name)) {
                this.legalHold.setStatus(this.getText());
            }
        }
    }

    public static class ListBucketInventoryConfigurationsHandler
    extends AbstractHandler {
        private final ListBucketInventoryConfigurationsResult result = new ListBucketInventoryConfigurationsResult();
        private InventoryConfiguration currentConfiguration;
        private List<String> currentOptionalFieldsList;
        private InventoryDestination currentDestination;
        private InventoryFilter currentFilter;
        private InventoryS3BucketDestination currentS3BucketDestination;
        private InventorySchedule currentSchedule;

        public ListBucketInventoryConfigurationsResult getResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListInventoryConfigurationsResult")) {
                if (name.equals("InventoryConfiguration")) {
                    this.currentConfiguration = new InventoryConfiguration();
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration")) {
                if (name.equals("Destination")) {
                    this.currentDestination = new InventoryDestination();
                } else if (name.equals("Filter")) {
                    this.currentFilter = new InventoryFilter();
                } else if (name.equals("Schedule")) {
                    this.currentSchedule = new InventorySchedule();
                } else if (name.equals("OptionalFields")) {
                    this.currentOptionalFieldsList = new ArrayList<String>();
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration", "Destination") && name.equals("S3BucketDestination")) {
                this.currentS3BucketDestination = new InventoryS3BucketDestination();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListInventoryConfigurationsResult")) {
                if (name.equals("InventoryConfiguration")) {
                    if (this.result.getInventoryConfigurationList() == null) {
                        this.result.setInventoryConfigurationList(new ArrayList<InventoryConfiguration>());
                    }
                    this.result.getInventoryConfigurationList().add(this.currentConfiguration);
                    this.currentConfiguration = null;
                } else if (name.equals("IsTruncated")) {
                    this.result.setTruncated("true".equals(this.getText()));
                } else if (name.equals("ContinuationToken")) {
                    this.result.setContinuationToken(this.getText());
                } else if (name.equals("NextContinuationToken")) {
                    this.result.setNextContinuationToken(this.getText());
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration")) {
                if (name.equals("Id")) {
                    this.currentConfiguration.setId(this.getText());
                } else if (name.equals("Destination")) {
                    this.currentConfiguration.setDestination(this.currentDestination);
                    this.currentDestination = null;
                } else if (name.equals("IsEnabled")) {
                    this.currentConfiguration.setEnabled("true".equals(this.getText()));
                } else if (name.equals("Filter")) {
                    this.currentConfiguration.setInventoryFilter(this.currentFilter);
                    this.currentFilter = null;
                } else if (name.equals("IncludedObjectVersions")) {
                    this.currentConfiguration.setIncludedObjectVersions(this.getText());
                } else if (name.equals("Schedule")) {
                    this.currentConfiguration.setSchedule(this.currentSchedule);
                    this.currentSchedule = null;
                } else if (name.equals("OptionalFields")) {
                    this.currentConfiguration.setOptionalFields(this.currentOptionalFieldsList);
                    this.currentOptionalFieldsList = null;
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration", "Destination")) {
                if (name.equals("S3BucketDestination")) {
                    this.currentDestination.setS3BucketDestination(this.currentS3BucketDestination);
                    this.currentS3BucketDestination = null;
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration", "Destination", "S3BucketDestination")) {
                if (name.equals("AccountId")) {
                    this.currentS3BucketDestination.setAccountId(this.getText());
                } else if (name.equals("Bucket")) {
                    this.currentS3BucketDestination.setBucketArn(this.getText());
                } else if (name.equals("Format")) {
                    this.currentS3BucketDestination.setFormat(this.getText());
                } else if (name.equals("Prefix")) {
                    this.currentS3BucketDestination.setPrefix(this.getText());
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.currentFilter.setPredicate(new InventoryPrefixPredicate(this.getText()));
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration", "Schedule")) {
                if (name.equals("Frequency")) {
                    this.currentSchedule.setFrequency(this.getText());
                }
            } else if (this.in("ListInventoryConfigurationsResult", "InventoryConfiguration", "OptionalFields") && name.equals("Field")) {
                this.currentOptionalFieldsList.add(this.getText());
            }
        }
    }

    public static class GetBucketInventoryConfigurationHandler
    extends AbstractHandler {
        public static final String SSE_S3 = "SSE-S3";
        public static final String SSE_KMS = "SSE-KMS";
        private final GetBucketInventoryConfigurationResult result = new GetBucketInventoryConfigurationResult();
        private final InventoryConfiguration configuration = new InventoryConfiguration();
        private List<String> optionalFields;
        private InventoryDestination inventoryDestination;
        private InventoryFilter filter;
        private InventoryS3BucketDestination s3BucketDestination;
        private InventorySchedule inventorySchedule;

        public GetBucketInventoryConfigurationResult getResult() {
            return this.result.withInventoryConfiguration(this.configuration);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("InventoryConfiguration")) {
                if (name.equals("Destination")) {
                    this.inventoryDestination = new InventoryDestination();
                } else if (name.equals("Filter")) {
                    this.filter = new InventoryFilter();
                } else if (name.equals("Schedule")) {
                    this.inventorySchedule = new InventorySchedule();
                } else if (name.equals("OptionalFields")) {
                    this.optionalFields = new ArrayList<String>();
                }
            } else if (this.in("InventoryConfiguration", "Destination") && name.equals("S3BucketDestination")) {
                this.s3BucketDestination = new InventoryS3BucketDestination();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("InventoryConfiguration")) {
                if (name.equals("Id")) {
                    this.configuration.setId(this.getText());
                } else if (name.equals("Destination")) {
                    this.configuration.setDestination(this.inventoryDestination);
                    this.inventoryDestination = null;
                } else if (name.equals("IsEnabled")) {
                    this.configuration.setEnabled("true".equals(this.getText()));
                } else if (name.equals("Filter")) {
                    this.configuration.setInventoryFilter(this.filter);
                    this.filter = null;
                } else if (name.equals("IncludedObjectVersions")) {
                    this.configuration.setIncludedObjectVersions(this.getText());
                } else if (name.equals("Schedule")) {
                    this.configuration.setSchedule(this.inventorySchedule);
                    this.inventorySchedule = null;
                } else if (name.equals("OptionalFields")) {
                    this.configuration.setOptionalFields(this.optionalFields);
                    this.optionalFields = null;
                }
            } else if (this.in("InventoryConfiguration", "Destination")) {
                if (name.equals("S3BucketDestination")) {
                    this.inventoryDestination.setS3BucketDestination(this.s3BucketDestination);
                    this.s3BucketDestination = null;
                }
            } else if (this.in("InventoryConfiguration", "Destination", "S3BucketDestination")) {
                if (name.equals("AccountId")) {
                    this.s3BucketDestination.setAccountId(this.getText());
                } else if (name.equals("Bucket")) {
                    this.s3BucketDestination.setBucketArn(this.getText());
                } else if (name.equals("Format")) {
                    this.s3BucketDestination.setFormat(this.getText());
                } else if (name.equals("Prefix")) {
                    this.s3BucketDestination.setPrefix(this.getText());
                }
            } else if (this.in("InventoryConfiguration", "Destination", "S3BucketDestination", "Encryption")) {
                if (name.equals(SSE_S3)) {
                    this.s3BucketDestination.setEncryption(new ServerSideEncryptionS3());
                } else if (name.equals(SSE_KMS)) {
                    ServerSideEncryptionKMS kmsEncryption = new ServerSideEncryptionKMS().withKeyId(this.getText());
                    this.s3BucketDestination.setEncryption(kmsEncryption);
                }
            } else if (this.in("InventoryConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.filter.setPredicate(new InventoryPrefixPredicate(this.getText()));
                }
            } else if (this.in("InventoryConfiguration", "Schedule")) {
                if (name.equals("Frequency")) {
                    this.inventorySchedule.setFrequency(this.getText());
                }
            } else if (this.in("InventoryConfiguration", "OptionalFields") && name.equals("Field")) {
                this.optionalFields.add(this.getText());
            }
        }
    }

    public static class ListBucketIntelligentTieringConfigurationHandler
    extends AbstractHandler {
        private final ListBucketIntelligentTieringConfigurationsResult result = new ListBucketIntelligentTieringConfigurationsResult();
        private IntelligentTieringConfiguration currentConfiguration;
        private IntelligentTieringFilter currentFilter;
        private List<IntelligentTieringFilterPredicate> andOperandsList;
        private Tiering currentTiering;
        private String currentTagKey;
        private String currentTagValue;

        public ListBucketIntelligentTieringConfigurationsResult getResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListIntelligentTieringConfigurationsResult")) {
                if (name.equals("IntelligentTieringConfiguration")) {
                    this.currentConfiguration = new IntelligentTieringConfiguration();
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration")) {
                if (name.equals("Filter")) {
                    this.currentFilter = new IntelligentTieringFilter();
                } else if (name.equals("Tiering")) {
                    this.currentTiering = new Tiering();
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration", "Filter") && name.equals("And")) {
                this.andOperandsList = new ArrayList<IntelligentTieringFilterPredicate>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListIntelligentTieringConfigurationsResult")) {
                if (name.equals("IntelligentTieringConfiguration")) {
                    if (this.result.getIntelligentTieringConfigurationList() == null) {
                        this.result.setIntelligentTieringConfigurationList(new ArrayList<IntelligentTieringConfiguration>());
                    }
                    this.result.getIntelligentTieringConfigurationList().add(this.currentConfiguration);
                    this.currentConfiguration = null;
                } else if (name.equals("IsTruncated")) {
                    this.result.setTruncated("true".equals(this.getText()));
                } else if (name.equals("ContinuationToken")) {
                    this.result.setContinuationToken(this.getText());
                } else if (name.equals("NextContinuationToken")) {
                    this.result.setNextContinuationToken(this.getText());
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration")) {
                if (name.equals("Id")) {
                    this.currentConfiguration.setId(this.getText());
                } else if (name.equals("Filter")) {
                    this.currentConfiguration.setFilter(this.currentFilter);
                } else if (name.equals("Status")) {
                    this.currentConfiguration.setStatus(IntelligentTieringStatus.fromValue(this.getText()));
                } else if (name.equals("Tiering")) {
                    if (this.currentConfiguration.getTierings() == null) {
                        this.currentConfiguration.setTierings(new ArrayList<Tiering>());
                    }
                    this.currentConfiguration.getTierings().add(this.currentTiering);
                    this.currentTiering = null;
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.currentFilter.setPredicate(new IntelligentTieringPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.currentFilter.setPredicate(new IntelligentTieringTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("And")) {
                    this.currentFilter.setPredicate(new IntelligentTieringAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration", "Filter", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration", "Filter", "And")) {
                if (name.equals("Prefix")) {
                    this.andOperandsList.add(new IntelligentTieringPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.andOperandsList.add(new IntelligentTieringTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration", "Filter", "And", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("ListIntelligentTieringConfigurationsResult", "IntelligentTieringConfiguration", "Tiering")) {
                if (name.equals("AccessTier")) {
                    this.currentTiering.setAccessTier(IntelligentTieringAccessTier.fromValue(this.getText()));
                } else if (name.equals("Days")) {
                    this.currentTiering.setDays(Integer.parseInt(this.getText()));
                }
            }
        }
    }

    public static class GetBucketIntelligentTieringConfigurationHandler
    extends AbstractHandler {
        private final IntelligentTieringConfiguration configuration = new IntelligentTieringConfiguration();
        private IntelligentTieringFilter filter;
        private List<IntelligentTieringFilterPredicate> andOperandsList;
        private Tiering currentTiering;
        private String currentTagKey;
        private String currentTagValue;

        public GetBucketIntelligentTieringConfigurationResult getResult() {
            return new GetBucketIntelligentTieringConfigurationResult().withIntelligentTieringConfiguration(this.configuration);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("IntelligentTieringConfiguration")) {
                if (name.equals("Filter")) {
                    this.filter = new IntelligentTieringFilter();
                } else if (name.equals("Tiering")) {
                    this.currentTiering = new Tiering();
                }
            } else if (this.in("IntelligentTieringConfiguration", "Filter") && name.equals("And")) {
                this.andOperandsList = new ArrayList<IntelligentTieringFilterPredicate>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("IntelligentTieringConfiguration")) {
                if (name.equals("Id")) {
                    this.configuration.setId(this.getText());
                } else if (name.equals("Filter")) {
                    this.configuration.setFilter(this.filter);
                } else if (name.equals("Status")) {
                    this.configuration.setStatus(IntelligentTieringStatus.fromValue(this.getText()));
                } else if (name.equals("Tiering")) {
                    if (this.configuration.getTierings() == null) {
                        this.configuration.setTierings(new ArrayList<Tiering>());
                    }
                    this.configuration.getTierings().add(this.currentTiering);
                    this.currentTiering = null;
                }
            } else if (this.in("IntelligentTieringConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.filter.setPredicate(new IntelligentTieringPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.filter.setPredicate(new IntelligentTieringTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("And")) {
                    this.filter.setPredicate(new IntelligentTieringAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                }
            } else if (this.in("IntelligentTieringConfiguration", "Filter", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("IntelligentTieringConfiguration", "Filter", "And")) {
                if (name.equals("Prefix")) {
                    this.andOperandsList.add(new IntelligentTieringPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.andOperandsList.add(new IntelligentTieringTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                }
            } else if (this.in("IntelligentTieringConfiguration", "Filter", "And", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("IntelligentTieringConfiguration", "Tiering")) {
                if (name.equals("AccessTier")) {
                    this.currentTiering.setAccessTier(IntelligentTieringAccessTier.fromValue(this.getText()));
                } else if (name.equals("Days")) {
                    this.currentTiering.setDays(Integer.parseInt(this.getText()));
                }
            }
        }
    }

    public static class ListBucketAnalyticsConfigurationHandler
    extends AbstractHandler {
        private final ListBucketAnalyticsConfigurationsResult result = new ListBucketAnalyticsConfigurationsResult();
        private AnalyticsConfiguration currentConfiguration;
        private AnalyticsFilter currentFilter;
        private List<AnalyticsFilterPredicate> andOperandsList;
        private StorageClassAnalysis storageClassAnalysis;
        private StorageClassAnalysisDataExport dataExport;
        private AnalyticsExportDestination destination;
        private AnalyticsS3BucketDestination s3BucketDestination;
        private String currentTagKey;
        private String currentTagValue;

        public ListBucketAnalyticsConfigurationsResult getResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListBucketAnalyticsConfigurationsResult")) {
                if (name.equals("AnalyticsConfiguration")) {
                    this.currentConfiguration = new AnalyticsConfiguration();
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration")) {
                if (name.equals("Filter")) {
                    this.currentFilter = new AnalyticsFilter();
                } else if (name.equals("StorageClassAnalysis")) {
                    this.storageClassAnalysis = new StorageClassAnalysis();
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "Filter")) {
                if (name.equals("And")) {
                    this.andOperandsList = new ArrayList<AnalyticsFilterPredicate>();
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "StorageClassAnalysis")) {
                if (name.equals("DataExport")) {
                    this.dataExport = new StorageClassAnalysisDataExport();
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "StorageClassAnalysis", "DataExport")) {
                if (name.equals("Destination")) {
                    this.destination = new AnalyticsExportDestination();
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "StorageClassAnalysis", "DataExport", "Destination") && name.equals("S3BucketDestination")) {
                this.s3BucketDestination = new AnalyticsS3BucketDestination();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListBucketAnalyticsConfigurationsResult")) {
                if (name.equals("AnalyticsConfiguration")) {
                    if (this.result.getAnalyticsConfigurationList() == null) {
                        this.result.setAnalyticsConfigurationList(new ArrayList<AnalyticsConfiguration>());
                    }
                    this.result.getAnalyticsConfigurationList().add(this.currentConfiguration);
                    this.currentConfiguration = null;
                } else if (name.equals("IsTruncated")) {
                    this.result.setTruncated("true".equals(this.getText()));
                } else if (name.equals("ContinuationToken")) {
                    this.result.setContinuationToken(this.getText());
                } else if (name.equals("NextContinuationToken")) {
                    this.result.setNextContinuationToken(this.getText());
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration")) {
                if (name.equals("Id")) {
                    this.currentConfiguration.setId(this.getText());
                } else if (name.equals("Filter")) {
                    this.currentConfiguration.setFilter(this.currentFilter);
                } else if (name.equals("StorageClassAnalysis")) {
                    this.currentConfiguration.setStorageClassAnalysis(this.storageClassAnalysis);
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.currentFilter.setPredicate(new AnalyticsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.currentFilter.setPredicate(new AnalyticsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("And")) {
                    this.currentFilter.setPredicate(new AnalyticsAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "Filter", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "Filter", "And")) {
                if (name.equals("Prefix")) {
                    this.andOperandsList.add(new AnalyticsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.andOperandsList.add(new AnalyticsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "Filter", "And", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "StorageClassAnalysis")) {
                if (name.equals("DataExport")) {
                    this.storageClassAnalysis.setDataExport(this.dataExport);
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "StorageClassAnalysis", "DataExport")) {
                if (name.equals("OutputSchemaVersion")) {
                    this.dataExport.setOutputSchemaVersion(this.getText());
                } else if (name.equals("Destination")) {
                    this.dataExport.setDestination(this.destination);
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "StorageClassAnalysis", "DataExport", "Destination")) {
                if (name.equals("S3BucketDestination")) {
                    this.destination.setS3BucketDestination(this.s3BucketDestination);
                }
            } else if (this.in("ListBucketAnalyticsConfigurationsResult", "AnalyticsConfiguration", "StorageClassAnalysis", "DataExport", "Destination", "S3BucketDestination")) {
                if (name.equals("Format")) {
                    this.s3BucketDestination.setFormat(this.getText());
                } else if (name.equals("BucketAccountId")) {
                    this.s3BucketDestination.setBucketAccountId(this.getText());
                } else if (name.equals("Bucket")) {
                    this.s3BucketDestination.setBucketArn(this.getText());
                } else if (name.equals("Prefix")) {
                    this.s3BucketDestination.setPrefix(this.getText());
                }
            }
        }
    }

    public static class GetBucketAnalyticsConfigurationHandler
    extends AbstractHandler {
        private final AnalyticsConfiguration configuration = new AnalyticsConfiguration();
        private AnalyticsFilter filter;
        private List<AnalyticsFilterPredicate> andOperandsList;
        private StorageClassAnalysis storageClassAnalysis;
        private StorageClassAnalysisDataExport dataExport;
        private AnalyticsExportDestination destination;
        private AnalyticsS3BucketDestination s3BucketDestination;
        private String currentTagKey;
        private String currentTagValue;

        public GetBucketAnalyticsConfigurationResult getResult() {
            return new GetBucketAnalyticsConfigurationResult().withAnalyticsConfiguration(this.configuration);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("AnalyticsConfiguration")) {
                if (name.equals("Filter")) {
                    this.filter = new AnalyticsFilter();
                } else if (name.equals("StorageClassAnalysis")) {
                    this.storageClassAnalysis = new StorageClassAnalysis();
                }
            } else if (this.in("AnalyticsConfiguration", "Filter")) {
                if (name.equals("And")) {
                    this.andOperandsList = new ArrayList<AnalyticsFilterPredicate>();
                }
            } else if (this.in("AnalyticsConfiguration", "StorageClassAnalysis")) {
                if (name.equals("DataExport")) {
                    this.dataExport = new StorageClassAnalysisDataExport();
                }
            } else if (this.in("AnalyticsConfiguration", "StorageClassAnalysis", "DataExport")) {
                if (name.equals("Destination")) {
                    this.destination = new AnalyticsExportDestination();
                }
            } else if (this.in("AnalyticsConfiguration", "StorageClassAnalysis", "DataExport", "Destination") && name.equals("S3BucketDestination")) {
                this.s3BucketDestination = new AnalyticsS3BucketDestination();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("AnalyticsConfiguration")) {
                if (name.equals("Id")) {
                    this.configuration.setId(this.getText());
                } else if (name.equals("Filter")) {
                    this.configuration.setFilter(this.filter);
                } else if (name.equals("StorageClassAnalysis")) {
                    this.configuration.setStorageClassAnalysis(this.storageClassAnalysis);
                }
            } else if (this.in("AnalyticsConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.filter.setPredicate(new AnalyticsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.filter.setPredicate(new AnalyticsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("And")) {
                    this.filter.setPredicate(new AnalyticsAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                }
            } else if (this.in("AnalyticsConfiguration", "Filter", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("AnalyticsConfiguration", "Filter", "And")) {
                if (name.equals("Prefix")) {
                    this.andOperandsList.add(new AnalyticsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.andOperandsList.add(new AnalyticsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                }
            } else if (this.in("AnalyticsConfiguration", "Filter", "And", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("AnalyticsConfiguration", "StorageClassAnalysis")) {
                if (name.equals("DataExport")) {
                    this.storageClassAnalysis.setDataExport(this.dataExport);
                }
            } else if (this.in("AnalyticsConfiguration", "StorageClassAnalysis", "DataExport")) {
                if (name.equals("OutputSchemaVersion")) {
                    this.dataExport.setOutputSchemaVersion(this.getText());
                } else if (name.equals("Destination")) {
                    this.dataExport.setDestination(this.destination);
                }
            } else if (this.in("AnalyticsConfiguration", "StorageClassAnalysis", "DataExport", "Destination")) {
                if (name.equals("S3BucketDestination")) {
                    this.destination.setS3BucketDestination(this.s3BucketDestination);
                }
            } else if (this.in("AnalyticsConfiguration", "StorageClassAnalysis", "DataExport", "Destination", "S3BucketDestination")) {
                if (name.equals("Format")) {
                    this.s3BucketDestination.setFormat(this.getText());
                } else if (name.equals("BucketAccountId")) {
                    this.s3BucketDestination.setBucketAccountId(this.getText());
                } else if (name.equals("Bucket")) {
                    this.s3BucketDestination.setBucketArn(this.getText());
                } else if (name.equals("Prefix")) {
                    this.s3BucketDestination.setPrefix(this.getText());
                }
            }
        }
    }

    public static class GetBucketOwnershipControlsHandler
    extends AbstractHandler {
        private List<OwnershipControlsRule> rulesList;

        public GetBucketOwnershipControlsResult getResult() {
            OwnershipControls ownershipControls = new OwnershipControls().withRules(this.rulesList);
            return new GetBucketOwnershipControlsResult().withOwnershipControls(ownershipControls);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("OwnershipControls") && name.equals("Rule") && this.rulesList == null) {
                this.rulesList = new ArrayList<OwnershipControlsRule>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("OwnershipControls", "Rule") && name.equals("ObjectOwnership")) {
                this.rulesList.add(new OwnershipControlsRule().withOwnership(this.getText()));
            }
        }
    }

    public static class ListBucketMetricsConfigurationsHandler
    extends AbstractHandler {
        private final ListBucketMetricsConfigurationsResult result = new ListBucketMetricsConfigurationsResult();
        private MetricsConfiguration currentConfiguration;
        private MetricsFilter currentFilter;
        private List<MetricsFilterPredicate> andOperandsList;
        private String currentTagKey;
        private String currentTagValue;

        public ListBucketMetricsConfigurationsResult getResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListMetricsConfigurationsResult")) {
                if (name.equals("MetricsConfiguration")) {
                    this.currentConfiguration = new MetricsConfiguration();
                }
            } else if (this.in("ListMetricsConfigurationsResult", "MetricsConfiguration")) {
                if (name.equals("Filter")) {
                    this.currentFilter = new MetricsFilter();
                }
            } else if (this.in("ListMetricsConfigurationsResult", "MetricsConfiguration", "Filter") && name.equals("And")) {
                this.andOperandsList = new ArrayList<MetricsFilterPredicate>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListMetricsConfigurationsResult")) {
                if (name.equals("MetricsConfiguration")) {
                    if (this.result.getMetricsConfigurationList() == null) {
                        this.result.setMetricsConfigurationList(new ArrayList<MetricsConfiguration>());
                    }
                    this.result.getMetricsConfigurationList().add(this.currentConfiguration);
                    this.currentConfiguration = null;
                } else if (name.equals("IsTruncated")) {
                    this.result.setTruncated("true".equals(this.getText()));
                } else if (name.equals("ContinuationToken")) {
                    this.result.setContinuationToken(this.getText());
                } else if (name.equals("NextContinuationToken")) {
                    this.result.setNextContinuationToken(this.getText());
                }
            } else if (this.in("ListMetricsConfigurationsResult", "MetricsConfiguration")) {
                if (name.equals("Id")) {
                    this.currentConfiguration.setId(this.getText());
                } else if (name.equals("Filter")) {
                    this.currentConfiguration.setFilter(this.currentFilter);
                    this.currentFilter = null;
                }
            } else if (this.in("ListMetricsConfigurationsResult", "MetricsConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.currentFilter.setPredicate(new MetricsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.currentFilter.setPredicate(new MetricsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("And")) {
                    this.currentFilter.setPredicate(new MetricsAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                } else if (name.equals("AccessPointArn")) {
                    this.currentFilter.setPredicate(new MetricsAccessPointArnPredicate(this.getText()));
                }
            } else if (this.in("ListMetricsConfigurationsResult", "MetricsConfiguration", "Filter", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("ListMetricsConfigurationsResult", "MetricsConfiguration", "Filter", "And")) {
                if (name.equals("Prefix")) {
                    this.andOperandsList.add(new MetricsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.andOperandsList.add(new MetricsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("AccessPointArn")) {
                    this.andOperandsList.add(new MetricsAccessPointArnPredicate(this.getText()));
                }
            } else if (this.in("ListMetricsConfigurationsResult", "MetricsConfiguration", "Filter", "And", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            }
        }
    }

    public static class GetBucketMetricsConfigurationHandler
    extends AbstractHandler {
        private final MetricsConfiguration configuration = new MetricsConfiguration();
        private MetricsFilter filter;
        private List<MetricsFilterPredicate> andOperandsList;
        private String currentTagKey;
        private String currentTagValue;

        public GetBucketMetricsConfigurationResult getResult() {
            return new GetBucketMetricsConfigurationResult().withMetricsConfiguration(this.configuration);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("MetricsConfiguration")) {
                if (name.equals("Filter")) {
                    this.filter = new MetricsFilter();
                }
            } else if (this.in("MetricsConfiguration", "Filter") && name.equals("And")) {
                this.andOperandsList = new ArrayList<MetricsFilterPredicate>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("MetricsConfiguration")) {
                if (name.equals("Id")) {
                    this.configuration.setId(this.getText());
                } else if (name.equals("Filter")) {
                    this.configuration.setFilter(this.filter);
                    this.filter = null;
                }
            } else if (this.in("MetricsConfiguration", "Filter")) {
                if (name.equals("Prefix")) {
                    this.filter.setPredicate(new MetricsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.filter.setPredicate(new MetricsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("AccessPointArn")) {
                    this.filter.setPredicate(new MetricsAccessPointArnPredicate(this.getText()));
                } else if (name.equals("And")) {
                    this.filter.setPredicate(new MetricsAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                }
            } else if (this.in("MetricsConfiguration", "Filter", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("MetricsConfiguration", "Filter", "And")) {
                if (name.equals("Prefix")) {
                    this.andOperandsList.add(new MetricsPrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.andOperandsList.add(new MetricsTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("AccessPointArn")) {
                    this.andOperandsList.add(new MetricsAccessPointArnPredicate(this.getText()));
                }
            } else if (this.in("MetricsConfiguration", "Filter", "And", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            }
        }
    }

    public static class BucketCrossOriginConfigurationHandler
    extends AbstractHandler {
        private final BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration(new ArrayList<CORSRule>());
        private CORSRule currentRule;
        private List<CORSRule.AllowedMethods> allowedMethods = null;
        private List<String> allowedOrigins = null;
        private List<String> exposedHeaders = null;
        private List<String> allowedHeaders = null;

        public BucketCrossOriginConfiguration getConfiguration() {
            return this.configuration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("CORSConfiguration")) {
                if (name.equals("CORSRule")) {
                    this.currentRule = new CORSRule();
                }
            } else if (this.in("CORSConfiguration", "CORSRule")) {
                if (name.equals("AllowedOrigin")) {
                    if (this.allowedOrigins == null) {
                        this.allowedOrigins = new ArrayList<String>();
                    }
                } else if (name.equals("AllowedMethod")) {
                    if (this.allowedMethods == null) {
                        this.allowedMethods = new ArrayList<CORSRule.AllowedMethods>();
                    }
                } else if (name.equals("ExposeHeader")) {
                    if (this.exposedHeaders == null) {
                        this.exposedHeaders = new ArrayList<String>();
                    }
                } else if (name.equals("AllowedHeader") && this.allowedHeaders == null) {
                    this.allowedHeaders = new LinkedList<String>();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("CORSConfiguration")) {
                if (name.equals("CORSRule")) {
                    this.currentRule.setAllowedHeaders(this.allowedHeaders);
                    this.currentRule.setAllowedMethods(this.allowedMethods);
                    this.currentRule.setAllowedOrigins(this.allowedOrigins);
                    this.currentRule.setExposedHeaders(this.exposedHeaders);
                    this.allowedHeaders = null;
                    this.allowedMethods = null;
                    this.allowedOrigins = null;
                    this.exposedHeaders = null;
                    this.configuration.getRules().add(this.currentRule);
                    this.currentRule = null;
                }
            } else if (this.in("CORSConfiguration", "CORSRule")) {
                if (name.equals("ID")) {
                    this.currentRule.setId(this.getText());
                } else if (name.equals("AllowedOrigin")) {
                    this.allowedOrigins.add(this.getText());
                } else if (name.equals("AllowedMethod")) {
                    this.allowedMethods.add(CORSRule.AllowedMethods.fromValue(this.getText()));
                } else if (name.equals("MaxAgeSeconds")) {
                    this.currentRule.setMaxAgeSeconds(Integer.parseInt(this.getText()));
                } else if (name.equals("ExposeHeader")) {
                    this.exposedHeaders.add(this.getText());
                } else if (name.equals("AllowedHeader")) {
                    this.allowedHeaders.add(this.getText());
                }
            }
        }
    }

    public static class BucketLifecycleConfigurationHandler
    extends AbstractHandler {
        private final BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration(new ArrayList<BucketLifecycleConfiguration.Rule>());
        private BucketLifecycleConfiguration.Rule currentRule;
        private BucketLifecycleConfiguration.Transition currentTransition;
        private BucketLifecycleConfiguration.NoncurrentVersionTransition currentNcvTransition;
        private BucketLifecycleConfiguration.NoncurrentVersionExpiration ncvExpiration;
        private AbortIncompleteMultipartUpload abortIncompleteMultipartUpload;
        private LifecycleFilter currentFilter;
        private List<LifecycleFilterPredicate> andOperandsList;
        private String currentTagKey;
        private String currentTagValue;

        public BucketLifecycleConfiguration getConfiguration() {
            return this.configuration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("LifecycleConfiguration")) {
                if (name.equals("Rule")) {
                    this.currentRule = new BucketLifecycleConfiguration.Rule();
                }
            } else if (this.in("LifecycleConfiguration", "Rule")) {
                if (name.equals("Transition")) {
                    this.currentTransition = new BucketLifecycleConfiguration.Transition();
                } else if (name.equals("NoncurrentVersionTransition")) {
                    this.currentNcvTransition = new BucketLifecycleConfiguration.NoncurrentVersionTransition();
                } else if (name.equals("NoncurrentVersionExpiration")) {
                    this.ncvExpiration = new BucketLifecycleConfiguration.NoncurrentVersionExpiration();
                } else if (name.equals("AbortIncompleteMultipartUpload")) {
                    this.abortIncompleteMultipartUpload = new AbortIncompleteMultipartUpload();
                } else if (name.equals("Filter")) {
                    this.currentFilter = new LifecycleFilter();
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "Filter") && name.equals("And")) {
                this.andOperandsList = new ArrayList<LifecycleFilterPredicate>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("LifecycleConfiguration")) {
                if (name.equals("Rule")) {
                    this.configuration.getRules().add(this.currentRule);
                    this.currentRule = null;
                }
            } else if (this.in("LifecycleConfiguration", "Rule")) {
                if (name.equals("ID")) {
                    this.currentRule.setId(this.getText());
                } else if (name.equals("Prefix")) {
                    this.currentRule.setPrefix(this.getText());
                } else if (name.equals("Status")) {
                    this.currentRule.setStatus(this.getText());
                } else if (name.equals("Transition")) {
                    this.currentRule.addTransition(this.currentTransition);
                    this.currentTransition = null;
                } else if (name.equals("NoncurrentVersionTransition")) {
                    this.currentRule.addNoncurrentVersionTransition(this.currentNcvTransition);
                    this.currentNcvTransition = null;
                } else if (name.equals("NoncurrentVersionExpiration")) {
                    this.currentRule.setNoncurrentVersionExpiration(this.ncvExpiration);
                    this.ncvExpiration = null;
                } else if (name.equals("AbortIncompleteMultipartUpload")) {
                    this.currentRule.setAbortIncompleteMultipartUpload(this.abortIncompleteMultipartUpload);
                    this.abortIncompleteMultipartUpload = null;
                } else if (name.equals("Filter")) {
                    this.currentRule.setFilter(this.currentFilter);
                    this.currentFilter = null;
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "Expiration")) {
                if (name.equals("Date")) {
                    this.currentRule.setExpirationDate(ServiceUtils.parseIso8601Date(this.getText()));
                } else if (name.equals("Days")) {
                    this.currentRule.setExpirationInDays(Integer.parseInt(this.getText()));
                } else if (name.equals("ExpiredObjectDeleteMarker") && "true".equals(this.getText())) {
                    this.currentRule.setExpiredObjectDeleteMarker(true);
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "Transition")) {
                if (name.equals("StorageClass")) {
                    this.currentTransition.setStorageClass(this.getText());
                } else if (name.equals("Date")) {
                    this.currentTransition.setDate(ServiceUtils.parseIso8601Date(this.getText()));
                } else if (name.equals("Days")) {
                    this.currentTransition.setDays(Integer.parseInt(this.getText()));
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "NoncurrentVersionExpiration")) {
                if (name.equals("NoncurrentDays")) {
                    this.ncvExpiration.setDays(Integer.parseInt(this.getText()));
                } else if (name.equals("NewerNoncurrentVersions")) {
                    this.ncvExpiration.setNewerNoncurrentVersions(Integer.parseInt(this.getText()));
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "NoncurrentVersionTransition")) {
                if (name.equals("StorageClass")) {
                    this.currentNcvTransition.setStorageClass(this.getText());
                } else if (name.equals("NoncurrentDays")) {
                    this.currentNcvTransition.setDays(Integer.parseInt(this.getText()));
                } else if (name.equals("NewerNoncurrentVersions")) {
                    this.currentNcvTransition.setNewerNoncurrentVersions(Integer.parseInt(this.getText()));
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "AbortIncompleteMultipartUpload")) {
                if (name.equals("DaysAfterInitiation")) {
                    this.abortIncompleteMultipartUpload.setDaysAfterInitiation(Integer.parseInt(this.getText()));
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "Filter")) {
                if (name.equals("Prefix")) {
                    this.currentFilter.setPredicate(new LifecyclePrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.currentFilter.setPredicate(new LifecycleTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("ObjectSizeGreaterThan")) {
                    this.currentFilter.setPredicate(new LifecycleObjectSizeGreaterThanPredicate(Long.parseLong(this.getText())));
                } else if (name.equals("ObjectSizeLessThan")) {
                    this.currentFilter.setPredicate(new LifecycleObjectSizeLessThanPredicate(Long.parseLong(this.getText())));
                } else if (name.equals("And")) {
                    this.currentFilter.setPredicate(new LifecycleAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "Filter", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "Filter", "And")) {
                if (name.equals("Prefix")) {
                    this.andOperandsList.add(new LifecyclePrefixPredicate(this.getText()));
                } else if (name.equals("Tag")) {
                    this.andOperandsList.add(new LifecycleTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals("ObjectSizeGreaterThan")) {
                    this.andOperandsList.add(new LifecycleObjectSizeGreaterThanPredicate(Long.parseLong(this.getText())));
                } else if (name.equals("ObjectSizeLessThan")) {
                    this.andOperandsList.add(new LifecycleObjectSizeLessThanPredicate(Long.parseLong(this.getText())));
                }
            } else if (this.in("LifecycleConfiguration", "Rule", "Filter", "And", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            }
        }
    }

    public static class DeleteObjectsHandler
    extends AbstractHandler {
        private final DeleteObjectsResponse response = new DeleteObjectsResponse();
        private DeleteObjectsResult.DeletedObject currentDeletedObject = null;
        private MultiObjectDeleteException.DeleteError currentError = null;

        public DeleteObjectsResponse getDeleteObjectResult() {
            return this.response;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("DeleteResult")) {
                if (name.equals("Deleted")) {
                    this.currentDeletedObject = new DeleteObjectsResult.DeletedObject();
                } else if (name.equals("Error")) {
                    this.currentError = new MultiObjectDeleteException.DeleteError();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("DeleteResult")) {
                if (name.equals("Deleted")) {
                    this.response.getDeletedObjects().add(this.currentDeletedObject);
                    this.currentDeletedObject = null;
                } else if (name.equals("Error")) {
                    this.response.getErrors().add(this.currentError);
                    this.currentError = null;
                }
            } else if (this.in("DeleteResult", "Deleted")) {
                if (name.equals("Key")) {
                    this.currentDeletedObject.setKey(this.getText());
                } else if (name.equals("VersionId")) {
                    this.currentDeletedObject.setVersionId(this.getText());
                } else if (name.equals("DeleteMarker")) {
                    this.currentDeletedObject.setDeleteMarker(this.getText().equals("true"));
                } else if (name.equals("DeleteMarkerVersionId")) {
                    this.currentDeletedObject.setDeleteMarkerVersionId(this.getText());
                }
            } else if (this.in("DeleteResult", "Error")) {
                if (name.equals("Key")) {
                    this.currentError.setKey(this.getText());
                } else if (name.equals("VersionId")) {
                    this.currentError.setVersionId(this.getText());
                } else if (name.equals("Code")) {
                    this.currentError.setCode(this.getText());
                } else if (name.equals("Message")) {
                    this.currentError.setMessage(this.getText());
                }
            }
        }
    }

    public static class GetObjectTaggingHandler
    extends AbstractHandler {
        private GetObjectTaggingResult getObjectTaggingResult;
        private List<Tag> tagSet;
        private String currentTagValue;
        private String currentTagKey;

        public GetObjectTaggingResult getResult() {
            return this.getObjectTaggingResult;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("Tagging") && name.equals("TagSet")) {
                this.tagSet = new ArrayList<Tag>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("Tagging") && name.equals("TagSet")) {
                this.getObjectTaggingResult = new GetObjectTaggingResult(this.tagSet);
                this.tagSet = null;
            }
            if (this.in("Tagging", "TagSet")) {
                if (name.equals("Tag")) {
                    this.tagSet.add(new Tag(this.currentTagKey, this.currentTagValue));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                }
            } else if (this.in("Tagging", "TagSet", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            }
        }
    }

    public static class BucketTaggingConfigurationHandler
    extends AbstractHandler {
        private final BucketTaggingConfiguration configuration = new BucketTaggingConfiguration();
        private Map<String, String> currentTagSet;
        private String currentTagKey;
        private String currentTagValue;

        public BucketTaggingConfiguration getConfiguration() {
            return this.configuration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("Tagging") && name.equals("TagSet")) {
                this.currentTagSet = new LinkedHashMap<String, String>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("Tagging")) {
                if (name.equals("TagSet")) {
                    this.configuration.getAllTagSets().add(new TagSet(this.currentTagSet));
                    this.currentTagSet = null;
                }
            } else if (this.in("Tagging", "TagSet")) {
                if (name.equals("Tag")) {
                    if (this.currentTagKey != null && this.currentTagValue != null) {
                        this.currentTagSet.put(this.currentTagKey, this.currentTagValue);
                    }
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                }
            } else if (this.in("Tagging", "TagSet", "Tag")) {
                if (name.equals("Key")) {
                    this.currentTagKey = this.getText();
                } else if (name.equals("Value")) {
                    this.currentTagValue = this.getText();
                }
            }
        }
    }

    public static class BucketReplicationConfigurationHandler
    extends AbstractHandler {
        private final BucketReplicationConfiguration bucketReplicationConfiguration = new BucketReplicationConfiguration();
        private String currentRuleId;
        private ReplicationRule currentRule;
        private ReplicationFilter currentFilter;
        private List<ReplicationFilterPredicate> andOperandsList;
        private String currentTagKey;
        private String currentTagValue;
        private ExistingObjectReplication existingObjectReplication;
        private DeleteMarkerReplication deleteMarkerReplication;
        private ReplicationDestinationConfig destinationConfig;
        private AccessControlTranslation accessControlTranslation;
        private EncryptionConfiguration encryptionConfiguration;
        private ReplicationTime replicationTime;
        private Metrics metrics;
        private SourceSelectionCriteria sourceSelectionCriteria;
        private SseKmsEncryptedObjects sseKmsEncryptedObjects;
        private ReplicaModifications replicaModifications;
        private static final String REPLICATION_CONFIG = "ReplicationConfiguration";
        private static final String ROLE = "Role";
        private static final String RULE = "Rule";
        private static final String DESTINATION = "Destination";
        private static final String ID = "ID";
        private static final String PREFIX = "Prefix";
        private static final String FILTER = "Filter";
        private static final String AND = "And";
        private static final String TAG = "Tag";
        private static final String TAG_KEY = "Key";
        private static final String TAG_VALUE = "Value";
        private static final String EXISTING_OBJECT_REPLICATION = "ExistingObjectReplication";
        private static final String DELETE_MARKER_REPLICATION = "DeleteMarkerReplication";
        private static final String PRIORITY = "Priority";
        private static final String STATUS = "Status";
        private static final String BUCKET = "Bucket";
        private static final String STORAGECLASS = "StorageClass";
        private static final String ACCOUNT = "Account";
        private static final String ACCESS_CONTROL_TRANSLATION = "AccessControlTranslation";
        private static final String OWNER = "Owner";
        private static final String ENCRYPTION_CONFIGURATION = "EncryptionConfiguration";
        private static final String REPLICATION_TIME = "ReplicationTime";
        private static final String TIME = "Time";
        private static final String MINUTES = "Minutes";
        private static final String METRICS = "Metrics";
        private static final String EVENT_THRESHOLD = "EventThreshold";
        private static final String REPLICA_KMS_KEY_ID = "ReplicaKmsKeyID";
        private static final String SOURCE_SELECTION_CRITERIA = "SourceSelectionCriteria";
        private static final String SSE_KMS_ENCRYPTED_OBJECTS = "SseKmsEncryptedObjects";
        private static final String REPLICA_MODIFICATIONS = "ReplicaModifications";

        public BucketReplicationConfiguration getConfiguration() {
            return this.bucketReplicationConfiguration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in(REPLICATION_CONFIG)) {
                if (name.equals(RULE)) {
                    this.currentRule = new ReplicationRule();
                }
            } else if (this.in(REPLICATION_CONFIG, RULE)) {
                if (name.equals(DESTINATION)) {
                    this.destinationConfig = new ReplicationDestinationConfig();
                } else if (name.equals(SOURCE_SELECTION_CRITERIA)) {
                    this.sourceSelectionCriteria = new SourceSelectionCriteria();
                } else if (name.equals(EXISTING_OBJECT_REPLICATION)) {
                    this.existingObjectReplication = new ExistingObjectReplication();
                } else if (name.equals(DELETE_MARKER_REPLICATION)) {
                    this.deleteMarkerReplication = new DeleteMarkerReplication();
                } else if (name.equals(FILTER)) {
                    this.currentFilter = new ReplicationFilter();
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION)) {
                if (name.equals(ACCESS_CONTROL_TRANSLATION)) {
                    this.accessControlTranslation = new AccessControlTranslation();
                } else if (name.equals(ENCRYPTION_CONFIGURATION)) {
                    this.encryptionConfiguration = new EncryptionConfiguration();
                } else if (name.equals(REPLICATION_TIME)) {
                    this.replicationTime = new ReplicationTime();
                } else if (name.equals(METRICS)) {
                    this.metrics = new Metrics();
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, REPLICATION_TIME)) {
                if (name.equals(TIME)) {
                    this.replicationTime.setTime(new ReplicationTimeValue());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, METRICS)) {
                if (name.equals(EVENT_THRESHOLD)) {
                    this.metrics.setEventThreshold(new ReplicationTimeValue());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, SOURCE_SELECTION_CRITERIA)) {
                if (name.equals(SSE_KMS_ENCRYPTED_OBJECTS)) {
                    this.sseKmsEncryptedObjects = new SseKmsEncryptedObjects();
                } else if (name.equals(REPLICA_MODIFICATIONS)) {
                    this.replicaModifications = new ReplicaModifications();
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, FILTER) && name.equals(AND)) {
                this.andOperandsList = new ArrayList<ReplicationFilterPredicate>();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in(REPLICATION_CONFIG)) {
                if (name.equals(RULE)) {
                    this.bucketReplicationConfiguration.addRule(this.currentRuleId, this.currentRule);
                    this.currentRule = null;
                    this.currentRuleId = null;
                    this.existingObjectReplication = null;
                    this.deleteMarkerReplication = null;
                    this.destinationConfig = null;
                    this.sseKmsEncryptedObjects = null;
                    this.accessControlTranslation = null;
                    this.encryptionConfiguration = null;
                    this.replicaModifications = null;
                } else if (name.equals(ROLE)) {
                    this.bucketReplicationConfiguration.setRoleARN(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE)) {
                if (name.equals(ID)) {
                    this.currentRuleId = this.getText();
                } else if (name.equals(PREFIX)) {
                    this.currentRule.setPrefix(this.getText());
                } else if (name.equals(PRIORITY)) {
                    this.currentRule.setPriority(Integer.valueOf(this.getText()));
                } else if (name.equals(EXISTING_OBJECT_REPLICATION)) {
                    this.currentRule.setExistingObjectReplication(this.existingObjectReplication);
                } else if (name.equals(DELETE_MARKER_REPLICATION)) {
                    this.currentRule.setDeleteMarkerReplication(this.deleteMarkerReplication);
                } else if (name.equals(SOURCE_SELECTION_CRITERIA)) {
                    this.currentRule.setSourceSelectionCriteria(this.sourceSelectionCriteria);
                } else if (name.equals(FILTER)) {
                    this.currentRule.setFilter(this.currentFilter);
                    this.currentFilter = null;
                } else if (name.equals(STATUS)) {
                    this.currentRule.setStatus(this.getText());
                } else if (name.equals(DESTINATION)) {
                    this.currentRule.setDestinationConfig(this.destinationConfig);
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, FILTER)) {
                if (name.equals(PREFIX)) {
                    this.currentFilter.setPredicate(new ReplicationPrefixPredicate(this.getText()));
                } else if (name.equals(TAG)) {
                    this.currentFilter.setPredicate(new ReplicationTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                } else if (name.equals(AND)) {
                    this.currentFilter.setPredicate(new ReplicationAndOperator(this.andOperandsList));
                    this.andOperandsList = null;
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, FILTER, TAG)) {
                if (name.equals(TAG_KEY)) {
                    this.currentTagKey = this.getText();
                } else if (name.equals(TAG_VALUE)) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, FILTER, AND)) {
                if (name.equals(PREFIX)) {
                    this.andOperandsList.add(new ReplicationPrefixPredicate(this.getText()));
                } else if (name.equals(TAG)) {
                    this.andOperandsList.add(new ReplicationTagPredicate(new Tag(this.currentTagKey, this.currentTagValue)));
                    this.currentTagKey = null;
                    this.currentTagValue = null;
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, FILTER, AND, TAG)) {
                if (name.equals(TAG_KEY)) {
                    this.currentTagKey = this.getText();
                } else if (name.equals(TAG_VALUE)) {
                    this.currentTagValue = this.getText();
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, SOURCE_SELECTION_CRITERIA)) {
                if (name.equals(SSE_KMS_ENCRYPTED_OBJECTS)) {
                    this.sourceSelectionCriteria.setSseKmsEncryptedObjects(this.sseKmsEncryptedObjects);
                } else if (name.equals(REPLICA_MODIFICATIONS)) {
                    this.sourceSelectionCriteria.setReplicaModifications(this.replicaModifications);
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, SOURCE_SELECTION_CRITERIA, SSE_KMS_ENCRYPTED_OBJECTS)) {
                if (name.equals(STATUS)) {
                    this.sseKmsEncryptedObjects.setStatus(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, SOURCE_SELECTION_CRITERIA, REPLICA_MODIFICATIONS)) {
                if (name.equals(STATUS)) {
                    this.replicaModifications.setStatus(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, EXISTING_OBJECT_REPLICATION)) {
                if (name.equals(STATUS)) {
                    this.existingObjectReplication.setStatus(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DELETE_MARKER_REPLICATION)) {
                if (name.equals(STATUS)) {
                    this.deleteMarkerReplication.setStatus(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION)) {
                if (name.equals(BUCKET)) {
                    this.destinationConfig.setBucketARN(this.getText());
                } else if (name.equals(STORAGECLASS)) {
                    this.destinationConfig.setStorageClass(this.getText());
                } else if (name.equals(ACCOUNT)) {
                    this.destinationConfig.setAccount(this.getText());
                } else if (name.equals(ACCESS_CONTROL_TRANSLATION)) {
                    this.destinationConfig.setAccessControlTranslation(this.accessControlTranslation);
                } else if (name.equals(ENCRYPTION_CONFIGURATION)) {
                    this.destinationConfig.setEncryptionConfiguration(this.encryptionConfiguration);
                } else if (name.equals(REPLICATION_TIME)) {
                    this.destinationConfig.setReplicationTime(this.replicationTime);
                } else if (name.equals(METRICS)) {
                    this.destinationConfig.setMetrics(this.metrics);
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, ACCESS_CONTROL_TRANSLATION)) {
                if (name.equals(OWNER)) {
                    this.accessControlTranslation.setOwner(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, ENCRYPTION_CONFIGURATION)) {
                if (name.equals(REPLICA_KMS_KEY_ID)) {
                    this.encryptionConfiguration.setReplicaKmsKeyID(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, REPLICATION_TIME)) {
                if (name.equals(STATUS)) {
                    this.replicationTime.setStatus(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, REPLICATION_TIME, TIME)) {
                if (name.equals(MINUTES)) {
                    this.replicationTime.getTime().setMinutes(Integer.parseInt(this.getText()));
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, METRICS)) {
                if (name.equals(STATUS)) {
                    this.metrics.setStatus(this.getText());
                }
            } else if (this.in(REPLICATION_CONFIG, RULE, DESTINATION, METRICS, EVENT_THRESHOLD) && name.equals(MINUTES)) {
                this.metrics.getEventThreshold().setMinutes(Integer.parseInt(this.getText()));
            }
        }
    }

    public static class ListPartsHandler
    extends AbstractHandler {
        private final PartListing result = new PartListing();
        private PartSummary currentPart;
        private Owner currentOwner;

        public PartListing getListPartsResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListPartsResult")) {
                if (name.equals("Part")) {
                    this.currentPart = new PartSummary();
                } else if (name.equals("Owner") || name.equals("Initiator")) {
                    this.currentOwner = new Owner();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListPartsResult")) {
                if (name.equals("Bucket")) {
                    this.result.setBucketName(this.getText());
                } else if (name.equals("Key")) {
                    this.result.setKey(this.getText());
                } else if (name.equals("UploadId")) {
                    this.result.setUploadId(this.getText());
                } else if (name.equals("Owner")) {
                    this.result.setOwner(this.currentOwner);
                    this.currentOwner = null;
                } else if (name.equals("Initiator")) {
                    this.result.setInitiator(this.currentOwner);
                    this.currentOwner = null;
                } else if (name.equals("StorageClass")) {
                    this.result.setStorageClass(this.getText());
                } else if (name.equals("PartNumberMarker")) {
                    this.result.setPartNumberMarker(this.parseInteger(this.getText()));
                } else if (name.equals("NextPartNumberMarker")) {
                    this.result.setNextPartNumberMarker(this.parseInteger(this.getText()));
                } else if (name.equals("MaxParts")) {
                    this.result.setMaxParts(this.parseInteger(this.getText()));
                } else if (name.equals("EncodingType")) {
                    this.result.setEncodingType(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("IsTruncated")) {
                    this.result.setTruncated(Boolean.parseBoolean(this.getText()));
                } else if (name.equals("Part")) {
                    this.result.getParts().add(this.currentPart);
                    this.currentPart = null;
                }
            } else if (this.in("ListPartsResult", "Part")) {
                if (name.equals("PartNumber")) {
                    this.currentPart.setPartNumber(Integer.parseInt(this.getText()));
                } else if (name.equals("LastModified")) {
                    this.currentPart.setLastModified(ServiceUtils.parseIso8601Date(this.getText()));
                } else if (name.equals("ETag")) {
                    this.currentPart.setETag(ServiceUtils.removeQuotes(this.getText()));
                } else if (name.equals("Size")) {
                    this.currentPart.setSize(Long.parseLong(this.getText()));
                }
            } else if (this.in("ListPartsResult", "Owner") || this.in("ListPartsResult", "Initiator")) {
                if (name.equals("ID")) {
                    this.currentOwner.setId(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("DisplayName")) {
                    this.currentOwner.setDisplayName(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                }
            }
        }

        private Integer parseInteger(String text) {
            text = XmlResponsesSaxParser.checkForEmptyString(this.getText());
            if (text == null) {
                return null;
            }
            return Integer.parseInt(text);
        }
    }

    public static class ListMultipartUploadsHandler
    extends AbstractHandler {
        private final MultipartUploadListing result = new MultipartUploadListing();
        private MultipartUpload currentMultipartUpload;
        private Owner currentOwner;

        public MultipartUploadListing getListMultipartUploadsResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListMultipartUploadsResult")) {
                if (name.equals("Upload")) {
                    this.currentMultipartUpload = new MultipartUpload();
                }
            } else if (this.in("ListMultipartUploadsResult", "Upload") && (name.equals("Owner") || name.equals("Initiator"))) {
                this.currentOwner = new Owner();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListMultipartUploadsResult")) {
                if (name.equals("Bucket")) {
                    this.result.setBucketName(this.getText());
                } else if (name.equals("KeyMarker")) {
                    this.result.setKeyMarker(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("Delimiter")) {
                    this.result.setDelimiter(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("Prefix")) {
                    this.result.setPrefix(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("UploadIdMarker")) {
                    this.result.setUploadIdMarker(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("NextKeyMarker")) {
                    this.result.setNextKeyMarker(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("NextUploadIdMarker")) {
                    this.result.setNextUploadIdMarker(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("MaxUploads")) {
                    this.result.setMaxUploads(Integer.parseInt(this.getText()));
                } else if (name.equals("EncodingType")) {
                    this.result.setEncodingType(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("IsTruncated")) {
                    this.result.setTruncated(Boolean.parseBoolean(this.getText()));
                } else if (name.equals("Upload")) {
                    this.result.getMultipartUploads().add(this.currentMultipartUpload);
                    this.currentMultipartUpload = null;
                }
            } else if (this.in("ListMultipartUploadsResult", "CommonPrefixes")) {
                if (name.equals("Prefix")) {
                    this.result.getCommonPrefixes().add(this.getText());
                }
            } else if (this.in("ListMultipartUploadsResult", "Upload")) {
                if (name.equals("Key")) {
                    this.currentMultipartUpload.setKey(this.getText());
                } else if (name.equals("UploadId")) {
                    this.currentMultipartUpload.setUploadId(this.getText());
                } else if (name.equals("Owner")) {
                    this.currentMultipartUpload.setOwner(this.currentOwner);
                    this.currentOwner = null;
                } else if (name.equals("Initiator")) {
                    this.currentMultipartUpload.setInitiator(this.currentOwner);
                    this.currentOwner = null;
                } else if (name.equals("StorageClass")) {
                    this.currentMultipartUpload.setStorageClass(this.getText());
                } else if (name.equals("Initiated")) {
                    this.currentMultipartUpload.setInitiated(ServiceUtils.parseIso8601Date(this.getText()));
                }
            } else if (this.in("ListMultipartUploadsResult", "Upload", "Owner") || this.in("ListMultipartUploadsResult", "Upload", "Initiator")) {
                if (name.equals("ID")) {
                    this.currentOwner.setId(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("DisplayName")) {
                    this.currentOwner.setDisplayName(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                }
            }
        }
    }

    public static class InitiateMultipartUploadHandler
    extends AbstractHandler {
        private final InitiateMultipartUploadResult result = new InitiateMultipartUploadResult();

        public InitiateMultipartUploadResult getInitiateMultipartUploadResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("InitiateMultipartUploadResult")) {
                if (name.equals("Bucket")) {
                    this.result.setBucketName(this.getText());
                } else if (name.equals("Key")) {
                    this.result.setKey(this.getText());
                } else if (name.equals("UploadId")) {
                    this.result.setUploadId(this.getText());
                }
            }
        }
    }

    public static class CompleteMultipartUploadHandler
    extends AbstractSSEHandler
    implements ObjectExpirationResult,
    S3VersionResult,
    S3RequesterChargedResult {
        private CompleteMultipartUploadResult result;
        private AmazonS3Exception ase;
        private String hostId;
        private String requestId;
        private String errorCode;

        @Override
        protected ServerSideEncryptionResult sseResult() {
            return this.result;
        }

        @Override
        public Date getExpirationTime() {
            return this.result == null ? null : this.result.getExpirationTime();
        }

        @Override
        public void setExpirationTime(Date expirationTime) {
            if (this.result != null) {
                this.result.setExpirationTime(expirationTime);
            }
        }

        @Override
        public String getExpirationTimeRuleId() {
            return this.result == null ? null : this.result.getExpirationTimeRuleId();
        }

        @Override
        public void setExpirationTimeRuleId(String expirationTimeRuleId) {
            if (this.result != null) {
                this.result.setExpirationTimeRuleId(expirationTimeRuleId);
            }
        }

        @Override
        public void setVersionId(String versionId) {
            if (this.result != null) {
                this.result.setVersionId(versionId);
            }
        }

        @Override
        public String getVersionId() {
            return this.result == null ? null : this.result.getVersionId();
        }

        @Override
        public boolean isRequesterCharged() {
            return this.result == null ? false : this.result.isRequesterCharged();
        }

        @Override
        public void setRequesterCharged(boolean isRequesterCharged) {
            if (this.result != null) {
                this.result.setRequesterCharged(isRequesterCharged);
            }
        }

        public CompleteMultipartUploadResult getCompleteMultipartUploadResult() {
            return this.result;
        }

        public AmazonS3Exception getAmazonS3Exception() {
            return this.ase;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.atTopLevel() && name.equals("CompleteMultipartUploadResult")) {
                this.result = new CompleteMultipartUploadResult();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.atTopLevel()) {
                if (name.equals("Error") && this.ase != null) {
                    this.ase.setErrorCode(this.errorCode);
                    this.ase.setRequestId(this.requestId);
                    this.ase.setExtendedRequestId(this.hostId);
                }
            } else if (this.in("CompleteMultipartUploadResult")) {
                if (name.equals("Location")) {
                    this.result.setLocation(this.getText());
                } else if (name.equals("Bucket")) {
                    this.result.setBucketName(this.getText());
                } else if (name.equals("Key")) {
                    this.result.setKey(this.getText());
                } else if (name.equals("ETag")) {
                    this.result.setETag(ServiceUtils.removeQuotes(this.getText()));
                }
            } else if (this.in("Error")) {
                if (name.equals("Code")) {
                    this.errorCode = this.getText();
                } else if (name.equals("Message")) {
                    this.ase = new AmazonS3Exception(this.getText());
                } else if (name.equals("RequestId")) {
                    this.requestId = this.getText();
                } else if (name.equals("HostId")) {
                    this.hostId = this.getText();
                }
            }
        }
    }

    public static class BucketAccelerateConfigurationHandler
    extends AbstractHandler {
        private final BucketAccelerateConfiguration configuration = new BucketAccelerateConfiguration((String)null);

        public BucketAccelerateConfiguration getConfiguration() {
            return this.configuration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("AccelerateConfiguration") && name.equals("Status")) {
                this.configuration.setStatus(this.getText());
            }
        }
    }

    public static class BucketVersioningConfigurationHandler
    extends AbstractHandler {
        private final BucketVersioningConfiguration configuration = new BucketVersioningConfiguration();

        public BucketVersioningConfiguration getConfiguration() {
            return this.configuration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("VersioningConfiguration")) {
                if (name.equals("Status")) {
                    this.configuration.setStatus(this.getText());
                } else if (name.equals("MfaDelete")) {
                    String mfaDeleteStatus = this.getText();
                    if (mfaDeleteStatus.equals("Disabled")) {
                        this.configuration.setMfaDeleteEnabled(false);
                    } else if (mfaDeleteStatus.equals("Enabled")) {
                        this.configuration.setMfaDeleteEnabled(true);
                    } else {
                        this.configuration.setMfaDeleteEnabled(null);
                    }
                }
            }
        }
    }

    public static class BucketWebsiteConfigurationHandler
    extends AbstractHandler {
        private final BucketWebsiteConfiguration configuration = new BucketWebsiteConfiguration(null);
        private RoutingRuleCondition currentCondition = null;
        private RedirectRule currentRedirectRule = null;
        private RoutingRule currentRoutingRule = null;

        public BucketWebsiteConfiguration getConfiguration() {
            return this.configuration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("WebsiteConfiguration")) {
                if (name.equals("RedirectAllRequestsTo")) {
                    this.currentRedirectRule = new RedirectRule();
                }
            } else if (this.in("WebsiteConfiguration", "RoutingRules")) {
                if (name.equals("RoutingRule")) {
                    this.currentRoutingRule = new RoutingRule();
                }
            } else if (this.in("WebsiteConfiguration", "RoutingRules", "RoutingRule")) {
                if (name.equals("Condition")) {
                    this.currentCondition = new RoutingRuleCondition();
                } else if (name.equals("Redirect")) {
                    this.currentRedirectRule = new RedirectRule();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("WebsiteConfiguration")) {
                if (name.equals("RedirectAllRequestsTo")) {
                    this.configuration.setRedirectAllRequestsTo(this.currentRedirectRule);
                    this.currentRedirectRule = null;
                }
            } else if (this.in("WebsiteConfiguration", "IndexDocument")) {
                if (name.equals("Suffix")) {
                    this.configuration.setIndexDocumentSuffix(this.getText());
                }
            } else if (this.in("WebsiteConfiguration", "ErrorDocument")) {
                if (name.equals("Key")) {
                    this.configuration.setErrorDocument(this.getText());
                }
            } else if (this.in("WebsiteConfiguration", "RoutingRules")) {
                if (name.equals("RoutingRule")) {
                    this.configuration.getRoutingRules().add(this.currentRoutingRule);
                    this.currentRoutingRule = null;
                }
            } else if (this.in("WebsiteConfiguration", "RoutingRules", "RoutingRule")) {
                if (name.equals("Condition")) {
                    this.currentRoutingRule.setCondition(this.currentCondition);
                    this.currentCondition = null;
                } else if (name.equals("Redirect")) {
                    this.currentRoutingRule.setRedirect(this.currentRedirectRule);
                    this.currentRedirectRule = null;
                }
            } else if (this.in("WebsiteConfiguration", "RoutingRules", "RoutingRule", "Condition")) {
                if (name.equals("KeyPrefixEquals")) {
                    this.currentCondition.setKeyPrefixEquals(this.getText());
                } else if (name.equals("HttpErrorCodeReturnedEquals")) {
                    this.currentCondition.setHttpErrorCodeReturnedEquals(this.getText());
                }
            } else if (this.in("WebsiteConfiguration", "RedirectAllRequestsTo") || this.in("WebsiteConfiguration", "RoutingRules", "RoutingRule", "Redirect")) {
                if (name.equals("Protocol")) {
                    this.currentRedirectRule.setProtocol(this.getText());
                } else if (name.equals("HostName")) {
                    this.currentRedirectRule.setHostName(this.getText());
                } else if (name.equals("ReplaceKeyPrefixWith")) {
                    this.currentRedirectRule.setReplaceKeyPrefixWith(this.getText());
                } else if (name.equals("ReplaceKeyWith")) {
                    this.currentRedirectRule.setReplaceKeyWith(this.getText());
                } else if (name.equals("HttpRedirectCode")) {
                    this.currentRedirectRule.setHttpRedirectCode(this.getText());
                }
            }
        }
    }

    public static class ListVersionsHandler
    extends AbstractHandler {
        private final VersionListing versionListing = new VersionListing();
        private final boolean shouldSDKDecodeResponse;
        private S3VersionSummary currentVersionSummary;
        private Owner currentOwner;

        public ListVersionsHandler(boolean shouldSDKDecodeResponse) {
            this.shouldSDKDecodeResponse = shouldSDKDecodeResponse;
        }

        public VersionListing getListing() {
            return this.versionListing;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListVersionsResult")) {
                if (name.equals("Version")) {
                    this.currentVersionSummary = new S3VersionSummary();
                    this.currentVersionSummary.setBucketName(this.versionListing.getBucketName());
                } else if (name.equals("DeleteMarker")) {
                    this.currentVersionSummary = new S3VersionSummary();
                    this.currentVersionSummary.setBucketName(this.versionListing.getBucketName());
                    this.currentVersionSummary.setIsDeleteMarker(true);
                }
            } else if ((this.in("ListVersionsResult", "Version") || this.in("ListVersionsResult", "DeleteMarker")) && name.equals("Owner")) {
                this.currentOwner = new Owner();
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListVersionsResult")) {
                if (name.equals("Name")) {
                    this.versionListing.setBucketName(this.getText());
                } else if (name.equals("Prefix")) {
                    this.versionListing.setPrefix(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                } else if (name.equals("KeyMarker")) {
                    this.versionListing.setKeyMarker(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                } else if (name.equals("VersionIdMarker")) {
                    this.versionListing.setVersionIdMarker(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("MaxKeys")) {
                    this.versionListing.setMaxKeys(Integer.parseInt(this.getText()));
                } else if (name.equals("Delimiter")) {
                    this.versionListing.setDelimiter(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                } else if (name.equals("EncodingType")) {
                    this.versionListing.setEncodingType(this.shouldSDKDecodeResponse ? null : XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                } else if (name.equals("NextKeyMarker")) {
                    this.versionListing.setNextKeyMarker(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                } else if (name.equals("NextVersionIdMarker")) {
                    this.versionListing.setNextVersionIdMarker(this.getText());
                } else if (name.equals("IsTruncated")) {
                    this.versionListing.setTruncated("true".equals(this.getText()));
                } else if (name.equals("Version") || name.equals("DeleteMarker")) {
                    this.versionListing.getVersionSummaries().add(this.currentVersionSummary);
                    this.currentVersionSummary = null;
                }
            } else if (this.in("ListVersionsResult", "CommonPrefixes")) {
                if (name.equals("Prefix")) {
                    String commonPrefix = XmlResponsesSaxParser.checkForEmptyString(this.getText());
                    this.versionListing.getCommonPrefixes().add(this.shouldSDKDecodeResponse ? SdkHttpUtils.urlDecode(commonPrefix) : commonPrefix);
                }
            } else if (this.in("ListVersionsResult", "Version") || this.in("ListVersionsResult", "DeleteMarker")) {
                if (name.equals("Key")) {
                    this.currentVersionSummary.setKey(XmlResponsesSaxParser.decodeIfSpecified(this.getText(), this.shouldSDKDecodeResponse));
                } else if (name.equals("VersionId")) {
                    this.currentVersionSummary.setVersionId(this.getText());
                } else if (name.equals("IsLatest")) {
                    this.currentVersionSummary.setIsLatest("true".equals(this.getText()));
                } else if (name.equals("LastModified")) {
                    this.currentVersionSummary.setLastModified(ServiceUtils.parseIso8601Date(this.getText()));
                } else if (name.equals("ETag")) {
                    this.currentVersionSummary.setETag(ServiceUtils.removeQuotes(this.getText()));
                } else if (name.equals("Size")) {
                    this.currentVersionSummary.setSize(Long.parseLong(this.getText()));
                } else if (name.equals("Owner")) {
                    this.currentVersionSummary.setOwner(this.currentOwner);
                    this.currentOwner = null;
                } else if (name.equals("StorageClass")) {
                    this.currentVersionSummary.setStorageClass(this.getText());
                }
            } else if (this.in("ListVersionsResult", "Version", "Owner") || this.in("ListVersionsResult", "DeleteMarker", "Owner")) {
                if (name.equals("ID")) {
                    this.currentOwner.setId(this.getText());
                } else if (name.equals("DisplayName")) {
                    this.currentOwner.setDisplayName(this.getText());
                }
            }
        }
    }

    public static class RequestPaymentConfigurationHandler
    extends AbstractHandler {
        private String payer = null;

        public RequestPaymentConfiguration getConfiguration() {
            return new RequestPaymentConfiguration(RequestPaymentConfiguration.Payer.valueOf(this.payer));
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("RequestPaymentConfiguration") && name.equals("Payer")) {
                this.payer = this.getText();
            }
        }
    }

    public static class CopyObjectResultHandler
    extends AbstractSSEHandler
    implements ObjectExpirationResult,
    S3RequesterChargedResult,
    S3VersionResult {
        private final CopyObjectResult result = new CopyObjectResult();
        private String errorCode = null;
        private String errorMessage = null;
        private String errorRequestId = null;
        private String errorHostId = null;
        private boolean receivedErrorResponse = false;

        @Override
        protected ServerSideEncryptionResult sseResult() {
            return this.result;
        }

        public Date getLastModified() {
            return this.result.getLastModifiedDate();
        }

        @Override
        public String getVersionId() {
            return this.result.getVersionId();
        }

        @Override
        public void setVersionId(String versionId) {
            this.result.setVersionId(versionId);
        }

        @Override
        public Date getExpirationTime() {
            return this.result.getExpirationTime();
        }

        @Override
        public void setExpirationTime(Date expirationTime) {
            this.result.setExpirationTime(expirationTime);
        }

        @Override
        public String getExpirationTimeRuleId() {
            return this.result.getExpirationTimeRuleId();
        }

        @Override
        public void setExpirationTimeRuleId(String expirationTimeRuleId) {
            this.result.setExpirationTimeRuleId(expirationTimeRuleId);
        }

        public String getETag() {
            return this.result.getETag();
        }

        public String getErrorCode() {
            return this.errorCode;
        }

        public String getErrorHostId() {
            return this.errorHostId;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public String getErrorRequestId() {
            return this.errorRequestId;
        }

        public boolean isErrorResponse() {
            return this.receivedErrorResponse;
        }

        @Override
        public boolean isRequesterCharged() {
            return this.result.isRequesterCharged();
        }

        @Override
        public void setRequesterCharged(boolean isRequesterCharged) {
            this.result.setRequesterCharged(isRequesterCharged);
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.atTopLevel()) {
                if (name.equals("CopyObjectResult") || name.equals("CopyPartResult")) {
                    this.receivedErrorResponse = false;
                } else if (name.equals("Error")) {
                    this.receivedErrorResponse = true;
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("CopyObjectResult") || this.in("CopyPartResult")) {
                if (name.equals("LastModified")) {
                    this.result.setLastModifiedDate(ServiceUtils.parseIso8601Date(this.getText()));
                } else if (name.equals("ETag")) {
                    this.result.setETag(ServiceUtils.removeQuotes(this.getText()));
                }
            } else if (this.in("Error")) {
                if (name.equals("Code")) {
                    this.errorCode = this.getText();
                } else if (name.equals("Message")) {
                    this.errorMessage = this.getText();
                } else if (name.equals("RequestId")) {
                    this.errorRequestId = this.getText();
                } else if (name.equals("HostId")) {
                    this.errorHostId = this.getText();
                }
            }
        }
    }

    public static class BucketLocationHandler
    extends AbstractHandler {
        private String location = null;

        public String getLocation() {
            return this.location;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.atTopLevel() && name.equals("LocationConstraint")) {
                String elementText = this.getText();
                this.location = elementText.length() == 0 ? null : elementText;
            }
        }
    }

    public static class BucketLoggingConfigurationHandler
    extends AbstractHandler {
        private final BucketLoggingConfiguration bucketLoggingConfiguration = new BucketLoggingConfiguration();

        public BucketLoggingConfiguration getBucketLoggingConfiguration() {
            return this.bucketLoggingConfiguration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("BucketLoggingStatus", "LoggingEnabled")) {
                if (name.equals("TargetBucket")) {
                    this.bucketLoggingConfiguration.setDestinationBucketName(this.getText());
                } else if (name.equals("TargetPrefix")) {
                    this.bucketLoggingConfiguration.setLogFilePrefix(this.getText());
                }
            }
        }
    }

    public static class AccessControlListHandler
    extends AbstractHandler {
        private final AccessControlList accessControlList = new AccessControlList();
        private Grantee currentGrantee = null;
        private Permission currentPermission = null;

        public AccessControlList getAccessControlList() {
            return this.accessControlList;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("AccessControlPolicy")) {
                if (name.equals("Owner")) {
                    this.accessControlList.setOwner(new Owner());
                }
            } else if (this.in("AccessControlPolicy", "AccessControlList", "Grant") && name.equals("Grantee")) {
                String type = XmlResponsesSaxParser.findAttributeValue("xsi:type", attrs);
                if ("AmazonCustomerByEmail".equals(type)) {
                    this.currentGrantee = new EmailAddressGrantee(null);
                } else if ("CanonicalUser".equals(type)) {
                    this.currentGrantee = new CanonicalGrantee(null);
                } else if ("Group".equals(type)) {
                    // empty if block
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("AccessControlPolicy", "Owner")) {
                if (name.equals("ID")) {
                    this.accessControlList.getOwner().setId(this.getText());
                } else if (name.equals("DisplayName")) {
                    this.accessControlList.getOwner().setDisplayName(this.getText());
                }
            } else if (this.in("AccessControlPolicy", "AccessControlList")) {
                if (name.equals("Grant")) {
                    this.accessControlList.grantPermission(this.currentGrantee, this.currentPermission);
                    this.currentGrantee = null;
                    this.currentPermission = null;
                }
            } else if (this.in("AccessControlPolicy", "AccessControlList", "Grant")) {
                if (name.equals("Permission")) {
                    this.currentPermission = Permission.parsePermission(this.getText());
                }
            } else if (this.in("AccessControlPolicy", "AccessControlList", "Grant", "Grantee")) {
                if (name.equals("ID")) {
                    this.currentGrantee.setIdentifier(this.getText());
                } else if (name.equals("EmailAddress")) {
                    this.currentGrantee.setIdentifier(this.getText());
                } else if (name.equals("URI")) {
                    this.currentGrantee = GroupGrantee.parseGroupGrantee(this.getText());
                } else if (name.equals("DisplayName")) {
                    ((CanonicalGrantee)this.currentGrantee).setDisplayName(this.getText());
                }
            }
        }
    }

    public static class ListAllMyBucketsHandler
    extends AbstractHandler {
        private final List<Bucket> buckets = new ArrayList<Bucket>();
        private Owner bucketsOwner = null;
        private Bucket currentBucket = null;

        public List<Bucket> getBuckets() {
            return this.buckets;
        }

        public Owner getOwner() {
            return this.bucketsOwner;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListAllMyBucketsResult")) {
                if (name.equals("Owner")) {
                    this.bucketsOwner = new Owner();
                }
            } else if (this.in("ListAllMyBucketsResult", "Buckets") && name.equals("Bucket")) {
                this.currentBucket = new Bucket();
                this.currentBucket.setOwner(this.bucketsOwner);
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.in("ListAllMyBucketsResult", "Owner")) {
                if (name.equals("ID")) {
                    this.bucketsOwner.setId(this.getText());
                } else if (name.equals("DisplayName")) {
                    this.bucketsOwner.setDisplayName(this.getText());
                }
            } else if (this.in("ListAllMyBucketsResult", "Buckets")) {
                if (name.equals("Bucket")) {
                    this.buckets.add(this.currentBucket);
                    this.currentBucket = null;
                }
            } else if (this.in("ListAllMyBucketsResult", "Buckets", "Bucket")) {
                if (name.equals("Name")) {
                    this.currentBucket.setName(this.getText());
                } else if (name.equals("CreationDate")) {
                    Date creationDate = DateUtils.parseISO8601Date(this.getText());
                    this.currentBucket.setCreationDate(creationDate);
                }
            }
        }
    }

    public static class ListObjectsV2Handler
    extends AbstractHandler {
        private final ListObjectsV2Result result = new ListObjectsV2Result();
        private final boolean shouldSDKDecodeResponse;
        private S3ObjectSummary currentObject = null;
        private Owner currentOwner = null;
        private String lastKey = null;

        public ListObjectsV2Handler(boolean shouldSDKDecodeResponse) {
            this.shouldSDKDecodeResponse = shouldSDKDecodeResponse;
        }

        public ListObjectsV2Result getResult() {
            return this.result;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListBucketResult")) {
                if (name.equals("Contents")) {
                    this.currentObject = new S3ObjectSummary();
                    this.currentObject.setBucketName(this.result.getBucketName());
                }
            } else if (this.in("ListBucketResult", "Contents") && name.equals("Owner")) {
                this.currentOwner = new Owner();
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.atTopLevel()) {
                if (!name.equals("ListBucketResult") || !this.result.isTruncated() || this.result.getNextContinuationToken() != null) return;
                String nextContinuationToken = null;
                if (!this.result.getObjectSummaries().isEmpty()) {
                    nextContinuationToken = this.result.getObjectSummaries().get(this.result.getObjectSummaries().size() - 1).getKey();
                } else {
                    log.error((Object)"S3 response indicates truncated results, but contains no object summaries.");
                }
                this.result.setNextContinuationToken(nextContinuationToken);
                return;
            } else if (this.in("ListBucketResult")) {
                if (name.equals("Name")) {
                    this.result.setBucketName(this.getText());
                    if (!log.isDebugEnabled()) return;
                    log.debug((Object)("Examining listing for bucket: " + this.result.getBucketName()));
                    return;
                } else if (name.equals("Prefix")) {
                    this.result.setPrefix(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("MaxKeys")) {
                    this.result.setMaxKeys(XmlResponsesSaxParser.parseInt(this.getText()));
                    return;
                } else if (name.equals("NextContinuationToken")) {
                    this.result.setNextContinuationToken(this.getText());
                    return;
                } else if (name.equals("ContinuationToken")) {
                    this.result.setContinuationToken(this.getText());
                    return;
                } else if (name.equals("StartAfter")) {
                    this.result.setStartAfter(XmlResponsesSaxParser.decodeIfSpecified(this.getText(), this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("KeyCount")) {
                    this.result.setKeyCount(XmlResponsesSaxParser.parseInt(this.getText()));
                    return;
                } else if (name.equals("Delimiter")) {
                    this.result.setDelimiter(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("EncodingType")) {
                    this.result.setEncodingType(XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                    return;
                } else if (name.equals("IsTruncated")) {
                    String isTruncatedStr = StringUtils.lowerCase(this.getText());
                    if (isTruncatedStr.startsWith("false")) {
                        this.result.setTruncated(false);
                        return;
                    } else {
                        if (!isTruncatedStr.startsWith("true")) throw new IllegalStateException("Invalid value for IsTruncated field: " + isTruncatedStr);
                        this.result.setTruncated(true);
                    }
                    return;
                } else {
                    if (!name.equals("Contents")) return;
                    this.result.getObjectSummaries().add(this.currentObject);
                    this.currentObject = null;
                }
                return;
            } else if (this.in("ListBucketResult", "Contents")) {
                if (name.equals("Key")) {
                    this.lastKey = this.getText();
                    this.currentObject.setKey(XmlResponsesSaxParser.decodeIfSpecified(this.lastKey, this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("LastModified")) {
                    this.currentObject.setLastModified(ServiceUtils.parseIso8601Date(this.getText()));
                    return;
                } else if (name.equals("ETag")) {
                    this.currentObject.setETag(ServiceUtils.removeQuotes(this.getText()));
                    return;
                } else if (name.equals("Size")) {
                    this.currentObject.setSize(XmlResponsesSaxParser.parseLong(this.getText()));
                    return;
                } else if (name.equals("StorageClass")) {
                    this.currentObject.setStorageClass(this.getText());
                    return;
                } else {
                    if (!name.equals("Owner")) return;
                    this.currentObject.setOwner(this.currentOwner);
                    this.currentOwner = null;
                }
                return;
            } else if (this.in("ListBucketResult", "Contents", "Owner")) {
                if (name.equals("ID")) {
                    this.currentOwner.setId(this.getText());
                    return;
                } else {
                    if (!name.equals("DisplayName")) return;
                    this.currentOwner.setDisplayName(this.getText());
                }
                return;
            } else {
                if (!this.in("ListBucketResult", "CommonPrefixes") || !name.equals("Prefix")) return;
                this.result.getCommonPrefixes().add(XmlResponsesSaxParser.decodeIfSpecified(this.getText(), this.shouldSDKDecodeResponse));
            }
        }
    }

    public static class ListBucketHandler
    extends AbstractHandler {
        private final ObjectListing objectListing = new ObjectListing();
        private final boolean shouldSDKDecodeResponse;
        private S3ObjectSummary currentObject = null;
        private Owner currentOwner = null;
        private String lastKey = null;

        public ListBucketHandler(boolean shouldSDKDecodeResponse) {
            this.shouldSDKDecodeResponse = shouldSDKDecodeResponse;
        }

        public ObjectListing getObjectListing() {
            return this.objectListing;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName, Attributes attrs) {
            if (this.in("ListBucketResult")) {
                if (name.equals("Contents")) {
                    this.currentObject = new S3ObjectSummary();
                    this.currentObject.setBucketName(this.objectListing.getBucketName());
                }
            } else if (this.in("ListBucketResult", "Contents") && name.equals("Owner")) {
                this.currentOwner = new Owner();
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (this.atTopLevel()) {
                if (!name.equals("ListBucketResult") || !this.objectListing.isTruncated() || this.objectListing.getNextMarker() != null) return;
                String nextMarker = null;
                if (!this.objectListing.getObjectSummaries().isEmpty()) {
                    nextMarker = this.objectListing.getObjectSummaries().get(this.objectListing.getObjectSummaries().size() - 1).getKey();
                } else if (!this.objectListing.getCommonPrefixes().isEmpty()) {
                    nextMarker = this.objectListing.getCommonPrefixes().get(this.objectListing.getCommonPrefixes().size() - 1);
                } else {
                    log.error((Object)"S3 response indicates truncated results, but contains no object summaries or common prefixes.");
                }
                this.objectListing.setNextMarker(nextMarker);
                return;
            } else if (this.in("ListBucketResult")) {
                if (name.equals("Name")) {
                    this.objectListing.setBucketName(this.getText());
                    if (!log.isDebugEnabled()) return;
                    log.debug((Object)("Examining listing for bucket: " + this.objectListing.getBucketName()));
                    return;
                } else if (name.equals("Prefix")) {
                    this.objectListing.setPrefix(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("Marker")) {
                    this.objectListing.setMarker(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("NextMarker")) {
                    this.objectListing.setNextMarker(XmlResponsesSaxParser.decodeIfSpecified(this.getText(), this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("MaxKeys")) {
                    this.objectListing.setMaxKeys(XmlResponsesSaxParser.parseInt(this.getText()));
                    return;
                } else if (name.equals("Delimiter")) {
                    this.objectListing.setDelimiter(XmlResponsesSaxParser.decodeIfSpecified(XmlResponsesSaxParser.checkForEmptyString(this.getText()), this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("EncodingType")) {
                    this.objectListing.setEncodingType(this.shouldSDKDecodeResponse ? null : XmlResponsesSaxParser.checkForEmptyString(this.getText()));
                    return;
                } else if (name.equals("IsTruncated")) {
                    String isTruncatedStr = StringUtils.lowerCase(this.getText());
                    if (isTruncatedStr.startsWith("false")) {
                        this.objectListing.setTruncated(false);
                        return;
                    } else {
                        if (!isTruncatedStr.startsWith("true")) throw new IllegalStateException("Invalid value for IsTruncated field: " + isTruncatedStr);
                        this.objectListing.setTruncated(true);
                    }
                    return;
                } else {
                    if (!name.equals("Contents")) return;
                    this.objectListing.getObjectSummaries().add(this.currentObject);
                    this.currentObject = null;
                }
                return;
            } else if (this.in("ListBucketResult", "Contents")) {
                if (name.equals("Key")) {
                    this.lastKey = this.getText();
                    this.currentObject.setKey(XmlResponsesSaxParser.decodeIfSpecified(this.lastKey, this.shouldSDKDecodeResponse));
                    return;
                } else if (name.equals("LastModified")) {
                    this.currentObject.setLastModified(ServiceUtils.parseIso8601Date(this.getText()));
                    return;
                } else if (name.equals("ETag")) {
                    this.currentObject.setETag(ServiceUtils.removeQuotes(this.getText()));
                    return;
                } else if (name.equals("Size")) {
                    this.currentObject.setSize(XmlResponsesSaxParser.parseLong(this.getText()));
                    return;
                } else if (name.equals("StorageClass")) {
                    this.currentObject.setStorageClass(this.getText());
                    return;
                } else {
                    if (!name.equals("Owner")) return;
                    this.currentObject.setOwner(this.currentOwner);
                    this.currentOwner = null;
                }
                return;
            } else if (this.in("ListBucketResult", "Contents", "Owner")) {
                if (name.equals("ID")) {
                    this.currentOwner.setId(this.getText());
                    return;
                } else {
                    if (!name.equals("DisplayName")) return;
                    this.currentOwner.setDisplayName(this.getText());
                }
                return;
            } else {
                if (!this.in("ListBucketResult", "CommonPrefixes") || !name.equals("Prefix")) return;
                this.objectListing.getCommonPrefixes().add(XmlResponsesSaxParser.decodeIfSpecified(this.getText(), this.shouldSDKDecodeResponse));
            }
        }
    }
}

