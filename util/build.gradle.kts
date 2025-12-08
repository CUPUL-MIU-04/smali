dependencies {
    implementation(project(":dexlib2"))
    implementation("com.beust:jcommander:1.82")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // ANTLR runtime (necesario para algunas clases de util)
    implementation("org.antlr:antlr-runtime:3.5.2")
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
}