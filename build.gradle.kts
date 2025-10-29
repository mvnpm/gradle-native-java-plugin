plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1" apply false
}

val gradlePublish = project.hasProperty("gradlePublish")
if (gradlePublish) {
    plugins.apply("signing")
    plugins.apply("com.gradle.plugin-publish")
}


group = "io.mvnpm.gradle.plugin"
version = "1.0.0"


gradlePlugin {
    website = "https://github.com/mvnpm/gradle-native-java-plugin"
    vcsUrl = "https://github.com/mvnpm/gradle-native-java-plugin.git"
    plugins.register("nativeJavaPlugin") {
        id = "io.mvnpm.gradle.plugin.native-java-plugin"
        implementationClass = "io.mvnpm.gradle.plugin.NativeJavaPlugin"
        displayName = "Native Java Plugin"
        description =
            "Automatically sets \"org.gradle.native.operatingSystem\" and \"org.gradle.native.architecture\" attributes for variant resolution on JVM builds."
        tags = listOf("native", "jvm", "architecture", "operatingSystem", "os", "variants")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
}


if (gradlePublish) {
    configure<SigningExtension> {
        useGpgCmd()
        sign(publishing.publications)
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

tasks.test {
    useJUnitPlatform()
}