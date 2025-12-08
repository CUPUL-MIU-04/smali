plugins {
    `java-library`
}

dependencies {
    implementation(project(":dexlib2"))
    implementation(project(":util"))
    
    implementation("com.google.guava:guava:32.1.2-android")
    implementation("com.beust:jcommander:1.82")
    implementation("com.google.code.gson:gson:2.10.1")
    
    testImplementation("junit:junit:4.13.2")
    testImplementation(project(":smali"))
}

// Propagar versión a recursos
tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("**/*.properties") {
        expand("version" to project.version)
    }
}

// Tarea para crear fatJar
tasks.register<Jar>("fatJar") {
    archiveBaseName.set("baksmali")
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
        attributes("Main-Class" to "org.jf.baksmali.Main")
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
            
            pom {
                name.set("Baksmali")
                description.set("A disassembler for the dex format used by dalvik, Android's Java VM implementation")
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