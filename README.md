### About

smali/baksmali is an assembler/disassembler for the dex format used by dalvik, Android's Java VM implementation. The syntax is loosely based on Jasmin's/dedexer's syntax, and supports the full functionality of the dex format (annotations, debug info, line info, etc.)

### About this Fork (CUPUL-MIU-04/smali)

This repository is a maintained fork of the original smali project with the following modifications:

#### 📦 Package Information
- **Package Version**: `2.5.3-a3836655`
- **Group ID**: `com.revoid`
- **Available on**: GitHub Packages (Maven)

#### 🛠️ Build & Compatibility
- **Gradle Wrapper Version**: 8.2.1
- **Java Compatibility**: Java 8 (sourceCompatibility = 1.8)
- **Build System**: Gradle Kotlin DSL (.kts)

#### 📋 Modifications by CUPUL-MIU-04
1. **Build System Migration**: Converted from traditional Gradle to Gradle Kotlin DSL
2. **GitHub Packages Integration**: Configured for publishing to GitHub Packages Maven registry
3. **JFlex Integration**: Fixed lexer generation configuration
4. **ANTLR 3 Configuration**: Corrected output directory structure for generated sources
5. **Multi-module Support**: Enhanced build configuration for all submodules (dexlib2, util, smali, baksmali)
6. **Fat Jar Support**: Added fatJar task for standalone executable JAR

#### 🔄 Changes from Original Repository
- Updated dependencies to newer versions
- Fixed ANTLR 3/JFlex code generation issues
- Added Maven publication configuration
- Improved build scripts for modern Gradle
- Enhanced documentation and build instructions

#### 📁 Repository Information
- **Original Repository**: https://github.com/JesusFreke/smali
- **Fork Repository**: https://github.com/CUPUL-MIU-04/smali
- **Package Repository**: https://maven.pkg.github.com/CUPUL-MIU-04/smali

#### 📦 Using this Fork
Add to your `build.gradle.kts`:
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/CUPUL-MIU-04/smali")
        credentials {
            username = System.getenv("RE_USER") ?: project.findProperty("re_gpr_user")
            password = System.getenv("RE_TOKEN") ?: project.findProperty("re_gpr_token")
        }
    }
}

dependencies {
    implementation("com.revoid:dexlib2:2.5.3-a3836655")
    implementation("com.revoid:util:2.5.3-a3836655")
    implementation("com.revoid:smali:2.5.3-a3836655")
    implementation("com.revoid:baksmali:2.5.3-a3836655")
}
```

Building from Source

```bash
# Clone repository
git clone https://github.com/CUPUL-MIU-04/smali.git

# Build all modules
./gradlew clean build -x test

# Build individual modules
./gradlew :smali:fatJar
./gradlew :baksmali:fatJar

# Publish to GitHub Packages (requires credentials)
export RE_USER=CUPUL-MIU-04
export RE_TOKEN=your_github_token
./gradlew publishAllToGitHubPackages
```

---

Downloads are at  https://bitbucket.org/JesusFreke/smali/downloads/. If you are interested in submitting a patch, feel free to send me a pull request here.

See the wiki for more info/news/release notes/etc.

Support

· github Issue tracker - For any bugs/issues/feature requests
· #smali on freenode - Free free to drop by and ask a question. Don't expect an instant response, but if you hang around someone will respond.

Some useful links for getting started with smali

· Official dex bytecode reference
· Registers wiki page
· Types, Methods and Fields wiki page
· Official dex format reference

---

License

This project is licensed under the BSD 2-Clause License - see the LICENSE file for details.

| Acknowledgments |
|-------------|
| **Proyecto original:** [JesúsFreke](https://github.com/JesusFreke) |
| **Mantenimiento actual:** [CUPUL-MIU-04](https://github.com/CUPUL-MIU-04) |
