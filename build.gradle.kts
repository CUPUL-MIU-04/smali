plugins {
    id("java")
    id("antlr")
}

allprojects {
    group = "com.revoid"
    version = "2.5.3-a3836655"
    
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
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")  // ¡Esto es correcto!
    
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
