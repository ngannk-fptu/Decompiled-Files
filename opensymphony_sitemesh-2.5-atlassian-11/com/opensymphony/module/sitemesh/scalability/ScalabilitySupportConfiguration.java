/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.factory.FilterConfigParameterFactory;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupportFactory;
import com.opensymphony.module.sitemesh.scalability.outputlength.ExceptionThrowingOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.NoopOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.NoopSecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.TempDirSecondaryStorage;
import com.opensymphony.module.sitemesh.util.ClassLoaderUtil;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public class ScalabilitySupportConfiguration
extends FilterConfigParameterFactory {
    private final ScalabilitySupportFactory scalabilitySupportFactory;

    public ScalabilitySupportConfiguration(FilterConfig filterConfig) {
        super(filterConfig);
        String scalabilitySupportFactoryClass = this.getStringVal("scalability.support.factory.class", null);
        ScalabilitySupportFactory hostProvided = null;
        if (scalabilitySupportFactoryClass != null) {
            try {
                hostProvided = (ScalabilitySupportFactory)ClassLoaderUtil.loadClass(scalabilitySupportFactoryClass, this.getClass()).newInstance();
            }
            catch (ClassNotFoundException e) {
                throw new FactoryException("Could not load SecondaryStorageFactory class : " + scalabilitySupportFactoryClass, e);
            }
            catch (Exception e) {
                throw new FactoryException("Could not instantiate SecondaryStorageFactory class : " + scalabilitySupportFactoryClass, e);
            }
        }
        this.scalabilitySupportFactory = new DefaultScalabilitySupportFactory(hostProvided);
    }

    public ScalabilitySupport getScalabilitySupport(HttpServletRequest httpServletRequest) {
        final SecondaryStorage secondaryStorage = this.scalabilitySupportFactory.getSecondaryStorage(httpServletRequest);
        final OutputLengthObserver outputLengthObserver = this.getOutputLengthObserver();
        final int initialBufferSize = this.scalabilitySupportFactory.getInitialBufferSize();
        final boolean isMaxOutputLengthExceededThrown = this.scalabilitySupportFactory.isMaxOutputLengthExceededThrown();
        return new ScalabilitySupport(){

            public OutputLengthObserver getOutputLengthObserver() {
                return outputLengthObserver;
            }

            public SecondaryStorage getSecondaryStorage() {
                return secondaryStorage;
            }

            public int getInitialBufferSize() {
                return initialBufferSize;
            }

            public boolean isMaxOutputLengthExceededThrown() {
                return isMaxOutputLengthExceededThrown;
            }
        };
    }

    ScalabilitySupportFactory getScalabilitySupportFactory() {
        return this.scalabilitySupportFactory;
    }

    private OutputLengthObserver getOutputLengthObserver() {
        if (this.scalabilitySupportFactory.getMaximumOutputLength() > 0L) {
            return new ExceptionThrowingOutputLengthObserver(this.scalabilitySupportFactory.getMaximumOutputLength(), this.scalabilitySupportFactory.getMaximumOutputExceededHttpCode());
        }
        return new NoopOutputLengthObserver();
    }

    class DefaultScalabilitySupportFactory
    implements ScalabilitySupportFactory {
        private final long secondaryStorageLimit;
        private final long maxOutputLength;
        private final int maximumOutputExceededHttpCode;
        private final ScalabilitySupportFactory hostProvidedFactory;
        private final int initialBufferSize;
        private final boolean throwsException;

        DefaultScalabilitySupportFactory(ScalabilitySupportFactory hostProvidedFactory) {
            this.hostProvidedFactory = hostProvidedFactory;
            if (hostProvidedFactory == null) {
                this.maxOutputLength = ScalabilitySupportConfiguration.this.longVal("scalability.maxoutput.length", -1L);
                this.throwsException = ScalabilitySupportConfiguration.this.booleanVal("scalability.maxoutput.throw.exception", true);
                this.maximumOutputExceededHttpCode = ScalabilitySupportConfiguration.this.intVal("scalability.maxoutput.httpcode", 509);
                this.secondaryStorageLimit = ScalabilitySupportConfiguration.this.longVal("scalability.secondarystorage.limit", -1L);
                this.initialBufferSize = ScalabilitySupportConfiguration.this.intVal("scalability.initial.buffer.size", 8192);
            } else {
                this.secondaryStorageLimit = 0L;
                this.maxOutputLength = 0L;
                this.maximumOutputExceededHttpCode = 0;
                this.initialBufferSize = 0;
                this.throwsException = true;
            }
        }

        public int getInitialBufferSize() {
            return this.hostProvidedFactory != null ? this.hostProvidedFactory.getInitialBufferSize() : this.initialBufferSize;
        }

        public long getMaximumOutputLength() {
            return this.hostProvidedFactory != null ? this.hostProvidedFactory.getMaximumOutputLength() : this.maxOutputLength;
        }

        public int getMaximumOutputExceededHttpCode() {
            return this.hostProvidedFactory != null ? this.hostProvidedFactory.getMaximumOutputExceededHttpCode() : this.maximumOutputExceededHttpCode;
        }

        public long getSecondaryStorageLimit() {
            return this.hostProvidedFactory != null ? this.hostProvidedFactory.getSecondaryStorageLimit() : this.secondaryStorageLimit;
        }

        public boolean isMaxOutputLengthExceededThrown() {
            return this.hostProvidedFactory != null ? this.hostProvidedFactory.isMaxOutputLengthExceededThrown() : this.throwsException;
        }

        public boolean hasCustomSecondaryStorage() {
            return true;
        }

        public SecondaryStorage getSecondaryStorage(HttpServletRequest httpServletRequest) {
            httpServletRequest.setAttribute(RequestConstants.SECONDARY_STORAGE_LIMIT, (Object)this.secondaryStorageLimit);
            if (this.hostProvidedFactory != null && this.hostProvidedFactory.hasCustomSecondaryStorage()) {
                return this.hostProvidedFactory.getSecondaryStorage(httpServletRequest);
            }
            if (this.secondaryStorageLimit > 0L) {
                return new TempDirSecondaryStorage(this.secondaryStorageLimit);
            }
            return new NoopSecondaryStorage();
        }
    }
}

