import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

import kotlin.reflect.KProperty
import kotlin.properties.ReadOnlyProperty
import org.gradle.internal.Cast.uncheckedCast

val kotlinVersion by property<String>()
val projectGroup by property<String>()
val projectName by property<String>()
val projectVersion by property<String>()

buildscript {
    repositories {
        maven { setUrl("https://repo.gradle.org/gradle/repo") } //kotlinEAP11()
        jcenter()
        gradleScriptKotlin()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.1")
    }
}

apply {
    plugin("kotlin")
    plugin("com.jfrog.bintray")
    plugin("maven-publish")
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


val sourcesJar = task<Jar>("sourcesJar") {
    dependsOn("classes")

    from(sourceSets("main").allSource)
    classifier = "sources"
}

configure<PublishingExtension>() {
    publications {
        create<MavenPublication>("JCenterPublication") {
            from(components.getByName("java"))
            artifact(sourcesJar)

            groupId = projectGroup
            artifactId = projectName
            version = projectVersion
        }
    }
}

configure<BintrayExtension> {
    user = project.property("jcenter.personal.user").toString()
    key = project.property("jcenter.personal.api_key").toString()
    setPublications("JCenterPublication")
    pkg.apply {
        repo = "maven"
        name = "async-under8"
        desc = "Kotlin coroutine async without java 8 limitation"
        websiteUrl = "https://github.com/fboldog/async-under8"
        vcsUrl = "https://github.com/fboldog/async-under8"
        publish = true
        setLabels("kotlin")
        setLicenses("Apache-2.0")
        publicDownloadNumbers = true
        version.apply {
            name = projectVersion
            desc = "Kotlin coroutine async without java 8 limitation"
        }
    }
}

class property<T> : ReadOnlyProperty<Project, T> {
     override fun getValue(thisRef: Project, property: KProperty<*>): T {
        return uncheckedCast<T>(thisRef.properties[property.name])
    }
}

fun KotlinRepositoryHandler.kotlinEAP11() = maven { setUrl("https://repo.gradle.org/gradle/repo") }
fun KotlinRepositoryHandler.kotlinFutures() = maven { setUrl("https://dl.bintray.com/rahulrav/kotlin-futures") }

inline fun Project.artifacts(configuration: KotlinArtifactsHandler.() -> Unit) =
        KotlinArtifactsHandler(artifacts).configuration()

class KotlinArtifactsHandler(val artifacts: ArtifactHandler) : ArtifactHandler by artifacts {

    operator fun String.invoke(dependencyNotation: Any): PublishArtifact =
            artifacts.add(this, dependencyNotation)

    inline operator fun invoke(configuration: KotlinArtifactsHandler.() -> Unit) =
            configuration()
}

fun sourceSets(name: String) = the<JavaPluginConvention>().sourceSets.getByName(name)