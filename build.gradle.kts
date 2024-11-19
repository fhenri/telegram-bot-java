plugins {
    id("java")
    id("application")
}

group = "io.cloud06.gmeet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.json:json:20240303")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("io.cloud06.gmeet.GMeetBot")
}

tasks.test {
    useJUnitPlatform()
}