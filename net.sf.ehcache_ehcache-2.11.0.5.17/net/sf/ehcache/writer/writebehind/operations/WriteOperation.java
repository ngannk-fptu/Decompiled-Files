/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind.operations;

import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.BatchOperation;
import net.sf.ehcache.writer.writebehind.operations.KeyBasedOperation;
import net.sf.ehcache.writer.writebehind.operations.SingleOperation;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;
import net.sf.ehcache.writer.writebehind.operations.WriteAllOperation;

public class WriteOperation
implements SingleOperation {
    private final Element element;
    private final long creationTime;

    public WriteOperation(Element element) {
        this(element, System.currentTimeMillis());
    }

    public WriteOperation(Element element, long creationTime) {
        this.element = new Element(element.getObjectKey(), element.getObjectValue(), element.getVersion(), element.getCreationTime(), element.getLastAccessTime(), element.getHitCount(), false, element.getTimeToLive(), element.getTimeToIdle(), element.getLastUpdateTime());
        this.creationTime = creationTime;
    }

    @Override
    public void performSingleOperation(CacheWriter cacheWriter) {
        cacheWriter.write(this.element);
    }

    @Override
    public BatchOperation createBatchOperation(List<? extends SingleOperation> operations) {
        ArrayList<Element> elements = new ArrayList<Element>();
        for (KeyBasedOperation keyBasedOperation : operations) {
            elements.add(((WriteOperation)keyBasedOperation).element);
        }
        return new WriteAllOperation(elements);
    }

    @Override
    public Object getKey() {
        return this.element.getObjectKey();
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public Element getElement() {
        return this.element;
    }

    @Override
    public SingleOperationType getType() {
        return SingleOperationType.WRITE;
    }

    @Override
    public void throwAway(CacheWriter cacheWriter, RuntimeException e) {
        cacheWriter.throwAway(this.element, SingleOperationType.WRITE, e);
    }

    public int hashCode() {
        int hash = (int)this.getCreationTime();
        hash = hash * 31 + this.getKey().hashCode();
        return hash;
    }

    public boolean equals(Object other) {
        if (other instanceof WriteOperation) {
            return this.getCreationTime() == ((WriteOperation)other).getCreationTime() && this.getKey().equals(((WriteOperation)other).getKey());
        }
        return false;
    }
}

