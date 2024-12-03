/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.bedework.synch.wsmessages;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.bedework.synch.wsmessages.ActiveSubscriptionRequestType;
import org.bedework.synch.wsmessages.AlreadySubscribedType;
import org.bedework.synch.wsmessages.ArrayOfSynchConnectorInfo;
import org.bedework.synch.wsmessages.ArrayOfSynchProperties;
import org.bedework.synch.wsmessages.ArrayOfSynchPropertyInfo;
import org.bedework.synch.wsmessages.BaseSynchRequestType;
import org.bedework.synch.wsmessages.ConnectorInfoType;
import org.bedework.synch.wsmessages.GetInfoRequestType;
import org.bedework.synch.wsmessages.GetInfoResponseType;
import org.bedework.synch.wsmessages.InvalidTokenType;
import org.bedework.synch.wsmessages.KeepAliveNotificationType;
import org.bedework.synch.wsmessages.KeepAliveResponseType;
import org.bedework.synch.wsmessages.ServiceStoppedType;
import org.bedework.synch.wsmessages.StartServiceNotificationType;
import org.bedework.synch.wsmessages.StartServiceResponseType;
import org.bedework.synch.wsmessages.SubscribeRequestType;
import org.bedework.synch.wsmessages.SubscribeResponseType;
import org.bedework.synch.wsmessages.SubscriptionStatusRequestType;
import org.bedework.synch.wsmessages.SubscriptionStatusResponseType;
import org.bedework.synch.wsmessages.SynchConnectorInfoType;
import org.bedework.synch.wsmessages.SynchIdTokenType;
import org.bedework.synch.wsmessages.SynchInfoType;
import org.bedework.synch.wsmessages.SynchPropertyInfoType;
import org.bedework.synch.wsmessages.SynchPropertyType;
import org.bedework.synch.wsmessages.UnknownSubscriptionType;
import org.bedework.synch.wsmessages.UnreachableTargetType;
import org.bedework.synch.wsmessages.UnsubscribeRequestType;
import org.bedework.synch.wsmessages.UnsubscribeResponseType;

@XmlRegistry
public class ObjectFactory {
    private static final QName _UnsubscribeResponse_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "unsubscribeResponse");
    private static final QName _SynchIdToken_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "synchIdToken");
    private static final QName _KeepAliveResponse_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "keepAliveResponse");
    private static final QName _UnreachableTarget_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "unreachableTarget");
    private static final QName _GetInfoResponse_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "getInfoResponse");
    private static final QName _Unsubscribe_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "unsubscribe");
    private static final QName _KeepAliveNotification_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "keepAliveNotification");
    private static final QName _StartServiceResponse_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "startServiceResponse");
    private static final QName _StartServiceNotification_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "startServiceNotification");
    private static final QName _GetInfo_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "getInfo");
    private static final QName _InvalidToken_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "invalidToken");
    private static final QName _UnknownSubscription_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "unknownSubscription");
    private static final QName _SubscriptionStatusResponse_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "subscriptionStatusResponse");
    private static final QName _ServiceStopped_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "serviceStopped");
    private static final QName _SubscribeResponse_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "subscribeResponse");
    private static final QName _Subscribe_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "subscribe");
    private static final QName _SubscriptionStatus_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "subscriptionStatus");
    private static final QName _AlreadySubscribed_QNAME = new QName("http://www.bedework.org/synch/wsmessages", "alreadySubscribed");

    public ServiceStoppedType createServiceStoppedType() {
        return new ServiceStoppedType();
    }

    public ConnectorInfoType createConnectorInfoType() {
        return new ConnectorInfoType();
    }

    public AlreadySubscribedType createAlreadySubscribedType() {
        return new AlreadySubscribedType();
    }

    public ArrayOfSynchPropertyInfo createArrayOfSynchPropertyInfo() {
        return new ArrayOfSynchPropertyInfo();
    }

    public SynchPropertyType createSynchPropertyType() {
        return new SynchPropertyType();
    }

    public StartServiceResponseType createStartServiceResponseType() {
        return new StartServiceResponseType();
    }

    public GetInfoResponseType createGetInfoResponseType() {
        return new GetInfoResponseType();
    }

    public SubscriptionStatusResponseType createSubscriptionStatusResponseType() {
        return new SubscriptionStatusResponseType();
    }

    public GetInfoRequestType createGetInfoRequestType() {
        return new GetInfoRequestType();
    }

    public ArrayOfSynchProperties createArrayOfSynchProperties() {
        return new ArrayOfSynchProperties();
    }

    public InvalidTokenType createInvalidTokenType() {
        return new InvalidTokenType();
    }

    public SynchPropertyInfoType createSynchPropertyInfoType() {
        return new SynchPropertyInfoType();
    }

    public UnsubscribeResponseType createUnsubscribeResponseType() {
        return new UnsubscribeResponseType();
    }

    public UnreachableTargetType createUnreachableTargetType() {
        return new UnreachableTargetType();
    }

    public UnknownSubscriptionType createUnknownSubscriptionType() {
        return new UnknownSubscriptionType();
    }

    public UnsubscribeRequestType createUnsubscribeRequestType() {
        return new UnsubscribeRequestType();
    }

    public KeepAliveResponseType createKeepAliveResponseType() {
        return new KeepAliveResponseType();
    }

    public SynchIdTokenType createSynchIdTokenType() {
        return new SynchIdTokenType();
    }

    public SynchInfoType createSynchInfoType() {
        return new SynchInfoType();
    }

    public KeepAliveNotificationType createKeepAliveNotificationType() {
        return new KeepAliveNotificationType();
    }

    public ActiveSubscriptionRequestType createActiveSubscriptionRequestType() {
        return new ActiveSubscriptionRequestType();
    }

    public StartServiceNotificationType createStartServiceNotificationType() {
        return new StartServiceNotificationType();
    }

    public ArrayOfSynchConnectorInfo createArrayOfSynchConnectorInfo() {
        return new ArrayOfSynchConnectorInfo();
    }

    public SubscribeResponseType createSubscribeResponseType() {
        return new SubscribeResponseType();
    }

    public SubscriptionStatusRequestType createSubscriptionStatusRequestType() {
        return new SubscriptionStatusRequestType();
    }

    public BaseSynchRequestType createBaseSynchRequestType() {
        return new BaseSynchRequestType();
    }

    public SynchConnectorInfoType createSynchConnectorInfoType() {
        return new SynchConnectorInfoType();
    }

    public SubscribeRequestType createSubscribeRequestType() {
        return new SubscribeRequestType();
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="unsubscribeResponse")
    public JAXBElement<UnsubscribeResponseType> createUnsubscribeResponse(UnsubscribeResponseType value) {
        return new JAXBElement(_UnsubscribeResponse_QNAME, UnsubscribeResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="synchIdToken")
    public JAXBElement<SynchIdTokenType> createSynchIdToken(SynchIdTokenType value) {
        return new JAXBElement(_SynchIdToken_QNAME, SynchIdTokenType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="keepAliveResponse")
    public JAXBElement<KeepAliveResponseType> createKeepAliveResponse(KeepAliveResponseType value) {
        return new JAXBElement(_KeepAliveResponse_QNAME, KeepAliveResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="unreachableTarget", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<UnreachableTargetType> createUnreachableTarget(UnreachableTargetType value) {
        return new JAXBElement(_UnreachableTarget_QNAME, UnreachableTargetType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="getInfoResponse")
    public JAXBElement<GetInfoResponseType> createGetInfoResponse(GetInfoResponseType value) {
        return new JAXBElement(_GetInfoResponse_QNAME, GetInfoResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="unsubscribe")
    public JAXBElement<UnsubscribeRequestType> createUnsubscribe(UnsubscribeRequestType value) {
        return new JAXBElement(_Unsubscribe_QNAME, UnsubscribeRequestType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="keepAliveNotification")
    public JAXBElement<KeepAliveNotificationType> createKeepAliveNotification(KeepAliveNotificationType value) {
        return new JAXBElement(_KeepAliveNotification_QNAME, KeepAliveNotificationType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="startServiceResponse")
    public JAXBElement<StartServiceResponseType> createStartServiceResponse(StartServiceResponseType value) {
        return new JAXBElement(_StartServiceResponse_QNAME, StartServiceResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="startServiceNotification")
    public JAXBElement<StartServiceNotificationType> createStartServiceNotification(StartServiceNotificationType value) {
        return new JAXBElement(_StartServiceNotification_QNAME, StartServiceNotificationType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="getInfo")
    public JAXBElement<GetInfoRequestType> createGetInfo(GetInfoRequestType value) {
        return new JAXBElement(_GetInfo_QNAME, GetInfoRequestType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="invalidToken", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<InvalidTokenType> createInvalidToken(InvalidTokenType value) {
        return new JAXBElement(_InvalidToken_QNAME, InvalidTokenType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="unknownSubscription", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<UnknownSubscriptionType> createUnknownSubscription(UnknownSubscriptionType value) {
        return new JAXBElement(_UnknownSubscription_QNAME, UnknownSubscriptionType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="subscriptionStatusResponse")
    public JAXBElement<SubscriptionStatusResponseType> createSubscriptionStatusResponse(SubscriptionStatusResponseType value) {
        return new JAXBElement(_SubscriptionStatusResponse_QNAME, SubscriptionStatusResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="serviceStopped", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<ServiceStoppedType> createServiceStopped(ServiceStoppedType value) {
        return new JAXBElement(_ServiceStopped_QNAME, ServiceStoppedType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="subscribeResponse")
    public JAXBElement<SubscribeResponseType> createSubscribeResponse(SubscribeResponseType value) {
        return new JAXBElement(_SubscribeResponse_QNAME, SubscribeResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="subscribe")
    public JAXBElement<SubscribeRequestType> createSubscribe(SubscribeRequestType value) {
        return new JAXBElement(_Subscribe_QNAME, SubscribeRequestType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="subscriptionStatus")
    public JAXBElement<SubscriptionStatusRequestType> createSubscriptionStatus(SubscriptionStatusRequestType value) {
        return new JAXBElement(_SubscriptionStatus_QNAME, SubscriptionStatusRequestType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.bedework.org/synch/wsmessages", name="alreadySubscribed", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<AlreadySubscribedType> createAlreadySubscribed(AlreadySubscribedType value) {
        return new JAXBElement(_AlreadySubscribed_QNAME, AlreadySubscribedType.class, null, (Object)value);
    }
}

