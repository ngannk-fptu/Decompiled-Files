/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.addressing.v200408;

import javax.xml.namespace.QName;

public interface MemberSubmissionAddressingConstants {
    public static final String WSA_NAMESPACE_NAME = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    public static final String WSA_NAMESPACE_WSDL_NAME = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    public static final String WSA_NAMESPACE_POLICY_NAME = "http://schemas.xmlsoap.org/ws/2004/08/addressing/policy";
    public static final QName WSA_ACTION_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "Action");
    public static final String WSA_SERVICENAME_NAME = "ServiceName";
    public static final String WSA_PORTTYPE_NAME = "PortType";
    public static final String WSA_PORTNAME_NAME = "PortName";
    public static final String WSA_ADDRESS_NAME = "Address";
    public static final QName WSA_ADDRESS_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "Address");
    public static final String WSA_EPR_NAME = "EndpointReference";
    public static final QName WSA_EPR_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "EndpointReference");
    public static final String WSA_ANONYMOUS_ADDRESS = "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous";
    public static final String WSA_NONE_ADDRESS = "";
    public static final String WSA_DEFAULT_FAULT_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/addressing/fault";
    public static final QName INVALID_MAP_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "InvalidMessageInformationHeader");
    public static final QName MAP_REQUIRED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "MessageInformationHeaderRequired");
    public static final QName DESTINATION_UNREACHABLE_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "DestinationUnreachable");
    public static final QName ACTION_NOT_SUPPORTED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ActionNotSupported");
    public static final QName ENDPOINT_UNAVAILABLE_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "EndpointUnavailable");
    public static final String ACTION_NOT_SUPPORTED_TEXT = "The \"%s\" cannot be processed at the receiver.";
    public static final String DESTINATION_UNREACHABLE_TEXT = "No route can be determined to reach the destination role defined by the WS-Addressing To.";
    public static final String ENDPOINT_UNAVAILABLE_TEXT = "The endpoint is unable to process the message at this time.";
    public static final String INVALID_MAP_TEXT = "A message information header is not valid and the message cannot be processed.";
    public static final String MAP_REQUIRED_TEXT = "A required message information header, To, MessageID, or Action, is not present.";
    public static final QName PROBLEM_ACTION_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ProblemAction");
    public static final QName PROBLEM_HEADER_QNAME_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ProblemHeaderQName");
    public static final QName FAULT_DETAIL_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "FaultDetail");
    public static final String ANONYMOUS_EPR = "<EndpointReference xmlns=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n    <Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</Address>\n</EndpointReference>";
    public static final QName MEX_METADATA = new QName("http://schemas.xmlsoap.org/ws/2004/09/mex", "Metadata", "mex");
    public static final QName MEX_METADATA_SECTION = new QName("http://schemas.xmlsoap.org/ws/2004/09/mex", "MetadataSection", "mex");
    public static final String MEX_METADATA_DIALECT_ATTRIBUTE = "Dialect";
    public static final String MEX_METADATA_DIALECT_VALUE = "http://schemas.xmlsoap.org/wsdl/";
}

