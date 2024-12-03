/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.sprm.SprmIterator;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.hwpf.sprm.SprmUncompressor;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.SectionProperties;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.Internal;

@Internal
public final class SectionSprmUncompressor
extends SprmUncompressor {
    private static final Logger LOG = LogManager.getLogger(SectionSprmUncompressor.class);

    public static SectionProperties uncompressSEP(byte[] grpprl, int offset) {
        SectionProperties newProperties = new SectionProperties();
        SprmIterator sprmIt = new SprmIterator(grpprl, offset);
        while (sprmIt.hasNext()) {
            SprmOperation sprm = sprmIt.next();
            SectionSprmUncompressor.unCompressSEPOperation(newProperties, sprm);
        }
        return newProperties;
    }

    static void unCompressSEPOperation(SectionProperties newSEP, SprmOperation sprm) {
        int operation = sprm.getOperation();
        switch (operation) {
            case 0: {
                newSEP.setCnsPgn((byte)sprm.getOperand());
                break;
            }
            case 1: {
                newSEP.setIHeadingPgn((byte)sprm.getOperand());
                break;
            }
            case 2: {
                byte[] buf = Arrays.copyOfRange(sprm.getGrpprl(), sprm.getGrpprlOffset(), sprm.getGrpprlOffset() + (sprm.size() - 3));
                newSEP.setOlstAnm(buf);
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                newSEP.setFEvenlySpaced(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 6: {
                newSEP.setFUnlocked(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 7: {
                newSEP.setDmBinFirst((short)sprm.getOperand());
                break;
            }
            case 8: {
                newSEP.setDmBinOther((short)sprm.getOperand());
                break;
            }
            case 9: {
                newSEP.setBkc((byte)sprm.getOperand());
                break;
            }
            case 10: {
                newSEP.setFTitlePage(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 11: {
                newSEP.setCcolM1((short)sprm.getOperand());
                break;
            }
            case 12: {
                newSEP.setDxaColumns(sprm.getOperand());
                break;
            }
            case 13: {
                newSEP.setFAutoPgn(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 14: {
                newSEP.setNfcPgn((byte)sprm.getOperand());
                break;
            }
            case 15: {
                newSEP.setDyaPgn((short)sprm.getOperand());
                break;
            }
            case 16: {
                newSEP.setDxaPgn((short)sprm.getOperand());
                break;
            }
            case 17: {
                newSEP.setFPgnRestart(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 18: {
                newSEP.setFEndNote(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 19: {
                newSEP.setLnc((byte)sprm.getOperand());
                break;
            }
            case 20: {
                newSEP.setGrpfIhdt((byte)sprm.getOperand());
                break;
            }
            case 21: {
                newSEP.setNLnnMod((short)sprm.getOperand());
                break;
            }
            case 22: {
                newSEP.setDxaLnn(sprm.getOperand());
                break;
            }
            case 23: {
                newSEP.setDyaHdrTop(sprm.getOperand());
                break;
            }
            case 24: {
                newSEP.setDyaHdrBottom(sprm.getOperand());
                break;
            }
            case 25: {
                newSEP.setFLBetween(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 26: {
                newSEP.setVjc((byte)sprm.getOperand());
                break;
            }
            case 27: {
                newSEP.setLnnMin((short)sprm.getOperand());
                break;
            }
            case 28: {
                newSEP.setPgnStart((short)sprm.getOperand());
                break;
            }
            case 29: {
                newSEP.setDmOrientPage(sprm.getOperand() != 0);
                break;
            }
            case 30: {
                break;
            }
            case 31: {
                newSEP.setXaPage(sprm.getOperand());
                break;
            }
            case 32: {
                newSEP.setYaPage(sprm.getOperand());
                break;
            }
            case 33: {
                newSEP.setDxaLeft(sprm.getOperand());
                break;
            }
            case 34: {
                newSEP.setDxaRight(sprm.getOperand());
                break;
            }
            case 35: {
                newSEP.setDyaTop(sprm.getOperand());
                break;
            }
            case 36: {
                newSEP.setDyaBottom(sprm.getOperand());
                break;
            }
            case 37: {
                newSEP.setDzaGutter(sprm.getOperand());
                break;
            }
            case 38: {
                newSEP.setDmPaperReq((short)sprm.getOperand());
                break;
            }
            case 39: {
                newSEP.setFPropMark(SectionSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 40: {
                break;
            }
            case 41: {
                break;
            }
            case 42: {
                break;
            }
            case 43: {
                newSEP.setBrcTop(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 44: {
                newSEP.setBrcLeft(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 45: {
                newSEP.setBrcBottom(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 46: {
                newSEP.setBrcRight(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 47: {
                newSEP.setPgbProp(sprm.getOperand());
                break;
            }
            case 48: {
                newSEP.setDxtCharSpace(sprm.getOperand());
                break;
            }
            case 49: {
                newSEP.setDyaLinePitch(sprm.getOperand());
                break;
            }
            case 51: {
                newSEP.setWTextFlow((short)sprm.getOperand());
                break;
            }
            case 60: {
                newSEP.setRncFtn((short)sprm.getOperand());
                break;
            }
            case 62: {
                newSEP.setRncEdn((short)sprm.getOperand());
                break;
            }
            case 63: {
                newSEP.setNFtn(sprm.getOperand());
                break;
            }
            case 64: {
                newSEP.setNfcFtnRef(sprm.getOperand());
                break;
            }
            case 65: {
                newSEP.setNEdn(sprm.getOperand());
                break;
            }
            case 66: {
                newSEP.setNfcEdnRef(sprm.getOperand());
                break;
            }
            default: {
                LOG.atInfo().log("Unsupported Sprm operation: {} ({})", (Object)Unbox.box(operation), (Object)HexDump.byteToHex(operation));
            }
        }
    }
}

