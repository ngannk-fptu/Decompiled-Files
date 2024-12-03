/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;

class AnnotationTargetFilterCollection<T extends AnnotationTarget>
extends AbstractCollection<AnnotationInstance> {
    private final Map<?, List<AnnotationInstance>> map;
    private final Class<T> type;
    private int size;

    AnnotationTargetFilterCollection(Map<?, List<AnnotationInstance>> map, Class<T> type) {
        this.map = map;
        this.type = type;
    }

    @Override
    public Iterator<AnnotationInstance> iterator() {
        return new Iterator<AnnotationInstance>(){
            final Iterator<List<AnnotationInstance>> mapIterator;
            AnnotationInstance next;
            Iterator<AnnotationInstance> nextList;
            {
                this.mapIterator = AnnotationTargetFilterCollection.this.map.values().iterator();
            }

            void advance() {
                AnnotationInstance next;
                if (this.next != null) {
                    return;
                }
                Class type = AnnotationTargetFilterCollection.this.type;
                block0: while (true) {
                    if (this.nextList == null || !this.nextList.hasNext()) {
                        if (!this.mapIterator.hasNext()) {
                            return;
                        }
                        this.nextList = this.mapIterator.next().iterator();
                    }
                    Iterator<AnnotationInstance> nextList = this.nextList;
                    do {
                        if (!nextList.hasNext()) continue block0;
                    } while ((next = nextList.next()).target().getClass() != type);
                    break;
                }
                this.next = next;
            }

            @Override
            public boolean hasNext() {
                this.advance();
                return this.next != null;
            }

            @Override
            public AnnotationInstance next() {
                this.advance();
                AnnotationInstance next = this.next;
                this.next = null;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() {
        if (this.size != 0) {
            return this.size;
        }
        if (this.map.size() == 0) {
            return 0;
        }
        int size = 0;
        Class<T> type = this.type;
        for (List<AnnotationInstance> instances : this.map.values()) {
            for (AnnotationInstance instance : instances) {
                if (type != instance.target().getClass()) continue;
                ++size;
            }
        }
        this.size = size;
        return this.size;
    }
}

