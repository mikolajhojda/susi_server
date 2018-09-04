/**
 *  ExampleSkillService
 *  Copyright 18.06.2016 by Michael Peter Christen, @0rb1t3r
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program in the file lgpl21.txt
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package ai.susi.server.api.cms;

import ai.susi.DAO;
import ai.susi.json.JsonObjectWithDefault;
import ai.susi.mind.SusiSkill;
import ai.susi.server.*;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 This Servlet gives a API Endpoint to list examples for all the Skills given its model, group and language.
 Can be tested on http://127.0.0.1:4000/cms/getExampleSkill.json
 */
public class ExampleSkillService extends AbstractAPIHandler implements APIHandler {

    private static final long serialVersionUID = -8691223678852307876L;

    @Override
    public UserRole getMinimalUserRole() { return UserRole.ANONYMOUS; }

    @Override
    public JSONObject getDefaultPermissions(UserRole baseUserRole) {
        return null;
    }

    @Override
    public String getAPIPath() {
        return "/cms/getExampleSkill.json";
    }

    @Override
    public ServiceResponse serviceImpl(Query call, HttpServletResponse response, Authorization rights, final JsonObjectWithDefault permissions) {

        DAO.observe(); // get a database update
        
        String model = call.get("model", "");
        String group = call.get("group", "");
        String language = call.get("language", "");

        JSONObject examples = new JSONObject(true);
        for (Map.Entry<SusiSkill.ID, SusiSkill> entry : DAO.susi.getSkillMetadata().entrySet()) {
            SusiSkill.ID skill = entry.getKey();
            if (skill.hasModel(model) &&
                skill.hasGroup(group) &&
                skill.hasLanguage(language)) {
                examples.put(skill.getPath(), entry.getValue().getExamples());
            }
        }
        
        JSONObject json = new JSONObject(true)
                .put("model", model)
                .put("group", group)
                .put("language", language)
                .put("examples", examples);
        json.put("accepted", true);
        json.put("message", "Success: Examples fetched");
        return new ServiceResponse(json);
    }

}
