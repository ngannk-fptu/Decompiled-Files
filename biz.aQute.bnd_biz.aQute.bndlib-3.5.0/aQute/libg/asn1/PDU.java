/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.asn1;

import aQute.libg.asn1.Types;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.Iterator;

public class PDU
implements Types,
Iterable<PDU> {
    final int identifier;
    final Object payload;
    byte[] data = new byte[100];

    public PDU(int id, Object payload) {
        this.identifier = id;
        this.payload = payload;
    }

    public PDU(Date payload) {
        this.identifier = 23;
        this.payload = payload;
    }

    public PDU(int n) {
        this(2, n);
    }

    public PDU(boolean value) {
        this(1, value);
    }

    public PDU(String s) throws Exception {
        this(22, s);
    }

    public PDU(byte[] data) {
        this(4, data);
    }

    public PDU(BitSet bits) {
        this(3, bits);
    }

    public PDU(int top, int l1, int ... remainder) {
        this.identifier = 6;
        int[] ids = new int[remainder.length + 2];
        ids[0] = top;
        ids[1] = l1;
        System.arraycopy(remainder, 0, ids, 2, remainder.length);
        this.payload = ids;
    }

    public PDU(int tag, PDU ... set) {
        this(tag, (Object)set);
    }

    public PDU(PDU ... set) {
        this(0x20000010, set);
    }

    public int getTag() {
        return this.identifier & 0x1FFFFFFF;
    }

    int getClss() {
        return this.identifier & 0xC0000000;
    }

    public boolean isConstructed() {
        return (this.identifier & 0x20000000) != 0;
    }

    public String getString() {
        return (String)this.payload;
    }

    @Override
    public Iterator<PDU> iterator() {
        return Arrays.asList((PDU[])this.payload).iterator();
    }

    public int[] getOID() {
        assert (this.getTag() == 6);
        return (int[])this.payload;
    }

    public Boolean getBoolean() {
        assert (this.getTag() == 1);
        return (Boolean)this.payload;
    }

    public BitSet getBits() {
        assert (this.getTag() == 3);
        return (BitSet)this.payload;
    }

    public int getInt() {
        assert (this.getTag() == 2 || this.getTag() == 10);
        return (Integer)this.payload;
    }

    public byte[] getBytes() {
        return (byte[])this.payload;
    }

    public PDU[] getChildren() {
        assert (this.isConstructed());
        return (PDU[])this.payload;
    }

    public Date getDate() {
        assert (this.getTag() == 23 || this.getTag() == 24);
        return (Date)this.payload;
    }
}

