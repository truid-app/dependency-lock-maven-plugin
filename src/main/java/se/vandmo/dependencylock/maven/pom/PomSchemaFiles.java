package se.vandmo.dependencylock.maven.pom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PomSchemaFiles {

    private static SchemaFile mavenSchemaFile;
    private static SchemaFile vandmoSchemaFile;

    public static void place(File parentDir) throws IOException {
        synchronized(PomSchemaFiles.class) {
            if(mavenSchemaFile == null) {
                mavenSchemaFile = new SchemaFile("/maven-v4_0_0_ext.xsd");
            }
            if(vandmoSchemaFile == null) {
                vandmoSchemaFile = new SchemaFile("/vandmo_dependencylock.xsd");
            }
        }
        mavenSchemaFile.placeFile(parentDir);
        vandmoSchemaFile.placeFile(parentDir);
    }

    private static class SchemaFile {
        private byte[] content;
        private String name;

        SchemaFile(String name) {
            this.name = name;
            try {
                this.content = DependenciesLockFilePom.class.getResourceAsStream(name).readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read resource: " + name, e);
            }
        }

        void placeFile(File dir) {
            try {
                File subFolder = new File(dir, "/maven-schemas");
                subFolder.mkdir();
                Files.write(new File(subFolder, name).toPath(), content);
            } catch (IOException e) {
                throw  new RuntimeException("Failed to write maven schema: " + name + " at location: " + dir.getAbsolutePath(), e);
            }
        }

    }

}
