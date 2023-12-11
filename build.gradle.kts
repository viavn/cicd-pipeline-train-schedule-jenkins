import com.github.gradle.node.npm.proxy.ProxySettings
import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.npm.task.NpxTask
import com.github.gradle.node.task.NodeTask

// This file shows how to use this plugin with the Kotlin DSL.
// All the properties are set, most of the time with the default value.
// /!\ We recommend to set only the values for which the default value is not satisfying.

plugins {
    base

    // You have to specify the plugin version, for instance
    // id("com.github.node-gradle.node") version "3.0.0"
    // This works as is in the integration tests context
    id("com.github.node-gradle.node") version "7.0.1"
}

node {
    version.set("18.17.1")
    npmVersion.set("")
    npmInstallCommand.set("install")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    workDir.set(file("${project.projectDir}/.cache/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/.cache/npm"))
    nodeProjectDir.set(file("${project.projectDir}"))
    nodeProxySettings.set(ProxySettings.SMART)
}

tasks.npmInstall {
    nodeModulesOutputFilter {
        exclude("notExistingFile")
    }
}

val testTaskUsingNpx = tasks.register<NpxTask>("testNpx") {
    dependsOn(tasks.npmInstall)
    command.set("mocha")
    args.set(listOf("test", "--grep", "should say hello"))
    ignoreExitValue.set(false)
    environment.set(mapOf("MY_CUSTOM_VARIABLE" to "hello"))
    workingDir.set(projectDir)
    execOverrides {
        standardOutput = System.out
    }
    inputs.dir("node_modules")
    inputs.file("package.json")
    inputs.dir("src")
    inputs.dir("test")
    outputs.upToDateWhen {
        true
    }
}

val testTaskUsingNpm = tasks.register<NpmTask>("testNpm") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "test"))
    args.set(listOf("test"))
    ignoreExitValue.set(false)
    environment.set(mapOf("MY_CUSTOM_VARIABLE" to "hello"))
    workingDir.set(projectDir)
    execOverrides {
        standardOutput = System.out
    }
    inputs.dir("node_modules")
    inputs.file("package.json")
    inputs.dir("test")
    outputs.upToDateWhen {
        true
    }
}

tasks.register<NodeTask>("run") {
    dependsOn(testTaskUsingNpx, testTaskUsingNpm)
    script.set(file("src/main.js"))
    args.set(listOf("Bobby"))
    ignoreExitValue.set(false)
    environment.set(mapOf("MY_CUSTOM_VARIABLE" to "hello"))
    workingDir.set(projectDir)
    execOverrides {
        standardOutput = System.out
    }
    inputs.dir("src")
    outputs.upToDateWhen {
        false
    }
}

val buildTaskUsingNpx = tasks.register<NpxTask>("buildNpx") {
    dependsOn(tasks.npmInstall)
    command.set("babel")
    args.set(listOf("src", "--out-dir", "${buildDir}/npx-output"))
    inputs.dir("src")
    outputs.dir("${buildDir}/npx-output")
}

val buildTaskUsingNpm = tasks.register<NpmTask>("buildNpm") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    args.set(listOf("--", "--out-dir", "${buildDir}/npm-output"))
    inputs.dir("src")
    outputs.dir("${buildDir}/npm-output")
}

tasks.register<Zip>("package") {
    archiveFileName.set("app.zip")
    destinationDirectory.set(buildDir)
    from(buildTaskUsingNpx) {
        into("npx")
    }
    from(buildTaskUsingNpm) {
        into("npm")
    }
}

val webAssetPatterns = Action<CopySpec> {
    include("app.js", "*.json", "bin/**", "data/**", "public/**", "routes/**", "views/**")
}

tasks.register<Zip>("myZip") {
    dependsOn(tasks.npmInstall)
    dependsOn(testTaskUsingNpm)
    archiveFileName = "trainSchedule.zip"
    destinationDirectory = file("dist")

    from(".", webAssetPatterns)

    doLast {
        println(" Artefato gerado ^^D ")
    }
}
