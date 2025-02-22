/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.StringTokenizer;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.IPAddress;

public class GeneralName
extends ASN1Object
implements ASN1Choice {
    public static final int otherName = 0;
    public static final int rfc822Name = 1;
    public static final int dNSName = 2;
    public static final int x400Address = 3;
    public static final int directoryName = 4;
    public static final int ediPartyName = 5;
    public static final int uniformResourceIdentifier = 6;
    public static final int iPAddress = 7;
    public static final int registeredID = 8;
    private ASN1Encodable obj;
    private int tag;

    public GeneralName(X500Name dirName) {
        this.obj = dirName;
        this.tag = 4;
    }

    public GeneralName(int tag, ASN1Encodable name) {
        this.obj = name;
        this.tag = tag;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public GeneralName(int tag, String name) {
        this.tag = tag;
        if (tag == 1 || tag == 2 || tag == 6) {
            this.obj = new DERIA5String(name);
            return;
        } else if (tag == 8) {
            this.obj = new ASN1ObjectIdentifier(name);
            return;
        } else if (tag == 4) {
            this.obj = new X500Name(name);
            return;
        } else {
            if (tag != 7) throw new IllegalArgumentException("can't process String for tag: " + tag);
            byte[] enc = this.toGeneralNameEncoding(name);
            if (enc == null) throw new IllegalArgumentException("IP Address is invalid");
            this.obj = new DEROctetString(enc);
        }
    }

    public static GeneralName getInstance(Object obj) {
        if (obj == null || obj instanceof GeneralName) {
            return (GeneralName)obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            ASN1TaggedObject tagObj = (ASN1TaggedObject)obj;
            int tag = tagObj.getTagNo();
            switch (tag) {
                case 0: 
                case 3: 
                case 5: {
                    return new GeneralName(tag, ASN1Sequence.getInstance(tagObj, false));
                }
                case 1: 
                case 2: 
                case 6: {
                    return new GeneralName(tag, ASN1IA5String.getInstance(tagObj, false));
                }
                case 4: {
                    return new GeneralName(tag, X500Name.getInstance(tagObj, true));
                }
                case 7: {
                    return new GeneralName(tag, ASN1OctetString.getInstance(tagObj, false));
                }
                case 8: {
                    return new GeneralName(tag, ASN1ObjectIdentifier.getInstance(tagObj, false));
                }
            }
            throw new IllegalArgumentException("unknown tag: " + tag);
        }
        if (obj instanceof byte[]) {
            try {
                return GeneralName.getInstance(ASN1Primitive.fromByteArray((byte[])obj));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("unable to parse encoded general name");
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static GeneralName getInstance(ASN1TaggedObject tagObj, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("choice item must be explicitly tagged");
        }
        return GeneralName.getInstance(ASN1TaggedObject.getInstance(tagObj, true));
    }

    public int getTagNo() {
        return this.tag;
    }

    public ASN1Encodable getName() {
        return this.obj;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.tag);
        buf.append(": ");
        switch (this.tag) {
            case 1: 
            case 2: 
            case 6: {
                buf.append(ASN1IA5String.getInstance(this.obj).getString());
                break;
            }
            case 4: {
                buf.append(X500Name.getInstance(this.obj).toString());
                break;
            }
            default: {
                buf.append(this.obj.toString());
            }
        }
        return buf.toString();
    }

    private byte[] toGeneralNameEncoding(String ip) {
        if (IPAddress.isValidIPv6WithNetmask(ip) || IPAddress.isValidIPv6(ip)) {
            int slashIndex = ip.indexOf(47);
            if (slashIndex < 0) {
                byte[] addr = new byte[16];
                int[] parsedIp = this.parseIPv6(ip);
                this.copyInts(parsedIp, addr, 0);
                return addr;
            }
            byte[] addr = new byte[32];
            int[] parsedIp = this.parseIPv6(ip.substring(0, slashIndex));
            this.copyInts(parsedIp, addr, 0);
            String mask = ip.substring(slashIndex + 1);
            parsedIp = mask.indexOf(58) > 0 ? this.parseIPv6(mask) : this.parseMask(mask);
            this.copyInts(parsedIp, addr, 16);
            return addr;
        }
        if (IPAddress.isValidIPv4WithNetmask(ip) || IPAddress.isValidIPv4(ip)) {
            int slashIndex = ip.indexOf(47);
            if (slashIndex < 0) {
                byte[] addr = new byte[4];
                this.parseIPv4(ip, addr, 0);
                return addr;
            }
            byte[] addr = new byte[8];
            this.parseIPv4(ip.substring(0, slashIndex), addr, 0);
            String mask = ip.substring(slashIndex + 1);
            if (mask.indexOf(46) > 0) {
                this.parseIPv4(mask, addr, 4);
            } else {
                this.parseIPv4Mask(mask, addr, 4);
            }
            return addr;
        }
        return null;
    }

    private void parseIPv4Mask(String mask, byte[] addr, int offset) {
        int maskVal = Integer.parseInt(mask);
        for (int i = 0; i != maskVal; ++i) {
            int n = i / 8 + offset;
            addr[n] = (byte)(addr[n] | 1 << 7 - i % 8);
        }
    }

    private void parseIPv4(String ip, byte[] addr, int offset) {
        StringTokenizer sTok = new StringTokenizer(ip, "./");
        int index = 0;
        while (sTok.hasMoreTokens()) {
            addr[offset + index++] = (byte)Integer.parseInt(sTok.nextToken());
        }
    }

    private int[] parseMask(String mask) {
        int[] res = new int[8];
        int maskVal = Integer.parseInt(mask);
        for (int i = 0; i != maskVal; ++i) {
            int n = i / 16;
            res[n] = res[n] | 1 << 15 - i % 16;
        }
        return res;
    }

    private void copyInts(int[] parsedIp, byte[] addr, int offSet) {
        for (int i = 0; i != parsedIp.length; ++i) {
            addr[i * 2 + offSet] = (byte)(parsedIp[i] >> 8);
            addr[i * 2 + 1 + offSet] = (byte)parsedIp[i];
        }
    }

    private int[] parseIPv6(String ip) {
        StringTokenizer sTok = new StringTokenizer(ip, ":", true);
        int index = 0;
        int[] val = new int[8];
        if (ip.charAt(0) == ':' && ip.charAt(1) == ':') {
            sTok.nextToken();
        }
        int doubleColon = -1;
        while (sTok.hasMoreTokens()) {
            String e = sTok.nextToken();
            if (e.equals(":")) {
                doubleColon = index;
                val[index++] = 0;
                continue;
            }
            if (e.indexOf(46) < 0) {
                val[index++] = Integer.parseInt(e, 16);
                if (!sTok.hasMoreTokens()) continue;
                sTok.nextToken();
                continue;
            }
            StringTokenizer eTok = new StringTokenizer(e, ".");
            val[index++] = Integer.parseInt(eTok.nextToken()) << 8 | Integer.parseInt(eTok.nextToken());
            val[index++] = Integer.parseInt(eTok.nextToken()) << 8 | Integer.parseInt(eTok.nextToken());
        }
        if (index != val.length) {
            System.arraycopy(val, doubleColon, val, val.length - (index - doubleColon), index - doubleColon);
            for (int i = doubleColon; i != val.length - (index - doubleColon); ++i) {
                val[i] = 0;
            }
        }
        return val;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        boolean explicit = this.tag == 4;
        return new DERTaggedObject(explicit, this.tag, this.obj);
    }
}

