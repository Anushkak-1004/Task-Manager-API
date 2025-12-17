# Database Configuration Guide

This document explains how to run the Task Manager API with different database profiles for development and production environments.

## Overview

The Task Manager API supports two profiles:
- **dev** (default): Uses H2 in-memory database for development
- **prod**: Uses MySQL or PostgreSQL for production

## Development Profile (Default)

The development profile uses H2 in-memory database and is configured by default in `application.properties`.

### Running in Development Mode

```bash
# Run with default profile (dev)
mvn spring-boot:run

# Or explicitly specify dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Development Configuration

- **Database**: H2 in-memory
- **URL**: `jdbc:h2:mem:taskdb`
- **Username**: `sa`
- **Password**: (empty)
- **H2 Console**: Enabled at `http://localhost:8080/h2-console`
- **Schema Management**: `create-drop` (recreates schema on each startup)
- **SQL Logging**: Enabled for debugging

### Accessing H2 Console

1. Start the application
2. Navigate to `http://localhost:8080/h2-console`
3. Use the following connection details:
   - JDBC URL: `jdbc:h2:mem:taskdb`
   - Username: `sa`
   - Password: (leave empty)

## Production Profile

The production profile supports both MySQL and PostgreSQL databases and is configured in `application-prod.properties`.

### Required Environment Variables

Before running in production mode, you must set the following environment variables:

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `DB_URL` | Yes | Database connection URL | `jdbc:mysql://localhost:3306/taskdb` or `jdbc:postgresql://localhost:5432/taskdb` |
| `DB_USERNAME` | Yes | Database username | `taskmanager_user` |
| `DB_PASSWORD` | Yes | Database password | `secure_password_here` |
| `DB_DRIVER` | No | JDBC driver class (defaults to MySQL) | `com.mysql.cj.jdbc.Driver` or `org.postgresql.Driver` |
| `DB_DIALECT` | No | Hibernate dialect (auto-detected) | `org.hibernate.dialect.MySQLDialect` or `org.hibernate.dialect.PostgreSQLDialect` |
| `SERVER_PORT` | No | Server port (defaults to 8080) | `8080` |

### Running with MySQL

#### 1. Set Environment Variables (Linux/Mac)

```bash
export DB_URL=jdbc:mysql://localhost:3306/taskdb
export DB_USERNAME=taskmanager_user
export DB_PASSWORD=your_secure_password
export DB_DRIVER=com.mysql.cj.jdbc.Driver
export DB_DIALECT=org.hibernate.dialect.MySQLDialect
```

#### 2. Set Environment Variables (Windows CMD)

```cmd
set DB_URL=jdbc:mysql://localhost:3306/taskdb
set DB_USERNAME=taskmanager_user
set DB_PASSWORD=your_secure_password
set DB_DRIVER=com.mysql.cj.jdbc.Driver
set DB_DIALECT=org.hibernate.dialect.MySQLDialect
```

#### 3. Set Environment Variables (Windows PowerShell)

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/taskdb"
$env:DB_USERNAME="taskmanager_user"
$env:DB_PASSWORD="your_secure_password"
$env:DB_DRIVER="com.mysql.cj.jdbc.Driver"
$env:DB_DIALECT="org.hibernate.dialect.MySQLDialect"
```

#### 4. Create MySQL Database

```sql
CREATE DATABASE taskdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'taskmanager_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON taskdb.* TO 'taskmanager_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 5. Run Application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Running with PostgreSQL

#### 1. Set Environment Variables (Linux/Mac)

```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskdb
export DB_USERNAME=taskmanager_user
export DB_PASSWORD=your_secure_password
export DB_DRIVER=org.postgresql.Driver
export DB_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

#### 2. Set Environment Variables (Windows CMD)

```cmd
set DB_URL=jdbc:postgresql://localhost:5432/taskdb
set DB_USERNAME=taskmanager_user
set DB_PASSWORD=your_secure_password
set DB_DRIVER=org.postgresql.Driver
set DB_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

#### 3. Set Environment Variables (Windows PowerShell)

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/taskdb"
$env:DB_USERNAME="taskmanager_user"
$env:DB_PASSWORD="your_secure_password"
$env:DB_DRIVER="org.postgresql.Driver"
$env:DB_DIALECT="org.hibernate.dialect.PostgreSQLDialect"
```

#### 4. Create PostgreSQL Database

```sql
CREATE DATABASE taskdb;
CREATE USER taskmanager_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE taskdb TO taskmanager_user;
```

#### 5. Run Application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Production Configuration Details

### Schema Management

- **Mode**: `update` (automatically updates schema without dropping data)
- The application will create tables on first run
- Schema changes are applied incrementally on subsequent runs
- **Important**: For production deployments, consider using database migration tools like Flyway or Liquibase for better control

### Connection Pooling

The production profile uses HikariCP (Spring Boot default) with the following settings:
- Maximum pool size: 10 connections
- Minimum idle connections: 5
- Connection timeout: 20 seconds
- Idle timeout: 5 minutes

### Security Considerations

1. **Never commit credentials**: Always use environment variables for sensitive data
2. **Use strong passwords**: Ensure database passwords are complex and secure
3. **Restrict database access**: Configure firewall rules to limit database access
4. **Use SSL/TLS**: For remote database connections, enable SSL encryption
5. **Regular backups**: Implement automated backup strategies for production data

### Logging

Production logging is configured for optimal performance:
- SQL logging: Disabled (set to WARN level)
- Application logging: INFO level
- SQL formatting: Disabled

## Running as JAR

### Build the Application

```bash
mvn clean package
```

### Run with Production Profile

```bash
# Set environment variables first, then run
java -jar target/task-manager-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Docker Deployment Example

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/task-manager-api-0.0.1-SNAPSHOT.jar app.jar

# Environment variables will be provided by Docker Compose or Kubernetes
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose Example with MySQL

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: taskdb
      MYSQL_USER: taskmanager_user
      MYSQL_PASSWORD: secure_password
      MYSQL_ROOT_PASSWORD: root_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:mysql://mysql:3306/taskdb
      DB_USERNAME: taskmanager_user
      DB_PASSWORD: secure_password
      DB_DRIVER: com.mysql.cj.jdbc.Driver
    depends_on:
      - mysql

volumes:
  mysql_data:
```

## Troubleshooting

### Connection Issues

**Problem**: Application fails to connect to database

**Solutions**:
1. Verify database is running: `mysql -u root -p` or `psql -U postgres`
2. Check environment variables are set correctly
3. Verify database user has proper permissions
4. Check firewall rules allow connections
5. Ensure database URL is correct (host, port, database name)

### Schema Issues

**Problem**: Tables not created or schema mismatch

**Solutions**:
1. Check `spring.jpa.hibernate.ddl-auto` is set to `update`
2. Verify database user has CREATE/ALTER permissions
3. Review application logs for Hibernate errors
4. Consider manually creating schema or using migration tools

### Performance Issues

**Problem**: Slow database queries

**Solutions**:
1. Adjust connection pool settings in `application-prod.properties`
2. Add database indexes for frequently queried columns
3. Enable query logging temporarily to identify slow queries
4. Monitor database performance metrics

## Summary

- **Development**: Use default profile with H2 for quick local development
- **Production**: Use `prod` profile with MySQL or PostgreSQL
- **Always**: Use environment variables for sensitive configuration
- **Security**: Never commit credentials to version control

For additional help, refer to the [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html).
