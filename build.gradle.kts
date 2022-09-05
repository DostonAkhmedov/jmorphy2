group = "company.evo.jmorphy2"

subprojects {
    apply(plugin="java-library")

    group = rootProject.group

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        exclude("**/*Benchmark*")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }
}
