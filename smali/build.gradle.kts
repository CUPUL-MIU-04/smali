plugins {
    `java-library`
    antlr
}

dependencies {
    implementation(project(":dexlib2"))
    implementation(project(":util"))

    // ANTLR 3 dependencies
    antlr("org.antlr:antlr:3.5.2")
    implementation("org.antlr:antlr-runtime:3.5.2")
    implementation("org.antlr:stringtemplate:4.0.2")

    implementation("com.google.guava:guava:32.1.2-android")
    implementation("com.beust:jcommander:1.82")
    implementation("com.google.code.gson:gson:2.10.1")

    // JFlex dependency for generation
    // Nota: jflex se usa solo para generar el lexer, no en runtime
    // Lo incluimos en antlr configuration
    antlr("de.jflex:jflex:1.9.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.4.0")
}

// Configuración de ANTLR
tasks.generateGrammarSource {
    outputDirectory = file("${buildDir}/generated-src/antlr/main")
    arguments = arguments + listOf("-visitor", "-message-format", "gnu")
}

// Tarea para generar el lexer con JFlex
val generateJflex by tasks.registering(JavaExec::class) {
    group = "build"
    description = "Generates the lexer using JFlex"

    // CORRECCIÓN: Usar configurations.antlr.get() en lugar de runtimeClasspath
    classpath = configurations.antlr.get()
    mainClass.set("jflex.Main")

    args = listOf(
        "-d", "${buildDir}/generated-src/jflex/main",
        "src/main/jflex/smaliLexer.jflex"
    )

    doFirst {
        mkdir("${buildDir}/generated-src/jflex/main")
    }
}

// Incluir el código generado en el classpath
sourceSets.main {
    java.srcDirs(
        "src/main/java", 
        "${buildDir}/generated-src/antlr/main",
        "${buildDir}/generated-src/jflex/main"
    )
}

// Hacer que compileJava dependa de generateJflex
tasks.compileJava {
    dependsOn(generateJflex)
}

// Configurar processResources para que dependa de generateGrammarSource
tasks.processResources {
    dependsOn(tasks.generateGrammarSource)

    // Incluir los archivos .tokens generados por ANTLR
    from("${buildDir}/generated-src/antlr/main") {
        include("**/*.tokens")
    }

    // Propagar versión
    inputs.property("version", project.version)
    filesMatching("**/*.properties") {
        expand("version" to project.version)
    }
}

// Tarea para crear fatJar
tasks.register<Jar>("fatJar") {
    archiveBaseName.set("smali")
    archiveClassifier.set("fat")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    manifest {
        attributes("Main-Class" to "org.jf.smali.Main")
    }
}

tasks.named("build") {
    dependsOn("fatJar")
}

// Configuración de publicación para GitHub Packages
publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            
            // Incluir el fatJar como artifact adicional
            artifact(tasks.named("fatJar").get()) {
                classifier = "fat"
            }
            
            // Información del POM
            pom {
                name.set("Smali")
                description.set("An assembler/disassembler for the dex format used by dalvik, Android's Java VM implementation")
                url.set("https://github.com/CUPUL-MIU-04/smali")
                
                licenses {
                    license {
                        name.set("BSD 2-Clause License")
                        url.set("https://opensource.org/licenses/BSD-2-Clause")
                    }
                }
                
                developers {
                    developer {
                        id.set("CUPUL-MIU-04")
                        name.set("CUPUL-MIU-04 Team")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/CUPUL-MIU-04/smali.git")
                    developerConnection.set("scm:git:ssh://github.com:CUPUL-MIU-04/smali.git")
                    url.set("https://github.com/CUPUL-MIU-04/smali")
                }
            }
        }
    }
}

dexlib2/build.gradle.kts
dependencies {
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-util:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-tree:9.5")
    implementation("org.ow2.asm:asm-analysis:9.5")
    
    testImplementation("junit:junit:4.13.2")
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            
            pom {
                name.set("Dexlib2")
                description.set("A library for reading/writing Android dex files")
                // ... resto de configuración similar
            }
        }
    }
}
