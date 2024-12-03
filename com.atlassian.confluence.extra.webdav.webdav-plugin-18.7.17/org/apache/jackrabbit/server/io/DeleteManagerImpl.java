/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.DeleteContext;
import org.apache.jackrabbit.server.io.DeleteHandler;
import org.apache.jackrabbit.server.io.DeleteManager;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

public class DeleteManagerImpl
implements DeleteManager {
    private static DeleteManager DEFAULT_MANAGER;
    private final List<DeleteHandler> deleteHandlers = new ArrayList<DeleteHandler>();

    @Override
    public boolean delete(DeleteContext deleteContext, DavResource member) throws DavException {
        boolean success = false;
        DeleteHandler[] deleteHandlers = this.getDeleteHandlers();
        for (int i = 0; i < deleteHandlers.length && !success; ++i) {
            DeleteHandler dh = deleteHandlers[i];
            if (!dh.canDelete(deleteContext, member)) continue;
            success = dh.delete(deleteContext, member);
        }
        return success;
    }

    @Override
    public void addDeleteHandler(DeleteHandler deleteHandler) {
        if (deleteHandler == null) {
            throw new IllegalArgumentException("'null' is not a valid DeleteHandler.");
        }
        this.deleteHandlers.add(deleteHandler);
    }

    @Override
    public DeleteHandler[] getDeleteHandlers() {
        return this.deleteHandlers.toArray(new DeleteHandler[this.deleteHandlers.size()]);
    }

    public static DeleteManager getDefaultManager() {
        if (DEFAULT_MANAGER == null) {
            DeleteManagerImpl manager = new DeleteManagerImpl();
            manager.addDeleteHandler(new DefaultHandler());
            DEFAULT_MANAGER = manager;
        }
        return DEFAULT_MANAGER;
    }
}

