/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.CharArraySource;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.ReaderSource;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.io.WstxInputSource;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.Location;

public final class InputSourceFactory {
    public static ReaderSource constructEntitySource(ReaderConfig cfg, WstxInputSource parent, String entityName, InputBootstrapper bs, String pubId, SystemId sysId, int xmlVersion, Reader r) {
        ReaderSource rs = new ReaderSource(cfg, parent, entityName, pubId, sysId, r, true);
        if (bs != null) {
            rs.setInputOffsets(bs.getInputTotal(), bs.getInputRow(), -bs.getInputColumn());
        }
        return rs;
    }

    public static BranchingReaderSource constructDocumentSource(ReaderConfig cfg, InputBootstrapper bs, String pubId, SystemId sysId, Reader r, boolean realClose) {
        URL url = cfg.getBaseURL();
        if (url != null) {
            sysId = SystemId.construct(url);
        }
        BranchingReaderSource rs = new BranchingReaderSource(cfg, pubId, sysId, r, realClose);
        if (bs != null) {
            rs.setInputOffsets(bs.getInputTotal(), bs.getInputRow(), -bs.getInputColumn());
        }
        return rs;
    }

    public static WstxInputSource constructCharArraySource(WstxInputSource parent, String fromEntity, char[] text, int offset, int len, Location loc, URL src) {
        SystemId sysId = SystemId.construct(loc.getSystemId(), src);
        return new CharArraySource(parent, fromEntity, text, offset, len, loc, sysId);
    }
}

