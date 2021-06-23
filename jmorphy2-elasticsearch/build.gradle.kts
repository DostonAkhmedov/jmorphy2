buildscript {
    repositories {
        mavenCentral()
        maven(url="http://maven.restlet.org")
        jcenter()
    }
    dependencies {
        classpath("org.elasticsearch.gradle:build-tools:${project.getElasticsearchVersion()}")
    }
}

apply(plugin="idea")
apply(plugin="elasticsearch.esplugin")

configure<org.elasticsearch.gradle.plugin.PluginPropertiesExtension> {
    name = "analysis-jmorphy2"
    description = "Jmorphy2 plugin for ElasticSearch"
    classname = "company.evo.jmorphy2.elasticsearch.plugin.AnalysisJmorphy2Plugin"
    version = project.version.toString()
    licenseFile = project.file("LICENSE.txt")
    noticeFile = project.file("NOTICE.txt")
}


val libVersion: String = project.getLibraryVersion()

version = "${libVersion}-es${project.getElasticsearchVersion()}"

dependencies {
    compile(project(":jmorphy2-lucene"))
    compile(project(":jmorphy2-dicts-ru"))
    compile(project(":jmorphy2-dicts-uk"))
}

tasks.findByName("validateNebulaPom")?.enabled = false