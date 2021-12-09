plugins {
    java
    id("io.izzel.taboolib") version "1.31"
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
}

taboolib {
    description {
        contributors {
            name("kzheart")
        }
    }
    install("common")
    install("common-5")
    install("module-configuration")
    install("module-database")
    install("module-lang")
    install("module-chat")
    install("module-nms")
    install("module-nms-util")
    install("module-porticus")
    install("platform-bukkit", "platform-bungee")
    install("expansion-command-helper")
    install("module-ui")
    version = "6.0.6-3"
    classifier = null
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    implementation("junit:junit:4.13.1")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11200:11200:all")
    testCompileOnly("ink.ptms.core:v11200:11200:all")
//    compileOnly("ink.ptms.core:v11701:11701:mapped")
//    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    compileOnly("net.md-5:bungeecord-bootstrap:1.12-SNAPSHOT")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
