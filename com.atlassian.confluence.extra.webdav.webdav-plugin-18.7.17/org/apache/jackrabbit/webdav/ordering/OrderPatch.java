/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.ordering;

import java.util.ArrayList;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.ordering.OrderingConstants;
import org.apache.jackrabbit.webdav.ordering.Position;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OrderPatch
implements OrderingConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(OrderPatch.class);
    private Member[] instructions;
    private String orderingType;

    public OrderPatch(String orderingType, Member instruction) {
        this(orderingType, new Member[]{instruction});
    }

    public OrderPatch(String orderingType, Member[] instructions) {
        if (orderingType == null || instructions == null) {
            throw new IllegalArgumentException("ordering type and instructions cannot be null.");
        }
        this.orderingType = orderingType;
        this.instructions = instructions;
    }

    public String getOrderingType() {
        return this.orderingType;
    }

    public Member[] getOrderInstructions() {
        return this.instructions;
    }

    @Override
    public Element toXml(Document document) {
        Element orderPatch = DomUtil.createElement(document, "orderpatch", NAMESPACE);
        Element otype = DomUtil.addChildElement(orderPatch, "ordering-type", NAMESPACE);
        otype.appendChild(DomUtil.hrefToXml(this.orderingType, document));
        for (Member instruction : this.instructions) {
            orderPatch.appendChild(instruction.toXml(document));
        }
        return orderPatch;
    }

    public static OrderPatch createFromXml(Element orderPatchElement) throws DavException {
        if (!DomUtil.matches(orderPatchElement, "orderpatch", NAMESPACE)) {
            log.warn("ORDERPATH request body must start with an 'orderpatch' element.");
            throw new DavException(400);
        }
        Element otype = DomUtil.getChildElement(orderPatchElement, "ordering-type", NAMESPACE);
        if (otype == null) {
            log.warn("ORDERPATH request body must contain an 'ordering-type' child element.");
            throw new DavException(400);
        }
        String orderingType = DomUtil.getChildText(otype, "href", DavConstants.NAMESPACE);
        ArrayList<Member> tmpList = new ArrayList<Member>();
        ElementIterator it = DomUtil.getChildren(orderPatchElement, "order-member", NAMESPACE);
        while (it.hasNext()) {
            Element el = it.nextElement();
            try {
                String segment = DomUtil.getChildText(el, "segment", NAMESPACE);
                Position pos = Position.createFromXml(DomUtil.getChildElement(el, "position", NAMESPACE));
                Member om = new Member(segment, pos);
                tmpList.add(om);
            }
            catch (IllegalArgumentException e) {
                log.warn("Invalid element in 'orderpatch' request body: " + e.getMessage());
                throw new DavException(400);
            }
        }
        Member[] instructions = tmpList.toArray(new Member[tmpList.size()]);
        return new OrderPatch(orderingType, instructions);
    }

    public static class Member
    implements XmlSerializable {
        private String memberHandle;
        private Position position;

        public Member(String memberHandle, Position position) {
            this.memberHandle = memberHandle;
            this.position = position;
        }

        public String getMemberHandle() {
            return this.memberHandle;
        }

        public Position getPosition() {
            return this.position;
        }

        @Override
        public Element toXml(Document document) {
            Element memberElem = DomUtil.createElement(document, "order-member", OrderingConstants.NAMESPACE);
            DomUtil.addChildElement(memberElem, "segment", OrderingConstants.NAMESPACE, this.memberHandle);
            memberElem.appendChild(this.position.toXml(document));
            return memberElem;
        }
    }
}

