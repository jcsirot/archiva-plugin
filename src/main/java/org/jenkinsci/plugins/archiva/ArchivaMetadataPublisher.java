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

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adrien Lecharpentier
 */
public class ArchivaMetadataPublisher extends Recorder {

    @Override
    public ArchivaDescriptor getDescriptor() {
        return (ArchivaDescriptor) super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static class ArchivaDescriptor extends BuildStepDescriptor<Publisher> {

        private List<ArchivaInstance> instances;

        public ArchivaDescriptor() {
            super(ArchivaMetadataPublisher.class);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            Object servers = json.get("archivaServer");
            if (!JSONNull.getInstance().equals(servers)) {
                instances = req.bindJSONToList(ArchivaInstance.class, servers);
            } else {
                instances = null;
            }
            save();
            return super.configure(req, json);
        }

        @Override
        public String getDisplayName() {
            return "Archiva";
        }

        public List<ArchivaInstance> getInstances() {
            return instances;
        }

        public FormValidation doTestConnection(@QueryParameter("url") String url)
                throws IOException, ServletException {
            if (StringUtils.isBlank(url)) {
                return FormValidation.error("Please, set a correct url");
            }
            url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            defaultHttpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            HttpGet get = new HttpGet(url + "/restServices/redbackServices/loginService/ping");

            try {
                HttpResponse httpResponse = defaultHttpClient.execute(get);
                if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    return FormValidation.error("Can not connect to the server. " +
                            "Please check url and Archiva version (>=1.4-M3)");
                }
            } finally {
                defaultHttpClient.getConnectionManager().shutdown();
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUrl(@QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("Please, set an url");
            }
            try {
                new URI(value);
            } catch (URISyntaxException e) {
                return FormValidation.error("Please, set a correct url");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckName(@QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("You have to set a name on this instance");
            }
            return FormValidation.ok();
        }
    }
}
