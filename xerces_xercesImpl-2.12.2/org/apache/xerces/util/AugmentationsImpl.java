/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.xerces.xni.Augmentations;

public class AugmentationsImpl
implements Augmentations {
    private AugmentationsItemsContainer fAugmentationsContainer = new SmallContainer();

    @Override
    public Object putItem(String string, Object object) {
        Object object2 = this.fAugmentationsContainer.putItem(string, object);
        if (object2 == null && this.fAugmentationsContainer.isFull()) {
            this.fAugmentationsContainer = this.fAugmentationsContainer.expand();
        }
        return object2;
    }

    @Override
    public Object getItem(String string) {
        return this.fAugmentationsContainer.getItem(string);
    }

    @Override
    public Object removeItem(String string) {
        return this.fAugmentationsContainer.removeItem(string);
    }

    @Override
    public Enumeration keys() {
        return this.fAugmentationsContainer.keys();
    }

    @Override
    public void removeAllItems() {
        this.fAugmentationsContainer.clear();
    }

    public String toString() {
        return this.fAugmentationsContainer.toString();
    }

    static final class LargeContainer
    extends AugmentationsItemsContainer {
        private final HashMap fAugmentations = new HashMap();

        LargeContainer() {
        }

        @Override
        public Object getItem(Object object) {
            return this.fAugmentations.get(object);
        }

        @Override
        public Object putItem(Object object, Object object2) {
            return this.fAugmentations.put(object, object2);
        }

        @Override
        public Object removeItem(Object object) {
            return this.fAugmentations.remove(object);
        }

        @Override
        public Enumeration keys() {
            return Collections.enumeration(this.fAugmentations.keySet());
        }

        @Override
        public void clear() {
            this.fAugmentations.clear();
        }

        @Override
        public boolean isFull() {
            return false;
        }

        @Override
        public AugmentationsItemsContainer expand() {
            return this;
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("LargeContainer");
            for (Map.Entry entry : this.fAugmentations.entrySet()) {
                stringBuffer.append("\nkey == ");
                stringBuffer.append(entry.getKey());
                stringBuffer.append("; value == ");
                stringBuffer.append(entry.getValue());
            }
            return stringBuffer.toString();
        }
    }

    static final class SmallContainer
    extends AugmentationsItemsContainer {
        static final int SIZE_LIMIT = 10;
        final Object[] fAugmentations = new Object[20];
        int fNumEntries = 0;

        SmallContainer() {
        }

        @Override
        public Enumeration keys() {
            return new SmallContainerKeyEnumeration();
        }

        @Override
        public Object getItem(Object object) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (!this.fAugmentations[i].equals(object)) continue;
                return this.fAugmentations[i + 1];
            }
            return null;
        }

        @Override
        public Object putItem(Object object, Object object2) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (!this.fAugmentations[i].equals(object)) continue;
                Object object3 = this.fAugmentations[i + 1];
                this.fAugmentations[i + 1] = object2;
                return object3;
            }
            this.fAugmentations[this.fNumEntries * 2] = object;
            this.fAugmentations[this.fNumEntries * 2 + 1] = object2;
            ++this.fNumEntries;
            return null;
        }

        @Override
        public Object removeItem(Object object) {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                if (!this.fAugmentations[i].equals(object)) continue;
                Object object2 = this.fAugmentations[i + 1];
                for (int j = i; j < this.fNumEntries * 2 - 2; j += 2) {
                    this.fAugmentations[j] = this.fAugmentations[j + 2];
                    this.fAugmentations[j + 1] = this.fAugmentations[j + 3];
                }
                this.fAugmentations[this.fNumEntries * 2 - 2] = null;
                this.fAugmentations[this.fNumEntries * 2 - 1] = null;
                --this.fNumEntries;
                return object2;
            }
            return null;
        }

        @Override
        public void clear() {
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                this.fAugmentations[i] = null;
                this.fAugmentations[i + 1] = null;
            }
            this.fNumEntries = 0;
        }

        @Override
        public boolean isFull() {
            return this.fNumEntries == 10;
        }

        @Override
        public AugmentationsItemsContainer expand() {
            LargeContainer largeContainer = new LargeContainer();
            for (int i = 0; i < this.fNumEntries * 2; i += 2) {
                largeContainer.putItem(this.fAugmentations[i], this.fAugmentations[i + 1]);
            }
            return largeContainer;
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("SmallContainer - fNumEntries == ").append(this.fNumEntries);
            for (int i = 0; i < 20; i += 2) {
                stringBuffer.append("\nfAugmentations[");
                stringBuffer.append(i);
                stringBuffer.append("] == ");
                stringBuffer.append(this.fAugmentations[i]);
                stringBuffer.append("; fAugmentations[");
                stringBuffer.append(i + 1);
                stringBuffer.append("] == ");
                stringBuffer.append(this.fAugmentations[i + 1]);
            }
            return stringBuffer.toString();
        }

        final class SmallContainerKeyEnumeration
        implements Enumeration {
            Object[] enumArray;
            int next;

            SmallContainerKeyEnumeration() {
                this.enumArray = new Object[SmallContainer.this.fNumEntries];
                this.next = 0;
                for (int i = 0; i < SmallContainer.this.fNumEntries; ++i) {
                    this.enumArray[i] = SmallContainer.this.fAugmentations[i * 2];
                }
            }

            @Override
            public boolean hasMoreElements() {
                return this.next < this.enumArray.length;
            }

            public Object nextElement() {
                if (this.next >= this.enumArray.length) {
                    throw new NoSuchElementException();
                }
                Object object = this.enumArray[this.next];
                this.enumArray[this.next] = null;
                ++this.next;
                return object;
            }
        }
    }

    static abstract class AugmentationsItemsContainer {
        AugmentationsItemsContainer() {
        }

        public abstract Object putItem(Object var1, Object var2);

        public abstract Object getItem(Object var1);

        public abstract Object removeItem(Object var1);

        public abstract Enumeration keys();

        public abstract void clear();

        public abstract boolean isFull();

        public abstract AugmentationsItemsContainer expand();
    }
}

