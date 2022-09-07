package io.quarkiverse.sqlpad.devservices.deployment;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SqlPadDataSourceBuildItem that = (SqlPadDataSourceBuildItem) o;
        return port == that.port && Objects.equals(name, that.name) && Objects.equals(driver, that.driver)
                && Objects.equals(host, that.host) && Objects.equals(database, that.database)
                && Objects.equals(user, that.user) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, driver, host, port, database, user, password);
    }

    @Override
    public String toString() {
        return "SqlPadDataSourceBuildItem{" +
                "name='" + name + '\'' +
                ", driver='" + driver + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
