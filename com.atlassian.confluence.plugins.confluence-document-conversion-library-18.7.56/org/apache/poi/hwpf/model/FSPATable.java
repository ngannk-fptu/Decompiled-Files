/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hwpf.model.FSPA;
import org.apache.poi.hwpf.model.FSPADocumentPart;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.types.FSPAAbstractType;
import org.apache.poi.util.Internal;

@Internal
public final class FSPATable {
    private final Map<Integer, GenericPropertyNode> _byStart = new LinkedHashMap<Integer, GenericPropertyNode>();

    public FSPATable(byte[] tableStream, FileInformationBlock fib, FSPADocumentPart part) {
        int offset = fib.getFSPAPlcfOffset(part);
        int length = fib.getFSPAPlcfLength(part);
        PlexOfCps plex = new PlexOfCps(tableStream, offset, length, FSPAAbstractType.getSize());
        for (int i = 0; i < plex.length(); ++i) {
            GenericPropertyNode property = plex.getProperty(i);
            this._byStart.put(property.getStart(), property);
        }
    }

    public FSPA getFspaFromCp(int cp) {
        GenericPropertyNode propertyNode = this._byStart.get(cp);
        if (propertyNode == null) {
            return null;
        }
        return new FSPA(propertyNode.getBytes(), 0);
    }

    public List<FSPA> getShapes() {
        ArrayList<FSPA> result = new ArrayList<FSPA>(this._byStart.size());
        for (GenericPropertyNode propertyNode : this._byStart.values()) {
            result.add(new FSPA(propertyNode.getBytes(), 0));
        }
        return result;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[FPSA PLC size=").append(this._byStart.size()).append("]\n");
        for (Map.Entry<Integer, GenericPropertyNode> entry : this._byStart.entrySet()) {
            Integer i = entry.getKey();
            buf.append("  ").append(i).append(" => \t");
            try {
                FSPA fspa = this.getFspaFromCp(i);
                buf.append(fspa);
            }
            catch (Exception exc) {
                buf.append(exc.getMessage());
            }
            buf.append("\n");
        }
        buf.append("[/FSPA PLC]");
        return buf.toString();
    }
}

