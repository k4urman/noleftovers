// Location.java
package com.leftovers.app;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Data
public class Location {
    public void generateConfig() throws IOException {
        String configContent = """
        spring.datasource.url=jdbc:h2:mem:leftovers
        spring.datasource.driverClassName=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=
        spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
        spring.jpa.hibernate.ddl-auto=update
        spring.jpa.show-sql=true
        spring.h2.console.enabled=true
        spring.h2.console.path=/h2-console
        server.port=8080
        logging.level.org.springframework=INFO
        logging.level.com.leftovers.app=DEBUG
        """;
        
        Files.write(Paths.get("src/main/resources/application.properties"), configContent.getBytes());
    }
}
