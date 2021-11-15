package net.troja;

import junit.framework.Assert;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.MojoExecutionEvent;
import org.apache.maven.execution.MojoExecutionListener;
import org.apache.maven.execution.scope.internal.MojoExecutionScope;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.session.scope.internal.SessionScope;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.DefaultRepositorySystemSession;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

public class BaseMojoTest extends AbstractMojoTestCase {

    public BaseMojoTest() {
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputStream getPublicDescriptorStream() throws Exception {
        return super.getPublicDescriptorStream();
    }

    public String getPluginDescriptorPath() {
        return super.getPluginDescriptorPath();
    }

    public String getPluginDescriptorLocation() {
        return super.getPluginDescriptorLocation();
    }

    public void setupContainer() {
        super.setupContainer();
    }

    public ContainerConfiguration setupContainerConfiguration() {
        return super.setupContainerConfiguration();
    }

    public PlexusContainer getContainer() {
        return super.getContainer();
    }

    public Mojo lookupMojo(String goal, String pluginPom)  {
        return lookupMojo(goal, pluginPom);
    }

    public Mojo lookupEmptyMojo(String goal, String pluginPom) throws Exception {
        return lookupEmptyMojo(goal, new File(pluginPom));
    }

    public Mojo lookupMojo(String goal, File pom) {
        return lookupMojo(goal, pom);
    }

    public Mojo lookupEmptyMojo(String goal, File pom) {
        return lookupEmptyMojo(goal, pom);
    }

    public Mojo lookupMojo(String groupId, String artifactId, String version, String goal, PlexusConfiguration pluginConfiguration) {
        return lookupMojo(groupId, artifactId, version, goal, pluginConfiguration);
    }

    public Mojo lookupConfiguredMojo(MavenProject project, String goal) {
        return lookupConfiguredMojo(project, goal);
    }

    public Mojo lookupConfiguredMojo(MavenSession session, MojoExecution execution) throws Exception {
        return super.lookupConfiguredMojo(session, execution);
    }

    public MavenSession newMavenSession(MavenProject project) {
        return super.newMavenSession(project);
    }

    public MojoExecution newMojoExecution(String goal) {
        return super.newMojoExecution(goal);
    }

    public PlexusConfiguration extractPluginConfiguration(String artifactId, File pom) throws Exception {
        return super.extractPluginConfiguration(artifactId, pom);
    }

    public PlexusConfiguration extractPluginConfiguration(String artifactId, Xpp3Dom pomDom) throws Exception {
        return super.extractPluginConfiguration(artifactId, pomDom);
    }

    public Mojo configureMojo(Mojo mojo, String artifactId, File pom) throws Exception {
        return super.configureMojo(mojo, artifactId, pom);
    }

    public Mojo configureMojo(Mojo mojo, PlexusConfiguration pluginConfiguration) throws Exception {
        return super.configureMojo(mojo, pluginConfiguration);
    }

    public Object getVariableValueFromObject(Object object, String variable) throws IllegalAccessException {
        return super.getVariableValueFromObject(object, variable);
    }

    public Map<String, Object> getVariablesAndValuesFromObject(Object object) throws IllegalAccessException {
        return super.getVariablesAndValuesFromObject(object);
    }

    public Map<String, Object> getVariablesAndValuesFromObject(Class<?> clazz, Object object) throws IllegalAccessException {
        return super.getVariablesAndValuesFromObject(clazz, object);
    }

    public void setVariableValueToObject(Object object, String variable, Object value) throws IllegalAccessException {
        super.setVariableValueToObject(object, variable, value);
    }

    public MavenProject readMavenProject(File basedir) throws Exception {
        File pom = new File(basedir, "pom.xml");
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setBaseDirectory(basedir);
        ProjectBuildingRequest configuration = request.getProjectBuildingRequest();
        configuration.setRepositorySession(new DefaultRepositorySystemSession());
        MavenProject project = this.lookup(ProjectBuilder.class).build(pom, configuration).getProject();
        Assert.assertNotNull(project);
        return project;
    }

    public void executeMojo(File basedir, String goal) throws Exception {
        MavenProject project = this.readMavenProject(basedir);
        MavenSession session = this.newMavenSession(project);
        MojoExecution execution = this.newMojoExecution(goal);
        this.executeMojo(session, project, execution);
    }

    public Mojo lookupConfiguredMojo(File basedir, String goal) throws Exception {
        MavenProject project = this.readMavenProject(basedir);
        MavenSession session = this.newMavenSession(project);
        MojoExecution execution = this.newMojoExecution(goal);
        return this.lookupConfiguredMojo(session, execution);
    }

    public void executeMojo(MavenProject project, String goal, Xpp3Dom... parameters) throws Exception {
        MavenSession session = this.newMavenSession(project);
        this.executeMojo(session, project, goal, parameters);
    }

    public void executeMojo(MavenSession session, MavenProject project, String goal, Xpp3Dom... parameters) throws Exception {
        MojoExecution execution = this.newMojoExecution(goal);
        if (parameters != null) {
            Xpp3Dom configuration = execution.getConfiguration();
            Xpp3Dom[] arr$ = parameters;
            int len$ = parameters.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Xpp3Dom parameter = arr$[i$];
                configuration.addChild(parameter);
            }
        }

        this.executeMojo(session, project, execution);
    }

    public void executeMojo(MavenSession session, MavenProject project, MojoExecution execution) throws Exception {
        SessionScope sessionScope = this.lookup(SessionScope.class);

        try {
            sessionScope.enter();
            sessionScope.seed(MavenSession.class, session);
            MojoExecutionScope executionScope = this.lookup(MojoExecutionScope.class);

            try {
                executionScope.enter();
                executionScope.seed(MavenProject.class, project);
                executionScope.seed(MojoExecution.class, execution);
                Mojo mojo = this.lookupConfiguredMojo(session, execution);
                mojo.execute();
                MojoExecutionEvent event = new MojoExecutionEvent(session, project, execution, mojo);
                Iterator i$ = this.getContainer().lookupList(MojoExecutionListener.class).iterator();

                while(i$.hasNext()) {
                    MojoExecutionListener listener = (MojoExecutionListener)i$.next();
                    listener.afterMojoExecutionSuccess(event);
                }
            } finally {
                executionScope.exit();
            }
        } finally {
            sessionScope.exit();
        }

    }
}
