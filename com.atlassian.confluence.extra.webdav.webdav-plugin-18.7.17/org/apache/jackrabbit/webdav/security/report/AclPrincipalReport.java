/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security.report;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.security.report.AbstractSecurityReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;

public class AclPrincipalReport
extends AbstractSecurityReport {
    public static final String REPORT_NAME = "acl-principal-prop-set";
    public static final ReportType REPORT_TYPE = ReportType.register("acl-principal-prop-set", SecurityConstants.NAMESPACE, AclPrincipalReport.class);

    @Override
    public ReportType getType() {
        return REPORT_TYPE;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        DavProperty<?> acl = resource.getProperty(SecurityConstants.ACL);
        if (!(acl instanceof AclProperty)) {
            throw new DavException(500, "DAV:acl property expected.");
        }
        DavResourceLocator loc = resource.getLocator();
        HashMap<String, MultiStatusResponse> respMap = new HashMap<String, MultiStatusResponse>();
        Object list = ((AclProperty)acl).getValue();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            AclProperty.Ace ace = (AclProperty.Ace)iterator.next();
            String href = this.normalizeResourceHref(ace.getPrincipal().getHref());
            if (href == null || respMap.containsKey(href)) continue;
            DavResourceLocator princLocator = loc.getFactory().createResourceLocator(loc.getPrefix(), href);
            DavResource principalResource = resource.getFactory().createResource(princLocator, resource.getSession());
            respMap.put(href, new MultiStatusResponse(principalResource, info.getPropertyNameSet()));
        }
        this.responses = respMap.values().toArray(new MultiStatusResponse[respMap.size()]);
    }
}

