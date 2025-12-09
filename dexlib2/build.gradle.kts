plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-util:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-tree:9.5")
    implementation("org.ow2.asm:asm-analysis:9.5")
    
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.4.0")
}

// Configuración de publicación para GitHub Packages
publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            artifactId = "dexlib2"
            
            pom {
                name.set("Dexlib2")
                description.set("A library for reading/writing Android dex files")
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
