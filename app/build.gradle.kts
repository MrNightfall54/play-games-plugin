import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val pluginName : String by project
val logName : String by project

android {
    namespace = "com.dynamikwizard.playgamesplugin"
    //noinspection GradleDependency
    compileSdk = 36

    defaultConfig {
        minSdk = 25
        buildConfigField("String", "PLUGIN_NAME", "\"$pluginName\"")
        buildConfigField("String", "LOG_NAME", "\"$logName\"")
        manifestPlaceholders["plugin_name"] = pluginName
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
    kotlin {
        compilerOptions{
            jvmTarget = JvmTarget.JVM_17
        }
    }
    libraryVariants.all {
        outputs.all {
            val variant = this
            val outputImpl = variant as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            outputImpl.outputFileName = "${pluginName}-${variant.name}.aar"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    compileOnly(files("libs/godot-lib.aar"))
    implementation(libs.play.game)
}

val godotAddonsDir =
    rootProject.layout.projectDirectory.dir(
        "../game/addons/play_games"
    )

fun registerGodotCopyTask(variant: String) {
    val variantCapitalized =
        variant.replaceFirstChar {
            it.uppercase()
        }

    val copyTask = tasks.register<Copy>(
        "copy${variantCapitalized}AarToGodot"
    ) {
        group = "godot"

        description =
            "Copy $variant AAR to Godot addons"

        from(
            layout.buildDirectory.file(
                "outputs/aar/${pluginName}-${variant}.aar"
            )
        )

        into(godotAddonsDir)
    }

    tasks.matching {
        it.name == "assemble$variantCapitalized"
    }.configureEach {
        finalizedBy(copyTask)
    }
}

registerGodotCopyTask("debug")
registerGodotCopyTask("release")
