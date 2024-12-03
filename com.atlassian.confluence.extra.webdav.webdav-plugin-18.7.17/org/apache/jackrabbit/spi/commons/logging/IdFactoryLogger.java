/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.commons.logging.AbstractLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class IdFactoryLogger
extends AbstractLogger
implements IdFactory {
    private final IdFactory idFactory;

    public IdFactoryLogger(IdFactory idFactory, LogWriter writer) {
        super(writer);
        this.idFactory = idFactory;
    }

    public IdFactory getIdFactory() {
        return this.idFactory;
    }

    @Override
    public PropertyId createPropertyId(final NodeId parentId, final Name propertyName) {
        return (PropertyId)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return IdFactoryLogger.this.idFactory.createPropertyId(parentId, propertyName);
            }
        }, "createPropertyId(NodeId, Name)", new Object[]{parentId, propertyName});
    }

    @Override
    public NodeId createNodeId(final NodeId parentId, final Path path) {
        return (NodeId)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return IdFactoryLogger.this.idFactory.createNodeId(parentId, path);
            }
        }, "createNodeId(NodeId, Path)", new Object[]{parentId, path});
    }

    @Override
    public NodeId createNodeId(final String uniqueID, final Path path) {
        return (NodeId)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return IdFactoryLogger.this.idFactory.createNodeId(uniqueID, path);
            }
        }, "createNodeId(String, Path)", new Object[]{uniqueID, path});
    }

    @Override
    public NodeId createNodeId(final String uniqueID) {
        return (NodeId)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return IdFactoryLogger.this.idFactory.createNodeId(uniqueID);
            }
        }, "createNodeId(String)", new Object[]{uniqueID});
    }

    @Override
    public String toJcrIdentifier(final NodeId nodeId) {
        return (String)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return IdFactoryLogger.this.idFactory.toJcrIdentifier(nodeId);
            }
        }, "toJcrIdentifier(String)", new Object[]{nodeId});
    }

    @Override
    public NodeId fromJcrIdentifier(final String jcrIdentifier) {
        return (NodeId)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return IdFactoryLogger.this.idFactory.fromJcrIdentifier(jcrIdentifier);
            }
        }, "fromJcrIdentifier(String)", new Object[]{jcrIdentifier});
    }
}

