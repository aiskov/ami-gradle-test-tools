package com.aiskov.gradle.plugins.asserts;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import static com.aiskov.gradle.plugins.asserts.AssertionGenerator.generateAssertionClass;

public class AssertsGenerationTask extends DefaultTask {
    public AssertsGenerationTask() {
        super.setGroup("source generation");
        super.dependsOn(super.getProject().getTasks().getByName("compileJava"));
    }

    @TaskAction
    public void generate() {
        AssertsGenerationExtension config = super.getProject().getExtensions().getByType(AssertsGenerationExtension.class);
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
            List<? extends Class<?>> allClasses = config.getTargetClasses().stream()
                    .map(targetClass -> {
                        try {
                            return classLoader.loadClass(targetClass);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            String absolutePath = super.getProject().getProjectDir().toPath().resolve(config.getOutputDir()).toString();
            allClasses.forEach(target -> generateAssertionClass(target, absolutePath, allClasses));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
