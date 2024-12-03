/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.confluence.plugins.rest.entities.AttachmentEntity;
import com.atlassian.confluence.plugins.rest.entities.AttachmentEntityList;
import com.atlassian.confluence.plugins.rest.entities.documentation.DateEntityExampleDocument;
import com.atlassian.confluence.plugins.rest.entities.documentation.LinkExamples;
import com.atlassian.confluence.plugins.rest.entities.documentation.SpaceEntityExampleDocument;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import java.util.ArrayList;

public class AttachmentEntityExampleDocument {
    public static final AttachmentEntity ATTACHMENT_ENTITY = new AttachmentEntity();
    public static final AttachmentEntityList ATTACHMENT_ENTITY_LIST_EXPANDED;

    static {
        ATTACHMENT_ENTITY.addLink(LinkExamples.SELF);
        ATTACHMENT_ENTITY.addLink(LinkExamples.ALTERNATE);
        ATTACHMENT_ENTITY.setComment("Shot of the Harbour Bridge from below");
        ATTACHMENT_ENTITY.setContentType("image/jpeg");
        ATTACHMENT_ENTITY.setCreatedDate(DateEntityExampleDocument.DATE_ENTITY);
        ATTACHMENT_ENTITY.setFileName("harbour bridge3.jpg");
        ATTACHMENT_ENTITY.setFileSize(150652L);
        ATTACHMENT_ENTITY.setId("6488237l");
        ATTACHMENT_ENTITY.setLastModifiedDate(DateEntityExampleDocument.DATE_ENTITY);
        ATTACHMENT_ENTITY.setNiceFileSize("147 kB");
        ATTACHMENT_ENTITY.setNiceType("Image");
        ATTACHMENT_ENTITY.setOwnerId("6389783");
        ATTACHMENT_ENTITY.setSpace(SpaceEntityExampleDocument.MINIMAL_SPACE_ENTITY);
        ATTACHMENT_ENTITY.setVersion(1);
        ATTACHMENT_ENTITY.setWikiLink("[ds:Josh^harbour bridge3.jpg]");
        ATTACHMENT_ENTITY_LIST_EXPANDED = new AttachmentEntityList(2, (ListWrapperCallback<AttachmentEntity>)((ListWrapperCallback)indexes -> {
            ArrayList<AttachmentEntity> list = new ArrayList<AttachmentEntity>(2);
            list.add(ATTACHMENT_ENTITY);
            list.add(ATTACHMENT_ENTITY);
            return list;
        }));
        ATTACHMENT_ENTITY_LIST_EXPANDED.buildAttachmentListFromWrapper(0, 2);
    }
}

