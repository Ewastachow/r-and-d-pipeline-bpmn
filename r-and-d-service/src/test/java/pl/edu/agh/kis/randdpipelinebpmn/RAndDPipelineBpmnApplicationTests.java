package pl.edu.agh.kis.randdpipelinebpmn;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootTest
@AutoConfigureWebTestClient
public class RAndDPipelineBpmnApplicationTests extends RAndDPipelineBpmnApplication {

	@Test
	public void contextLoads() {
		final RouterFunction<ServerResponse> route = route();
	}

}
