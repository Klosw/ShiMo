plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.klosw"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.alibaba:fastjson:2.0.24")
    implementation("cn.hutool:hutool-all:5.8.12")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("org.projectlombok:lombok:1.18.26")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}