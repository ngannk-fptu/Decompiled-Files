/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateColorBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateEffectBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateMotionBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateRotationBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateScaleBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLCommandBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLMediaNodeAudio
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLSetBehavior
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeNodeExclusive
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeNodeSequence
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateColorBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateEffectBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateMotionBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateRotationBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLAnimateScaleBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLCommandBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLMediaNodeAudio;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLMediaNodeVideo;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLSetBehavior;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeNodeExclusive;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeNodeParallel;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeNodeSequence;

public interface CTTimeNodeList
extends XmlObject {
    public static final DocumentFactory<CTTimeNodeList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttimenodelist0258type");
    public static final SchemaType type = Factory.getType();

    public List<CTTLTimeNodeParallel> getParList();

    public CTTLTimeNodeParallel[] getParArray();

    public CTTLTimeNodeParallel getParArray(int var1);

    public int sizeOfParArray();

    public void setParArray(CTTLTimeNodeParallel[] var1);

    public void setParArray(int var1, CTTLTimeNodeParallel var2);

    public CTTLTimeNodeParallel insertNewPar(int var1);

    public CTTLTimeNodeParallel addNewPar();

    public void removePar(int var1);

    public List<CTTLTimeNodeSequence> getSeqList();

    public CTTLTimeNodeSequence[] getSeqArray();

    public CTTLTimeNodeSequence getSeqArray(int var1);

    public int sizeOfSeqArray();

    public void setSeqArray(CTTLTimeNodeSequence[] var1);

    public void setSeqArray(int var1, CTTLTimeNodeSequence var2);

    public CTTLTimeNodeSequence insertNewSeq(int var1);

    public CTTLTimeNodeSequence addNewSeq();

    public void removeSeq(int var1);

    public List<CTTLTimeNodeExclusive> getExclList();

    public CTTLTimeNodeExclusive[] getExclArray();

    public CTTLTimeNodeExclusive getExclArray(int var1);

    public int sizeOfExclArray();

    public void setExclArray(CTTLTimeNodeExclusive[] var1);

    public void setExclArray(int var1, CTTLTimeNodeExclusive var2);

    public CTTLTimeNodeExclusive insertNewExcl(int var1);

    public CTTLTimeNodeExclusive addNewExcl();

    public void removeExcl(int var1);

    public List<CTTLAnimateBehavior> getAnimList();

    public CTTLAnimateBehavior[] getAnimArray();

    public CTTLAnimateBehavior getAnimArray(int var1);

    public int sizeOfAnimArray();

    public void setAnimArray(CTTLAnimateBehavior[] var1);

    public void setAnimArray(int var1, CTTLAnimateBehavior var2);

    public CTTLAnimateBehavior insertNewAnim(int var1);

    public CTTLAnimateBehavior addNewAnim();

    public void removeAnim(int var1);

    public List<CTTLAnimateColorBehavior> getAnimClrList();

    public CTTLAnimateColorBehavior[] getAnimClrArray();

    public CTTLAnimateColorBehavior getAnimClrArray(int var1);

    public int sizeOfAnimClrArray();

    public void setAnimClrArray(CTTLAnimateColorBehavior[] var1);

    public void setAnimClrArray(int var1, CTTLAnimateColorBehavior var2);

    public CTTLAnimateColorBehavior insertNewAnimClr(int var1);

    public CTTLAnimateColorBehavior addNewAnimClr();

    public void removeAnimClr(int var1);

    public List<CTTLAnimateEffectBehavior> getAnimEffectList();

    public CTTLAnimateEffectBehavior[] getAnimEffectArray();

    public CTTLAnimateEffectBehavior getAnimEffectArray(int var1);

    public int sizeOfAnimEffectArray();

    public void setAnimEffectArray(CTTLAnimateEffectBehavior[] var1);

    public void setAnimEffectArray(int var1, CTTLAnimateEffectBehavior var2);

    public CTTLAnimateEffectBehavior insertNewAnimEffect(int var1);

    public CTTLAnimateEffectBehavior addNewAnimEffect();

    public void removeAnimEffect(int var1);

    public List<CTTLAnimateMotionBehavior> getAnimMotionList();

    public CTTLAnimateMotionBehavior[] getAnimMotionArray();

    public CTTLAnimateMotionBehavior getAnimMotionArray(int var1);

    public int sizeOfAnimMotionArray();

    public void setAnimMotionArray(CTTLAnimateMotionBehavior[] var1);

    public void setAnimMotionArray(int var1, CTTLAnimateMotionBehavior var2);

    public CTTLAnimateMotionBehavior insertNewAnimMotion(int var1);

    public CTTLAnimateMotionBehavior addNewAnimMotion();

    public void removeAnimMotion(int var1);

    public List<CTTLAnimateRotationBehavior> getAnimRotList();

    public CTTLAnimateRotationBehavior[] getAnimRotArray();

    public CTTLAnimateRotationBehavior getAnimRotArray(int var1);

    public int sizeOfAnimRotArray();

    public void setAnimRotArray(CTTLAnimateRotationBehavior[] var1);

    public void setAnimRotArray(int var1, CTTLAnimateRotationBehavior var2);

    public CTTLAnimateRotationBehavior insertNewAnimRot(int var1);

    public CTTLAnimateRotationBehavior addNewAnimRot();

    public void removeAnimRot(int var1);

    public List<CTTLAnimateScaleBehavior> getAnimScaleList();

    public CTTLAnimateScaleBehavior[] getAnimScaleArray();

    public CTTLAnimateScaleBehavior getAnimScaleArray(int var1);

    public int sizeOfAnimScaleArray();

    public void setAnimScaleArray(CTTLAnimateScaleBehavior[] var1);

    public void setAnimScaleArray(int var1, CTTLAnimateScaleBehavior var2);

    public CTTLAnimateScaleBehavior insertNewAnimScale(int var1);

    public CTTLAnimateScaleBehavior addNewAnimScale();

    public void removeAnimScale(int var1);

    public List<CTTLCommandBehavior> getCmdList();

    public CTTLCommandBehavior[] getCmdArray();

    public CTTLCommandBehavior getCmdArray(int var1);

    public int sizeOfCmdArray();

    public void setCmdArray(CTTLCommandBehavior[] var1);

    public void setCmdArray(int var1, CTTLCommandBehavior var2);

    public CTTLCommandBehavior insertNewCmd(int var1);

    public CTTLCommandBehavior addNewCmd();

    public void removeCmd(int var1);

    public List<CTTLSetBehavior> getSetList();

    public CTTLSetBehavior[] getSetArray();

    public CTTLSetBehavior getSetArray(int var1);

    public int sizeOfSetArray();

    public void setSetArray(CTTLSetBehavior[] var1);

    public void setSetArray(int var1, CTTLSetBehavior var2);

    public CTTLSetBehavior insertNewSet(int var1);

    public CTTLSetBehavior addNewSet();

    public void removeSet(int var1);

    public List<CTTLMediaNodeAudio> getAudioList();

    public CTTLMediaNodeAudio[] getAudioArray();

    public CTTLMediaNodeAudio getAudioArray(int var1);

    public int sizeOfAudioArray();

    public void setAudioArray(CTTLMediaNodeAudio[] var1);

    public void setAudioArray(int var1, CTTLMediaNodeAudio var2);

    public CTTLMediaNodeAudio insertNewAudio(int var1);

    public CTTLMediaNodeAudio addNewAudio();

    public void removeAudio(int var1);

    public List<CTTLMediaNodeVideo> getVideoList();

    public CTTLMediaNodeVideo[] getVideoArray();

    public CTTLMediaNodeVideo getVideoArray(int var1);

    public int sizeOfVideoArray();

    public void setVideoArray(CTTLMediaNodeVideo[] var1);

    public void setVideoArray(int var1, CTTLMediaNodeVideo var2);

    public CTTLMediaNodeVideo insertNewVideo(int var1);

    public CTTLMediaNodeVideo addNewVideo();

    public void removeVideo(int var1);
}

