plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation(project(":dexlib2"))
    implementation("com.beust:jcommander:1.82")
    implementation("com.google.code.gson:gson:2.10.1")
    
    implementation("org.antlr:antlr-runtime:3.5.2")
    
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.4.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}

tasks.withType<Test> {
    useJUnit()
}

// Configuración de publicación para GitHub Packages
publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            
            pom {
                name.set("Util")
                description.set("Utility library for smali/baksmali")
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
    
    // ¡AGREGA ESTE BLOQUE!
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/CUPUL-MIU-04/smali")
            credentials {
                username = System.getenv("RE_USER") ?: project.findProperty("re_gpr_user")?.toString()
                password = System.getenv("RE_TOKEN") ?: project.findProperty("re_gpr_token")?.toString()
            }
        }
    }
}
