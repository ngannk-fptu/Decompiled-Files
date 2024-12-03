/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaService
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 *  org.apache.commons.collections.FastHashMap
 */
package com.octo.captcha.service;

import com.octo.captcha.Captcha;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.AbstractCaptchaService;
import com.octo.captcha.service.AbstractManageableCaptchaServiceMBean;
import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.apache.commons.collections.FastHashMap;

public abstract class AbstractManageableCaptchaService
extends AbstractCaptchaService
implements AbstractManageableCaptchaServiceMBean,
CaptchaService {
    private int minGuarantedStorageDelayInSeconds;
    private int captchaStoreMaxSize;
    private int captchaStoreSizeBeforeGarbageCollection;
    private int numberOfGeneratedCaptchas = 0;
    private int numberOfCorrectResponse = 0;
    private int numberOfUncorrectResponse = 0;
    private int numberOfGarbageCollectedCaptcha = 0;
    private FastHashMap times;
    private long oldestCaptcha = 0L;

    protected AbstractManageableCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize) {
        super(captchaStore, captchaEngine);
        this.setCaptchaStoreMaxSize(maxCaptchaStoreSize);
        this.setMinGuarantedStorageDelayInSeconds(minGuarantedStorageDelayInSeconds);
        this.setCaptchaStoreSizeBeforeGarbageCollection((int)Math.round(0.8 * (double)maxCaptchaStoreSize));
        this.times = new FastHashMap();
    }

    protected AbstractManageableCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        this(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize);
        if (maxCaptchaStoreSize < captchaStoreLoadBeforeGarbageCollection) {
            throw new IllegalArgumentException("the max store size can't be less than garbage collection size. if you want to disable garbage collection (this is not recommended) you may set them equals (max=garbage)");
        }
        this.setCaptchaStoreSizeBeforeGarbageCollection(captchaStoreLoadBeforeGarbageCollection);
    }

    @Override
    public String getCaptchaEngineClass() {
        return this.engine.getClass().getName();
    }

    @Override
    public void setCaptchaEngineClass(String theClassName) throws IllegalArgumentException {
        try {
            Object engine = Class.forName(theClassName).newInstance();
            if (!(engine instanceof CaptchaEngine)) {
                throw new IllegalArgumentException("Class is not instance of CaptchaEngine! " + theClassName);
            }
            this.engine = (CaptchaEngine)engine;
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (RuntimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public CaptchaEngine getEngine() {
        return this.engine;
    }

    @Override
    public void setCaptchaEngine(CaptchaEngine engine) {
        this.engine = engine;
    }

    @Override
    public int getMinGuarantedStorageDelayInSeconds() {
        return this.minGuarantedStorageDelayInSeconds;
    }

    @Override
    public void setMinGuarantedStorageDelayInSeconds(int theMinGuarantedStorageDelayInSeconds) {
        this.minGuarantedStorageDelayInSeconds = theMinGuarantedStorageDelayInSeconds;
    }

    @Override
    public long getNumberOfGeneratedCaptchas() {
        return this.numberOfGeneratedCaptchas;
    }

    @Override
    public long getNumberOfCorrectResponses() {
        return this.numberOfCorrectResponse;
    }

    @Override
    public long getNumberOfUncorrectResponses() {
        return this.numberOfUncorrectResponse;
    }

    @Override
    public int getCaptchaStoreSize() {
        return this.store.getSize();
    }

    @Override
    public int getNumberOfGarbageCollectableCaptchas() {
        return this.getGarbageCollectableCaptchaIds(System.currentTimeMillis()).size();
    }

    @Override
    public long getNumberOfGarbageCollectedCaptcha() {
        return this.numberOfGarbageCollectedCaptcha;
    }

    @Override
    public int getCaptchaStoreSizeBeforeGarbageCollection() {
        return this.captchaStoreSizeBeforeGarbageCollection;
    }

    @Override
    public void setCaptchaStoreSizeBeforeGarbageCollection(int captchaStoreSizeBeforeGarbageCollection) {
        if (this.captchaStoreMaxSize < captchaStoreSizeBeforeGarbageCollection) {
            throw new IllegalArgumentException("the max store size can't be less than garbage collection size. if you want to disable garbage collection (this is not recommended) you may set them equals (max=garbage)");
        }
        this.captchaStoreSizeBeforeGarbageCollection = captchaStoreSizeBeforeGarbageCollection;
    }

    @Override
    public void setCaptchaStoreMaxSize(int size) {
        if (size < this.captchaStoreSizeBeforeGarbageCollection) {
            throw new IllegalArgumentException("the max store size can't be less than garbage collection size. if you want to disable garbage collection (this is not recommended) you may set them equals (max=garbage)");
        }
        this.captchaStoreMaxSize = size;
    }

    @Override
    public int getCaptchaStoreMaxSize() {
        return this.captchaStoreMaxSize;
    }

    protected void garbageCollectCaptchaStore(Iterator garbageCollectableCaptchaIds) {
        long now = System.currentTimeMillis();
        long limit = now - (long)(1000 * this.minGuarantedStorageDelayInSeconds);
        while (garbageCollectableCaptchaIds.hasNext()) {
            String id = garbageCollectableCaptchaIds.next().toString();
            if ((Long)this.times.get((Object)id) >= limit) continue;
            this.times.remove((Object)id);
            this.store.removeCaptcha(id);
            ++this.numberOfGarbageCollectedCaptcha;
        }
    }

    @Override
    public void garbageCollectCaptchaStore() {
        long now = System.currentTimeMillis();
        Collection garbageCollectableCaptchaIds = this.getGarbageCollectableCaptchaIds(now);
        this.garbageCollectCaptchaStore(garbageCollectableCaptchaIds.iterator());
    }

    @Override
    public void emptyCaptchaStore() {
        this.store.empty();
        this.times = new FastHashMap();
    }

    private Collection getGarbageCollectableCaptchaIds(long now) {
        HashSet<String> garbageCollectableCaptchas = new HashSet<String>();
        long limit = now - (long)(1000 * this.getMinGuarantedStorageDelayInSeconds());
        if (limit > this.oldestCaptcha) {
            for (String id : new HashSet(this.times.keySet())) {
                long captchaDate = (Long)this.times.get((Object)id);
                this.oldestCaptcha = Math.min(captchaDate, this.oldestCaptcha == 0L ? captchaDate : this.oldestCaptcha);
                if (captchaDate >= limit) continue;
                garbageCollectableCaptchas.add(id);
            }
        }
        return garbageCollectableCaptchas;
    }

    @Override
    protected Captcha generateAndStoreCaptcha(Locale locale, String ID) {
        if (this.isCaptchaStoreFull()) {
            long now = System.currentTimeMillis();
            Collection garbageCollectableCaptchaIds = this.getGarbageCollectableCaptchaIds(now);
            if (garbageCollectableCaptchaIds.size() > 0) {
                this.garbageCollectCaptchaStore(garbageCollectableCaptchaIds.iterator());
                return this.generateAndStoreCaptcha(locale, ID);
            }
            throw new CaptchaServiceException("Store is full, try to increase CaptchaStore Size orto decrease time out, or to decrease CaptchaStoreSizeBeforeGrbageCollection");
        }
        if (this.isCaptchaStoreQuotaReached()) {
            this.garbageCollectCaptchaStore();
        }
        return this.generateCountTimeStampAndStoreCaptcha(ID, locale);
    }

    private Captcha generateCountTimeStampAndStoreCaptcha(String ID, Locale locale) {
        ++this.numberOfGeneratedCaptchas;
        Long now = new Long(System.currentTimeMillis());
        this.times.put((Object)ID, (Object)now);
        Captcha captcha = super.generateAndStoreCaptcha(locale, ID);
        return captcha;
    }

    protected boolean isCaptchaStoreFull() {
        return this.getCaptchaStoreMaxSize() == 0 ? false : this.getCaptchaStoreSize() >= this.getCaptchaStoreMaxSize();
    }

    protected boolean isCaptchaStoreQuotaReached() {
        return this.getCaptchaStoreSize() >= this.getCaptchaStoreSizeBeforeGarbageCollection();
    }

    @Override
    public Boolean validateResponseForID(String ID, Object response) throws CaptchaServiceException {
        Boolean valid = super.validateResponseForID(ID, response);
        this.times.remove((Object)ID);
        if (valid.booleanValue()) {
            ++this.numberOfCorrectResponse;
        } else {
            ++this.numberOfUncorrectResponse;
        }
        return valid;
    }
}

