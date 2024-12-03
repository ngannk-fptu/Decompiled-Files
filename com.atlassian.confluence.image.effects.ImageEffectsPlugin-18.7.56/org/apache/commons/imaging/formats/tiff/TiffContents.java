/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffHeader;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.internal.Debug;

public class TiffContents {
    public final TiffHeader header;
    public final List<TiffDirectory> directories;
    public final List<TiffField> tiffFields;

    public TiffContents(TiffHeader tiffHeader, List<TiffDirectory> directories, List<TiffField> tiffFields) {
        this.header = tiffHeader;
        this.directories = Collections.unmodifiableList(directories);
        this.tiffFields = Collections.unmodifiableList(tiffFields);
    }

    public List<TiffElement> getElements() throws ImageReadException {
        ArrayList<TiffElement> result = new ArrayList<TiffElement>();
        result.add(this.header);
        for (TiffDirectory directory : this.directories) {
            result.add(directory);
            List<TiffField> fields = directory.entries;
            for (TiffField field : fields) {
                TiffElement oversizeValue = field.getOversizeValueElement();
                if (null == oversizeValue) continue;
                result.add(oversizeValue);
            }
            if (directory.hasTiffImageData()) {
                result.addAll(directory.getTiffRawImageDataElements());
            }
            if (!directory.hasJpegImageData()) continue;
            result.add(directory.getJpegRawImageDataElement());
        }
        return result;
    }

    public TiffField findField(TagInfo tag) throws ImageReadException {
        for (TiffDirectory directory : this.directories) {
            TiffField field = directory.findField(tag);
            if (null == field) continue;
            return field;
        }
        return null;
    }

    public void dissect() throws ImageReadException {
        List<TiffElement> elements = this.getElements();
        Collections.sort(elements, TiffElement.COMPARATOR);
        long lastEnd = 0L;
        for (TiffElement element : elements) {
            if (element.offset > lastEnd) {
                Debug.debug("\tgap: " + (element.offset - lastEnd));
            }
            if (element.offset < lastEnd) {
                Debug.debug("\toverlap");
            }
            Debug.debug("element, start: " + element.offset + ", length: " + element.length + ", end: " + (element.offset + (long)element.length) + ": " + element.getElementDescription());
            String verbosity = element.getElementDescription();
            if (null != verbosity) {
                Debug.debug(verbosity);
            }
            lastEnd = element.offset + (long)element.length;
        }
        Debug.debug("end: " + lastEnd);
        Debug.debug();
    }
}

