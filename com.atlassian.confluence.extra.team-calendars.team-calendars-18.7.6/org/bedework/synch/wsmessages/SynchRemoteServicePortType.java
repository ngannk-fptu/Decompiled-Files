/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebMethod
 *  javax.jws.WebParam
 *  javax.jws.WebResult
 *  javax.jws.WebService
 *  javax.jws.soap.SOAPBinding
 *  javax.jws.soap.SOAPBinding$ParameterStyle
 *  javax.xml.bind.annotation.XmlSeeAlso
 */
package org.bedework.synch.wsmessages;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.bedework.synch.wsmessages.GetInfoRequestType;
import org.bedework.synch.wsmessages.GetInfoResponseType;
import org.bedework.synch.wsmessages.KeepAliveNotificationType;
import org.bedework.synch.wsmessages.KeepAliveResponseType;
import org.bedework.synch.wsmessages.ObjectFactory;
import org.bedework.synch.wsmessages.StartServiceNotificationType;
import org.bedework.synch.wsmessages.StartServiceResponseType;
import org.bedework.synch.wsmessages.SubscribeRequestType;
import org.bedework.synch.wsmessages.SubscribeResponseType;
import org.bedework.synch.wsmessages.SubscriptionStatusRequestType;
import org.bedework.synch.wsmessages.SubscriptionStatusResponseType;
import org.bedework.synch.wsmessages.SynchIdTokenType;
import org.bedework.synch.wsmessages.UnsubscribeRequestType;
import org.bedework.synch.wsmessages.UnsubscribeResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarMultigetType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemType;

@WebService(name="SynchRemoteServicePortType", targetNamespace="http://www.bedework.org/synch/wsmessages")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso(value={org.oasis_open.docs.ns.xri.xrd_1.ObjectFactory.class, org.oasis_open.docs.ws_calendar.ns.soap.ObjectFactory.class, ietf.params.xml.ns.icalendar_2.ObjectFactory.class, ObjectFactory.class})
public interface SynchRemoteServicePortType {
    @WebMethod(operationName="StartService", action="http://www.bedework.org/synch/wsmessages/startService")
    @WebResult(name="startServiceResponse", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="response")
    public StartServiceResponseType startService(@WebParam(name="startServiceNotification", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="request") StartServiceNotificationType var1);

    @WebMethod(operationName="PingService", action="http://www.bedework.org/synch/wsmessages/pingService")
    @WebResult(name="keepAliveResponse", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="response")
    public KeepAliveResponseType pingService(@WebParam(name="keepAliveNotification", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="request") KeepAliveNotificationType var1);

    @WebMethod(operationName="GetInfo", action="http://www.bedework.org/synch/wsmessages/getInfo")
    @WebResult(name="getInfoResponse", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="response")
    public GetInfoResponseType getInfo(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="getInfo", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="request") GetInfoRequestType var2);

    @WebMethod(operationName="Subscribe", action="http://www.bedework.org/synch/wsmessages/subscribe")
    @WebResult(name="subscribeResponse", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="response")
    public SubscribeResponseType subscribe(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="subscribe", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="request") SubscribeRequestType var2);

    @WebMethod(operationName="Unsubscribe", action="http://www.bedework.org/synch/wsmessages/unsubscribe")
    @WebResult(name="unsubscribeResponse", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="response")
    public UnsubscribeResponseType unsubscribe(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="unsubscribe", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="request") UnsubscribeRequestType var2);

    @WebMethod(operationName="SubscriptionStatus", action="http://www.bedework.org/synch/wsmessages/subscriptionStatus")
    @WebResult(name="subscriptionStatusResponse", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="response")
    public SubscriptionStatusResponseType subscriptionStatus(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="subscriptionStatus", targetNamespace="http://www.bedework.org/synch/wsmessages", partName="request") SubscriptionStatusRequestType var2);

    @WebMethod(operationName="GetProperties", action="http://docs.oasis-open.org/ws-calendar/ns/soap/getProperties")
    @WebResult(name="getPropertiesResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public GetPropertiesResponseType getProperties(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="getProperties", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") GetPropertiesType var2);

    @WebMethod(operationName="CalendarQuery", action="http://docs.oasis-open.org/ws-calendar/ns/soap/CalendarQuery")
    @WebResult(name="calendarQueryResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public CalendarQueryResponseType calendarQuery(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="calendarQuery", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") CalendarQueryType var2);

    @WebMethod(operationName="CalendarMultiget", action="http://docs.oasis-open.org/ws-calendar/ns/soap/CalendarMultiget")
    @WebResult(name="calendarQueryResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public CalendarQueryResponseType calendarMultiget(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="calendarMultiget", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") CalendarMultigetType var2);

    @WebMethod(operationName="AddItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/additem")
    @WebResult(name="addItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="addItemResponse")
    public AddItemResponseType addItem(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="addItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="addItemRequest") AddItemType var2);

    @WebMethod(operationName="FetchItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/fetchitem")
    @WebResult(name="fetchItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="fetchItemResponse")
    public FetchItemResponseType fetchItem(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="fetchItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="fetchItemRequest") FetchItemType var2);

    @WebMethod(operationName="DeleteItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/deleteItem")
    @WebResult(name="deleteItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public DeleteItemResponseType deleteItem(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="deleteItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") DeleteItemType var2);

    @WebMethod(operationName="UpdateItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/updateitem")
    @WebResult(name="updateItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public UpdateItemResponseType updateItem(@WebParam(name="synchIdToken", targetNamespace="http://www.bedework.org/synch/wsmessages", header=true, partName="idToken") SynchIdTokenType var1, @WebParam(name="updateItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") UpdateItemType var2);
}

