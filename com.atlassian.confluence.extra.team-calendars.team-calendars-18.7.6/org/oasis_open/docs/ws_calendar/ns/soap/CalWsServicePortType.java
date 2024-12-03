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
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarMultigetType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesType;
import org.oasis_open.docs.ws_calendar.ns.soap.ObjectFactory;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemType;

@WebService(name="CalWsServicePortType", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso(value={ObjectFactory.class, ietf.params.xml.ns.icalendar_2.ObjectFactory.class})
public interface CalWsServicePortType {
    @WebMethod(operationName="GetProperties", action="http://docs.oasis-open.org/ws-calendar/ns/soap/getProperties")
    @WebResult(name="getPropertiesResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public GetPropertiesResponseType getProperties(@WebParam(name="getProperties", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") GetPropertiesType var1);

    @WebMethod(operationName="FreebusyReport", action="http://docs.oasis-open.org/ws-calendar/ns/soap/FreebusyReport")
    @WebResult(name="freebusyReportResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public FreebusyReportResponseType freebusyReport(@WebParam(name="freebusyReport", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") FreebusyReportType var1);

    @WebMethod(operationName="CalendarQuery", action="http://docs.oasis-open.org/ws-calendar/ns/soap/CalendarQuery")
    @WebResult(name="calendarQueryResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public CalendarQueryResponseType calendarQuery(@WebParam(name="calendarQuery", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") CalendarQueryType var1);

    @WebMethod(operationName="CalendarMultiget", action="http://docs.oasis-open.org/ws-calendar/ns/soap/CalendarMultiget")
    @WebResult(name="calendarQueryResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public CalendarQueryResponseType calendarMultiget(@WebParam(name="calendarMultiget", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") CalendarMultigetType var1);

    @WebMethod(operationName="AddItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/addItem")
    @WebResult(name="addItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public AddItemResponseType addItem(@WebParam(name="addItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") AddItemType var1);

    @WebMethod(operationName="FetchItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/fetchItem")
    @WebResult(name="fetchItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public FetchItemResponseType fetchItem(@WebParam(name="fetchItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") FetchItemType var1);

    @WebMethod(operationName="DeleteItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/deleteItem")
    @WebResult(name="deleteItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public DeleteItemResponseType deleteItem(@WebParam(name="deleteItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") DeleteItemType var1);

    @WebMethod(operationName="UpdateItem", action="http://docs.oasis-open.org/ws-calendar/ns/soap/updateItem")
    @WebResult(name="updateItemResponse", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="response")
    public UpdateItemResponseType updateItem(@WebParam(name="updateItem", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", partName="request") UpdateItemType var1);
}

