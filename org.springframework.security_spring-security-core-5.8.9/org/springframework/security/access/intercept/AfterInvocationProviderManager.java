/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.security.access.intercept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Deprecated
public class AfterInvocationProviderManager
implements AfterInvocationManager,
InitializingBean {
    protected static final Log logger = LogFactory.getLog(AfterInvocationProviderManager.class);
    private List<AfterInvocationProvider> providers;

    public void afterPropertiesSet() {
        this.checkIfValidList(this.providers);
    }

    @Override
    public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> config, Object returnedObject) throws AccessDeniedException {
        Object result = returnedObject;
        for (AfterInvocationProvider provider : this.providers) {
            result = provider.decide(authentication, object, config, result);
        }
        return result;
    }

    public List<AfterInvocationProvider> getProviders() {
        return this.providers;
    }

    public void setProviders(List<?> newList) {
        this.checkIfValidList(newList);
        this.providers = new ArrayList<AfterInvocationProvider>(newList.size());
        for (Object currentObject : newList) {
            Assert.isInstanceOf(AfterInvocationProvider.class, currentObject, () -> "AfterInvocationProvider " + currentObject.getClass().getName() + " must implement AfterInvocationProvider");
            this.providers.add((AfterInvocationProvider)currentObject);
        }
    }

    private void checkIfValidList(List<?> listToCheck) {
        Assert.isTrue((!CollectionUtils.isEmpty(listToCheck) ? 1 : 0) != 0, (String)"A list of AfterInvocationProviders is required");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        for (AfterInvocationProvider provider : this.providers) {
            logger.debug((Object)LogMessage.format((String)"Evaluating %s against %s", (Object)attribute, (Object)provider));
            if (!provider.supports(attribute)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        for (AfterInvocationProvider provider : this.providers) {
            if (provider.supports(clazz)) continue;
            return false;
        }
        return true;
    }
}

