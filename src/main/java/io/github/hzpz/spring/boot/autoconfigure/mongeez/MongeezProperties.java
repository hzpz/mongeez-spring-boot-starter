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
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Mongeez.
 *
 * @author Timo Kockert
 */
@ConfigurationProperties(prefix = "mongeez", ignoreUnknownFields = true)
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
        try {
            return new MongoAuth(this.username, new String(this.password));
        } finally {
            clearPassword();
        }
    }

}
