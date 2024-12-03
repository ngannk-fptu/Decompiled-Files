/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.properties;

import aQute.bnd.properties.ITextStore;

public class CopyOnWriteTextStore
implements ITextStore {
    protected ITextStore fTextStore = new StringTextStore();
    private final ITextStore fModifiableTextStore;

    public CopyOnWriteTextStore(ITextStore modifiableTextStore) {
        this.fModifiableTextStore = modifiableTextStore;
    }

    @Override
    public char get(int offset) {
        return this.fTextStore.get(offset);
    }

    @Override
    public String get(int offset, int length) {
        return this.fTextStore.get(offset, length);
    }

    @Override
    public int getLength() {
        return this.fTextStore.getLength();
    }

    @Override
    public void replace(int offset, int length, String text) {
        if (this.fTextStore != this.fModifiableTextStore) {
            String content = this.fTextStore.get(0, this.fTextStore.getLength());
            this.fTextStore = this.fModifiableTextStore;
            this.fTextStore.set(content);
        }
        this.fTextStore.replace(offset, length, text);
    }

    @Override
    public void set(String text) {
        this.fTextStore = new StringTextStore(text);
        this.fModifiableTextStore.set("");
    }

    private static class StringTextStore
    implements ITextStore {
        private String fText = "";

        StringTextStore() {
        }

        StringTextStore(String text) {
            this.set(text);
        }

        @Override
        public char get(int offset) {
            return this.fText.charAt(offset);
        }

        @Override
        public String get(int offset, int length) {
            return this.fText.substring(offset, offset + length);
        }

        @Override
        public int getLength() {
            return this.fText.length();
        }

        @Override
        public void replace(int offset, int length, String text) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(String text) {
            this.fText = text != null ? text : "";
        }
    }
}

