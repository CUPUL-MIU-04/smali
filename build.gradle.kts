plugins {
    id("java")
    id("antlr")
    id("maven-publish")
}

allprojects {
    group = "com.github.CUPUL-MIU-04"
    version = "2.5.3-a3836654"
    
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
    }
    
    apply(plugin = "java")
    
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

subprojects {
    // Aplicar plugins comunes
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    
    dependencies {
        implementation("com.google.guava:guava:32.1.2-android")
        implementation("com.google.code.findbugs:jsr305:3.0.2")
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
        
        publications {
            register<MavenPublication>("gpr") {
                from(components["java"])
                
                // Configuración específica para cada submódulo
                afterEvaluate {
                    artifactId = project.name
                    
                    pom {
                        name.set(project.name)
                        description.set(project.description ?: "Smali/Baksmali tools")
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
    }
}

// Tarea para publicar todos los módulos
tasks.register("publishAllToGitHubPackages") {
    dependsOn(
        ":dexlib2:publishGprPublicationToGitHubPackagesRepository",
        ":util:publishGprPublicationToGitHubPackagesRepository",
        ":smali:publishGprPublicationToGitHubPackagesRepository",
        ":baksmali:publishGprPublicationToGitHubPackagesRepository"
    )
}
