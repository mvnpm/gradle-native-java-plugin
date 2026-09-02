package io.mvnpm.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.AttributeCompatibilityRule;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.CompatibilityCheckDetails;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.nativeplatform.MachineArchitecture;
import org.gradle.nativeplatform.OperatingSystemFamily;
import org.jetbrains.annotations.NotNull;


/**
 * Automatically sets the following attributes on all Gradle configurations:
 * <ul>
 *     <li>{@code org.gradle.native.operatingSystem} → the current operating system
 *     (possible values OperatingSystemFamily: {@code macos}, {@code linux}, {@code windows})</li>
 *     <li>{@code org.gradle.native.architecture} → the current architecture
 *     (possible values MachineArchitecture: {@code x86-64}, {@code aarch64}), {@code x86})</li>
 * </ul>
 */
public class NativeJavaPlugin implements Plugin<@NotNull Project> {

    /** Creates a new instance. */
    public NativeJavaPlugin() {}

    @Override
    public void apply(Project project) {
        ObjectFactory objects = project.getObjects();
        Logger logger = project.getLogger();
        final OperatingSystemFamily detectedOs = getOperatingSystemFamily(objects, logger);
        final MachineArchitecture detectedArch = getMachineArchitecture(objects, logger);

        if (detectedOs != null || detectedArch != null) {
            project.getLogger().info("Native Java plugin - applied with: {} and {}", detectedOs, detectedArch);

            // Add compatibility rules so that artifacts without OS/architecture declarations
            // are considered compatible with any requested platform.
            // This allows platform-agnostic dependencies (like jQuery, Bootstrap) to coexist
            // with platform-specific ones (like @esbuild/darwin-arm64).
            if (detectedOs != null) {
                project.getDependencies().getAttributesSchema()
                        .attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE)
                        .getCompatibilityRules()
                        .add(OsCompatibilityRule.class);
            }
            if (detectedArch != null) {
                project.getDependencies().getAttributesSchema()
                        .attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE)
                        .getCompatibilityRules()
                        .add(ArchitectureCompatibilityRule.class);
            }

            project.getConfigurations().configureEach(configuration -> {
                if (configuration.isCanBeResolved()) {
                    addAttributes(configuration, detectedOs, detectedArch);
                }
            });
        }
    }

    static OperatingSystemFamily getOperatingSystemFamily(ObjectFactory objects) {
        return getOperatingSystemFamily(objects, null);
    }

    static OperatingSystemFamily getOperatingSystemFamily(ObjectFactory objects, Logger logger) {
        final OperatingSystem os = OperatingSystem.current();
        if (os.isMacOsX()) {
            return objects.named(OperatingSystemFamily.class, OperatingSystemFamily.MACOS);
        } else if (os.isLinux()) {
            return objects.named(OperatingSystemFamily.class, OperatingSystemFamily.LINUX);
        } else if (os.isWindows()) {
            return objects.named(OperatingSystemFamily.class, OperatingSystemFamily.WINDOWS);
        } else {
            if (logger != null) {
                logger.warn("Native Java plugin - Unsupported operating system: {}", os);
            }
            return null;
        }
    }

    static MachineArchitecture getMachineArchitecture(ObjectFactory objects) {
        return getMachineArchitecture(objects, null);
    }

    static MachineArchitecture getMachineArchitecture(ObjectFactory objects, Logger logger) {
        String systemArch = System.getProperty("os.arch").toLowerCase();
        if (systemArch.contains("aarch64") || systemArch.contains("arm64")) {
            return objects.named(MachineArchitecture.class, MachineArchitecture.ARM64);
        } else if (systemArch.contains("64")) {
            return objects.named(MachineArchitecture.class, MachineArchitecture.X86_64);
        } else if (systemArch.contains("x86")) {
            return objects.named(MachineArchitecture.class, MachineArchitecture.X86);
        } else {
            if (logger != null) {
                logger.warn("Native Java plugin - Unsupported architecture: {}", systemArch);
            }
            return null;
        }
    }

    static void addAttributes(Configuration configuration, OperatingSystemFamily detectedOs, MachineArchitecture detectedArch) {
        AttributeContainer attributes = configuration.getAttributes();

        if (detectedOs != null && !attributes.contains(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE)) {
            attributes.attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, detectedOs);
        }
        if (detectedArch != null && !attributes.contains(MachineArchitecture.ARCHITECTURE_ATTRIBUTE)) {
            attributes.attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, detectedArch);
        }
    }

    /** Marks artifacts that declare no OS attribute as compatible with any OS request. */
    public static class OsCompatibilityRule implements AttributeCompatibilityRule<OperatingSystemFamily> {
        /** Creates a new instance. */
        public OsCompatibilityRule() {}

        @Override
        public void execute(CompatibilityCheckDetails<OperatingSystemFamily> details) {
            if (details.getProducerValue() == null) {
                details.compatible();
            }
        }
    }

    /** Marks artifacts that declare no architecture attribute as compatible with any architecture request. */
    public static class ArchitectureCompatibilityRule implements AttributeCompatibilityRule<MachineArchitecture> {
        /** Creates a new instance. */
        public ArchitectureCompatibilityRule() {}

        @Override
        public void execute(CompatibilityCheckDetails<MachineArchitecture> details) {
            if (details.getProducerValue() == null) {
                details.compatible();
            }
        }
    }
}
