/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.ASN1VisibleString;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERVisibleString;

public class DisplayText
extends ASN1Object
implements ASN1Choice {
    public static final int CONTENT_TYPE_IA5STRING = 0;
    public static final int CONTENT_TYPE_BMPSTRING = 1;
    public static final int CONTENT_TYPE_UTF8STRING = 2;
    public static final int CONTENT_TYPE_VISIBLESTRING = 3;
    public static final int DISPLAY_TEXT_MAXIMUM_SIZE = 200;
    int contentType;
    ASN1String contents;

    public DisplayText(int type, String text) {
        if (text.length() > 200) {
            text = text.substring(0, 200);
        }
        this.contentType = type;
        switch (type) {
            case 0: {
                this.contents = new DERIA5String(text);
                break;
            }
            case 2: {
                this.contents = new DERUTF8String(text);
                break;
            }
            case 3: {
                this.contents = new DERVisibleString(text);
                break;
            }
            case 1: {
                this.contents = new DERBMPString(text);
                break;
            }
            default: {
                this.contents = new DERUTF8String(text);
            }
        }
    }

    public DisplayText(String text) {
        if (text.length() > 200) {
            text = text.substring(0, 200);
        }
        this.contentType = 2;
        this.contents = new DERUTF8String(text);
    }

    private DisplayText(ASN1String de) {
        this.contents = de;
        if (de instanceof ASN1UTF8String) {
            this.contentType = 2;
        } else if (de instanceof ASN1BMPString) {
            this.contentType = 1;
        } else if (de instanceof ASN1IA5String) {
            this.contentType = 0;
        } else if (de instanceof ASN1VisibleString) {
            this.contentType = 3;
        } else {
            throw new IllegalArgumentException("unknown STRING type in DisplayText");
        }
    }

    public static DisplayText getInstance(Object obj) {
        if (obj instanceof ASN1String) {
            return new DisplayText((ASN1String)obj);
        }
        if (obj == null || obj instanceof DisplayText) {
            return (DisplayText)obj;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DisplayText getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("choice item must be explicitly tagged");
        }
        return DisplayText.getInstance(obj.getExplicitBaseObject());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return (ASN1Primitive)((Object)this.contents);
    }

    public String getString() {
        return this.contents.getString();
    }
}

