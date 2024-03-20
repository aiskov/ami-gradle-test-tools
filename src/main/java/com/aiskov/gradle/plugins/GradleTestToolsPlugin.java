package com.aiskov.gradle.plugins;

import com.aiskov.gradle.plugins.asserts.AssertsGenerationTask;
import com.aiskov.gradle.plugins.asserts.AssertsGenerationExtension;
import com.aiskov.gradle.plugins.fixtures.FixturesGenerationExtension;
import com.aiskov.gradle.plugins.fixtures.FixturesGenerationTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

class GradleTestToolsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class);

        project.getExtensions().create("generateAssertions", AssertsGenerationExtension.class);
        project.getTasks().create("generateAssertions", AssertsGenerationTask.class);

        project.getExtensions().create("generateFixtures", FixturesGenerationExtension.class);
        project.getTasks().create("generateFixtures", FixturesGenerationTask.class);
    }
}
