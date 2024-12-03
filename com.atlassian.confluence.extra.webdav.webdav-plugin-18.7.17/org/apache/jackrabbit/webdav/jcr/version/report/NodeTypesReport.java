/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.commons.iterator.NodeTypeIteratorAdapter;
import org.apache.jackrabbit.commons.webdav.NodeTypeConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.nodetype.NodeDefinitionImpl;
import org.apache.jackrabbit.webdav.jcr.nodetype.PropertyDefinitionImpl;
import org.apache.jackrabbit.webdav.jcr.version.report.AbstractJcrReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NodeTypesReport
extends AbstractJcrReport
implements NodeTypeConstants {
    private static Logger log = LoggerFactory.getLogger(NodeTypesReport.class);
    public static final ReportType NODETYPES_REPORT = ReportType.register("nodetypes", ItemResourceConstants.NAMESPACE, NodeTypesReport.class);
    private NodeTypeIterator ntIter;

    @Override
    public ReportType getType() {
        return NODETYPES_REPORT;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        try {
            this.ntIter = NodeTypesReport.getNodeTypes(this.getRepositorySession(), info);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
        if (this.ntIter == null) {
            throw new DavException(500);
        }
    }

    @Override
    public Element toXml(Document document) {
        Element report = document.createElement("nodeTypes");
        while (this.ntIter.hasNext()) {
            NodeType nt = this.ntIter.nextNodeType();
            Element ntDef = document.createElement("nodeType");
            ntDef.setAttribute("name", nt.getName());
            ntDef.setAttribute("isMixin", Boolean.toString(nt.isMixin()));
            ntDef.setAttribute("hasOrderableChildNodes", Boolean.toString(nt.hasOrderableChildNodes()));
            ntDef.setAttribute("isAbstract", Boolean.toString(nt.isAbstract()));
            ntDef.setAttribute("isQueryable", Boolean.toString(nt.isQueryable()));
            Element supertypes = DomUtil.addChildElement(ntDef, "supertypes", null);
            for (NodeType snt : nt.getDeclaredSupertypes()) {
                DomUtil.addChildElement(supertypes, "supertype", null, snt.getName());
            }
            for (NodeDefinition aCnd : nt.getChildNodeDefinitions()) {
                if (!aCnd.getDeclaringNodeType().getName().equals(nt.getName())) continue;
                ntDef.appendChild(NodeDefinitionImpl.create(aCnd).toXml(document));
            }
            for (PropertyDefinition aPd : nt.getPropertyDefinitions()) {
                if (!aPd.getDeclaringNodeType().getName().equals(nt.getName())) continue;
                ntDef.appendChild(PropertyDefinitionImpl.create(aPd).toXml(document));
            }
            String primaryItemName = nt.getPrimaryItemName();
            if (primaryItemName != null) {
                ntDef.setAttribute("primaryItemName", primaryItemName);
            }
            report.appendChild(ntDef);
        }
        return report;
    }

    private static NodeTypeIterator getNodeTypes(Session session, ReportInfo info) throws RepositoryException, DavException {
        NodeTypeManager ntMgr = session.getWorkspace().getNodeTypeManager();
        if (info.containsContentElement("all-nodetypes", ItemResourceConstants.NAMESPACE)) {
            return ntMgr.getAllNodeTypes();
        }
        if (info.containsContentElement("mixin-nodetypes", ItemResourceConstants.NAMESPACE)) {
            return ntMgr.getMixinNodeTypes();
        }
        if (info.containsContentElement("primary-nodetypes", ItemResourceConstants.NAMESPACE)) {
            return ntMgr.getPrimaryNodeTypes();
        }
        List<Element> elemList = info.getContentElements("nodetype", ItemResourceConstants.NAMESPACE);
        if (elemList.isEmpty()) {
            throw new DavException(400, "NodeTypes report: request body has invalid format.");
        }
        ArrayList<NodeType> ntList = new ArrayList<NodeType>();
        for (Element el : elemList) {
            String nodetypeName = DomUtil.getChildTextTrim(el, "nodetypename", ItemResourceConstants.NAMESPACE);
            if (nodetypeName == null) continue;
            ntList.add(ntMgr.getNodeType(nodetypeName));
        }
        return new NodeTypeIteratorAdapter(ntList);
    }
}

