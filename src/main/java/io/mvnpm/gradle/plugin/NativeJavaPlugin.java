package io.mvnpm.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.Attribute;
import org.gradle.internal.os.OperatingSystem;

/**
 * Automatically sets the following attributes on all Gradle configurations:
 * <ul>
 *     <li>{@code org.gradle.native.operatingSystem} → the current operating system
 *     (possible values: {@code macos}, {@code linux}, {@code windows})</li>
 *     <li>{@code org.gradle.native.architecture} → the current architecture
 *     (possible values: {@code x86-64}, {@code aarch64})</li>
 * </ul>
 */
public class NativeJavaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getLogger().lifecycle("Native Java plugin has been applied");
        Attribute<String> osAttr = Attribute.of("org.gradle.native.operatingSystem", String.class);
        Attribute<String> archAttr = Attribute.of("org.gradle.native.architecture", String.class);

        String currentOs;
        OperatingSystem os = OperatingSystem.current();
        if (os.isMacOsX()) {
            currentOs = "macos";
        } else if (os.isLinux()) {
            currentOs = "linux";
        } else if (os.isWindows()) {
            currentOs = "windows";
        } else {
            return;
        }



        final String systemArch = System.getProperty("os.arch").toLowerCase();
        final String arch;
        if (systemArch.contains("aarch64") || systemArch.contains("arm64")) {
            arch = "aarch64";
        } else if (systemArch.contains("64")) {
            arch = "x86-64";
        } else {
            return;
        }

        project.getLogger().info("Applying NativeVariantPlugin for " + currentOs + "/" + arch);

        project.getDependencies().getAttributesSchema().attribute(osAttr);
        project.getDependencies().getAttributesSchema().attribute(archAttr);

        project.getConfigurations().all(configuration -> {
            configuration.getAttributes().attribute(osAttr, currentOs);
            configuration.getAttributes().attribute(archAttr, arch);
        });
    }
}