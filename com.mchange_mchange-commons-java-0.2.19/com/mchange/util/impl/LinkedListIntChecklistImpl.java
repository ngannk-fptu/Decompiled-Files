/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.IntChecklist;
import com.mchange.util.IntEnumeration;
import com.mchange.util.impl.IntEnumerationHelperBase;
import com.mchange.util.impl.LLICIRecord;
import java.util.NoSuchElementException;

public class LinkedListIntChecklistImpl
implements IntChecklist {
    private final LLICIRecord headRecord = new LLICIRecord();
    private int num_checked = 0;

    @Override
    public void check(int n) {
        LLICIRecord lLICIRecord = this.findPrevious(n);
        if (lLICIRecord.next == null || lLICIRecord.next.contained != n) {
            LLICIRecord lLICIRecord2 = new LLICIRecord();
            lLICIRecord2.next = lLICIRecord.next;
            lLICIRecord2.contained = n;
            lLICIRecord.next = lLICIRecord2;
            ++this.num_checked;
        }
    }

    @Override
    public void uncheck(int n) {
        LLICIRecord lLICIRecord = this.findPrevious(n);
        if (lLICIRecord.next != null && lLICIRecord.next.contained == n) {
            lLICIRecord.next = lLICIRecord.next.next;
            --this.num_checked;
        }
    }

    @Override
    public boolean isChecked(int n) {
        LLICIRecord lLICIRecord = this.findPrevious(n);
        return lLICIRecord.next != null && lLICIRecord.next.contained == n;
    }

    @Override
    public void clear() {
        this.headRecord.next = null;
        this.num_checked = 0;
    }

    @Override
    public int countChecked() {
        return this.num_checked;
    }

    @Override
    public int[] getChecked() {
        LLICIRecord lLICIRecord = this.headRecord;
        int[] nArray = new int[this.num_checked];
        int n = 0;
        while (lLICIRecord.next != null) {
            nArray[n++] = lLICIRecord.next.contained;
            lLICIRecord = lLICIRecord.next;
        }
        return nArray;
    }

    @Override
    public IntEnumeration checked() {
        return new IntEnumerationHelperBase(){
            LLICIRecord finger;
            {
                this.finger = LinkedListIntChecklistImpl.this.headRecord;
            }

            @Override
            public int nextInt() {
                try {
                    this.finger = this.finger.next;
                    return this.finger.contained;
                }
                catch (NullPointerException nullPointerException) {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public boolean hasMoreInts() {
                return this.finger.next != null;
            }
        };
    }

    private LLICIRecord findPrevious(int n) {
        LLICIRecord lLICIRecord = this.headRecord;
        while (lLICIRecord.next != null && lLICIRecord.next.contained < n) {
            lLICIRecord = lLICIRecord.next;
        }
        return lLICIRecord;
    }
}

