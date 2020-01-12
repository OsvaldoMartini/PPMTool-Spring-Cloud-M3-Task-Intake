package com.shifthunter.ppmtool;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.task.launcher.TaskLaunchRequest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Source.class)  //Bridging between here and the 'BUS' RabbitMQ source class for the stream
public class TaskProcessor {

	@Autowired
	private Source source;

	// Publish the Message to the Queue
	public void publishRequest(String payload) {

		//maven://[groupid]:[artifactid]:jar:[version]
		//String url = "maven://pluralsight.demo:pluralsight-springcloud-m3-task:jar:0.0.1-SNAPSHOT";
		
		String javaHome = System.getenv("JAVA_HOME");
		System.out.println("JAVA_HOME " + javaHome);
		
		String mavenHome = System.getenv("MAVEN_HOME");
		System.out.println("MAVEN_HOME " + mavenHome);
		
		String url = "maven://com.shifthunter.tasks:ppmtool-task:jar:0.0.1-SNAPSHOT";
		//String url = "classpath://ppmtool-task-0.0.1-SNAPSHOT.jar";
		//String url = "classpath://com.shifthunter.tasks:ppmtool-task:jar:0.0.1-SNAPSHOT";
		//String url = "jar:file:/D://Maven-Repo/com/shifthunter/tasks/ppmtool-task/0.0.1-SNAPSHOT/ppmtool-task-0.0.1-SNAPSHOT.jar!/";
		//URL url = new URL("jar:file:/D://Maven-Repo/com/shifthunter/tasks/ppmtool-task/0.0.1-SNAPSHOT/ppmtool-task-0.0.1-SNAPSHOT.jar!/");
		//String url = "maven://com.shifthunter.ppmtool:task-regexp:jar:0.0.1-SNAPSHOT";

		List<String> input = new ArrayList<String>(Arrays.asList(payload.split(",")));

		System.out.println("input.size" + input.size());
		
		
		TaskLaunchRequest request = new TaskLaunchRequest(url, input, null, null);

		System.out.println("created task launch request ...");

		// I am sending this message to the Source -> (RabbitMQ Exchange)
		GenericMessage<TaskLaunchRequest> message = new GenericMessage<TaskLaunchRequest>(request);
		
		
		
		this.source.output().send(message);
	}

	public InputStream getFileFromPath() {
		InputStream in = null;
		URL inputURL = null;
		String inputFile = "jar:file:/D:/Maven-Repo/com/shifthunter/tasks/ppmtool-task/0.0.1-SNAPSHOT/ppmtool-task-0.0.1-SNAPSHOT.jar";
		if (inputFile.startsWith("jar:")){
		  try {
			  inputURL = new URL(inputFile);
		    JarURLConnection conn = (JarURLConnection)inputURL.openConnection();
		    return in = conn.getInputStream();
		  } catch (MalformedURLException e1) {
		    System.err.println("Malformed input URL: "+inputURL);
		    return in;
		  } catch (IOException e1) {
		    System.err.println("IO error open connection");
		    return in;
		  }
		}
		return in; 
	}
	
}
