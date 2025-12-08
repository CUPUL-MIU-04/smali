rootProject.name = "smali"

include(":dexlib2")
include(":util") 
include(":smali")
include(":baksmali")

// Opcional: especificar la estructura de directorios
project(":dexlib2").projectDir = file("dexlib2")
project(":util").projectDir = file("util")
project(":smali").projectDir = file("smali")
project(":baksmali").projectDir = file("baksmali")