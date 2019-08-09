
# Spring Database Evolutions

Versioning and evolution of source code have evolved a lot in recent years and today is already consolidated through tools such as git and svn, unfortunately this scenario is not so favorable for versioning and evolution of the database. There are tools proposed to accomplish this task like the one that has been used by the Play Framework for some years (Play evolutions). The purpose of this project is to adapt Play Evolutions for full functionality and Spring Boot compatibility through a self configurable Spring Boot project.

## Getting Started

### Maven

Download jar `database-evolutions.jar` and add to your project `pom.xml`.

```xml
<dependency>
	<groupId>com.evolutions.database</groupId>
	<artifactId>database-evolutions</artifactId>
	<scope>system</scope>
	<systemPath>/jar_folder/database-evolutions.jar</systemPath>
</dependency>
```
### Gradle

Download jar `database-evolutions.jar` and add to your project `build.gradle`.

```groovy
dependencies {
    implementation files('jar_folder/database-evolutions.jar')
}
```
### Spring 
Show Spring Boot how to find your configuration classes.
```java
@SpringBootApplication(scanBasePackages = {"com.evolutions.database.*"})
```
#### Evolutions Configuration
Add configuration variables to the application.properties of your Spring Boot project.
```properties
# Whether evolutions are enabled  
play.evolutions.enabled = true  
  
# Database schema in which the generated evolution and lock tables will be saved to  
play.evolutions.schema = public  
  
# Whether evolution updates should be performed with autocommit or in a manually managed transaction  
play.evolutions.autocommit = false  
  
# Whether locks should be used when apply evolutions.  If this is true, a locks table will be created, and will  
# be used to synchronise between multiple Play instances trying to apply evolutions.  Set this to true in a multi  
# node environment.  
play.evolutions.useLocks = false  
  
# Whether evolutions should be automatically applied.  In prod mode, this will only apply ups, in dev mode, it will  
# cause both ups and downs to be automatically applied.  
play.evolutions.autoApply = false  
  
# Whether downs should be automatically applied.  This must be used in combination with autoApply, and only applies  
# to prod mode.  
play.evolutions.autoApplyDowns = false  
  
# Whether evolutions should be skipped, if the scripts are all down.  
play.evolutions.skipApplyDownsOnly = false  
  
# Specifies the play.evolutions execution mode.  
# The allowed modes are dev, test, prod.  
play.evolutions.mode = dev  

# Set the path of your database evolution scripts within the project resources folder.  
play.evolutions.path = evolutions
```

### Prerequisites

Spring Database Evolutions works with `java` version 8 or later

```
java8+
```
### Managing database evolutions ([Taken from Play Framework](https://www.playframework.com/documentation/2.7.x/Evolutions))

#### Evolutions scripts

Play tracks your database evolutions using several evolutions script. These scripts are written in plain old SQL and should be located in the `resources/evolutions/` directory of your application.
If you want to change the default location, set the `play.evolutions.path = {path}` configuration variable to the path within the folder within resource. 
Example, if your evolution scripts are within `resources/conf/evolutions` your configuration will look like this  `play.evolutions.path = conf/evolutions`

The first script is named  `1.sql`, the second script  `2.sql`, and so on…

Each script contains two parts:

-   The  **Ups**  part that describes the required transformations.
-   The  **Downs**  part that describes how to revert them.

For example, take a look at this first evolution script that bootstraps a basic application:
Add configuration variables to the application.properties of your Spring Boot project.

```sql
-- !Ups

CREATE TABLE User (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,
    PRIMARY KEY (id)
);

-- !Downs

DROP TABLE User;
```

The  **Ups**  and  **Downs**  parts are delimited by using a standard, single-line SQL comment in your script containing either  `!Ups`  or  `!Downs`, respectively. Both SQL92 (`--`) and MySQL (`#`) comment styles are supported, but we recommend using SQL92 syntax because it is supported by more databases.

>Play splits your `.sql` files into a series of semicolon-delimited statements before executing them one-by-one against the database. So if you need to use a semicolon _within_ a statement, escape it by entering `;;` instead of `;`. For example, `INSERT INTO punctuation(name, character) VALUES ('semicolon', ';;');`.

Evolutions are automatically activated if a database is configured in  `application.properties`  and evolution scripts are present. You can disable them by setting  `play.evolutions.enabled=false`. For example when tests set up their own database you can disable evolutions for the test environment.

When evolutions are activated, Play will check your database schema state before each request in DEV mode, or before starting the application in PROD mode. In DEV mode, if your database schema is not up to date, an error page will suggest that you synchronize your database schema by running the appropriate SQL script.

#### Synchronizing concurrent changes

Now let’s imagine that we have two developers working on this project. Jamie will work on a feature that requires a new database table. So Jamie will create the following  `2.sql`  evolution script:

```sql
-- Add Post

-- !Ups
CREATE TABLE Post (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    content text NOT NULL,
    postedAt date NOT NULL,
    author_id bigint(20) NOT NULL,
    FOREIGN KEY (author_id) REFERENCES User(id),
    PRIMARY KEY (id)
);

-- !Downs
DROP TABLE Post;
```

Play will apply this evolution script to Jamie’s database.

On the other hand, Robin will work on a feature that requires altering the User table. So Robin will also create the following  `2.sql`  evolution script:

```sql
-- Update User

-- !Ups
ALTER TABLE User ADD age INT;

-- !Downs
ALTER TABLE User DROP age;
```

Robin finishes the feature and commits (let’s say by using Git). Now Jamie has to merge Robin’s work before continuing, so Jamie runs git pull, and the merge has a conflict, like:

```
Auto-merging /evolutions/2.sql
CONFLICT (add/add): Merge conflict in /evolutions/2.sql
Automatic merge failed; fix conflicts and then commit the result.
```

Each developer has created a  `2.sql`  evolution script. So Jamie needs to merge the contents of this file:

```sql
<<<<<<< HEAD
-- Add Post

-- !Ups
CREATE TABLE Post (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    content text NOT NULL,
    postedAt date NOT NULL,
    author_id bigint(20) NOT NULL,
    FOREIGN KEY (author_id) REFERENCES User(id),
    PRIMARY KEY (id)
);

-- !Downs
DROP TABLE Post;
=======
-- Update User

-- !Ups
ALTER TABLE User ADD age INT;

-- !Downs
ALTER TABLE User DROP age;
>>>>>>> devB
```

The merge is really easy to do:

```sql
-- Add Post and update User

-- !Ups
ALTER TABLE User ADD age INT;

CREATE TABLE Post (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    content text NOT NULL,
    postedAt date NOT NULL,
    author_id bigint(20) NOT NULL,
    FOREIGN KEY (author_id) REFERENCES User(id),
    PRIMARY KEY (id)
);

-- !Downs
ALTER TABLE User DROP age;

DROP TABLE Post;
```

This evolution script represents the new revision 2 of the database, that is different of the previous revision 2 that Jamie has already applied.

So Play will detect it and ask Jamie to synchronize the database by first reverting the old revision 2 already applied, and by applying the new revision 2 script:

####   Inconsistent states

Sometimes you will make a mistake in your evolution scripts, and they will fail. In this case, Play will mark your database schema as being in an inconsistent state and will ask you to manually resolve the problem before continuing.

#### Transactional DDL
If your database supports  [Transactional DDL](https://wiki.postgresql.org/wiki/Transactional_DDL_in_PostgreSQL:_A_Competitive_Analysis)  you can set  `play.evolutions.autocommit=false`  in `application.properties` to change this behaviour, causing  **all**  statements to be executed in  **one transaction**  only. Now, when an evolution script fails to apply with autocommit disabled, the whole transaction gets rolled back and no changes will be applied at all. So your database stays “clean” and will not become inconsistent. This allows you to easily fix any DDL issues in the evolution scripts without having to modify the database by hand like described above.
>Note: Originally by default in Play Framework, each statement of each evolution script will be executed immediately. Here we decided to adopt the standard of executing all commands per transaction `play.evolutions.autocommit=false`.

#### Evolution storage and limitations

Evolutions are stored in your database in a table called  `play_evolutions`. A Text column stores the actual evolution script. Your database probably has a 64kb size limit on a text column. To work around the 64kb limitation you could: manually alter the play_evolutions table structure changing the column type or (preferred) create multiple evolutions scripts less than 64kb in size.

## License

This project is licensed under the Apache 2 - see the [LICENSE](https://github.com/gabriel-76/database-evolutions/blob/master/LICENSE) file for details