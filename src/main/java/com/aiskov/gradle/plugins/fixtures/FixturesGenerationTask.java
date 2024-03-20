package com.aiskov.gradle.plugins.fixtures;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.aiskov.gradle.plugins.fixtures.FixturesGenerator.generateFixtureClass;

public class FixturesGenerationTask extends DefaultTask {
    public FixturesGenerationTask() {
        super.setGroup("source generation");
        super.dependsOn(super.getProject().getTasks().getByName("compileJava"));
    }

    @TaskAction
    public void generate() {
        FixturesGenerationExtension config = super.getProject().getExtensions().getByType(FixturesGenerationExtension.class);
        SourceSetContainer sourceSets = super.getProject().getExtensions().getByType(SourceSetContainer.class);

        List<URL> urls = new LinkedList<>();
        sourceSets.getByName("main").getRuntimeClasspath().forEach(file -> {
            try {
                urls.add(file.toURI().toURL());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        try (URLClassLoader classLoader = new URLClassLoader(urls.toArray(URL[]::new))) {
            List<Class<?>> allClasses = new CopyOnWriteArrayList<>(config.getTargetClasses().stream()
                    .map(targetClass -> {
                        try {
                            return classLoader.loadClass(targetClass);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList());

            String absolutePath = super.getProject().getProjectDir().toPath().resolve(config.getOutputDir()).toString();

            for (int i = 0; i < allClasses.size(); i++) {
                generateFixtureClass(allClasses.get(i), absolutePath, allClasses, config.getValueProviderSnippets());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
