/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.bcel.verifier.VerifierFactoryObserver;
import org.apache.commons.lang3.ArrayUtils;

public class VerifierFactoryListModel
implements VerifierFactoryObserver,
ListModel<String> {
    private final List<ListDataListener> listeners = new ArrayList<ListDataListener>();
    private final Set<String> cache = new TreeSet<String>();

    public VerifierFactoryListModel() {
        VerifierFactory.attach(this);
        this.update(null);
    }

    @Override
    public synchronized void addListDataListener(ListDataListener l) {
        this.listeners.add(l);
    }

    @Override
    public synchronized String getElementAt(int index) {
        return this.cache.toArray(ArrayUtils.EMPTY_STRING_ARRAY)[index];
    }

    @Override
    public synchronized int getSize() {
        return this.cache.size();
    }

    @Override
    public synchronized void removeListDataListener(ListDataListener l) {
        this.listeners.remove(l);
    }

    @Override
    public synchronized void update(String s) {
        Verifier[] verifiers = VerifierFactory.getVerifiers();
        int verifierLen = verifiers.length;
        this.cache.clear();
        for (Verifier verifier : verifiers) {
            this.cache.add(verifier.getClassName());
        }
        for (ListDataListener listener : this.listeners) {
            listener.contentsChanged(new ListDataEvent(this, 0, 0, verifierLen - 1));
        }
    }
}

