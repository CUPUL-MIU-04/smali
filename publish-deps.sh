#!/bin/bash

echo "=== Publicando smali y multidexlib2 en GitHub Packages ==="

# Configuración
export GITHUB_TOKEN="tu_token_aqui"
export GITHUB_ACTOR="CUPUL-MIU-04"

echo "Usuario: $GITHUB_ACTOR"
echo "Token (primeros 10 chars): ${GITHUB_TOKEN:0:10}..."

# Función para publicar un proyecto
publish_project() {
    local project_name=$1
    local project_dir=$2
    
    echo ""
    echo "=== Publicando $project_name ==="
    
    if [ ! -d "$project_dir" ]; then
        echo "Error: El directorio $project_dir no existe"
        return 1
    fi
    
    cd "$project_dir"
    
    echo "Directorio actual: $(pwd)"
    echo "Configurando Gradle wrapper..."
    
    # Asegúrate de que gradlew existe
    if [ ! -f "gradlew" ]; then
        echo "Creando Gradle wrapper..."
        gradle wrapper --gradle-version=8.10.2 --distribution-type=bin
        chmod +x gradlew
    else
        chmod +x gradlew
    fi
    
    echo "Limpiando proyecto..."
    ./gradlew clean --stacktrace
    
    echo "Construyendo proyecto..."
    ./gradlew build --stacktrace
    
    if [ $? -eq 0 ]; then
        echo "✓ Construcción exitosa"
        
        echo "Publicando en GitHub Packages..."
        ./gradlew publish \
            -Pgpr.user="$GITHUB_ACTOR" \
            -Pgpr.key="$GITHUB_TOKEN" \
            --stacktrace
        
        if [ $? -eq 0 ]; then
            echo "✓ Publicación exitosa"
            
            # Verificar publicación
            echo "Verificando publicación..."
            sleep 5
            curl -s -H "Authorization: token $GITHUB_TOKEN" \
                "https://maven.pkg.github.com/CUPUL-MIU-04/$project_name/com/revoid/$project_name/" > /dev/null
            
            if [ $? -eq 0 ]; then
                echo "✓ Paquete publicado correctamente"
                echo "URL: https://maven.pkg.github.com/CUPUL-MIU-04/$project_name/com/revoid/$project_name/"
            else
                echo "⚠️  El paquete puede tardar unos minutos en aparecer"
            fi
        else
            echo "✗ Error en la publicación"
            return 1
        fi
    else
        echo "✗ Error en la construcción"
        return 1
    fi
    
    cd - > /dev/null
    return 0
}

# Publicar smali
publish_project "smali" "~/smali"

# Publicar multidexlib2
publish_project "multidexlib2" "~/multidexlib2"

echo ""
echo "=== Resumen ==="
echo "1. Smali: https://maven.pkg.github.com/CUPUL-MIU-04/smali/com/revoid/smali/2.5.3-a3836654/"
echo "2. Multidexlib2: https://maven.pkg.github.com/CUPUL-MIU-04/multidexlib2/com/revoid/multidexlib2/2.5.3-a3836654-SNAPSHOT/"
echo ""
echo "Para usar en Revoid-patcher:"
echo "dependencies {"
echo "    implementation 'com.revoid:smali:2.5.3-a3836654'"
echo "    implementation 'com.revoid:multidexlib2:2.5.3-a3836654-SNAPSHOT'"
echo "}"
