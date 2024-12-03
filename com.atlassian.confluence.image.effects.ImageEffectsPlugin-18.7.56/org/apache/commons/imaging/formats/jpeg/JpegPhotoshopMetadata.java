/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg;

import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.common.GenericImageMetadata;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.internal.Debug;

public class JpegPhotoshopMetadata
extends GenericImageMetadata {
    public final PhotoshopApp13Data photoshopApp13Data;

    public JpegPhotoshopMetadata(PhotoshopApp13Data photoshopApp13Data) {
        this.photoshopApp13Data = photoshopApp13Data;
        List<IptcRecord> records = photoshopApp13Data.getRecords();
        Collections.sort(records, IptcRecord.COMPARATOR);
        for (IptcRecord element : records) {
            if (element.iptcType == IptcTypes.RECORD_VERSION) continue;
            this.add(element.getIptcTypeName(), element.getValue());
        }
    }

    public void dump() {
        Debug.debug(this.toString());
    }
}

