package pl.edu.agh.kis.randdpipelinebpmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@SpringBootApplication
public class RAndDPipelineBpmnApplication {

	public static void main(String[] args) {
		SpringApplication.run(RAndDPipelineBpmnApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> route() {
		return RouterFunctions.route(RequestPredicates.GET("/R-and-D"),
				rq -> ServerResponse.ok().body(BodyInserters.fromValue("Welcome in BPMN in pipeline!")));
	}

}
