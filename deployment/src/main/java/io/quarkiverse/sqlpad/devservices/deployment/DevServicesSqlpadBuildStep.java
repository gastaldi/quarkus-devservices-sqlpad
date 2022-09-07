package io.quarkiverse.sqlpad.devservices.deployment;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.datasource.deployment.spi.DevServicesDatasourceResultBuildItem;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CuratedApplicationShutdownBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;

public final class DevServicesSqlpadBuildStep {

    private static final Logger log = Logger.getLogger(DevServicesSqlpadBuildStep.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("devservices-sqlpad");
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    SqlPadDataSourceBuildItem generateDevServiceDataSource(
            Optional<DevServicesDatasourceResultBuildItem> item) {
        if (item.isEmpty()) {
            return null;
        }
        DevServicesDatasourceResultBuildItem.DbResult defaultDatasource = item.get().getDefaultDatasource();
        if (defaultDatasource == null) {
            return null;
        }
        Map<String, String> configProperties = defaultDatasource.getConfigProperties();
        String jdbcUrl = configProperties.get("quarkus.datasource.jdbc.url");
        URI uri = URI.create(jdbcUrl.substring(5));
        return new SqlPadDataSourceBuildItem("DevServices",
                "postgres",
                uri.getHost(),
                uri.getPort(),
                "default",
                configProperties.get("quarkus.datasource.username"),
                configProperties.get("quarkus.datasource.password"));
    }

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    ServiceStartBuildItem enableSqlPad(List<SqlPadDataSourceBuildItem> dataSources,
            CuratedApplicationShutdownBuildItem closeBuildItem) {
        System.out.println(dataSources);
        //TODO: Choose image version?
        DockerImageName dockerImageName = DockerImageName.parse("sqlpad/sqlpad");
        Map<String, String> env = new HashMap<>();
        // TODO: Make it configurable?
        //        env.put("SQLPAD_ADMIN", "admin@sqlpad.com");
        //        env.put("SQLPAD_ADMIN_PASSWORD", "admin");
        env.put("SQLPAD_AUTH_DISABLED", "true");
        env.put("SQLPAD_AUTH_DISABLED_DEFAULT_ROLE", "admin");
        String pattern = "SQLPAD_CONNECTIONS__%s__%s";
        for (int i = 0; i < dataSources.size(); i++) {
            String key = "con" + i;
            SqlPadDataSourceBuildItem item = dataSources.get(i);
            env.put(String.format(pattern, key, "name"), item.getName());
            env.put(String.format(pattern, key, "driver"), item.getDriver());
            env.put(String.format(pattern, key, "host"), item.getHost());
            env.put(String.format(pattern, key, "port"), String.valueOf(item.getPort()));
            env.put(String.format(pattern, key, "database"), item.getDatabase());
            env.put(String.format(pattern, key, "username"), item.getUser());
            env.put(String.format(pattern, key, "password"), item.getPassword());
        }
        System.out.println(env);
        GenericContainer container = new GenericContainer(dockerImageName)
                .withEnv(env)
                .withExposedPorts(3000)
                .withAccessToHost(true)
                .withNetwork(Network.SHARED)
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("sqlpad")));
        container.start();
        String url = container.getHost() + ":" + container.getFirstMappedPort();
        System.out.println("URL : http://" + url);
        closeBuildItem.addCloseTask(new Runnable() {
            @Override
            public void run() {
                log.info("Closing SqlPad!");
                container.stop();
            }
        }, false);
        return new ServiceStartBuildItem("sqlpad");
    }
}
