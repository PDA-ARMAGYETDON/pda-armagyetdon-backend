package ARMAGYETDON.armagyeton_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ArmagyetonBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArmagyetonBackendApplication.class, args);
	}

}
