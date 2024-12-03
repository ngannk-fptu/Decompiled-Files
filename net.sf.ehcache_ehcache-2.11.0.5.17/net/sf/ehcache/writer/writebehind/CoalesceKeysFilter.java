/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sf.ehcache.writer.writebehind.OperationConverter;
import net.sf.ehcache.writer.writebehind.OperationsFilter;
import net.sf.ehcache.writer.writebehind.operations.KeyBasedOperation;

public class CoalesceKeysFilter
implements OperationsFilter<KeyBasedOperation> {
    @Override
    public void filter(List operations, OperationConverter<KeyBasedOperation> converter) {
        HashMap mostRecent = new HashMap();
        ArrayList<Object> operationsToRemove = new ArrayList<Object>();
        for (int i = 0; i < operations.size(); ++i) {
            Object e = operations.get(i);
            KeyBasedOperation keyBasedOperation = converter.convert(e);
            if (!mostRecent.containsKey(keyBasedOperation.getKey())) {
                mostRecent.put(keyBasedOperation.getKey(), e);
                continue;
            }
            Object previousOperation = mostRecent.get(keyBasedOperation.getKey());
            KeyBasedOperation keyBasedPreviousOperation = converter.convert(previousOperation);
            if (keyBasedPreviousOperation.getCreationTime() > keyBasedOperation.getCreationTime()) {
                operationsToRemove.add(e);
                continue;
            }
            operationsToRemove.add(previousOperation);
            mostRecent.put(keyBasedOperation.getKey(), e);
        }
        for (Object e : operationsToRemove) {
            operations.remove(e);
        }
    }
}

