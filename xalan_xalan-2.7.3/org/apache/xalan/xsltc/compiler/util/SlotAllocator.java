/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.Type;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

final class SlotAllocator {
    private int _firstAvailableSlot;
    private int _size = 8;
    private int _free = 0;
    private int[] _slotsTaken = new int[this._size];

    SlotAllocator() {
    }

    public void initialize(LocalVariableGen[] vars) {
        int length = vars.length;
        int slot = 0;
        for (int i = 0; i < length; ++i) {
            int size = vars[i].getType().getSize();
            int index = vars[i].getIndex();
            slot = Math.max(slot, index + size);
        }
        this._firstAvailableSlot = slot;
    }

    public int allocateSlot(Type type) {
        int size = type.getSize();
        int limit = this._free;
        int slot = this._firstAvailableSlot;
        int where = 0;
        if (this._free + size > this._size) {
            int[] array = new int[this._size *= 2];
            for (int j = 0; j < limit; ++j) {
                array[j] = this._slotsTaken[j];
            }
            this._slotsTaken = array;
        }
        while (where < limit) {
            if (slot + size <= this._slotsTaken[where]) {
                for (int j = limit - 1; j >= where; --j) {
                    this._slotsTaken[j + size] = this._slotsTaken[j];
                }
                break;
            }
            slot = this._slotsTaken[where++] + 1;
        }
        for (int j = 0; j < size; ++j) {
            this._slotsTaken[where + j] = slot + j;
        }
        this._free += size;
        return slot;
    }

    public void releaseSlot(LocalVariableGen lvg) {
        int size = lvg.getType().getSize();
        int slot = lvg.getIndex();
        int limit = this._free;
        for (int i = 0; i < limit; ++i) {
            if (this._slotsTaken[i] != slot) continue;
            int j = i + size;
            while (j < limit) {
                this._slotsTaken[i++] = this._slotsTaken[j++];
            }
            this._free -= size;
            return;
        }
        String state = "Variable slot allocation error(size=" + size + ", slot=" + slot + ", limit=" + limit + ")";
        ErrorMsg err = new ErrorMsg("INTERNAL_ERR", state);
        throw new Error(err.toString());
    }
}

