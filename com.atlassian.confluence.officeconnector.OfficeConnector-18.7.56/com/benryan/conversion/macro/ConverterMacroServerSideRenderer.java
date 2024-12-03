/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.conversion.macro;

import com.benryan.conversion.Converter;
import com.benryan.conversion.macro.ConverterMacroRenderer;
import com.benryan.conversion.macro.MacroParameters;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConverterMacroServerSideRenderer
implements ConverterMacroRenderer {
    private static final Logger log = LoggerFactory.getLogger(ConverterMacroServerSideRenderer.class);
    private final MacroParameters macroParameters;
    private final Converter converter;

    public ConverterMacroServerSideRenderer(MacroParameters macroParameters, Converter converter) {
        this.macroParameters = macroParameters;
        this.converter = converter;
    }

    @Override
    public void render(Appendable output) {
        try {
            output.append(this.converter.execute(this.macroParameters.get()));
        }
        catch (Exception e) {
            log.error(e.getMessage(), (Throwable)e);
            try {
                output.append("We can't preview this file. You'll have to download the file to view it.");
            }
            catch (IOException ioe) {
                log.error(ioe.getMessage(), (Throwable)ioe);
            }
        }
    }
}

