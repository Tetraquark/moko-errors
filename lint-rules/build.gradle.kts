plugins {
    `java-library`
    kotlin("jvm")
}

repositories {
    jcenter()
}

dependencies {
    compileOnly("com.android.tools.lint:lint-api:27.0.1")
    compileOnly("com.android.tools.lint:lint-checks:27.0.1")
}

tasks.jar {
    manifest {
        attributes("Lint-Registry-v2" to "ru.tetraquark.moko.errors.lint.CallRequiredRegistry")
    }
}

//java.sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_6
//java.targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_6
