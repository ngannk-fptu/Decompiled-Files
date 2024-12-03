/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.AfterMaxDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.AllcompType;
import org.oasis_open.docs.ws_calendar.ns.soap.AllpropType;
import org.oasis_open.docs.ws_calendar.ns.soap.AnyCompType;
import org.oasis_open.docs.ws_calendar.ns.soap.ArrayOfHrefs;
import org.oasis_open.docs.ws_calendar.ns.soap.ArrayOfOperations;
import org.oasis_open.docs.ws_calendar.ns.soap.ArrayOfResponses;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.BeforeMinDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarAccessFeatureType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarCollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarDataResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarMultigetType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryOrMultigetBaseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryType;
import org.oasis_open.docs.ws_calendar.ns.soap.ChildCollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.CollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.CompFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentsSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.CreationDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.DateTimePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.DisplayNameType;
import org.oasis_open.docs.ws_calendar.ns.soap.ErrorCodeType;
import org.oasis_open.docs.ws_calendar.ns.soap.ErrorResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.ExceedsMaxResourceSizeType;
import org.oasis_open.docs.ws_calendar.ns.soap.ExpandType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.ForbiddenType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesType;
import org.oasis_open.docs.ws_calendar.ns.soap.InboxType;
import org.oasis_open.docs.ws_calendar.ns.soap.IntegerPropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarCollectionLocationType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarDataType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarObjectResourceType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.IsNotDefinedType;
import org.oasis_open.docs.ws_calendar.ns.soap.LastModifiedDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.LimitFreebusySetType;
import org.oasis_open.docs.ws_calendar.ns.soap.LimitRecurrenceSetType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxAttendeesPerInstanceType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxInstancesType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxResourceSizeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MinDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MismatchedChangeTokenType;
import org.oasis_open.docs.ws_calendar.ns.soap.MissingChangeTokenType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultiOpResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultiOpType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatResponseElementType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatusPropElementType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatusType;
import org.oasis_open.docs.ws_calendar.ns.soap.NotCalendarDataType;
import org.oasis_open.docs.ws_calendar.ns.soap.OutboxType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParamFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParametersSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PartialSuccessType;
import org.oasis_open.docs.ws_calendar.ns.soap.PrincipalHomeType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertiesSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertyReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertySelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropstatType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceDescriptionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceOwnerType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceTimezoneIdType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceTypeType;
import org.oasis_open.docs.ws_calendar.ns.soap.StringPropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.SupportedCalendarComponentSetType;
import org.oasis_open.docs.ws_calendar.ns.soap.SupportedFeatureType;
import org.oasis_open.docs.ws_calendar.ns.soap.SupportedFeaturesType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetDoesNotExistType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetExistsType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetNotEntityType;
import org.oasis_open.docs.ws_calendar.ns.soap.TextMatchType;
import org.oasis_open.docs.ws_calendar.ns.soap.TimezoneIdType;
import org.oasis_open.docs.ws_calendar.ns.soap.TimezoneServerType;
import org.oasis_open.docs.ws_calendar.ns.soap.TimezoneType;
import org.oasis_open.docs.ws_calendar.ns.soap.TooManyAttendeesPerInstanceType;
import org.oasis_open.docs.ws_calendar.ns.soap.TooManyInstancesType;
import org.oasis_open.docs.ws_calendar.ns.soap.UTCTimeRangeType;
import org.oasis_open.docs.ws_calendar.ns.soap.UidConflictType;
import org.oasis_open.docs.ws_calendar.ns.soap.UnsupportedCalendarComponentType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.XresourceType;

@XmlRegistry
public class ObjectFactory {
    private static final QName _PrincipalHome_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "principalHome");
    private static final QName _PropFilter_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "propFilter");
    private static final QName _ChildCollection_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "childCollection");
    private static final QName _MultiOpResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "multiOpResponse");
    private static final QName _Error_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "error");
    private static final QName _AfterMaxDateTime_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "afterMaxDateTime");
    private static final QName _Timezone_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "timezone");
    private static final QName _SupportedFeatures_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "supportedFeatures");
    private static final QName _DeleteItemResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "deleteItemResponse");
    private static final QName _NotCalendarData_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "notCalendarData");
    private static final QName _CalendarCollection_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "calendarCollection");
    private static final QName _FreebusyReport_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "freebusyReport");
    private static final QName _MaxInstances_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "maxInstances");
    private static final QName _Allcomp_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "allcomp");
    private static final QName _BeforeMinDateTime_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "beforeMinDateTime");
    private static final QName _MinDateTime_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "minDateTime");
    private static final QName _CalendarAccessFeature_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "calendarAccessFeature");
    private static final QName _TextMatch_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "textMatch");
    private static final QName _Expand_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "expand");
    private static final QName _Outbox_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "outbox");
    private static final QName _InvalidCalendarObjectResource_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "invalidCalendarObjectResource");
    private static final QName _AddItemResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "addItemResponse");
    private static final QName _UnsupportedCalendarComponent_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "unsupportedCalendarComponent");
    private static final QName _TargetExists_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "targetExists");
    private static final QName _FreebusyReportResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "freebusyReportResponse");
    private static final QName _LimitRecurrenceSet_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "limit-recurrence-set");
    private static final QName _CalendarMultiget_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "calendarMultiget");
    private static final QName _Inbox_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "inbox");
    private static final QName _ResourceType_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "resourceType");
    private static final QName _TimezoneId_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "timezone-id");
    private static final QName _Filter_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "filter");
    private static final QName _MaxResourceSize_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "maxResourceSize");
    private static final QName _Depth_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "depth");
    private static final QName _ResourceDescription_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "resourceDescription");
    private static final QName _TimeRange_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "timeRange");
    private static final QName _TooManyAttendeesPerInstance_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "tooManyAttendeesPerInstance");
    private static final QName _GetPropertiesResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "getPropertiesResponse");
    private static final QName _InvalidCalendarData_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "invalidCalendarData");
    private static final QName _Xresource_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "xresource");
    private static final QName _MultiOp_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "multiOp");
    private static final QName _Allprop_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "allprop");
    private static final QName _MismatchedChangeToken_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "mismatchedChangeToken");
    private static final QName _UpdateItem_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "updateItem");
    private static final QName _PartialSuccess_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "partialSuccess");
    private static final QName _FetchItem_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "fetchItem");
    private static final QName _FetchItemResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "fetchItemResponse");
    private static final QName _AddItem_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "addItem");
    private static final QName _CalendarQueryResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "calendarQueryResponse");
    private static final QName _ResourceOwner_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "resourceOwner");
    private static final QName _UidConflict_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "uidConflict");
    private static final QName _MaxDateTime_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "maxDateTime");
    private static final QName _Propstat_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "propstat");
    private static final QName _TargetNotEntity_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "targetNotEntity");
    private static final QName _UpdateItemResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "updateItemResponse");
    private static final QName _LastModifiedDateTime_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "lastModifiedDateTime");
    private static final QName _MaxAttendeesPerInstance_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "maxAttendeesPerInstance");
    private static final QName _SupportedCalendarComponentSet_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "supportedCalendarComponentSet");
    private static final QName _Collection_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "collection");
    private static final QName _TargetDoesNotExist_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "targetDoesNotExist");
    private static final QName _InvalidFilter_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "invalidFilter");
    private static final QName _AnyComp_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "anyComp");
    private static final QName _TooManyInstances_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "tooManyInstances");
    private static final QName _TimezoneServer_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "timezoneServer");
    private static final QName _DisplayName_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "displayName");
    private static final QName _ParamFilter_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "paramFilter");
    private static final QName _ExceedsMaxResourceSize_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "exceedsMaxResourceSize");
    private static final QName _CompFilter_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "compFilter");
    private static final QName _DeleteItem_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "deleteItem");
    private static final QName _Forbidden_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "forbidden");
    private static final QName _CreationDateTime_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "creationDateTime");
    private static final QName _CalendarQuery_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "calendarQuery");
    private static final QName _LimitFreebusySet_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "limit-freebusy-set");
    private static final QName _BaseResponse_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "baseResponse");
    private static final QName _InvalidCalendarCollectionLocation_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "invalidCalendarCollectionLocation");
    private static final QName _ResourceTimezoneId_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "resourceTimezoneId");
    private static final QName _IsNotDefined_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "is-not-defined");
    private static final QName _MissingChangeToken_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "missingChangeToken");
    private static final QName _GetProperties_QNAME = new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "getProperties");

    public LimitRecurrenceSetType createLimitRecurrenceSetType() {
        return new LimitRecurrenceSetType();
    }

    public ParameterReferenceType createParameterReferenceType() {
        return new ParameterReferenceType();
    }

    public CalendarQueryOrMultigetBaseType createCalendarQueryOrMultigetBaseType() {
        return new CalendarQueryOrMultigetBaseType();
    }

    public UTCTimeRangeType createUTCTimeRangeType() {
        return new UTCTimeRangeType();
    }

    public FreebusyReportResponseType createFreebusyReportResponseType() {
        return new FreebusyReportResponseType();
    }

    public CalendarDataResponseType createCalendarDataResponseType() {
        return new CalendarDataResponseType();
    }

    public UpdateItemResponseType createUpdateItemResponseType() {
        return new UpdateItemResponseType();
    }

    public BaseRequestType createBaseRequestType() {
        return new BaseRequestType();
    }

    public CalendarQueryResponseType createCalendarQueryResponseType() {
        return new CalendarQueryResponseType();
    }

    public ResourceDescriptionType createResourceDescriptionType() {
        return new ResourceDescriptionType();
    }

    public TooManyInstancesType createTooManyInstancesType() {
        return new TooManyInstancesType();
    }

    public AnyCompType createAnyCompType() {
        return new AnyCompType();
    }

    public AfterMaxDateTimeType createAfterMaxDateTimeType() {
        return new AfterMaxDateTimeType();
    }

    public SupportedFeatureType createSupportedFeatureType() {
        return new SupportedFeatureType();
    }

    public ArrayOfOperations createArrayOfOperations() {
        return new ArrayOfOperations();
    }

    public FetchItemType createFetchItemType() {
        return new FetchItemType();
    }

    public CalendarQueryType createCalendarQueryType() {
        return new CalendarQueryType();
    }

    public FetchItemResponseType createFetchItemResponseType() {
        return new FetchItemResponseType();
    }

    public PropFilterType createPropFilterType() {
        return new PropFilterType();
    }

    public TimezoneType createTimezoneType() {
        return new TimezoneType();
    }

    public MultistatResponseElementType createMultistatResponseElementType() {
        return new MultistatResponseElementType();
    }

    public MultistatusPropElementType createMultistatusPropElementType() {
        return new MultistatusPropElementType();
    }

    public LimitFreebusySetType createLimitFreebusySetType() {
        return new LimitFreebusySetType();
    }

    public InboxType createInboxType() {
        return new InboxType();
    }

    public DeleteItemType createDeleteItemType() {
        return new DeleteItemType();
    }

    public MaxInstancesType createMaxInstancesType() {
        return new MaxInstancesType();
    }

    public MaxResourceSizeType createMaxResourceSizeType() {
        return new MaxResourceSizeType();
    }

    public CalendarCollectionType createCalendarCollectionType() {
        return new CalendarCollectionType();
    }

    public InvalidCalendarDataType createInvalidCalendarDataType() {
        return new InvalidCalendarDataType();
    }

    public ResourceTimezoneIdType createResourceTimezoneIdType() {
        return new ResourceTimezoneIdType();
    }

    public ComponentSelectionType createComponentSelectionType() {
        return new ComponentSelectionType();
    }

    public MaxAttendeesPerInstanceType createMaxAttendeesPerInstanceType() {
        return new MaxAttendeesPerInstanceType();
    }

    public ArrayOfResponses createArrayOfResponses() {
        return new ArrayOfResponses();
    }

    public CalendarAccessFeatureType createCalendarAccessFeatureType() {
        return new CalendarAccessFeatureType();
    }

    public ExpandType createExpandType() {
        return new ExpandType();
    }

    public TargetNotEntityType createTargetNotEntityType() {
        return new TargetNotEntityType();
    }

    public MultistatusType createMultistatusType() {
        return new MultistatusType();
    }

    public IntegerPropertyType createIntegerPropertyType() {
        return new IntegerPropertyType();
    }

    public FreebusyReportType createFreebusyReportType() {
        return new FreebusyReportType();
    }

    public FilterType createFilterType() {
        return new FilterType();
    }

    public ExceedsMaxResourceSizeType createExceedsMaxResourceSizeType() {
        return new ExceedsMaxResourceSizeType();
    }

    public UnsupportedCalendarComponentType createUnsupportedCalendarComponentType() {
        return new UnsupportedCalendarComponentType();
    }

    public ResourceOwnerType createResourceOwnerType() {
        return new ResourceOwnerType();
    }

    public ComponentsSelectionType createComponentsSelectionType() {
        return new ComponentsSelectionType();
    }

    public TargetExistsType createTargetExistsType() {
        return new TargetExistsType();
    }

    public ForbiddenType createForbiddenType() {
        return new ForbiddenType();
    }

    public MultiOpResponseType createMultiOpResponseType() {
        return new MultiOpResponseType();
    }

    public SupportedFeaturesType createSupportedFeaturesType() {
        return new SupportedFeaturesType();
    }

    public TooManyAttendeesPerInstanceType createTooManyAttendeesPerInstanceType() {
        return new TooManyAttendeesPerInstanceType();
    }

    public PropertiesSelectionType createPropertiesSelectionType() {
        return new PropertiesSelectionType();
    }

    public ErrorResponseType createErrorResponseType() {
        return new ErrorResponseType();
    }

    public TimezoneIdType createTimezoneIdType() {
        return new TimezoneIdType();
    }

    public PartialSuccessType createPartialSuccessType() {
        return new PartialSuccessType();
    }

    public ParametersSelectionType createParametersSelectionType() {
        return new ParametersSelectionType();
    }

    public OutboxType createOutboxType() {
        return new OutboxType();
    }

    public CollectionType createCollectionType() {
        return new CollectionType();
    }

    public AddItemType createAddItemType() {
        return new AddItemType();
    }

    public ChildCollectionType createChildCollectionType() {
        return new ChildCollectionType();
    }

    public InvalidCalendarObjectResourceType createInvalidCalendarObjectResourceType() {
        return new InvalidCalendarObjectResourceType();
    }

    public DateTimePropertyType createDateTimePropertyType() {
        return new DateTimePropertyType();
    }

    public InvalidCalendarCollectionLocationType createInvalidCalendarCollectionLocationType() {
        return new InvalidCalendarCollectionLocationType();
    }

    public TargetDoesNotExistType createTargetDoesNotExistType() {
        return new TargetDoesNotExistType();
    }

    public PropertySelectionType createPropertySelectionType() {
        return new PropertySelectionType();
    }

    public NotCalendarDataType createNotCalendarDataType() {
        return new NotCalendarDataType();
    }

    public LastModifiedDateTimeType createLastModifiedDateTimeType() {
        return new LastModifiedDateTimeType();
    }

    public ArrayOfHrefs createArrayOfHrefs() {
        return new ArrayOfHrefs();
    }

    public AllpropType createAllpropType() {
        return new AllpropType();
    }

    public ParamFilterType createParamFilterType() {
        return new ParamFilterType();
    }

    public TimezoneServerType createTimezoneServerType() {
        return new TimezoneServerType();
    }

    public AddItemResponseType createAddItemResponseType() {
        return new AddItemResponseType();
    }

    public CompFilterType createCompFilterType() {
        return new CompFilterType();
    }

    public CreationDateTimeType createCreationDateTimeType() {
        return new CreationDateTimeType();
    }

    public ResourceTypeType createResourceTypeType() {
        return new ResourceTypeType();
    }

    public DeleteItemResponseType createDeleteItemResponseType() {
        return new DeleteItemResponseType();
    }

    public DisplayNameType createDisplayNameType() {
        return new DisplayNameType();
    }

    public UidConflictType createUidConflictType() {
        return new UidConflictType();
    }

    public XresourceType createXresourceType() {
        return new XresourceType();
    }

    public MinDateTimeType createMinDateTimeType() {
        return new MinDateTimeType();
    }

    public MismatchedChangeTokenType createMismatchedChangeTokenType() {
        return new MismatchedChangeTokenType();
    }

    public StringPropertyType createStringPropertyType() {
        return new StringPropertyType();
    }

    public ComponentReferenceType createComponentReferenceType() {
        return new ComponentReferenceType();
    }

    public IsNotDefinedType createIsNotDefinedType() {
        return new IsNotDefinedType();
    }

    public PropstatType createPropstatType() {
        return new PropstatType();
    }

    public ParameterSelectionType createParameterSelectionType() {
        return new ParameterSelectionType();
    }

    public InvalidFilterType createInvalidFilterType() {
        return new InvalidFilterType();
    }

    public MaxDateTimeType createMaxDateTimeType() {
        return new MaxDateTimeType();
    }

    public GetPropertiesBasePropertyType createGetPropertiesBasePropertyType() {
        return new GetPropertiesBasePropertyType();
    }

    public MissingChangeTokenType createMissingChangeTokenType() {
        return new MissingChangeTokenType();
    }

    public GetPropertiesResponseType createGetPropertiesResponseType() {
        return new GetPropertiesResponseType();
    }

    public BeforeMinDateTimeType createBeforeMinDateTimeType() {
        return new BeforeMinDateTimeType();
    }

    public AllcompType createAllcompType() {
        return new AllcompType();
    }

    public UpdateItemType createUpdateItemType() {
        return new UpdateItemType();
    }

    public ErrorCodeType createErrorCodeType() {
        return new ErrorCodeType();
    }

    public TextMatchType createTextMatchType() {
        return new TextMatchType();
    }

    public MultiOpType createMultiOpType() {
        return new MultiOpType();
    }

    public GetPropertiesType createGetPropertiesType() {
        return new GetPropertiesType();
    }

    public CalendarMultigetType createCalendarMultigetType() {
        return new CalendarMultigetType();
    }

    public SupportedCalendarComponentSetType createSupportedCalendarComponentSetType() {
        return new SupportedCalendarComponentSetType();
    }

    public PropertyReferenceType createPropertyReferenceType() {
        return new PropertyReferenceType();
    }

    public PrincipalHomeType createPrincipalHomeType() {
        return new PrincipalHomeType();
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="principalHome")
    public JAXBElement<PrincipalHomeType> createPrincipalHome(PrincipalHomeType value) {
        return new JAXBElement(_PrincipalHome_QNAME, PrincipalHomeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="propFilter")
    public JAXBElement<PropFilterType> createPropFilter(PropFilterType value) {
        return new JAXBElement(_PropFilter_QNAME, PropFilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="childCollection")
    public JAXBElement<ChildCollectionType> createChildCollection(ChildCollectionType value) {
        return new JAXBElement(_ChildCollection_QNAME, ChildCollectionType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="multiOpResponse")
    public JAXBElement<MultiOpResponseType> createMultiOpResponse(MultiOpResponseType value) {
        return new JAXBElement(_MultiOpResponse_QNAME, MultiOpResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="error")
    public JAXBElement<ErrorCodeType> createError(ErrorCodeType value) {
        return new JAXBElement(_Error_QNAME, ErrorCodeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="afterMaxDateTime", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<AfterMaxDateTimeType> createAfterMaxDateTime(AfterMaxDateTimeType value) {
        return new JAXBElement(_AfterMaxDateTime_QNAME, AfterMaxDateTimeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="timezone")
    public JAXBElement<TimezoneType> createTimezone(TimezoneType value) {
        return new JAXBElement(_Timezone_QNAME, TimezoneType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="supportedFeatures")
    public JAXBElement<SupportedFeaturesType> createSupportedFeatures(SupportedFeaturesType value) {
        return new JAXBElement(_SupportedFeatures_QNAME, SupportedFeaturesType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="deleteItemResponse", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="baseResponse")
    public JAXBElement<DeleteItemResponseType> createDeleteItemResponse(DeleteItemResponseType value) {
        return new JAXBElement(_DeleteItemResponse_QNAME, DeleteItemResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="notCalendarData", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<NotCalendarDataType> createNotCalendarData(NotCalendarDataType value) {
        return new JAXBElement(_NotCalendarData_QNAME, NotCalendarDataType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="calendarCollection")
    public JAXBElement<CalendarCollectionType> createCalendarCollection(CalendarCollectionType value) {
        return new JAXBElement(_CalendarCollection_QNAME, CalendarCollectionType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="freebusyReport")
    public JAXBElement<FreebusyReportType> createFreebusyReport(FreebusyReportType value) {
        return new JAXBElement(_FreebusyReport_QNAME, FreebusyReportType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="maxInstances")
    public JAXBElement<MaxInstancesType> createMaxInstances(MaxInstancesType value) {
        return new JAXBElement(_MaxInstances_QNAME, MaxInstancesType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="allcomp")
    public JAXBElement<AllcompType> createAllcomp(AllcompType value) {
        return new JAXBElement(_Allcomp_QNAME, AllcompType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="beforeMinDateTime", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<BeforeMinDateTimeType> createBeforeMinDateTime(BeforeMinDateTimeType value) {
        return new JAXBElement(_BeforeMinDateTime_QNAME, BeforeMinDateTimeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="minDateTime")
    public JAXBElement<MinDateTimeType> createMinDateTime(MinDateTimeType value) {
        return new JAXBElement(_MinDateTime_QNAME, MinDateTimeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="calendarAccessFeature")
    public JAXBElement<CalendarAccessFeatureType> createCalendarAccessFeature(CalendarAccessFeatureType value) {
        return new JAXBElement(_CalendarAccessFeature_QNAME, CalendarAccessFeatureType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="textMatch")
    public JAXBElement<TextMatchType> createTextMatch(TextMatchType value) {
        return new JAXBElement(_TextMatch_QNAME, TextMatchType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="expand")
    public JAXBElement<ExpandType> createExpand(ExpandType value) {
        return new JAXBElement(_Expand_QNAME, ExpandType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="outbox")
    public JAXBElement<OutboxType> createOutbox(OutboxType value) {
        return new JAXBElement(_Outbox_QNAME, OutboxType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="invalidCalendarObjectResource", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<InvalidCalendarObjectResourceType> createInvalidCalendarObjectResource(InvalidCalendarObjectResourceType value) {
        return new JAXBElement(_InvalidCalendarObjectResource_QNAME, InvalidCalendarObjectResourceType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="addItemResponse", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="baseResponse")
    public JAXBElement<AddItemResponseType> createAddItemResponse(AddItemResponseType value) {
        return new JAXBElement(_AddItemResponse_QNAME, AddItemResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="unsupportedCalendarComponent", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<UnsupportedCalendarComponentType> createUnsupportedCalendarComponent(UnsupportedCalendarComponentType value) {
        return new JAXBElement(_UnsupportedCalendarComponent_QNAME, UnsupportedCalendarComponentType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="targetExists", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<TargetExistsType> createTargetExists(TargetExistsType value) {
        return new JAXBElement(_TargetExists_QNAME, TargetExistsType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="freebusyReportResponse", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="baseResponse")
    public JAXBElement<FreebusyReportResponseType> createFreebusyReportResponse(FreebusyReportResponseType value) {
        return new JAXBElement(_FreebusyReportResponse_QNAME, FreebusyReportResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="limit-recurrence-set")
    public JAXBElement<LimitRecurrenceSetType> createLimitRecurrenceSet(LimitRecurrenceSetType value) {
        return new JAXBElement(_LimitRecurrenceSet_QNAME, LimitRecurrenceSetType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="calendarMultiget")
    public JAXBElement<CalendarMultigetType> createCalendarMultiget(CalendarMultigetType value) {
        return new JAXBElement(_CalendarMultiget_QNAME, CalendarMultigetType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="inbox")
    public JAXBElement<InboxType> createInbox(InboxType value) {
        return new JAXBElement(_Inbox_QNAME, InboxType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="resourceType")
    public JAXBElement<ResourceTypeType> createResourceType(ResourceTypeType value) {
        return new JAXBElement(_ResourceType_QNAME, ResourceTypeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="timezone-id")
    public JAXBElement<TimezoneIdType> createTimezoneId(TimezoneIdType value) {
        return new JAXBElement(_TimezoneId_QNAME, TimezoneIdType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="filter")
    public JAXBElement<FilterType> createFilter(FilterType value) {
        return new JAXBElement(_Filter_QNAME, FilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="maxResourceSize")
    public JAXBElement<MaxResourceSizeType> createMaxResourceSize(MaxResourceSizeType value) {
        return new JAXBElement(_MaxResourceSize_QNAME, MaxResourceSizeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="depth")
    public JAXBElement<String> createDepth(String value) {
        return new JAXBElement(_Depth_QNAME, String.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="resourceDescription")
    public JAXBElement<ResourceDescriptionType> createResourceDescription(ResourceDescriptionType value) {
        return new JAXBElement(_ResourceDescription_QNAME, ResourceDescriptionType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="timeRange")
    public JAXBElement<UTCTimeRangeType> createTimeRange(UTCTimeRangeType value) {
        return new JAXBElement(_TimeRange_QNAME, UTCTimeRangeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="tooManyAttendeesPerInstance", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<TooManyAttendeesPerInstanceType> createTooManyAttendeesPerInstance(TooManyAttendeesPerInstanceType value) {
        return new JAXBElement(_TooManyAttendeesPerInstance_QNAME, TooManyAttendeesPerInstanceType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="getPropertiesResponse")
    public JAXBElement<GetPropertiesResponseType> createGetPropertiesResponse(GetPropertiesResponseType value) {
        return new JAXBElement(_GetPropertiesResponse_QNAME, GetPropertiesResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="invalidCalendarData", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<InvalidCalendarDataType> createInvalidCalendarData(InvalidCalendarDataType value) {
        return new JAXBElement(_InvalidCalendarData_QNAME, InvalidCalendarDataType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="xresource")
    public JAXBElement<XresourceType> createXresource(XresourceType value) {
        return new JAXBElement(_Xresource_QNAME, XresourceType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="multiOp")
    public JAXBElement<MultiOpType> createMultiOp(MultiOpType value) {
        return new JAXBElement(_MultiOp_QNAME, MultiOpType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="allprop")
    public JAXBElement<AllpropType> createAllprop(AllpropType value) {
        return new JAXBElement(_Allprop_QNAME, AllpropType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="mismatchedChangeToken", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<MismatchedChangeTokenType> createMismatchedChangeToken(MismatchedChangeTokenType value) {
        return new JAXBElement(_MismatchedChangeToken_QNAME, MismatchedChangeTokenType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="updateItem")
    public JAXBElement<UpdateItemType> createUpdateItem(UpdateItemType value) {
        return new JAXBElement(_UpdateItem_QNAME, UpdateItemType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="partialSuccess", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<PartialSuccessType> createPartialSuccess(PartialSuccessType value) {
        return new JAXBElement(_PartialSuccess_QNAME, PartialSuccessType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="fetchItem")
    public JAXBElement<FetchItemType> createFetchItem(FetchItemType value) {
        return new JAXBElement(_FetchItem_QNAME, FetchItemType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="fetchItemResponse", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="baseResponse")
    public JAXBElement<FetchItemResponseType> createFetchItemResponse(FetchItemResponseType value) {
        return new JAXBElement(_FetchItemResponse_QNAME, FetchItemResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="addItem")
    public JAXBElement<AddItemType> createAddItem(AddItemType value) {
        return new JAXBElement(_AddItem_QNAME, AddItemType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="calendarQueryResponse")
    public JAXBElement<CalendarQueryResponseType> createCalendarQueryResponse(CalendarQueryResponseType value) {
        return new JAXBElement(_CalendarQueryResponse_QNAME, CalendarQueryResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="resourceOwner")
    public JAXBElement<ResourceOwnerType> createResourceOwner(ResourceOwnerType value) {
        return new JAXBElement(_ResourceOwner_QNAME, ResourceOwnerType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="uidConflict", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<UidConflictType> createUidConflict(UidConflictType value) {
        return new JAXBElement(_UidConflict_QNAME, UidConflictType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="maxDateTime")
    public JAXBElement<MaxDateTimeType> createMaxDateTime(MaxDateTimeType value) {
        return new JAXBElement(_MaxDateTime_QNAME, MaxDateTimeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="propstat")
    public JAXBElement<PropstatType> createPropstat(PropstatType value) {
        return new JAXBElement(_Propstat_QNAME, PropstatType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="targetNotEntity", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<TargetNotEntityType> createTargetNotEntity(TargetNotEntityType value) {
        return new JAXBElement(_TargetNotEntity_QNAME, TargetNotEntityType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="updateItemResponse", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="baseResponse")
    public JAXBElement<UpdateItemResponseType> createUpdateItemResponse(UpdateItemResponseType value) {
        return new JAXBElement(_UpdateItemResponse_QNAME, UpdateItemResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="lastModifiedDateTime")
    public JAXBElement<LastModifiedDateTimeType> createLastModifiedDateTime(LastModifiedDateTimeType value) {
        return new JAXBElement(_LastModifiedDateTime_QNAME, LastModifiedDateTimeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="maxAttendeesPerInstance")
    public JAXBElement<MaxAttendeesPerInstanceType> createMaxAttendeesPerInstance(MaxAttendeesPerInstanceType value) {
        return new JAXBElement(_MaxAttendeesPerInstance_QNAME, MaxAttendeesPerInstanceType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="supportedCalendarComponentSet")
    public JAXBElement<SupportedCalendarComponentSetType> createSupportedCalendarComponentSet(SupportedCalendarComponentSetType value) {
        return new JAXBElement(_SupportedCalendarComponentSet_QNAME, SupportedCalendarComponentSetType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="collection")
    public JAXBElement<CollectionType> createCollection(CollectionType value) {
        return new JAXBElement(_Collection_QNAME, CollectionType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="targetDoesNotExist", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<TargetDoesNotExistType> createTargetDoesNotExist(TargetDoesNotExistType value) {
        return new JAXBElement(_TargetDoesNotExist_QNAME, TargetDoesNotExistType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="invalidFilter", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<InvalidFilterType> createInvalidFilter(InvalidFilterType value) {
        return new JAXBElement(_InvalidFilter_QNAME, InvalidFilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="anyComp")
    public JAXBElement<AnyCompType> createAnyComp(AnyCompType value) {
        return new JAXBElement(_AnyComp_QNAME, AnyCompType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="tooManyInstances", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<TooManyInstancesType> createTooManyInstances(TooManyInstancesType value) {
        return new JAXBElement(_TooManyInstances_QNAME, TooManyInstancesType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="timezoneServer")
    public JAXBElement<TimezoneServerType> createTimezoneServer(TimezoneServerType value) {
        return new JAXBElement(_TimezoneServer_QNAME, TimezoneServerType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="displayName")
    public JAXBElement<DisplayNameType> createDisplayName(DisplayNameType value) {
        return new JAXBElement(_DisplayName_QNAME, DisplayNameType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="paramFilter")
    public JAXBElement<ParamFilterType> createParamFilter(ParamFilterType value) {
        return new JAXBElement(_ParamFilter_QNAME, ParamFilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="exceedsMaxResourceSize", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<ExceedsMaxResourceSizeType> createExceedsMaxResourceSize(ExceedsMaxResourceSizeType value) {
        return new JAXBElement(_ExceedsMaxResourceSize_QNAME, ExceedsMaxResourceSizeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="compFilter")
    public JAXBElement<CompFilterType> createCompFilter(CompFilterType value) {
        return new JAXBElement(_CompFilter_QNAME, CompFilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="deleteItem")
    public JAXBElement<DeleteItemType> createDeleteItem(DeleteItemType value) {
        return new JAXBElement(_DeleteItem_QNAME, DeleteItemType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="forbidden", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<ForbiddenType> createForbidden(ForbiddenType value) {
        return new JAXBElement(_Forbidden_QNAME, ForbiddenType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="creationDateTime")
    public JAXBElement<CreationDateTimeType> createCreationDateTime(CreationDateTimeType value) {
        return new JAXBElement(_CreationDateTime_QNAME, CreationDateTimeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="calendarQuery")
    public JAXBElement<CalendarQueryType> createCalendarQuery(CalendarQueryType value) {
        return new JAXBElement(_CalendarQuery_QNAME, CalendarQueryType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="limit-freebusy-set")
    public JAXBElement<LimitFreebusySetType> createLimitFreebusySet(LimitFreebusySetType value) {
        return new JAXBElement(_LimitFreebusySet_QNAME, LimitFreebusySetType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="baseResponse")
    public JAXBElement<BaseResponseType> createBaseResponse(BaseResponseType value) {
        return new JAXBElement(_BaseResponse_QNAME, BaseResponseType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="invalidCalendarCollectionLocation", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<InvalidCalendarCollectionLocationType> createInvalidCalendarCollectionLocation(InvalidCalendarCollectionLocationType value) {
        return new JAXBElement(_InvalidCalendarCollectionLocation_QNAME, InvalidCalendarCollectionLocationType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="resourceTimezoneId")
    public JAXBElement<ResourceTimezoneIdType> createResourceTimezoneId(ResourceTimezoneIdType value) {
        return new JAXBElement(_ResourceTimezoneId_QNAME, ResourceTimezoneIdType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="is-not-defined")
    public JAXBElement<IsNotDefinedType> createIsNotDefined(IsNotDefinedType value) {
        return new JAXBElement(_IsNotDefined_QNAME, IsNotDefinedType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="missingChangeToken", substitutionHeadNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", substitutionHeadName="error")
    public JAXBElement<MissingChangeTokenType> createMissingChangeToken(MissingChangeTokenType value) {
        return new JAXBElement(_MissingChangeToken_QNAME, MissingChangeTokenType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", name="getProperties")
    public JAXBElement<GetPropertiesType> createGetProperties(GetPropertiesType value) {
        return new JAXBElement(_GetProperties_QNAME, GetPropertiesType.class, null, (Object)value);
    }
}

