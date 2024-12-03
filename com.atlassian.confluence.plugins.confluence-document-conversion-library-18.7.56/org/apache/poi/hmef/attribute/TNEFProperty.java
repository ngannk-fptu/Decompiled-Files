/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TNEFProperty {
    private static Map<Integer, List<TNEFProperty>> properties = new HashMap<Integer, List<TNEFProperty>>();
    public static final int TYPE_TRIPLES = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_DATE = 3;
    public static final int TYPE_SHORT = 4;
    public static final int TYPE_LONG = 5;
    public static final int TYPE_BYTE = 6;
    public static final int TYPE_WORD = 7;
    public static final int TYPE_DWORD = 8;
    public static final int TYPE_MAX = 9;
    public static final int PTYPE_APPTIME = 7;
    public static final int PTYPE_BINARY = 258;
    public static final int PTYPE_BOOLEAN = 11;
    public static final int PTYPE_CLASSID = 72;
    public static final int PTYPE_CURRENCY = 6;
    public static final int PTYPE_DOUBLE = 5;
    public static final int PTYPE_ERROR = 10;
    public static final int PTYPE_I2 = 2;
    public static final int PTYPE_I8 = 20;
    public static final int PTYPE_LONG = 3;
    public static final int PTYPE_MULTIVALUED = 4096;
    public static final int PTYPE_NULL = 1;
    public static final int PTYPE_OBJECT = 13;
    public static final int PTYPE_R4 = 4;
    public static final int PTYPE_STRING8 = 30;
    public static final int PTYPE_SYSTIME = 64;
    public static final int PTYPE_UNICODE = 31;
    public static final int PTYPE_UNSPECIFIED = 0;
    public static final int LEVEL_MESSAGE = 1;
    public static final int LEVEL_ATTACHMENT = 2;
    public static final int LEVEL_END_OF_FILE = -1;
    public static final TNEFProperty ID_AIDOWNER = new TNEFProperty(8, 5, "AidOwner", "PR_OWNER_APPT_ID");
    public static final TNEFProperty ID_ATTACHCREATEDATE = new TNEFProperty(32786, 3, "AttachCreateDate", "PR_CREATION_TIME");
    public static final TNEFProperty ID_ATTACHDATA = new TNEFProperty(32783, 6, "AttachData", "PR_ATTACH_DATA_BIN");
    public static final TNEFProperty ID_ATTACHMENT = new TNEFProperty(36869, 6, "Attachment", null);
    public static final TNEFProperty ID_ATTACHMETAFILE = new TNEFProperty(32785, 6, "AttachMetaFile", "PR_ATTACH_RENDERING");
    public static final TNEFProperty ID_ATTACHMODIFYDATE = new TNEFProperty(32787, 3, "AttachModifyDate", "PR_LAST_MODIFICATION_TIME");
    public static final TNEFProperty ID_ATTACHRENDERDATA = new TNEFProperty(36866, 6, "AttachRenderData", "attAttachRenddata");
    public static final TNEFProperty ID_ATTACHTITLE = new TNEFProperty(32784, 1, "AttachTitle", "PR_ATTACH_FILENAME");
    public static final TNEFProperty ID_ATTACHTRANSPORTFILENAME = new TNEFProperty(36865, 6, "AttachTransportFilename", "PR_ATTACH_TRANSPORT_NAME");
    public static final TNEFProperty ID_BODY = new TNEFProperty(32780, 2, "Body", "PR_BODY");
    public static final TNEFProperty ID_CONVERSATIONID = new TNEFProperty(32779, 1, "ConversationId", "PR_CONVERSATION_KEY");
    public static final TNEFProperty ID_DATEEND = new TNEFProperty(7, 3, "DateEnd", "PR_END_DATE");
    public static final TNEFProperty ID_DATEMODIFIED = new TNEFProperty(32800, 3, "DateModified", "PR_LAST_MODIFICATION_TIME ");
    public static final TNEFProperty ID_DATERECEIVED = new TNEFProperty(32774, 3, "DateReceived", "PR_MESSAGE_DELIVERY_TIME ");
    public static final TNEFProperty ID_DATESENT = new TNEFProperty(32773, 3, "DateSent", "PR_CLIENT_SUBMIT_TIME ");
    public static final TNEFProperty ID_DATESTART = new TNEFProperty(6, 3, "DateStart", "PR_START_DATE ");
    public static final TNEFProperty ID_DELEGATE = new TNEFProperty(2, 6, "Delegate", "PR_RCVD_REPRESENTING_xxx ");
    public static final TNEFProperty ID_FROM = new TNEFProperty(32768, 1, "From", "PR_SENDER_ENTRYID");
    public static final TNEFProperty ID_MAPIPROPERTIES = new TNEFProperty(36867, 6, "MapiProperties", null);
    public static final TNEFProperty ID_MESSAGECLASS = new TNEFProperty(32776, 7, "MessageClass", "PR_MESSAGE_CLASS ");
    public static final TNEFProperty ID_MESSAGEID = new TNEFProperty(32777, 1, "MessageId", "PR_SEARCH_KEY");
    public static final TNEFProperty ID_MESSAGESTATUS = new TNEFProperty(32775, 6, "MessageStatus", "PR_MESSAGE_FLAGS");
    public static final TNEFProperty ID_NULL = new TNEFProperty(0, -1, "Null", null);
    public static final TNEFProperty ID_OEMCODEPAGE = new TNEFProperty(36871, 6, "OemCodepage", "AttOemCodepage");
    public static final TNEFProperty ID_ORIGINALMESSAGECLASS = new TNEFProperty(6, 7, "OriginalMessageClass", "PR_ORIG_MESSAGE_CLASS");
    public static final TNEFProperty ID_OWNER = new TNEFProperty(0, 6, "Owner", "PR_RCVD_REPRESENTING_xxx");
    public static final TNEFProperty ID_PARENTID = new TNEFProperty(32778, 1, "ParentId", "PR_PARENT_KEY");
    public static final TNEFProperty ID_PRIORITY = new TNEFProperty(32781, 4, "Priority", "PR_IMPORTANCE");
    public static final TNEFProperty ID_RECIPIENTTABLE = new TNEFProperty(36868, 6, "RecipientTable", "PR_MESSAGE_RECIPIENTS");
    public static final TNEFProperty ID_REQUESTRESPONSE = new TNEFProperty(9, 4, "RequestResponse", "PR_RESPONSE_REQUESTED");
    public static final TNEFProperty ID_SENTFOR = new TNEFProperty(1, 6, "SentFor", "PR_SENT_REPRESENTING_xxx");
    public static final TNEFProperty ID_SUBJECT = new TNEFProperty(32772, 1, "Subject", "PR_SUBJECT");
    public static final TNEFProperty ID_TNEFVERSION = new TNEFProperty(36870, 8, "TnefVersion", "attTnefVersion");
    public static final TNEFProperty ID_UNKNOWN = new TNEFProperty(-1, -1, "Unknown", null);
    public final int id;
    public final int usualType;
    public final String name;
    public final String mapiProperty;

    private TNEFProperty(int id, int usualType, String name, String mapiProperty) {
        this.id = id;
        this.usualType = usualType;
        this.name = name;
        this.mapiProperty = mapiProperty;
        if (!properties.containsKey(id)) {
            properties.put(id, new ArrayList());
        }
        properties.get(id).add(this);
    }

    public static TNEFProperty getBest(int id, int type) {
        List<TNEFProperty> attrs = properties.get(id);
        if (attrs == null) {
            return ID_UNKNOWN;
        }
        if (attrs.size() == 1) {
            return attrs.get(0);
        }
        for (TNEFProperty attr : attrs) {
            if (attr.usualType != type) continue;
            return attr;
        }
        return attrs.get(0);
    }

    public String toString() {
        return this.name + " [" + this.id + "]" + (this.mapiProperty == null ? "" : " (" + this.mapiProperty + ")");
    }
}

