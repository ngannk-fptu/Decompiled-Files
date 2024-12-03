/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.filter.ClientFilter;

public abstract class Filterable {
    private final ClientHandler root;
    private ClientHandler head;

    protected Filterable(ClientHandler root) {
        this.root = this.head = root;
    }

    protected Filterable(Filterable that) {
        this.root = that.root;
        this.head = that.head;
    }

    public void addFilter(ClientFilter f) {
        f.setNext(this.head);
        this.head = f;
    }

    public void removeFilter(ClientFilter f) {
        if (this.head == this.root) {
            return;
        }
        if (this.head == f) {
            this.head = f.getNext();
            return;
        }
        ClientFilter e = (ClientFilter)this.head;
        while (e.getNext() != f) {
            if (e.getNext() == this.root) {
                return;
            }
            e = (ClientFilter)e.getNext();
        }
        e.setNext(f.getNext());
    }

    @Deprecated
    public boolean isFilterPreset(ClientFilter filter) {
        return this.isFilterPresent(filter);
    }

    public boolean isFilterPresent(ClientFilter filter) {
        if (this.head == this.root) {
            return false;
        }
        if (this.head == filter) {
            return true;
        }
        ClientFilter e = (ClientFilter)this.head;
        while (e.getNext() != filter) {
            if (e.getNext() == this.root) {
                return false;
            }
            e = (ClientFilter)e.getNext();
        }
        return true;
    }

    public void removeAllFilters() {
        this.head = this.root;
    }

    public ClientHandler getHeadHandler() {
        return this.head;
    }
}

