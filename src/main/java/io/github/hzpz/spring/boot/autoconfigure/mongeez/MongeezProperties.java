/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hzpz.spring.boot.autoconfigure.mongeez;

import org.mongeez.MongoAuth;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * Configuration properties for Mongeez.
 *
 * @author Timo Kockert
 */
@ConfigurationProperties(prefix = "mongeez")
public class MongeezProperties {

    /**
     * Location of migration script.
     */
    private String location = "db/mongeez.xml";

    /**
     * Enable Mongeez.
     */
    private boolean enabled = true;

    /**
     * Login user of the database to migrate.
     */
    private String username;

    /**
     * Login password of the database to migrate.
     */
    private char[] password;

    /**
     * The database to migrate.
     */
    private String database;

    /**
     * Authentication database name.
     */
    private String authenticationDatabase;

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getAuthenticationDatabase() {
        return this.authenticationDatabase;
    }

    public void setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
    }

    public boolean hasCredentials() {
        return this.username != null && this.password != null;
    }

    public void clearPassword() {
        if (this.password == null) {
            return;
        }
        for (int i = 0; i < this.password.length; i++) {
            this.password[i] = 0;
        }
    }

    public MongoAuth createMongoAuth() {
        String authDb = this.authenticationDatabase == null ? this.database : this.authenticationDatabase;
        try {
            return instantiateMongoAuth(this.username, String.valueOf(this.password), authDb);
        } finally {
            clearPassword();
        }
    }

    // Work around breaking change introduced in Mongeez 0.9.6.
    private MongoAuth instantiateMongoAuth(String username, String password, String authDb) {
        Optional<Constructor<?>> constructor = ReflectionUtils.findConstructor(MongoAuth.class, username, password, authDb);
        if (constructor.isPresent()) {
            return (MongoAuth) BeanUtils.instantiateClass(constructor.get(), username, password, authDb);
        }

        constructor = ReflectionUtils.findConstructor(MongoAuth.class, username, password);
        if (constructor.isPresent()) {
            return (MongoAuth) BeanUtils.instantiateClass(constructor.get(), username, password);
        }

        throw new IllegalStateException("No suitable constructor found to instantiate MongoAuth. " +
                "Are you using a supported Mongeez version?");
    }

}
