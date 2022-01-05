package io.quarkiverse.sqlpad.devservices.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class SqlPadDataSourceBuildItem extends MultiBuildItem {
    private final String name;
    private final String driver;
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public SqlPadDataSourceBuildItem(String name, String driver, String host, int port, String database, String user,
            String password) {
        this.name = name;
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public String getDriver() {
        return driver;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
