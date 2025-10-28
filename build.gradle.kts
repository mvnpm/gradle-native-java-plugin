plugins {
    `java-gradle-plugin`
    `maven-publish`
}


group = "io.mvnpm.gradle.plugin"
version = "1.0.0"


gradlePlugin {
    plugins.register("nativeJavaPlugin") {
        id = "io.mvnpm.gradle.plugin.native-java-plugin"
        implementationClass = "io.mvnpm.gradle.plugin.NativeJavaPlugin"
        displayName = "Native Java Plugin"
        description = "Automatically sets org.gradle.native.* attributes for variant resolution on JVM builds."
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
}

publishing {
    repositories {
        mavenLocal()
    }
}

tasks.test {
    useJUnitPlatform()
}