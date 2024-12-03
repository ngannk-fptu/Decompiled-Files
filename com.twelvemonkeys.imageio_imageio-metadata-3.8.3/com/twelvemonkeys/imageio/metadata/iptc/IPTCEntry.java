/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.iptc;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;

class IPTCEntry
extends AbstractEntry {
    public IPTCEntry(int n, Object object) {
        super(n, object);
    }

    @Override
    public String getFieldName() {
        switch ((Integer)this.getIdentifier()) {
            case 512: {
                return "RecordVersion";
            }
            case 537: {
                return "Keywords";
            }
            case 552: {
                return "Instructions";
            }
            case 574: {
                return "DigitalCreationDate";
            }
            case 575: {
                return "DigitalCreationTime";
            }
            case 567: {
                return "DateCreated";
            }
            case 572: {
                return "TimeCreated";
            }
            case 597: {
                return "ByLineTitle";
            }
            case 602: {
                return "City";
            }
            case 604: {
                return "SubLocation";
            }
            case 607: {
                return "StateProvince";
            }
            case 612: {
                return "CountryCode";
            }
            case 613: {
                return "Country";
            }
            case 627: {
                return "Source";
            }
            case 632: {
                return "Caption";
            }
            case 628: {
                return "CopyrightNotice";
            }
            case 592: {
                return "ByLine";
            }
        }
        return null;
    }

    @Override
    protected String getNativeIdentifier() {
        int n = (Integer)this.getIdentifier();
        return String.format("%d:%02d", n >> 8, n & 0xFF);
    }
}

