/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.model.FieldsTables;
import org.apache.poi.hwpf.model.PlexOfField;
import org.apache.poi.hwpf.usermodel.Field;
import org.apache.poi.hwpf.usermodel.FieldImpl;
import org.apache.poi.hwpf.usermodel.Fields;
import org.apache.poi.util.Internal;

@Internal
public class FieldsImpl
implements Fields {
    private Map<FieldsDocumentPart, Map<Integer, FieldImpl>> _fieldsByOffset;
    private PlexOfFieldComparator comparator = new PlexOfFieldComparator();

    private static int binarySearch(List<PlexOfField> list, int startIndex, int endIndex, int requiredStartOffset) {
        FieldsImpl.checkIndexForBinarySearch(list.size(), startIndex, endIndex);
        int low = startIndex;
        int mid = -1;
        int high = endIndex - 1;
        while (low <= high) {
            mid = low + high >>> 1;
            int midStart = list.get(mid).getFcStart();
            if (midStart == requiredStartOffset) {
                return mid;
            }
            if (midStart < requiredStartOffset) {
                low = mid + 1;
                continue;
            }
            high = mid - 1;
        }
        if (mid < 0) {
            int insertPoint = endIndex;
            for (int index = startIndex; index < endIndex; ++index) {
                if (requiredStartOffset >= list.get(index).getFcStart()) continue;
                insertPoint = index;
            }
            return -insertPoint - 1;
        }
        return -mid - 1;
    }

    private static void checkIndexForBinarySearch(int length, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        if (length < end || 0 > start) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public FieldsImpl(FieldsTables fieldsTables) {
        this._fieldsByOffset = new HashMap<FieldsDocumentPart, Map<Integer, FieldImpl>>(FieldsDocumentPart.values().length);
        for (FieldsDocumentPart part : FieldsDocumentPart.values()) {
            ArrayList<PlexOfField> plexOfCps = fieldsTables.getFieldsPLCF(part);
            this._fieldsByOffset.put(part, this.parseFieldStructure(plexOfCps));
        }
    }

    @Override
    public Collection<Field> getFields(FieldsDocumentPart part) {
        Map<Integer, FieldImpl> map = this._fieldsByOffset.get((Object)part);
        if (map == null || map.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(map.values());
    }

    @Override
    public FieldImpl getFieldByStartOffset(FieldsDocumentPart documentPart, int offset) {
        Map<Integer, FieldImpl> map = this._fieldsByOffset.get((Object)documentPart);
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map.get(offset);
    }

    private Map<Integer, FieldImpl> parseFieldStructure(List<PlexOfField> plexOfFields) {
        if (plexOfFields == null || plexOfFields.isEmpty()) {
            return new HashMap<Integer, FieldImpl>();
        }
        plexOfFields.sort(this.comparator);
        ArrayList<FieldImpl> fields = new ArrayList<FieldImpl>(plexOfFields.size() / 3 + 1);
        this.parseFieldStructureImpl(plexOfFields, 0, plexOfFields.size(), fields);
        HashMap<Integer, FieldImpl> result = new HashMap<Integer, FieldImpl>(fields.size());
        for (FieldImpl field : fields) {
            result.put(field.getFieldStartOffset(), field);
        }
        return result;
    }

    private void parseFieldStructureImpl(List<PlexOfField> plexOfFields, int startOffsetInclusive, int endOffsetExclusive, List<FieldImpl> result) {
        int next = startOffsetInclusive;
        block4: while (next < endOffsetExclusive) {
            PlexOfField startPlexOfField = plexOfFields.get(next);
            if (startPlexOfField.getFld().getBoundaryType() != 19) {
                ++next;
                continue;
            }
            int nextNodePositionInList = FieldsImpl.binarySearch(plexOfFields, next + 1, endOffsetExclusive, startPlexOfField.getFcEnd());
            if (nextNodePositionInList < 0) {
                ++next;
                continue;
            }
            PlexOfField nextPlexOfField = plexOfFields.get(nextNodePositionInList);
            switch (nextPlexOfField.getFld().getBoundaryType()) {
                case 20: {
                    int endNodePositionInList = FieldsImpl.binarySearch(plexOfFields, nextNodePositionInList, endOffsetExclusive, nextPlexOfField.getFcEnd());
                    if (endNodePositionInList < 0) {
                        ++next;
                        continue block4;
                    }
                    PlexOfField endPlexOfField = plexOfFields.get(endNodePositionInList);
                    if (endPlexOfField.getFld().getBoundaryType() != 21) {
                        ++next;
                        continue block4;
                    }
                    FieldImpl field = new FieldImpl(startPlexOfField, nextPlexOfField, endPlexOfField);
                    result.add(field);
                    if (startPlexOfField.getFcStart() + 1 < nextPlexOfField.getFcStart() - 1) {
                        this.parseFieldStructureImpl(plexOfFields, next + 1, nextNodePositionInList, result);
                    }
                    if (nextPlexOfField.getFcStart() + 1 < endPlexOfField.getFcStart() - 1) {
                        this.parseFieldStructureImpl(plexOfFields, nextNodePositionInList + 1, endNodePositionInList, result);
                    }
                    next = endNodePositionInList + 1;
                    continue block4;
                }
                case 21: {
                    FieldImpl field = new FieldImpl(startPlexOfField, null, nextPlexOfField);
                    result.add(field);
                    if (startPlexOfField.getFcStart() + 1 < nextPlexOfField.getFcStart() - 1) {
                        this.parseFieldStructureImpl(plexOfFields, next + 1, nextNodePositionInList, result);
                    }
                    next = nextNodePositionInList + 1;
                    continue block4;
                }
            }
            ++next;
        }
    }

    private static final class PlexOfFieldComparator
    implements Comparator<PlexOfField>,
    Serializable {
        private PlexOfFieldComparator() {
        }

        @Override
        public int compare(PlexOfField o1, PlexOfField o2) {
            int thisVal = o1.getFcStart();
            int anotherVal = o2.getFcStart();
            return Integer.compare(thisVal, anotherVal);
        }
    }
}

