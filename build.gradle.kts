import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository

import kotlin.reflect.KProperty
import kotlin.properties.ReadOnlyProperty
import org.gradle.internal.Cast.uncheckedCast

val kotlinVersion by property<String>()

buildscript {
    repositories {
        maven { setUrl("https://repo.gradle.org/gradle/repo") } //kotlinEAP11()
        gradleScriptKotlin()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

apply {
    plugin("kotlin")
}

repositories {
    kotlinEAP11()
    kotlinFutures()
    jcenter()
    gradleScriptKotlin()
}

dependencies {
    compile(kotlinModule("stdlib", kotlinVersion))
    compile("com.rahulrav:com.rahulrav.futures:1.0.6")

    testCompile("junit:junit:4.12")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

class property<T> : ReadOnlyProperty<Project, T> {
     override fun getValue(thisRef: Project, property: KProperty<*>): T {
        return uncheckedCast<T>(thisRef.properties[property.name])
    }
}

fun KotlinRepositoryHandler.kotlinEAP11() = maven { setUrl("https://repo.gradle.org/gradle/repo") }
fun KotlinRepositoryHandler.kotlinFutures() = maven { setUrl("https://dl.bintray.com/rahulrav/kotlin-futures") }
