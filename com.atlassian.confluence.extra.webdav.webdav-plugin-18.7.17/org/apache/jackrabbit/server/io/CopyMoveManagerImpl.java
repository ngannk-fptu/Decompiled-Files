/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.server.io.CopyMoveContext;
import org.apache.jackrabbit.server.io.CopyMoveHandler;
import org.apache.jackrabbit.server.io.CopyMoveManager;
import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

public class CopyMoveManagerImpl
implements CopyMoveManager {
    private static CopyMoveManager DEFAULT_MANAGER;
    private final List<CopyMoveHandler> copyMoveHandlers = new ArrayList<CopyMoveHandler>();

    @Override
    public boolean copy(CopyMoveContext context, DavResource source, DavResource destination) throws DavException {
        boolean success = false;
        CopyMoveHandler[] copyMoveHandlers = this.getCopyMoveHandlers();
        for (int i = 0; i < copyMoveHandlers.length && !success; ++i) {
            CopyMoveHandler cmh = copyMoveHandlers[i];
            if (!cmh.canCopy(context, source, destination)) continue;
            success = cmh.copy(context, source, destination);
        }
        return success;
    }

    @Override
    public boolean move(CopyMoveContext context, DavResource source, DavResource destination) throws DavException {
        boolean success = false;
        CopyMoveHandler[] copyMoveHandlers = this.getCopyMoveHandlers();
        for (int i = 0; i < copyMoveHandlers.length && !success; ++i) {
            CopyMoveHandler cmh = copyMoveHandlers[i];
            if (!cmh.canMove(context, source, destination)) continue;
            success = cmh.move(context, source, destination);
        }
        return success;
    }

    @Override
    public void addCopyMoveHandler(CopyMoveHandler copyMoveHandler) {
        if (copyMoveHandler == null) {
            throw new IllegalArgumentException("'null' is not a valid copyMoveHandler.");
        }
        this.copyMoveHandlers.add(copyMoveHandler);
    }

    @Override
    public CopyMoveHandler[] getCopyMoveHandlers() {
        return this.copyMoveHandlers.toArray(new CopyMoveHandler[this.copyMoveHandlers.size()]);
    }

    public static CopyMoveManager getDefaultManager() {
        if (DEFAULT_MANAGER == null) {
            CopyMoveManagerImpl manager = new CopyMoveManagerImpl();
            manager.addCopyMoveHandler(new DefaultHandler());
            DEFAULT_MANAGER = manager;
        }
        return DEFAULT_MANAGER;
    }
}

