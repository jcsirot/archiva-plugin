package org.jenkinsci.plugins.archiva;

/*
 * Copyright 2012 Adrien Lecharpentier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * @author Adrien Lecharpentier
 */
public class ArchivaInstance implements Serializable {
    private String name;
    private String url;

    private ArchivaCredential credential;

    @DataBoundConstructor
    public ArchivaInstance(String name, String url, ArchivaCredential credential) {
        this.name = name;
        this.url = url;
        this.credential = credential;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArchivaCredential getCredential() {
        return credential;
    }

    public void setCredential(ArchivaCredential credential) {
        this.credential = credential;
    }
}
