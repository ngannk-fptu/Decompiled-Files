/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;

public class FacadeTaskHelper {
    private List<ImplementationSpecificArgument> args = new ArrayList<ImplementationSpecificArgument>();
    private String userChoice;
    private String magicValue;
    private String defaultValue;
    private Path implementationClasspath;

    public FacadeTaskHelper(String defaultValue) {
        this(defaultValue, null);
    }

    public FacadeTaskHelper(String defaultValue, String magicValue) {
        this.defaultValue = defaultValue;
        this.magicValue = magicValue;
    }

    public void setMagicValue(String magicValue) {
        this.magicValue = magicValue;
    }

    public void setImplementation(String userChoice) {
        this.userChoice = userChoice;
    }

    public String getImplementation() {
        return this.userChoice != null ? this.userChoice : (this.magicValue != null ? this.magicValue : this.defaultValue);
    }

    public String getExplicitChoice() {
        return this.userChoice;
    }

    public void addImplementationArgument(ImplementationSpecificArgument arg) {
        this.args.add(arg);
    }

    public String[] getArgs() {
        String implementation = this.getImplementation();
        return (String[])this.args.stream().map(arg -> arg.getParts(implementation)).filter(Objects::nonNull).flatMap(Stream::of).toArray(String[]::new);
    }

    public boolean hasBeenSet() {
        return this.userChoice != null || this.magicValue != null;
    }

    public Path getImplementationClasspath(Project project) {
        if (this.implementationClasspath == null) {
            this.implementationClasspath = new Path(project);
        }
        return this.implementationClasspath;
    }
}

