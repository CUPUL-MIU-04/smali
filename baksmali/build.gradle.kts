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
    
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/CUPUL-MIU-04/smali")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user")?.toString()
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.token")?.toString()
            }
        }
    }
}
