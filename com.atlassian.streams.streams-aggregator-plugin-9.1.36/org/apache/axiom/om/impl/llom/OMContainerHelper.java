/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.IChildNode;
import org.apache.axiom.om.impl.llom.IContainer;
import org.apache.axiom.om.impl.llom.IParentNode;
import org.apache.axiom.om.impl.llom.NamespaceURIInterningXMLStreamReaderWrapper;
import org.apache.axiom.om.impl.llom.OMStAXWrapper;
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class OMContainerHelper {
    private static final Log log = LogFactory.getLog(OMContainerHelper.class);
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();

    private OMContainerHelper() {
    }

    public static XMLStreamReader getXMLStreamReader(IContainer container, boolean cache) {
        return OMContainerHelper.getXMLStreamReader(container, cache, defaultReaderConfiguration);
    }

    public static XMLStreamReader getXMLStreamReader(IContainer container, boolean cache, OMXMLStreamReaderConfiguration configuration) {
        OMXMLStreamReader reader;
        OMXMLParserWrapper builder = container.getBuilder();
        if (builder != null && builder instanceof StAXOMBuilder && !container.isComplete() && ((StAXOMBuilder)builder).isLookahead()) {
            OMContainerHelper.buildNext(container);
        }
        boolean done = container.isComplete();
        if (builder == null && done) {
            reader = new OMStAXWrapper(null, container, false, configuration.isPreserveNamespaceContext());
        } else {
            if (builder == null && !cache) {
                throw new UnsupportedOperationException("This element was not created in a manner to be switched");
            }
            if (builder != null && builder.isCompleted() && !cache && !done) {
                throw new UnsupportedOperationException("The parser is already consumed!");
            }
            reader = new OMStAXWrapper(builder, container, cache, configuration.isPreserveNamespaceContext());
        }
        if (configuration.isNamespaceURIInterning()) {
            reader = new NamespaceURIInterningXMLStreamReaderWrapper(reader);
        }
        if (log.isDebugEnabled()) {
            reader = new OMXMLStreamReaderValidator(reader, false);
        }
        return reader;
    }

    public static void addChild(IContainer container, OMNode omNode, boolean fromBuilder) {
        OMNodeEx child;
        if (fromBuilder) {
            child = (OMNodeEx)omNode;
        } else {
            child = omNode.getOMFactory().getMetaFactory() == container.getOMFactory().getMetaFactory() ? (OMNodeEx)omNode : (OMNodeEx)((OMFactoryEx)container.getOMFactory()).importNode(omNode);
            if (!container.isComplete()) {
                container.build();
            }
            if (child.getParent() == container && child == container.getLastKnownOMChild()) {
                return;
            }
        }
        if (child.getParent() != null) {
            child.detach();
        }
        child.setParent(container);
        if (container.getFirstOMChildIfAvailable() == null) {
            container.setFirstChild(child);
        } else {
            OMNode lastChild = container.getLastKnownOMChild();
            child.setPreviousOMSibling(lastChild);
            ((OMNodeEx)lastChild).setNextOMSibling(child);
        }
        container.setLastChild(child);
        if (!(fromBuilder || child.isComplete() || child instanceof OMSourcedElement)) {
            container.setComplete(false);
        }
    }

    public static void build(IContainer container) {
        OMXMLParserWrapper builder = container.getBuilder();
        if (builder != null && builder.isCompleted()) {
            log.debug((Object)"Builder is already complete.");
        }
        while (!container.isComplete()) {
            builder.next();
            if (!builder.isCompleted() || container.isComplete()) continue;
            log.debug((Object)"Builder is complete.  Setting OMObject to complete.");
            container.setComplete(true);
        }
    }

    public static void buildNext(IParentNode that) {
        OMXMLParserWrapper builder = that.getBuilder();
        if (builder != null) {
            if (((StAXOMBuilder)builder).isClosed()) {
                throw new OMException("The builder has already been closed");
            }
            if (!builder.isCompleted()) {
                builder.next();
            } else {
                throw new IllegalStateException("Builder is already complete");
            }
        }
    }

    public static OMNode getFirstOMChild(IParentNode that) {
        OMNode firstChild = that.getFirstOMChildIfAvailable();
        if (firstChild == null) {
            switch (that.getState()) {
                case 2: {
                    ((StAXBuilder)that.getBuilder()).debugDiscarded(that);
                    throw new NodeUnavailableException();
                }
                case 0: {
                    do {
                        OMContainerHelper.buildNext(that);
                    } while (that.getState() == 0 && (firstChild = that.getFirstOMChildIfAvailable()) == null);
                }
            }
        }
        return firstChild;
    }

    public static void removeChildren(IContainer that) {
        boolean updateState;
        if (that.getState() == 0 && that.getBuilder() != null) {
            OMNode lastKnownChild = that.getLastKnownOMChild();
            if (lastKnownChild != null) {
                lastKnownChild.build();
            }
            ((StAXOMBuilder)that.getBuilder()).discard(that);
            updateState = true;
        } else {
            updateState = false;
        }
        IChildNode child = (IChildNode)that.getFirstOMChildIfAvailable();
        while (child != null) {
            IChildNode nextSibling = (IChildNode)child.getNextOMSiblingIfAvailable();
            child.setPreviousOMSibling(null);
            child.setNextOMSibling(null);
            child.setParent(null);
            child = nextSibling;
        }
        that.setFirstChild(null);
        that.setLastChild(null);
        if (updateState) {
            that.setComplete(true);
        }
    }
}

