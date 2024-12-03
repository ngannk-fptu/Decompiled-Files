/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jcr.Credentials;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.ItemInfoCache;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.NodeInfo;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.commons.AbstractRepositoryService;
import org.apache.jackrabbit.spi.commons.ItemInfoCacheImpl;

public abstract class AbstractReadableRepositoryService
extends AbstractRepositoryService {
    protected static final Set<String> WRITE_ACTIONS = new HashSet<String>(Arrays.asList("add_node", "set_property", "remove"));
    protected final List<String> wspNames;
    protected final String defaulWsp;

    public AbstractReadableRepositoryService(Map<String, QValue[]> descriptors, Map<String, String> namespaces, Reader cnd, List<String> wspNames, String defaultWsp) throws RepositoryException, ParseException, IllegalArgumentException {
        super(descriptors, namespaces, cnd);
        if (defaultWsp == null) {
            throw new IllegalArgumentException("Default workspace is null");
        }
        this.wspNames = Collections.unmodifiableList(new ArrayList<String>(wspNames));
        this.defaulWsp = defaultWsp;
    }

    @Override
    protected void checkWorkspace(String workspaceName) throws NoSuchWorkspaceException {
        if (workspaceName != null && !this.wspNames.contains(workspaceName)) {
            throw new NoSuchWorkspaceException(workspaceName);
        }
    }

    @Override
    protected SessionInfo createSessionInfo(Credentials credentials, String workspaceName) throws RepositoryException {
        return super.createSessionInfo(credentials, workspaceName == null ? this.defaulWsp : workspaceName);
    }

    @Override
    public ItemInfoCache getItemInfoCache(SessionInfo sessionInfo) {
        return new ItemInfoCacheImpl();
    }

    @Override
    public NodeInfo getNodeInfo(SessionInfo sessionInfo, NodeId nodeId) throws ItemNotFoundException, RepositoryException {
        Iterator<? extends ItemInfo> infos = this.getItemInfos(sessionInfo, nodeId);
        if (infos.hasNext()) {
            return (NodeInfo)infos.next();
        }
        throw new ItemNotFoundException();
    }

    @Override
    public String[] getWorkspaceNames(SessionInfo sessionInfo) throws RepositoryException {
        this.checkSessionInfo(sessionInfo);
        return this.wspNames.toArray(new String[this.wspNames.size()]);
    }

    @Override
    public boolean isGranted(SessionInfo sessionInfo, ItemId itemId, String[] actions) throws RepositoryException {
        this.checkSessionInfo(sessionInfo);
        for (String action : actions) {
            if (!WRITE_ACTIONS.contains(action)) continue;
            return false;
        }
        return true;
    }
}

