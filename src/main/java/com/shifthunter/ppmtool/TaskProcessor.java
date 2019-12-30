package com.shifthunter.ppmtool;

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
				
		String url = "maven://com.shifthunter.ppmtool:ppmtool-task:jar:0.0.1-SNAPSHOT";
		//String url = "maven://com.shifthunter.ppmtool:task-regexp:jar:0.0.1-SNAPSHOT";

		List<String> input = new ArrayList<String>(Arrays.asList(payload.split(",")));

		System.out.println("input.size" + input.size());
		
		
		TaskLaunchRequest request = new TaskLaunchRequest(url, input, null, null);

		System.out.println("created task launch request ...");

		// I am sending this message to the Source -> (RabbitMQ Exchange)
		GenericMessage<TaskLaunchRequest> message = new GenericMessage<TaskLaunchRequest>(request);
		
		
		
		this.source.output().send(message);
	}

}
