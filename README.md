# Gradle Native Java Plugin

Until this becomes a standard feature in Gradle (gradle/gradle#34845), this plugin bridges the gap.

**Gradle plugin for OS/architecture-aware and JVM-version-aware variant resolution in Java projects.**

Automatically sets the following attributes on all configurations:

- `org.gradle.native.operatingSystem` → current OS (`macos`, `linux`, `windows`)
- `org.gradle.native.architecture` → current architecture (`x86_64`, `arm64`)

This enables Gradle to automatically select the correct variant of libraries that publish platform-specific or JVM-version-specific artifacts (e.g., [`esbuild-java`](https://github.com/gluck/esbuild-java)).

---

## Features

- ✅ Safe to use with existing libraries — attributes are only used for variant matching if the library defines them.
- ✅ Compatible with both **Groovy** and **Kotlin DSL** builds.
- ✅ Automatically detects host OS and architecture.
- ✅ No configuration required — just apply the plugin.

---

## Usage

**Kotlin DSL (`build.gradle.kts`)**
```kotlin
plugins {
    id("io.mvnpm.gradle.plugin.native-java-plugin") version "1.0.0"
}
```

**Groovy DSL (`build.gradle`)**
```groovy
plugins {
    id 'io.mvnpm.gradle.plugin.native-java-plugin' version '1.0.0'
}
```

---

## How It Works

When applied, the plugin sets the following attributes on all configurations:

- `org.gradle.native.operatingSystem` — based on the current OS.
- `org.gradle.native.architecture` — based on the current CPU architecture.

These attributes are used by Gradle’s variant-aware dependency resolution to select the correct artifact variant when available.

---

## License

Apache License 2.0. See [LICENSE](./LICENSE) for details.

---

## Contributing

Pull requests and issues are welcome! If you find a bug or want to suggest a feature, feel free to open an issue.

---


