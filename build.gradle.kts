import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.flywaydb.flyway") version "7.7.2"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"
	kotlin("plugin.jpa") version "1.4.31"
}

group = "me.hl"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("io.r2dbc:r2dbc-postgresql")
	implementation("org.springframework.data:spring-data-r2dbc")
	implementation("org.flywaydb:flyway-core:7.7.2")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
	implementation("javax.validation:validation-api:2.0.1.Final")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("junit:junit")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	constraints {
		implementation("org.apache.logging.log4j:log4j-core") {
			version {
				strictly("[2.17, 3[")
				prefer("2.17.0")
			}
			because("CVE-2021-44228, CVE-2021-45046, CVE-2021-45105: Log4j vulnerable to remote code execution and other critical security vulnerabilities")
		}
	}
}
tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "15"
	}
}

tasks.withType<Test> {
	useJUnit()
}

flyway {
	url = "jdbc:postgresql://localhost:5432/postgres"
	user = "ITEM_USER"
	password = "ITEM_PASS\$"
	baselineOnMigrate = true
	locations = arrayOf("filesystem:src/main/resources/db/migration")
}
