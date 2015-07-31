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
     *
     * @see <a
     * href="https://github.com/secondmarket/mongeez/wiki/How-to-use-mongeez#create-a-mongeezxml-file-that-include-all-change-logs">
     * Create a mongeez.xml file that includes all change logs</a>
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
    private String password;

    /**
     * The database to migrate.

     */
    private String database;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

}
