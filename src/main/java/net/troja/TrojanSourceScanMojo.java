package net.troja;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Goal scans the source files for signs of trojan source.
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class TrojanSourceScanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    /**
     * List of file extensions to scan.
     */
    @Parameter(readonly = true)
    private List<String> fileExtensions;

    /**
     * List of alternative directories to scan, relative to base directory containing the pom.xml.
     */
    @Parameter(readonly = true)
    private List<String> directories;

    /**
     * Also scan the tests? Default: true
     */
    @Parameter(defaultValue = "true", readonly = true)
    private boolean scanTests;

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        SourceScanner analyzer = new SourceScanner(log);
        List<String> filesWithBidi = analyzer.findFilesWithBidi(getPathsToScan(), getExtension());

        if(!filesWithBidi.isEmpty()) {
            log.error("The following file(s) contain bidi characters, please check:");
            filesWithBidi.forEach(file -> log.error(" * " + file));
            throw new MojoExecutionException("Possible trojan source found");
        } else {
            log.info("No trojan source found");
        }
    }

    private Collection<String> getExtension() {
        Set<String> extensions = new HashSet<>();
        extensions.add("java");
        if(fileExtensions != null && !fileExtensions.isEmpty()) {
            extensions.addAll(fileExtensions);
        }
        return extensions;
    }

    private Collection<Path> getPathsToScan() {
        Set<Path> pathsToScan = new HashSet<>();
        pathsToScan.add(Paths.get(basedir.getPath(), "/src/main/java"));
        if(scanTests) {
            pathsToScan.add(Paths.get(basedir.getPath(), "/src/test/java"));
        }
        if(directories != null && !directories.isEmpty()) {
            directories.forEach(dir -> pathsToScan.add(Paths.get(basedir.getPath(), dir)));
        }
        return pathsToScan;
    }
}
