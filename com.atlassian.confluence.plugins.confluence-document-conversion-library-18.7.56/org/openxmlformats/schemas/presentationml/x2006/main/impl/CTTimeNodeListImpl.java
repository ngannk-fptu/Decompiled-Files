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
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
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
import org.openxmlformats.schemas.presentationml.x2006.main.CTTimeNodeList;

public class CTTimeNodeListImpl
extends XmlComplexContentImpl
implements CTTimeNodeList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "par"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "seq"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "excl"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "anim"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "animClr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "animEffect"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "animMotion"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "animRot"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "animScale"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cmd"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "set"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "audio"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "video")};

    public CTTimeNodeListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLTimeNodeParallel> getParList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLTimeNodeParallel>(this::getParArray, this::setParArray, this::insertNewPar, this::removePar, this::sizeOfParArray);
        }
    }

    @Override
    public CTTLTimeNodeParallel[] getParArray() {
        return (CTTLTimeNodeParallel[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTTLTimeNodeParallel[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeParallel getParArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeParallel target = null;
            target = (CTTLTimeNodeParallel)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfParArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setParArray(CTTLTimeNodeParallel[] parArray) {
        this.check_orphaned();
        this.arraySetterHelper(parArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setParArray(int i, CTTLTimeNodeParallel par) {
        this.generatedSetterHelperImpl(par, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeParallel insertNewPar(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeParallel target = null;
            target = (CTTLTimeNodeParallel)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeParallel addNewPar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeParallel target = null;
            target = (CTTLTimeNodeParallel)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePar(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLTimeNodeSequence> getSeqList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLTimeNodeSequence>(this::getSeqArray, this::setSeqArray, this::insertNewSeq, this::removeSeq, this::sizeOfSeqArray);
        }
    }

    @Override
    public CTTLTimeNodeSequence[] getSeqArray() {
        return (CTTLTimeNodeSequence[])this.getXmlObjectArray(PROPERTY_QNAME[1], (XmlObject[])new CTTLTimeNodeSequence[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeSequence getSeqArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeSequence target = null;
            target = (CTTLTimeNodeSequence)this.get_store().find_element_user(PROPERTY_QNAME[1], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSeqArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setSeqArray(CTTLTimeNodeSequence[] seqArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])seqArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setSeqArray(int i, CTTLTimeNodeSequence seq) {
        this.generatedSetterHelperImpl((XmlObject)seq, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeSequence insertNewSeq(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeSequence target = null;
            target = (CTTLTimeNodeSequence)this.get_store().insert_element_user(PROPERTY_QNAME[1], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeSequence addNewSeq() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeSequence target = null;
            target = (CTTLTimeNodeSequence)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSeq(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLTimeNodeExclusive> getExclList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLTimeNodeExclusive>(this::getExclArray, this::setExclArray, this::insertNewExcl, this::removeExcl, this::sizeOfExclArray);
        }
    }

    @Override
    public CTTLTimeNodeExclusive[] getExclArray() {
        return (CTTLTimeNodeExclusive[])this.getXmlObjectArray(PROPERTY_QNAME[2], (XmlObject[])new CTTLTimeNodeExclusive[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeExclusive getExclArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeExclusive target = null;
            target = (CTTLTimeNodeExclusive)this.get_store().find_element_user(PROPERTY_QNAME[2], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfExclArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setExclArray(CTTLTimeNodeExclusive[] exclArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])exclArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setExclArray(int i, CTTLTimeNodeExclusive excl) {
        this.generatedSetterHelperImpl((XmlObject)excl, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeExclusive insertNewExcl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeExclusive target = null;
            target = (CTTLTimeNodeExclusive)this.get_store().insert_element_user(PROPERTY_QNAME[2], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeNodeExclusive addNewExcl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeNodeExclusive target = null;
            target = (CTTLTimeNodeExclusive)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExcl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLAnimateBehavior> getAnimList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLAnimateBehavior>(this::getAnimArray, this::setAnimArray, this::insertNewAnim, this::removeAnim, this::sizeOfAnimArray);
        }
    }

    @Override
    public CTTLAnimateBehavior[] getAnimArray() {
        return (CTTLAnimateBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[3], (XmlObject[])new CTTLAnimateBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateBehavior getAnimArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateBehavior target = null;
            target = (CTTLAnimateBehavior)this.get_store().find_element_user(PROPERTY_QNAME[3], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAnimArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setAnimArray(CTTLAnimateBehavior[] animArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])animArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setAnimArray(int i, CTTLAnimateBehavior anim) {
        this.generatedSetterHelperImpl((XmlObject)anim, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateBehavior insertNewAnim(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateBehavior target = null;
            target = (CTTLAnimateBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[3], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateBehavior addNewAnim() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateBehavior target = null;
            target = (CTTLAnimateBehavior)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnim(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLAnimateColorBehavior> getAnimClrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLAnimateColorBehavior>(this::getAnimClrArray, this::setAnimClrArray, this::insertNewAnimClr, this::removeAnimClr, this::sizeOfAnimClrArray);
        }
    }

    @Override
    public CTTLAnimateColorBehavior[] getAnimClrArray() {
        return (CTTLAnimateColorBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[4], (XmlObject[])new CTTLAnimateColorBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateColorBehavior getAnimClrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateColorBehavior target = null;
            target = (CTTLAnimateColorBehavior)this.get_store().find_element_user(PROPERTY_QNAME[4], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAnimClrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setAnimClrArray(CTTLAnimateColorBehavior[] animClrArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])animClrArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setAnimClrArray(int i, CTTLAnimateColorBehavior animClr) {
        this.generatedSetterHelperImpl((XmlObject)animClr, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateColorBehavior insertNewAnimClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateColorBehavior target = null;
            target = (CTTLAnimateColorBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[4], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateColorBehavior addNewAnimClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateColorBehavior target = null;
            target = (CTTLAnimateColorBehavior)this.get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnimClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLAnimateEffectBehavior> getAnimEffectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLAnimateEffectBehavior>(this::getAnimEffectArray, this::setAnimEffectArray, this::insertNewAnimEffect, this::removeAnimEffect, this::sizeOfAnimEffectArray);
        }
    }

    @Override
    public CTTLAnimateEffectBehavior[] getAnimEffectArray() {
        return (CTTLAnimateEffectBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[5], (XmlObject[])new CTTLAnimateEffectBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateEffectBehavior getAnimEffectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateEffectBehavior target = null;
            target = (CTTLAnimateEffectBehavior)this.get_store().find_element_user(PROPERTY_QNAME[5], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAnimEffectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setAnimEffectArray(CTTLAnimateEffectBehavior[] animEffectArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])animEffectArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setAnimEffectArray(int i, CTTLAnimateEffectBehavior animEffect) {
        this.generatedSetterHelperImpl((XmlObject)animEffect, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateEffectBehavior insertNewAnimEffect(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateEffectBehavior target = null;
            target = (CTTLAnimateEffectBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[5], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateEffectBehavior addNewAnimEffect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateEffectBehavior target = null;
            target = (CTTLAnimateEffectBehavior)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnimEffect(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLAnimateMotionBehavior> getAnimMotionList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLAnimateMotionBehavior>(this::getAnimMotionArray, this::setAnimMotionArray, this::insertNewAnimMotion, this::removeAnimMotion, this::sizeOfAnimMotionArray);
        }
    }

    @Override
    public CTTLAnimateMotionBehavior[] getAnimMotionArray() {
        return (CTTLAnimateMotionBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[6], (XmlObject[])new CTTLAnimateMotionBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateMotionBehavior getAnimMotionArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateMotionBehavior target = null;
            target = (CTTLAnimateMotionBehavior)this.get_store().find_element_user(PROPERTY_QNAME[6], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAnimMotionArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setAnimMotionArray(CTTLAnimateMotionBehavior[] animMotionArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])animMotionArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setAnimMotionArray(int i, CTTLAnimateMotionBehavior animMotion) {
        this.generatedSetterHelperImpl((XmlObject)animMotion, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateMotionBehavior insertNewAnimMotion(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateMotionBehavior target = null;
            target = (CTTLAnimateMotionBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[6], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateMotionBehavior addNewAnimMotion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateMotionBehavior target = null;
            target = (CTTLAnimateMotionBehavior)this.get_store().add_element_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnimMotion(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLAnimateRotationBehavior> getAnimRotList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLAnimateRotationBehavior>(this::getAnimRotArray, this::setAnimRotArray, this::insertNewAnimRot, this::removeAnimRot, this::sizeOfAnimRotArray);
        }
    }

    @Override
    public CTTLAnimateRotationBehavior[] getAnimRotArray() {
        return (CTTLAnimateRotationBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[7], (XmlObject[])new CTTLAnimateRotationBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateRotationBehavior getAnimRotArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateRotationBehavior target = null;
            target = (CTTLAnimateRotationBehavior)this.get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAnimRotArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setAnimRotArray(CTTLAnimateRotationBehavior[] animRotArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])animRotArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setAnimRotArray(int i, CTTLAnimateRotationBehavior animRot) {
        this.generatedSetterHelperImpl((XmlObject)animRot, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateRotationBehavior insertNewAnimRot(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateRotationBehavior target = null;
            target = (CTTLAnimateRotationBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[7], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateRotationBehavior addNewAnimRot() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateRotationBehavior target = null;
            target = (CTTLAnimateRotationBehavior)this.get_store().add_element_user(PROPERTY_QNAME[7]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnimRot(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLAnimateScaleBehavior> getAnimScaleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLAnimateScaleBehavior>(this::getAnimScaleArray, this::setAnimScaleArray, this::insertNewAnimScale, this::removeAnimScale, this::sizeOfAnimScaleArray);
        }
    }

    @Override
    public CTTLAnimateScaleBehavior[] getAnimScaleArray() {
        return (CTTLAnimateScaleBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[8], (XmlObject[])new CTTLAnimateScaleBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateScaleBehavior getAnimScaleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateScaleBehavior target = null;
            target = (CTTLAnimateScaleBehavior)this.get_store().find_element_user(PROPERTY_QNAME[8], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAnimScaleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setAnimScaleArray(CTTLAnimateScaleBehavior[] animScaleArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])animScaleArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setAnimScaleArray(int i, CTTLAnimateScaleBehavior animScale) {
        this.generatedSetterHelperImpl((XmlObject)animScale, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateScaleBehavior insertNewAnimScale(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateScaleBehavior target = null;
            target = (CTTLAnimateScaleBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[8], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLAnimateScaleBehavior addNewAnimScale() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLAnimateScaleBehavior target = null;
            target = (CTTLAnimateScaleBehavior)this.get_store().add_element_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnimScale(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLCommandBehavior> getCmdList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLCommandBehavior>(this::getCmdArray, this::setCmdArray, this::insertNewCmd, this::removeCmd, this::sizeOfCmdArray);
        }
    }

    @Override
    public CTTLCommandBehavior[] getCmdArray() {
        return (CTTLCommandBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[9], (XmlObject[])new CTTLCommandBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLCommandBehavior getCmdArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLCommandBehavior target = null;
            target = (CTTLCommandBehavior)this.get_store().find_element_user(PROPERTY_QNAME[9], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfCmdArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setCmdArray(CTTLCommandBehavior[] cmdArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])cmdArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setCmdArray(int i, CTTLCommandBehavior cmd) {
        this.generatedSetterHelperImpl((XmlObject)cmd, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLCommandBehavior insertNewCmd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLCommandBehavior target = null;
            target = (CTTLCommandBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[9], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLCommandBehavior addNewCmd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLCommandBehavior target = null;
            target = (CTTLCommandBehavior)this.get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCmd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLSetBehavior> getSetList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLSetBehavior>(this::getSetArray, this::setSetArray, this::insertNewSet, this::removeSet, this::sizeOfSetArray);
        }
    }

    @Override
    public CTTLSetBehavior[] getSetArray() {
        return (CTTLSetBehavior[])this.getXmlObjectArray(PROPERTY_QNAME[10], (XmlObject[])new CTTLSetBehavior[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLSetBehavior getSetArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLSetBehavior target = null;
            target = (CTTLSetBehavior)this.get_store().find_element_user(PROPERTY_QNAME[10], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSetArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setSetArray(CTTLSetBehavior[] setArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])setArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setSetArray(int i, CTTLSetBehavior set) {
        this.generatedSetterHelperImpl((XmlObject)set, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLSetBehavior insertNewSet(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLSetBehavior target = null;
            target = (CTTLSetBehavior)this.get_store().insert_element_user(PROPERTY_QNAME[10], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLSetBehavior addNewSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLSetBehavior target = null;
            target = (CTTLSetBehavior)this.get_store().add_element_user(PROPERTY_QNAME[10]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSet(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLMediaNodeAudio> getAudioList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLMediaNodeAudio>(this::getAudioArray, this::setAudioArray, this::insertNewAudio, this::removeAudio, this::sizeOfAudioArray);
        }
    }

    @Override
    public CTTLMediaNodeAudio[] getAudioArray() {
        return (CTTLMediaNodeAudio[])this.getXmlObjectArray(PROPERTY_QNAME[11], (XmlObject[])new CTTLMediaNodeAudio[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLMediaNodeAudio getAudioArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLMediaNodeAudio target = null;
            target = (CTTLMediaNodeAudio)this.get_store().find_element_user(PROPERTY_QNAME[11], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAudioArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setAudioArray(CTTLMediaNodeAudio[] audioArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])audioArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setAudioArray(int i, CTTLMediaNodeAudio audio) {
        this.generatedSetterHelperImpl((XmlObject)audio, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLMediaNodeAudio insertNewAudio(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLMediaNodeAudio target = null;
            target = (CTTLMediaNodeAudio)this.get_store().insert_element_user(PROPERTY_QNAME[11], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLMediaNodeAudio addNewAudio() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLMediaNodeAudio target = null;
            target = (CTTLMediaNodeAudio)this.get_store().add_element_user(PROPERTY_QNAME[11]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAudio(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLMediaNodeVideo> getVideoList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLMediaNodeVideo>(this::getVideoArray, this::setVideoArray, this::insertNewVideo, this::removeVideo, this::sizeOfVideoArray);
        }
    }

    @Override
    public CTTLMediaNodeVideo[] getVideoArray() {
        return (CTTLMediaNodeVideo[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTTLMediaNodeVideo[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLMediaNodeVideo getVideoArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLMediaNodeVideo target = null;
            target = (CTTLMediaNodeVideo)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfVideoArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setVideoArray(CTTLMediaNodeVideo[] videoArray) {
        this.check_orphaned();
        this.arraySetterHelper(videoArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setVideoArray(int i, CTTLMediaNodeVideo video) {
        this.generatedSetterHelperImpl(video, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLMediaNodeVideo insertNewVideo(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLMediaNodeVideo target = null;
            target = (CTTLMediaNodeVideo)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLMediaNodeVideo addNewVideo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLMediaNodeVideo target = null;
            target = (CTTLMediaNodeVideo)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeVideo(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
        }
    }
}

