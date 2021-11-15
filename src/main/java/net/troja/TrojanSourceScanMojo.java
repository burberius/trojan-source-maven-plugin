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
import java.util.List;

/**
 * Goal scans the source files for signs of trojan source.
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class TrojanSourceScanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    public void execute() throws MojoExecutionException {
        Log log = getLog();
        Path sourcePath = Paths.get(basedir.getPath(), "/src/main/java");

        SourceAnalyzer analyzer = new SourceAnalyzer(log);
        List<String> filesWithBidi = analyzer.findFilesWithBidi(sourcePath, "java");

        if(!filesWithBidi.isEmpty()) {
            log.error("The following files contain bidi characters, please check:");
            filesWithBidi.forEach(file -> log.error(" * " + file));
            throw new MojoExecutionException("Possible trojan source found");
        } else {
            log.info("No trojan source found");
        }
    }
}
