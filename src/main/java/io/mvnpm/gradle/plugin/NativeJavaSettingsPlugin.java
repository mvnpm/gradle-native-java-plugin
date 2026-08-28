package io.mvnpm.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.nativeplatform.MachineArchitecture;
import org.gradle.nativeplatform.OperatingSystemFamily;

import javax.inject.Inject;
import java.util.Optional;

import static io.mvnpm.gradle.plugin.NativeJavaPlugin.addAttributes;
import static io.mvnpm.gradle.plugin.NativeJavaPlugin.getMachineArchitecture;
import static io.mvnpm.gradle.plugin.NativeJavaPlugin.getOperatingSystemFamily;

/**
 * Automatically sets the following attributes on the buildscript classpath configuration:
 * <ul>
 *     <li>{@code org.gradle.native.operatingSystem} → the current operating system
 *     (possible values OperatingSystemFamily: {@code macos}, {@code linux}, {@code windows})</li>
 *     <li>{@code org.gradle.native.architecture} → the current architecture
 *     (possible values MachineArchitecture: {@code x86-64}, {@code aarch64}), {@code x86})</li>
 * </ul>
 */
public class NativeJavaSettingsPlugin implements Plugin<Settings> {
    private final ObjectFactory objects;

    @Inject
    public NativeJavaSettingsPlugin(ObjectFactory objects) {
        this.objects = objects;
    }

    @Override
    public void apply(Settings settings) {
        Optional<Logger> logger = Optional.empty();
        final OperatingSystemFamily detectedOs = getOperatingSystemFamily(objects, logger);
        final MachineArchitecture detectedArch = getMachineArchitecture(objects, logger);

        if (detectedOs != null || detectedArch != null) {
            settings.getBuildscript().getConfigurations().named("classpath", configuration -> {
                addAttributes(configuration, detectedOs, detectedArch);
            });
        }
    }

}
